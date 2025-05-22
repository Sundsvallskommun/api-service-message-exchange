SET FOREIGN_KEY_CHECKS = 0;

truncate table attachment;
truncate table attachment_data;
truncate table conversation;
truncate table conversation_metadata;
truncate table conversation_metadata_values;
truncate table conversation_external_references;
truncate table conversation_external_references_values;
truncate table conversation_participants;
truncate table identifier;
truncate table message;
truncate table message_read_by;

SET FOREIGN_KEY_CHECKS = 1;
