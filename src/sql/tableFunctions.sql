CREATE FUNCTION get_user_roles(user_id BIGINT) RETURNS TEXT[] AS $$
BEGIN
    RETURN ARRAY(
            SELECT role_name FROM (
                  SELECT 'VETERINARIAN' AS role_name FROM veterinarian WHERE veterinarian.id = user_id
                  UNION ALL
                  SELECT 'ADMIN' FROM admin WHERE admin.id = user_id
              ) roles
           );
END;
$$ LANGUAGE plpgsql;