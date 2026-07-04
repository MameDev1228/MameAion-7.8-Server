ALTER TABLE `players` ADD `last_transfo` INT(10) NOT NULL DEFAULT '0' AFTER `minion_function`; 
UPDATE players SET exp=0, recoverexp =0, reposte_energy = 0, abyss_favor =0, berdin_star = 0; 