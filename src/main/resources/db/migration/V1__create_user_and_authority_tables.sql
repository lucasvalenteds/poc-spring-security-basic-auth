create table users
(
    id         serial,
    username   varchar not null,
    "password" varchar not null,

    constraint pk_user_id primary key (id)
);

create table authority
(
    id        serial,
    authority varchar not null,

    constraint pk_authority_id primary key (id)
);

create table user_authority
(
    user_id      bigint,
    authority_id bigint,

    constraint fk_user_authority_user_id foreign key (user_id) references users (id),
    constraint fk_user_authority_authority_id foreign key (authority_id) references authority (id)
);

create unique index ix_authority_user on user_authority (user_id, authority_id);
