WITH created_filters AS (SELECT sc.uuid           AS column_uuid,
                                gen_random_uuid() AS filter_uuid,
                                sc.filter_data_type,
                                sc.filter_type,
                                sc.filter_value,
                                sc.filter_tolerance
                         FROM spreadsheet_column sc
                         WHERE sc.filter_data_type IS NOT NULL
                            OR sc.filter_type IS NOT NULL
                            OR sc.filter_value IS NOT NULL
                            OR sc.filter_tolerance IS NOT NULL),
     inserted AS (
         INSERT INTO column_filter (uuid, filter_data_type, filter_type, filter_value, filter_tolerance)
             SELECT filter_uuid, filter_data_type, filter_type, filter_value, filter_tolerance
             FROM created_filters
             RETURNING uuid)
UPDATE spreadsheet_column sc
SET column_entity_column_filter_id = cf.filter_uuid
FROM created_filters cf
WHERE sc.uuid = cf.column_uuid;