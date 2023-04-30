create table card
(
    id                  bigint not null auto_increment,
    count_forgot        integer,
    creationldt         datetime,
    destin_lang         varchar(255),
    example             varchar(500),
    example_translation varchar(500),
    forgotldt           datetime,
    invisible           BIT    not null,
    learned             BIT    not null,
    learnedldt          datetime,
    picture             varchar(50),
    sound               varchar(500),
    source_lang         varchar(255),
    transcription       varchar(100),
    translation         varchar(100),
    unrepeated          BIT    not null,
    word                varchar(100),
    dictionary_id       bigint,
    primary key (id)
);
create table dictionary
(
    id          bigint not null auto_increment,
    creationldt datetime,
    destin_lang varchar(255),
    invisible   BIT    not null,
    name        varchar(100),
    parent      bigint,
    picture     varchar(50),
    priority    integer,
    source_lang varchar(255),
    unrepeated  BIT    not null,
    primary key (id)
);
create table picture
(
    id         bigint  not null auto_increment,
    path       varchar(255),
    priority   integer not null,
    source_url varchar(500),
    primary key (id)
);
create table user_role
(
    user_id bigint not null,
    roles   varchar(255)
);
create table user_subscribtions
(
    subscriber_id bigint not null,
    channel_id    bigint not null,
    primary key (channel_id, subscriber_id)
);
create table usr
(
    id               bigint not null auto_increment,
    active           bit    not null,
    email            varchar(100),
    last_modifiedldt datetime,
    password         varchar(255),
    sub              varchar(255),
    token            varchar(255),
    username         varchar(100),
    primary key (id)
);
create table verification_token
(
    token       varchar(16) not null,
    expiry_date datetime,
    user_id     bigint      not null,
    primary key (token)
);
alter table dictionary
    add constraint UKqo3d9m51el21jto9bkmbe0sn7 unique (name, unrepeated, creationldt);
alter table usr
    add constraint UK_g9l96r670qkidthshajdtxrqf unique (email);
alter table usr
    add constraint UK_1kgqn0yq8kgvc9relbd9xoinp unique (sub);
alter table usr
    add constraint UK_kb24scwmea9l7cdjxh1p8he22 unique (token);
alter table card
    add constraint FK2eiqnm3vylp7260lqpwmjw5n foreign key (dictionary_id) references dictionary (id);
alter table user_role
    add constraint FKfpm8swft53ulq2hl11yplpr5 foreign key (user_id) references usr (id);
alter table user_subscribtions
    add constraint FKok87ml2wy5svxyptbp1dpthwb foreign key (channel_id) references usr (id);
alter table user_subscribtions
    add constraint FK6ykm0axci5eekkax1cl081lkj foreign key (subscriber_id) references usr (id);
alter table verification_token
    add constraint FK5fi7mv5p74mow94h0xxw50rui foreign key (user_id) references usr (id);