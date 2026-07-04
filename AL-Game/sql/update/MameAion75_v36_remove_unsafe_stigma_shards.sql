-- MameAion75 v36
-- Remove unsafe stigma shards that were accidentally granted by earlier burning starter kits.
-- Run once while the GameServer is stopped.
DELETE FROM inventory WHERE item_id = 141000001;
