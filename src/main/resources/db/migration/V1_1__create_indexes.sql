drop index if exists idx_message_sequence_number 
   on message;

drop index if exists idx_conversation_municipality_id 
   on conversation;

drop index if exists idx_conversation_namespace 
   on conversation;


create index if not exists idx_conversation_namespace_municipality_id_id 
   on conversation (namespace, municipality_id, id);
   
create index if not exists idx_conversation_topic 
   on conversation (topic);
   
create index if not exists idx_conversation_external_reference_key 
   on conversation_external_reference (`key`);

create index if not exists idx_conversation_metadata_id 
   on conversation_metadata (conversation_id, `key`);

create index if not exists idx_message_conversation_id_created 
   on message (conversation_id, created);

create index if not exists idx_message_conversation_id_sequence_number 
   on message (conversation_id, sequence_number);

create index if not exists idx_message_in_reply_to_message_id 
   on message (in_reply_to_message_id);

create index if not exists idx_message_read_by_message_id_identifier_id 
   on message_read_by (message_id, identifier_id);
