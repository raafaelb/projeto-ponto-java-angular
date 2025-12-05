-- Habilita conexões de qualquer host
ALTER SYSTEM SET listen_addresses = '*';

-- Configura autenticação para rede Docker
CREATE OR REPLACE FUNCTION pg_reload_conf()
RETURNS BOOLEAN AS $$
BEGIN
  PERFORM pg_reload_conf();
  RETURN true;
END;
$$ LANGUAGE plpgsql;