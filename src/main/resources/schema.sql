create table if not exists url
(
    id          varchar(36)                              not null
        primary key,
    protocol    varchar(255)                             not null,
    host        varchar(255)                             not null,
    path        varchar(255)                             not null,
    url         varchar(255)                             not null,
    create_time datetime(6) default CURRENT_TIMESTAMP(6) not null,
    update_time datetime(6) default CURRENT_TIMESTAMP(6) not null on update CURRENT_TIMESTAMP(6),
    constraint url_url_uindex
        unique (url)
);

create index url_create_time_index
    on url (create_time desc);

create index url_host_index
    on url (host);

create index url_update_time_index
    on url (update_time desc);

create table if not exists user
(
    id          varchar(36)                              not null
        primary key,
    user_email  varchar(255)                             not null,
    name        varchar(255)                             not null,
    create_time datetime(6) default CURRENT_TIMESTAMP(6) not null,
    update_time datetime(6) default CURRENT_TIMESTAMP(6) not null on update CURRENT_TIMESTAMP(6),
    constraint user_user_email_uindex
        unique (user_email)
);

create table if not exists bookmark
(
    id           varchar(36)                              not null
        primary key,
    display_name varchar(1024)                            not null,
    url_id       varchar(36)                              not null,
    user_id      varchar(36)                              not null,
    create_time  datetime(6) default CURRENT_TIMESTAMP(6) not null,
    update_time  datetime(6) default CURRENT_TIMESTAMP(6) not null on update CURRENT_TIMESTAMP(6),
    constraint bookmark_url_id_fk
        foreign key (url_id) references url (id),
    constraint bookmark_user_id_fk
        foreign key (user_id) references user (id)
);

create index _update_time_index
    on bookmark (update_time desc);

create index bookmark_create_time_index
    on bookmark (create_time desc);

create table if not exists tag
(
    id          varchar(36)                              not null
        primary key,
    tag_name    varchar(255)                             not null,
    user_id     varchar(36)                              not null,
    create_time datetime(6) default CURRENT_TIMESTAMP(6) not null,

    constraint tag_tag_name_user_id_uindex
        unique (tag_name, user_id),
    constraint tag_user_id_fk
        foreign key (user_id) references user (id)
);

create index tag_create_time_index
    on tag (create_time desc);

create table if not exists tags_bookmarks
(
    id          varchar(36)                              not null
        primary key,
    bookmark_id varchar(36)                              not null,
    tag_id      varchar(36)                              not null,
    create_time datetime(6) default CURRENT_TIMESTAMP(6) not null,
    constraint tags_bookmarks_bookmark_id_tag_id_uindex
        unique (bookmark_id, tag_id),
    constraint tags_bookmarks_bookmark_id_fk
        foreign key (bookmark_id) references bookmark (id),
    constraint tags_bookmarks_tag_id_fk
        foreign key (tag_id) references tag (id)
);

create index tags_bookmarks_create_time_index
    on tags_bookmarks (create_time desc);

create index user_create_time_index
    on user (create_time desc);

create index user_update_time_index
    on user (update_time desc);
