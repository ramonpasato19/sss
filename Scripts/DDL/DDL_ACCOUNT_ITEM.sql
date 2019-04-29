alter table account_item add column cost_last_purchase numeric(13,4);
alter table public.account_item add column provider_last_purchase varchar(200);