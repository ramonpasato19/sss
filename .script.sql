ALTER TABLE region DROP CONSTRAINT region_uk;
ALTER TABLE region ADD CONSTRAINT region_uk UNIQUE (country_id, code);
ALTER TABLE state DROP CONSTRAINT state_uk;
ALTER TABLE state ADD CONSTRAINT state_uk UNIQUE (country_id, region_id, code);
ALTER TABLE city DROP CONSTRAINT city_uk;
ALTER TABLE city ADD CONSTRAINT city_uk UNIQUE (country_id, region_id, state_id, code);
ALTER TABLE
    district DROP CONSTRAINT district_uk;
ALTER TABLE
    district ADD CONSTRAINT district_uk UNIQUE (country_id, region_id,
    state_id, city_id, code);
    
CREATE TABLE
    account_portfolio_status
    (
        account_portfolio_status_id CHARACTER VARYING(3) NOT NULL,
        name CHARACTER VARYING(100) NOT NULL,
        CONSTRAINT account_portfolio_status_pk PRIMARY KEY (account_portfolio_status_id)
    );
insert into account_portfolio_status values ('001','OPERACION COMPRADA/ORIGINADA');
insert into account_portfolio_status values ('002','OPERACION VENDIDA');
insert into account_portfolio_status values ('003','OPERACION RECOMPRADA');

ALTER TABLE account_portfolio RENAME COLUMN status_id TO account_portfolio_status_id;

ALTER TABLE account_portfolio ADD CONSTRAINT acc_por_status_fk FOREIGN KEY (account_portfolio_status_id) REFERENCES account_portfolio_status (account_portfolio_status_id);

ALTER TABLE account_portfolio DROP COLUMN purchased_from_person;

ALTER TABLE account_portfolio DROP COLUMN sold_to_person;

CREATE TABLE
    category_type
    (
        category_type_id CHARACTER VARYING(3) NOT NULL,
        name CHARACTER VARYING(100) NOT NULL,
        CONSTRAINT category_type_pk PRIMARY KEY (category_type_id)
    );
insert into category_type values ('001','CATEGORIA NORMAL');
insert into category_type values ('002','ANTICIPOS');


ALTER TABLE category ADD COLUMN category_type_id CHARACTER VARYING(3);
update category set category_type_id = '001';

ALTER TABLE category ALTER COLUMN category_type_id SET NOT NULL;
ALTER TABLE category ADD CONSTRAINT category_cat_type_fk FOREIGN KEY (category_type_id) REFERENCES category_type ("category_type_id");

ALTER TABLE category ADD COLUMN printable INTEGER DEFAULT 0;
update category set printable = 0;
update category set printable = 1 where category_id in ('BALANCE','ADVANCE','ADVSALPORT');
ALTER TABLE category ALTER COLUMN printable SET NOT NULL;

CREATE SEQUENCE product_account_loan_employee
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;

INSERT INTO product (product_id, product_type_id, currency_id, name, interest_rate, prefix, sufix, sequence_db_name, single_account, own_product, lpad, rpad, default_interest_type, days_grace, days_grace_collection_fee, apply_default_interest_accrued, apply_automatic_debit, operating_condition_id, sale_portfolio_utility_distribution) VALUES ('110', '103', 'USD', 'PRESTAMO A EMPLEADOS', 15.20, 'PEM', '', 'PRODUCT_ACCOUNT_LOAN_EMPLOYEE', 0, 0, '000000', '', 'R', 5, 0, 1, 0, '001', null);

ALTER TABLE account DROP CONSTRAINT external_code_uk;
ALTER TABLE account ADD CONSTRAINT external_code_uk UNIQUE (product_id, person_id, external_code);

ALTER TABLE loan_collection_fee ADD COLUMN product_id CHARACTER VARYING(20);
update loan_collection_fee set product_id = '000';
ALTER TABLE loan_collection_fee ALTER COLUMN product_id SET NOT NULL;
ALTER TABLE loan_collection_fee ADD CONSTRAINT loancollectionfee_product_fk FOREIGN KEY (product_id) REFERENCES product (product_id);

ALTER TABLE loan_collection_fee ALTER COLUMN loan_collection_fee_id TYPE CHARACTER VARYING(10);
ALTER TABLE default_interest_rate ALTER COLUMN default_interest_rate_id TYPE CHARACTER VARYING(10);

insert into loan_collection_fee
select p.product_id||loan_collection_fee_id as loan_collection_fee,
from_amount, to_amount, from_days, to_days, value, p.product_id
from loan_collection_fee lcf, product p, product_type pt
where p.product_type_id = pt.product_type_id
and pt.product_class_id = '06'
and p.product_id not in (select product_id from loan_collection_fee);

delete from loan_collection_fee where product_id = '000';

INSERT INTO category (category_id, book_account, name, allows_negative_balance, expires_zero_balance, category_type_id, printable) VALUES ('BLOCKED', 'XXX', 'SALDO BLOQUEADO DE UNA CUENTA', 0, 0, '001', 1);

update category set category_type_id = '002' where category_id in ('ADVANCE','ADVSALPORT');

