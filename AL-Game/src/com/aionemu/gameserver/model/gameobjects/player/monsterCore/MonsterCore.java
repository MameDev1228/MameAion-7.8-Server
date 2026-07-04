package com.aionemu.gameserver.model.gameobjects.player.monsterCore;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatAddFunction;
import com.aionemu.gameserver.model.templates.monster_core.MonsterCoreTemplate;

import java.util.ArrayList;
import java.util.List;

public class MonsterCore implements StatOwner {

    private int id;
    private int collect;
    private int level;

    //stats function
    private MonsterCoreTemplate mt;
    private List<IStatFunction> functions = new ArrayList<IStatFunction>();

    public MonsterCore(int id) {
        this.id = id;
        mt = DataManager.MONSTER_CORE_DATA.getMonsterCoreId(this.id);
    }


    public int getId() {
        return id;
    }

    public int getCollect() {
        return collect;
    }

    public void setCollect(int step) {
        this.collect = step;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public MonsterCoreTemplate getMt() {
        return mt;
    }

    //stats function
    public void apply(Player player) {
        if(this.level != 0) {
            functions.add(new StatAddFunction(mt.getStatLists().get(this.level - 1).getStat(), mt.getStatLists().get(this.level - 1).getValue(), true));
            player.getGameStats().addEffect(this, functions);
        }
    }

    public void end(Player player) {
        functions.clear();
        player.getGameStats().endEffect(this);
    }
}
