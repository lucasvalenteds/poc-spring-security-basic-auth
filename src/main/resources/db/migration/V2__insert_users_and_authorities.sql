insert into users (username, "password")
values ('john.smith', '$2a$10$WO2txLQFe3lEo5PAsO/TxuzCXVqijJ/q1OIMPuAmlNpOhJUQEp8Fi'),
       ('mary.jane', '$2a$10$NloM01TWIlxXK4yL.mJKC.5huT1fnRTlxzfFi.lwCQVSZcCzE032W'),
       ('guest', '$2a$10$QoxlMvLYNIXe7O4iebRsY.NdhHn9hUiu7oxoPRzTjE7x9yPogeE/e');

insert into authority (authority)
values ('USER'),
       ('ADMIN');

insert into user_authority (user_id, authority_id)
values (1, 1),
       (1, 2),
       (2, 1);