ALTER TABLE account_overdue_balance ALTER COLUMN capital_reduced SET DEFAULT 0;
ALTER TABLE account_overdue_balance ALTER COLUMN insurance SET DEFAULT 0;
ALTER TABLE account_overdue_balance ALTER COLUMN capital SET DEFAULT 0;
ALTER TABLE account_overdue_balance ALTER COLUMN commission SET DEFAULT 0;
ALTER TABLE account_overdue_balance ALTER COLUMN interest SET DEFAULT 0;
ALTER TABLE account_overdue_balance ALTER COLUMN default_interest SET DEFAULT 0;
ALTER TABLE account_overdue_balance ALTER COLUMN insurance_mortgage SET DEFAULT 0;
ALTER TABLE account_overdue_balance ALTER COLUMN total SET DEFAULT 0;
ALTER TABLE account_overdue_balance ALTER COLUMN collection_fee SET DEFAULT 0;
ALTER TABLE account_overdue_balance ALTER COLUMN receivable_fee SET DEFAULT 0;
ALTER TABLE account_overdue_balance ALTER COLUMN legal_fee SET DEFAULT 0;


CREATE OR REPLACE FUNCTION schema.get_collection_fee(
    p_overdue_days integer,
    p_overdue_value numeric,
    p_account_id character varying,
    p_last_payment_date_collection_fee date)
  RETURNS numeric AS
$BODY$
declare
        v_rate numeric;
        v_product_id varchar;
        t_account_loan account_loan%ROWTYPE;
        
	v_collection_fee numeric := 0;
	v_days_grace integer := 0;
begin        
        if (p_overdue_value <=0 or p_overdue_days<=0 ) then
                return v_collection_fee;
        end if; 
        
        if (p_last_payment_date_collection_fee is not null) then
                 return v_collection_fee;
        end if;
        
        select a.product_id into v_product_id from schema.account a where a.account_id = p_account_id;
        
        select * into t_account_loan from schema.account_loan al where al.account_id = p_account_id;
        
        IF (t_account_loan.days_grace_collection_fee is not null) then
                v_days_grace := t_account_loan.days_grace_collection_fee;
        end if;
        
        if (p_overdue_days<=0 or p_overdue_days<=v_days_grace) then
                return v_collection_fee;
        end if; 
                
        SELECT coalesce(o.value,0) into v_collection_fee FROM schema.loan_collection_fee o 
	WHERE p_overdue_days BETWEEN o.from_days AND o.to_days 
	AND p_overdue_value BETWEEN o.from_amount AND o.to_amount 
	AND o.product_id = v_product_id;
	
        return round(v_collection_fee,2);
end;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION schema.get_collection_fee(integer, numeric, character varying, date)
  OWNER TO postgres;


CREATE OR REPLACE FUNCTION schema.get_default_interest(
    p_real_overdue_days integer,
    p_overdue_days integer,
    p_overdue_value numeric,
    p_account_id character varying,
    p_last_payment_date date)
  RETURNS numeric AS
$BODY$
declare
        v_rate numeric;
        v_product_id varchar;
        t_account_loan account_loan%ROWTYPE;
        t_product product%ROWTYPE;
        
	v_default_interest numeric := 0;
	v_day_rate numeric;
	v_default_interest_rate_or_value numeric;
	v_days_grace integer := 0;
	v_overdue_days integer := 0;
begin        
        if (p_overdue_value <=0 or p_overdue_days<=0 ) then
                return v_default_interest;
        end if; 
                
        select a.product_id into v_product_id from schema.account a where a.account_id = p_account_id;
        
        select * into t_account_loan from schema.account_loan al where al.account_id = p_account_id;
        
        select * into t_product from schema.product pro where pro.product_id = v_product_id;
        
        
        
        IF (t_account_loan.days_grace is not null) then
                v_days_grace := t_account_loan.days_grace;
        end if;
	
	if (p_last_payment_date is not null) then
                v_overdue_days := p_real_overdue_days;
                v_days_grace := 0;
        else 
                v_overdue_days := p_overdue_days;
        end if;
                
        select coalesce((case when p.default_interest_type = 'R' then t.rate else t.daily_value end),0)
        into v_default_interest_rate_or_value from schema.default_interest_rate t, schema.product p
        where p_overdue_days BETWEEN t.from_days AND t.to_days
	AND t.product_id = v_product_id
	and p.product_id = t.product_id;
                       
        IF (v_default_interest_rate_or_value) is null THEN
                return v_default_interest;
        END IF;
        
        if (v_default_interest_rate_or_value is not null AND v_default_interest_rate_or_value = 0) then
                return v_default_interest;
        END IF;
        
        if (v_overdue_days<=0 or v_overdue_days<=v_days_grace) then
                return v_default_interest;
        end if; 
                

        if (t_account_loan.apply_default_interest_accrued = 0) then
                v_overdue_days := v_overdue_days-v_days_grace;
        end if;
        
        if(t_product.default_interest_type = 'R' ) then
                v_default_interest_rate_or_value := v_default_interest_rate_or_value + 1;
                v_rate := t_account_loan.interest_rate * v_default_interest_rate_or_value;
                v_day_rate := v_rate/36000;
                v_default_interest := p_overdue_value * v_day_rate;
                v_default_interest := v_default_interest * v_overdue_days;
        end if;
        
        if(t_product.default_interest_type = 'V' ) then
        	v_default_interest := v_default_interest_rate_or_value * v_overdue_days;
	END IF;
		
        return round(v_default_interest,2);
end;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION schema.get_default_interest(integer, integer, numeric, character varying, date)
  OWNER TO postgres;
