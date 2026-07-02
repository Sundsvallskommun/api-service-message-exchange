create table message_read_by_part
(
    read_at    datetime(6)  not null,
    id         varchar(255) not null,
    part       varchar(255) not null,
    message_id varchar(255) not null,
    primary key (id)
) engine = InnoDB;

create index if not exists idx_message_read_by_part_message_id_part
   on message_read_by_part (message_id, part);

alter table if exists message_read_by_part
    add constraint fk_message_read_by_part
        foreign key (message_id)
            references message (id);
