create table if not exists tag
(
    id          varchar(36)                         not null
        primary key,
    tag_name    varchar(255)                        not null,
    create_time timestamp default CURRENT_TIMESTAMP not null,
    constraint tag_name
        unique (tag_name),
    constraint tag_pk_2
        unique (tag_name)
);

create index tag_create_time_index
    on tag (create_time desc);

create table if not exists url
(
    id          varchar(36)                         not null
        primary key,
    protocol    varchar(255)                        not null,
    host        varchar(255)                        not null,
    path        varchar(255)                        not null,
    url         varchar(255)                        not null,
    create_time timestamp default CURRENT_TIMESTAMP not null,
    update_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
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
    id          varchar(36)                         not null
        primary key,
    user_email  varchar(255)                        not null,
    create_time timestamp default CURRENT_TIMESTAMP not null,
    update_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    constraint user_user_email_uindex
        unique (user_email)
);

create table if not exists bookmark
(
    id          varchar(36)                         not null
        primary key,
    url_id      varchar(36)                         not null,
    user_id     varchar(36)                         not null,
    create_time timestamp default CURRENT_TIMESTAMP not null,
    update_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    constraint bookmark_url_id_fk
        foreign key (url_id) references url (id),
    constraint bookmark_user_id_fk
        foreign key (user_id) references user (id)
);

create index _update_time_index
    on bookmark (update_time desc);

create index bookmark_create_time_index
    on bookmark (create_time desc);

create table if not exists tags_bookmarks
(
    id          varchar(36)                         not null
        primary key,
    bookmark_id varchar(36)                         not null,
    tag_id      varchar(36)                         not null,
    create_time timestamp default CURRENT_TIMESTAMP not null,
    constraint tags_bookmarks_bookmark_id_fk
        foreign key (bookmark_id) references bookmark (id),
    constraint tags_bookmarks_tag_id_fk
        foreign key (tag_id) references tag (id)
);

create index tags_bookmarks_create_time_index
    on tags_bookmarks (create_time desc);

create table if not exists tags_users
(
    id          varchar(36)                         not null
        primary key,
    user_id     varchar(36)                         not null,
    tag_id      varchar(36)                         not null,
    create_time timestamp default CURRENT_TIMESTAMP not null,
    constraint tags_users_tag_id_fk
        foreign key (tag_id) references tag (id),
    constraint tags_users_user_id_fk
        foreign key (user_id) references user (id)
);

create index tags_users_create_time_index
    on tags_users (create_time desc);

create index user_create_time_index
    on user (create_time desc);

create index user_update_time_index
    on user (update_time desc);

