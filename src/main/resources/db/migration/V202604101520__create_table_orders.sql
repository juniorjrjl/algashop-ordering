create table orders (
    id bigint not null,
    shipping_cost numeric(38,2),
    shipping_expected_date date,
    total_amount numeric(38,2),
    total_items integer,
    canceled_at timestamp(6) with time zone,
    last_modified_at timestamp(6) with time zone,
    paid_at timestamp(6) with time zone,
    placed_at timestamp(6) with time zone,
    ready_at timestamp(6) with time zone,
    version bigint,
    created_by uuid,
    customer_id uuid not null,
    last_modified_by uuid,
    billing_address_city varchar(255),
    billing_address_complement varchar(255),
    billing_address_neighborhood varchar(255),
    billing_address_number varchar(255),
    billing_address_state varchar(255),
    billing_address_street varchar(255),
    billing_address_zip_code varchar(255),
    billing_document varchar(255),
    billing_email varchar(255),
    billing_first_name varchar(255),
    billing_last_name varchar(255),
    billing_phone varchar(255),
    order_status varchar(255),
    payment_method varchar(255),
    shipping_address_city varchar(255),
    shipping_address_complement varchar(255),
    shipping_address_neighborhood varchar(255),
    shipping_address_number varchar(255),
    shipping_address_state varchar(255),
    shipping_address_street varchar(255),
    shipping_address_zip_code varchar(255),
    shipping_recipient_document varchar(255),
    shipping_recipient_first_name varchar(255),
    shipping_recipient_last_name varchar(255),
    shipping_recipient_phone varchar(255),
    primary key (id)
);

create index idx_order_customer_id on public.orders (customer_id);
alter table public.orders add constraint fk_order_customer_id foreign key (customer_id) references public.customers(id);

create table public.order_items (
    id bigint not null,
    price numeric(38,2),
    quantity integer,
    total_amount numeric(38,2),
    order_id bigint not null,
    product_id uuid,
    product_name varchar(255),
    primary key (id)
);

create index idx_order_item_order_id on public.order_items (order_id);
alter table public.order_items add constraint fk_order_item_order_id foreign key (order_id) references public.orders(id);
