-- Backfill identifier.type from UPPER_UNDERSCORE to camelCase to match the OpenAPI contract.
-- Previously stored via Identifier.Type.name() (e.g. "AD_ACCOUNT"); now stored via
-- Identifier.getTypeString() (e.g. "adAccount").
update identifier set type = 'adAccount' where type = 'AD_ACCOUNT';
update identifier set type = 'partyId' where type = 'PARTY_ID';
