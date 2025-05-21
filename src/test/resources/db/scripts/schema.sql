
    create table attachment (
        file_size integer,
        created datetime(6),
        attachment_data_id varchar(255) not null,
        file_name varchar(255),
        id varchar(255) not null,
        message_id varchar(255) not null,
        mime_type varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table attachment_data (
        id varchar(255) not null,
        file longblob,
        primary key (id)
    ) engine=InnoDB;

    create table conversation (
        channel_id varchar(255),
        id varchar(255) not null,
        municipality_id varchar(255),
        namespace varchar(255),
        topic varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table conversation_meta_data (
        conversation_id varchar(255) not null,
        id varchar(255) not null,
        `key` varchar(255),
        meta_data_id varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table conversation_meta_data_values (
        conversation_meta_data_id varchar(255) not null,
        values varchar(255)
    ) engine=InnoDB;

    create table conversation_participants (
        conversation_id varchar(255) not null,
        type varchar(255),
        value varchar(255)
    ) engine=InnoDB;

    create table message (
        created datetime(6),
        content varchar(255),
        conversation_id varchar(255) not null,
        id varchar(255) not null,
        in_reply_to varchar(255),
        sequence_number varchar(255),
        type varchar(255),
        value varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table message_read_by (
        message_id varchar(255) not null,
        type varchar(255),
        value varchar(255)
    ) engine=InnoDB;

    alter table if exists conversation_meta_data 
       add constraint UK9u5nrrqfv1mgdu83joxidqs2x unique (meta_data_id);

    alter table if exists attachment 
       add constraint fk_attachment_data_attachment 
       foreign key (attachment_data_id) 
       references attachment_data (id);

    alter table if exists attachment 
       add constraint fk_attachment_message 
       foreign key (message_id) 
       references message (id);

    alter table if exists conversation_meta_data 
       add constraint FK5safa7b94n8qocs41ltj494g 
       foreign key (meta_data_id) 
       references conversation_meta_data (id);

    alter table if exists conversation_meta_data 
       add constraint fk_meta_data_conversation_id 
       foreign key (conversation_id) 
       references conversation (id);

    alter table if exists conversation_meta_data_values 
       add constraint fk_meta_data_values 
       foreign key (conversation_meta_data_id) 
       references conversation_meta_data (id);

    alter table if exists conversation_participants 
       add constraint fk_participants_conversation_id 
       foreign key (conversation_id) 
       references conversation (id);

    alter table if exists message 
       add constraint fk_message_conversation_id 
       foreign key (conversation_id) 
       references conversation (id);

    alter table if exists message_read_by 
       add constraint fk_read_by_message_id 
       foreign key (message_id) 
       references message (id);
