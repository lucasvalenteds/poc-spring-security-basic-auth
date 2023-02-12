create table product
(
    id     serial primary key,
    "name" varchar not null
);

create table user_product
(
    user_id    bigint,
    product_id bigint,

    constraint fk_user_id foreign key (user_id) references users (id),
    constraint fk_product_id foreign key (product_id) references product (id)
);
