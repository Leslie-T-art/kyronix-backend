UPDATE reference_data_entries
SET id = 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'
WHERE type = 'DEPARTMENT'
  AND code = 'OPS'
  AND id <> 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa';

UPDATE reference_data_entries
SET id = 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'
WHERE type = 'BRANCH'
  AND code = 'HQ'
  AND id <> 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb';
