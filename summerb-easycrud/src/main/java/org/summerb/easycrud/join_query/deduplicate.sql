









WITH filtered_rows AS (SELECT u.*, -- all neccessary columns selections
                              ROW_NUMBER() OVER (PARTITION BY u.id -- primary (selected (!)) query id
      ORDER BY r.id -- from joined tables, if any + add primary keys of joined tables
      ) AS __rn
                       FROM Users u -- FROMs and JOINs
                                INNER JOIN UserRoles r ON u.id = r.user_id
                       WHERE r.name = 'Admin' -- WHERE conditions
)
SELECT *
FROM filtered_users
WHERE __rn = 1
ORDER BY id -- Or whatever column you want to paginate by
    LIMIT 10
OFFSET 0; -- Your pagination
