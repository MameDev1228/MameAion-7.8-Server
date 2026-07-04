package com.aionemu.gameserver.world.handlers;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.world.WorldMap;
import com.aionemu.gameserver.world.zone.ZoneInstance;

public interface WorldHandler
{
    void onWorldCreate(WorldMap map);
    void onOpenDoor(Player player, int door);
    void onEnterZone(Player player, ZoneInstance zone);
    void onLeaveZone(Player player, ZoneInstance zone);
    void onPlayMovieEnd(Player player, int movieId);
    void onSkillUse(Player player, SkillTemplate template);
    boolean onDie(Player player, Creature lastAttacker);
    void onDie(Npc npc);
    void onDropRegistered(Npc npc);
    void onWorldDropRegistered(Npc npc);
    void onGather(Player player, Gatherable paramGatherable);
    void handleUseItemFinish(Player player, Npc npcId);
    void generateDrop();
    void checkPlayTime();
}