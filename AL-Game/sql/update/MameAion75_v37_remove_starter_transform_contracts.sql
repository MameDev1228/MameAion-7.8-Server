-- MameAion75 v37
-- Optional cleanup: remove transformation contracts accidentally granted by v36 starter kits.
-- Run while GameServer is stopped if you want to replace them with real transformation scrolls.
DELETE FROM inventory WHERE item_id = 190095122;
