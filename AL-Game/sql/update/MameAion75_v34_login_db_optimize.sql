-- MameAion75 v34 login / enter-world DB optimizer
-- Run once against the GameServer DB: mysql aion75_game < MameAion75_v34_login_db_optimize.sql
-- Safe to rerun. It adds only missing indexes and removes duplicate transform rows before the unique key.

-- account_transform originally has no key. That makes transform load/unlock slower and allows duplicates.
CREATE TEMPORARY TABLE IF NOT EXISTS mame_account_transform_dedup AS
SELECT account_id, card_id, MAX(`count`) AS `count`
FROM account_transform
GROUP BY account_id, card_id;

TRUNCATE TABLE account_transform;

INSERT INTO account_transform(account_id, card_id, `count`)
SELECT account_id, card_id, `count`
FROM mame_account_transform_dedup;

DROP TEMPORARY TABLE IF EXISTS mame_account_transform_dedup;

DROP PROCEDURE IF EXISTS mame_add_index_if_missing;
DELIMITER $$
CREATE PROCEDURE mame_add_index_if_missing(IN p_table VARCHAR(64), IN p_index VARCHAR(64), IN p_sql TEXT)
BEGIN
  IF NOT EXISTS (
    SELECT 1
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = p_table
      AND index_name = p_index
    LIMIT 1
  ) THEN
    SET @mame_sql = p_sql;
    PREPARE mame_stmt FROM @mame_sql;
    EXECUTE mame_stmt;
    DEALLOCATE PREPARE mame_stmt;
  END IF;
END$$
DELIMITER ;

-- Transform list: account-scoped load and duplicate-safe INSERT ... ON DUPLICATE KEY.
CALL mame_add_index_if_missing('account_transform', 'ux_mame_account_transform_account_card',
  'ALTER TABLE account_transform ADD UNIQUE KEY ux_mame_account_transform_account_card (account_id, card_id)');

-- Inventory/equipment load on enter-world uses item_owner + item_location + is_equiped.
CALL mame_add_index_if_missing('inventory', 'idx_mame_inventory_owner_location_equiped',
  'ALTER TABLE inventory ADD INDEX idx_mame_inventory_owner_location_equiped (item_owner, item_location, is_equiped)');

-- Player transform/skill-skin/minion tables are loaded by player_id on login but may lack a useful index in old schemas.
CALL mame_add_index_if_missing('player_transform', 'idx_mame_player_transform_player_id',
  'ALTER TABLE player_transform ADD INDEX idx_mame_player_transform_player_id (player_id)');
CALL mame_add_index_if_missing('player_skill_skins', 'idx_mame_player_skill_skins_player_id',
  'ALTER TABLE player_skill_skins ADD INDEX idx_mame_player_skill_skins_player_id (player_id)');
CALL mame_add_index_if_missing('player_minions', 'idx_mame_player_minions_player_name',
  'ALTER TABLE player_minions ADD INDEX idx_mame_player_minions_player_name (player_id, name)');

-- Mail unread check / mailbox list quality of plan.
CALL mame_add_index_if_missing('mail', 'idx_mame_mail_recipient_unread',
  'ALTER TABLE mail ADD INDEX idx_mame_mail_recipient_unread (mail_recipient_id, unread)');

-- Name change history lookup.
CALL mame_add_index_if_missing('old_names', 'idx_mame_old_names_old_name',
  'ALTER TABLE old_names ADD INDEX idx_mame_old_names_old_name (old_name)');

DROP PROCEDURE IF EXISTS mame_add_index_if_missing;

ANALYZE TABLE account_transform, inventory, player_transform, player_skill_skins, player_minions, mail, old_names;
