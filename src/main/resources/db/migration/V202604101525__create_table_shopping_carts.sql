create table shopping_carts (
    id uuid not null,
    total_amount numeric(38,2),
    total_items integer,
    created_at timestamp(6) with time zone,
    last_modified_at timestamp(6) with time zone,
    version bigint,
    created_by uuid,
    customer_id uuid not null,
    last_modified_by uuid,
    primary key (id)
);

create index idx_shopping_cart_customer_id on public.shopping_carts (customer_id);
alter table public.shopping_carts add constraint fk_shopping_cart_customer_id foreign key (customer_id) references public.customers(id);

create table shopping_cart_items (
    id uuid not null,
    available boolean,
    price numeric(38,2),
    quantity integer,
    total_amount numeric(38,2),
    product_id uuid,
    shopping_cart_id uuid not null,
    name varchar(255),
    primary key (id)
);

create index idx_shopping_cart_item_shopping_cart_id on public.shopping_cart_items (shopping_cart_id);
alter table public.shopping_cart_items add constraint fk_shopping_cart_item_shopping_cart_id foreign key (shopping_cart_id) references public.shopping_carts(id);
