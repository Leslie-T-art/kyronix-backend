UPDATE audit_events
SET new_values = '{"redacted":true,"reason":"legacy login audit payload contained sensitive token data"}'
WHERE event_type = 'AUTH_LOGIN_SUCCESS'
  AND new_values LIKE '%"accessToken"%';
