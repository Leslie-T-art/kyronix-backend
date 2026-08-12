UPDATE reference_data_entries
SET id = 101
WHERE type = 'DEPARTMENT'
  AND code = 'OPS'
  AND id <> 101;

UPDATE reference_data_entries
SET id = 201
WHERE type = 'BRANCH'
  AND code = 'HQ'
  AND id <> 201;
