-- MameAion 7.8.0 Phase19 GameServer auth check
-- Run on the LoginServer database host.

SELECT 'gameservers' AS section, id, mask, password FROM al78_server_ls.gameservers ORDER BY id;
SHOW CREATE TABLE al78_server_ls.gameservers;

-- Local single-GS example for gsid=10 / password=Mayu1020.
-- The older value 127.0.0.01 is now tolerated by NetworkUtils, but keep DB clean.
UPDATE al78_server_ls.gameservers
SET mask = '127.0.0.1', password = 'Mayu1020'
WHERE id = 10;

SELECT 'after_update' AS section, id, mask, password FROM al78_server_ls.gameservers ORDER BY id;
