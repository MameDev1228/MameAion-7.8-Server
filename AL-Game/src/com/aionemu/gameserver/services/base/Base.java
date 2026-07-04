/*
 * This file is part of Encom. **ENCOM FUCK OTHER SVN**
 *
 *  Encom is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Encom is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with Encom.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services.base;

import com.aionemu.commons.callbacks.EnhancedObject;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.base.BaseLocation;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.base.BaseNpc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.npc.NpcTemplateType;
import com.aionemu.gameserver.model.templates.spawns.SpawnGroup2;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.basespawns.BaseSpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.BaseService;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.spawnengine.SpawnHandlerType;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Rinzler
 */

public class Base<BL extends BaseLocation>
{
    private Npc boss, flag;
    private boolean started;
    private final BL baseLocation;
    private Future<?> startAssault, stopAssault;
    private List<Race> list = new ArrayList<Race>();
    private List<Npc> spawned = new ArrayList<Npc>();
    private List<Npc> attackers = new ArrayList<Npc>();
    private final AtomicBoolean finished = new AtomicBoolean();
    private final BaseBossDeathListener baseBossDeathListener = new BaseBossDeathListener(this);
	
    public Base(BL baseLocation) {
        list.add(Race.ASMODIANS);
        list.add(Race.ELYOS);
        list.add(Race.NPC);
        this.baseLocation = baseLocation;
    }
	
    public final void start() {
        boolean doubleStart = false;
        synchronized (this) {
            if (started) {
                doubleStart = true;
            } else {
                started = true;
            }
        } if (doubleStart) {
            return;
        }
        spawn();
    }
	
    public final void stop() {
        if (finished.compareAndSet(false, true)) {
            if (getBoss() != null) {
                rmvBaseBossListener();
            }
            despawn(getId());
        }
    }
	
    private List<SpawnGroup2> getBaseSpawns() {
        List<SpawnGroup2> spawns = DataManager.SPAWNS_DATA2.getBaseSpawnsByLocId(getId());
        if (spawns == null) {
        }
        return spawns;
    }
	
    protected void spawn() {
        for (SpawnGroup2 group : getBaseSpawns()) {
            for (SpawnTemplate spawn : group.getSpawnTemplates()) {
                final BaseSpawnTemplate template = (BaseSpawnTemplate) spawn;
                if (template.getBaseRace().equals(getBaseLocation().getRace())) {
                    if (template.getHandlerType() == null) {
                        Npc npc = (Npc) SpawnEngine.spawnObject(template, 1);
                        NpcTemplate npcTemplate = npc.getObjectTemplate();
                        if (npcTemplate.getNpcTemplateType().equals(NpcTemplateType.FLAG)) {
                            setFlag(npc);
                        }
                        getSpawned().add(npc);
                    }
                }
            }
        }
        delayedAssault();
        delayedSpawn(getRace());
    }
	
    private void delayedAssault() {
        startAssault = ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                chooseAttackersRace();
            }
        }, Rnd.get(120, 180) * 60000);
    }
	
    private void delayedSpawn(final Race race) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (getRace().equals(race) && getBoss() == null) {
                    spawnBoss();
                }
            }
        }, Rnd.get(5, 10) * 60000);
    }
	
    protected void spawnBoss() {
        for (SpawnGroup2 group : getBaseSpawns()) {
            for (SpawnTemplate spawn : group.getSpawnTemplates()) {
                final BaseSpawnTemplate template = (BaseSpawnTemplate) spawn;
                if (template.getBaseRace().equals(getBaseLocation().getRace())) {
                    if (template.getHandlerType() != null && template.getHandlerType().equals(SpawnHandlerType.CHIEF)) {
                        Npc npc = (Npc) SpawnEngine.spawnObject(template, 1);
                        setBoss(npc);
                        addBaseBossListeners();
                        getSpawned().add(npc);
                    }
                }
            }
        }
    }
	
    protected void chooseAttackersRace() {
        AtomicBoolean next = new AtomicBoolean(Math.random() < 0.5);
        for (Race race : list) {
            if (!race.equals(getRace())) {
                if (next.compareAndSet(true, false)) {
                    continue;
                }
                spawnAttackers(race);
            }
        }
    }
	
    public void spawnAttackers(Race race) {
        if (getFlag() == null) {
        } else if (!getFlag().getPosition().getMapRegion().isMapRegionActive()) {
            if (Math.random() < 0.5) {
                BaseService.getInstance().capture(getId(), race);
            } else {
                delayedAssault();
            }
            return;
        } if (!isAttacked()) {
            despawnAttackers();
            for (SpawnGroup2 group : getBaseSpawns()) {
                for (SpawnTemplate spawn : group.getSpawnTemplates()) {
                    final BaseSpawnTemplate template = (BaseSpawnTemplate) spawn;
                    if (template.getBaseRace().equals(race)) {
                        if (template.getHandlerType() != null && template.getHandlerType().equals(SpawnHandlerType.SLAYER)) {
                            Npc npc = (Npc) SpawnEngine.spawnObject(template, 1);
                            getAttackers().add(npc);
                        }
                    }
                }
            }
            if (getAttackers().isEmpty()) {
            } else {
                stopAssault = ThreadPoolManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        despawnAttackers();
                        delayedAssault();
                    }
                }, 5 * 60000);
            }
        }
    }
	
    public boolean isAttacked() {
        for (Npc attacker : getAttackers()) {
            if (!attacker.getLifeStats().isAlreadyDead()) {
                return true;
            }
        }
        return false;
    }
	
    protected void despawn(int baseLocationId) {
        setFlag(null);
        for (Npc npc: getSpawned()) {
            npc.getController().cancelTask(TaskId.RESPAWN);
            npc.getController().onDelete();
        }
        getSpawned().clear();
        if (startAssault != null) {
            startAssault.cancel(true);
        } if (stopAssault != null) {
            stopAssault.cancel(true);
            despawnAttackers();
        }
    }
	
    protected void despawnAttackers() {
        for (Npc attacker : getAttackers()) {
            attacker.getController().onDelete();
        }
        getAttackers().clear();
    }
	
    protected void addBaseBossListeners() {
        AbstractAI ai = (AbstractAI) getBoss().getAi2();
        EnhancedObject eo = (EnhancedObject) ai;
        eo.addCallback(getBaseBossDeathListener());
    }
	
    protected void rmvBaseBossListener() {
        AbstractAI ai = (AbstractAI) getBoss().getAi2();
        EnhancedObject eo = (EnhancedObject) ai;
        eo.removeCallback(getBaseBossDeathListener());
    }
	
    public Npc getFlag() {
        return flag;
    }
	
    public void setFlag(Npc flag) {
        this.flag = flag;
    }
	
    public Npc getBoss() {
        return boss;
    }
	
    public void setBoss(Npc boss) {
        this.boss = boss;
    }
	
    public BaseBossDeathListener getBaseBossDeathListener() {
        return baseBossDeathListener;
    }
	
    public boolean isFinished() {
        return finished.get();
    }
	
    public BL getBaseLocation() {
        return baseLocation;
    }
	
    public int getId() {
        return baseLocation.getId();
    }
	
    public Race getRace() {
        return baseLocation.getRace();
    }
	
    public void setRace(Race race) {
        baseLocation.setRace(race);
    }
	
    public List<Npc> getAttackers() {
        return attackers;
    }
	
    public List<Npc> getSpawned() {
        return spawned;
    }
}