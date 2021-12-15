create table dummy_user_first_name(
    user_first_name_id bigint not null auto_increment primary key,
    first_name varchar(255) not null,
    first_name_kana varchar(255) not null,
    first_name_roman varchar(255) not null
);

create table dummy_user_last_name(
    user_last_name_id bigint not null auto_increment primary key,
    last_name varchar(255) not null,
    last_name_kana varchar(255) not null,
    last_name_roman varchar(255) not null
);
