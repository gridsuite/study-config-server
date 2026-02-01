INSERT INTO column_filter (uuid, filter_data_type, filter_type, filter_value, filter_tolerance)
SELECT
    gen_random_uuid(),
    filter_data_type,
    filter_type,
    filter_value,
    filter_tolerance
FROM spreadsheet_column
WHERE
    filter_data_type IS NOT NULL
   OR filter_type IS NOT NULL
   OR filter_value IS NOT NULL
   OR filter_tolerance IS NOT NULL;

UPDATE spreadsheet_column sc
SET column_entity_column_filter_id = cf.uuid
FROM column_filter cf
WHERE
    sc.filter_data_type IS NOT DISTINCT FROM cf.filter_data_type
  AND sc.filter_type IS NOT DISTINCT FROM cf.filter_type
  AND sc.filter_value IS NOT DISTINCT FROM cf.filter_value
  AND sc.filter_tolerance IS NOT DISTINCT FROM cf.filter_tolerance;