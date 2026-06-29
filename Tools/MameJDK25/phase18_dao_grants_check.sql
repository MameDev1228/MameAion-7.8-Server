-- MameAion 7.8.0 Phase18 DAO grant/schema helper
-- Run on DB server as root/admin. Adjust host if needed.

GRANT ALL PRIVILEGES ON al78_server_gs.* TO 'mameaion'@'133.200.31.32';
GRANT ALL PRIVILEGES ON al78_server_ls.* TO 'mameaion'@'133.200.31.32';
GRANT SELECT ON al78_server_ls.account_data TO 'mameaion'@'133.200.31.32';
FLUSH PRIVILEGES;

ALTER TABLE al78_server_gs.houses
  MODIFY acquire_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

SHOW GRANTS FOR 'mameaion'@'133.200.31.32';
SHOW CREATE TABLE al78_server_gs.houses;
SHOW CREATE TABLE al78_server_gs.house_bids;
