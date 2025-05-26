
    create sequence message_sequence_id_generator start with 1 increment by 1 nocache;

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
        id varchar(255) not null,
        municipality_id varchar(255),
        namespace varchar(255),
        topic varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table conversation_external_reference (
        conversation_id varchar(255) not null,
        id varchar(255) not null,
        `key` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table conversation_external_reference_values (
        conversation_external_reference_id varchar(255) not null,
        value varchar(255)
    ) engine=InnoDB;

    create table conversation_metadata (
        conversation_id varchar(255) not null,
        id varchar(255) not null,
        `key` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table conversation_metadata_values (
        conversation_metadata_id varchar(255) not null,
        `values` varchar(255)
    ) engine=InnoDB;

    create table conversation_participants (
        conversation_id varchar(255) not null,
        identifier_id varchar(255) not null
    ) engine=InnoDB;

    create table identifier (
        id varchar(255) not null,
        type varchar(255),
        value varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table message (
        created datetime(6),
        sequence_number bigint not null,
        conversation_id varchar(255) not null,
        created_by varchar(255),
        id varchar(255) not null,
        in_reply_to_message_id varchar(255),
        content longtext,
        primary key (id)
    ) engine=InnoDB;

    create table message_read_by (
        read_at datetime(6) not null,
        id varchar(255) not null,
        identifier_id varchar(255) not null,
        message_id varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table message_sequence (
        id bigint not null,
        primary key (id)
    ) engine=InnoDB;

    create index idx_conversation_municipality_id 
       on conversation (municipality_id);

    create index idx_conversation_namespace 
       on conversation (namespace);

    create index idx_conversation_namespace_municipality_id_id 
       on conversation (namespace, municipality_id, id);

    alter table if exists conversation_participants 
       add constraint uq_conversation_participants_identifier_id unique (identifier_id);

    create index idx_message_conversation_id 
       on message (conversation_id);

    create index idx_message_sequence_number 
       on message (sequence_number);

    alter table if exists message 
       add constraint uq_message_sequence_number unique (sequence_number);

    alter table if exists message 
       add constraint uq_message_created_by unique (created_by);

    alter table if exists message_read_by 
       add constraint uq_message_read_by_identifier_id unique (identifier_id);

    alter table if exists attachment 
       add constraint fk_attachment_data_attachment 
       foreign key (attachment_data_id) 
       references attachment_data (id);

    alter table if exists attachment 
       add constraint fk_attachment_message 
       foreign key (message_id) 
       references message (id);

    alter table if exists conversation_external_reference 
       add constraint fk_external_references_conversation_id 
       foreign key (conversation_id) 
       references conversation (id);

    alter table if exists conversation_external_reference_values 
       add constraint fk_external_reference_value 
       foreign key (conversation_external_reference_id) 
       references conversation_external_reference (id);

    alter table if exists conversation_metadata 
       add constraint fk_metadata_conversation_id 
       foreign key (conversation_id) 
       references conversation (id);

    alter table if exists conversation_metadata_values 
       add constraint fk_metadata_values 
       foreign key (conversation_metadata_id) 
       references conversation_metadata (id);

    alter table if exists conversation_participants 
       add constraint fk_conversation_participants_identifier_id 
       foreign key (identifier_id) 
       references identifier (id);

    alter table if exists conversation_participants 
       add constraint fk_conversation_participants_conversation_id 
       foreign key (conversation_id) 
       references conversation (id);

    alter table if exists message 
       add constraint fk_message_conversation_id 
       foreign key (conversation_id) 
       references conversation (id);

    alter table if exists message 
       add constraint fk_message_created_by 
       foreign key (created_by) 
       references identifier (id);

    alter table if exists message 
       add constraint fk_message_sequence_number 
       foreign key (sequence_number) 
       references message_sequence (id);

    alter table if exists message_read_by 
       add constraint fk_read_by_identifier_id 
       foreign key (identifier_id) 
       references identifier (id);

    alter table if exists message_read_by 
       add constraint fk_message_read_by 
       foreign key (message_id) 
       references message (id);
