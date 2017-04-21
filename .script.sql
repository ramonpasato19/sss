create table account_item_lots (
	account_item_lots_id varchar(32) primary key,
	number_lot varchar(10),
	code varchar(50),
	individual boolean,
	manufacturing_date  timestamp,
	expire_date  timestamp,
	discount_days int,
	discount numeric(13,4),
	discount_value numeric(13,4),
	quantity numeric(13,4),
	current_quantity numeric(13,4),
	manual_quantity numeric(13,4),
	comment varchar(200),
	cellar_location varchar(200),
	active boolean,
	account_id varchar(20) references account(account_id),
	unity_id varchar(20) references unity(unity_id)
);

create table account_item_lots_invoice(
	account_item_lots_invoice_id varchar(32) primary key,
	account_item_lots_id varchar(32) references account_item_lots(account_item_lots_id),
	account_id varchar(20) references account(account_id),
	quantity numeric(13,4)
);
