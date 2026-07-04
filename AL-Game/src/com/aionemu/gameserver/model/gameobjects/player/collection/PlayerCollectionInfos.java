package com.aionemu.gameserver.model.gameobjects.player.collection;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatAddFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.templates.collection.CollectionExpTemplate;
import com.aionemu.gameserver.model.templates.collection.CollectionType;

import java.util.ArrayList;
import java.util.List;

public class PlayerCollectionInfos implements StatOwner {

    private CollectionType type;
    private int level;
    private int exp;
    private List<IStatFunction> functions = new ArrayList<IStatFunction>();

    public PlayerCollectionInfos(CollectionType type, int level, int exp) {
        this.type = type;
        this.level = level;
        this.exp = exp;
    }

    public CollectionType getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    //stats function
    public void apply(Player player) {
        if(this.type != CollectionType.EVENT && level != 1) {
            CollectionExpTemplate template = DataManager.COLLECTION_EXP_DATA.getTemplate(this.level, this.type);
            if(template.getModifiers() != null) {
                for (StatFunction modifiers : template.getModifiers().getModifiers()) {
                    functions.add(new StatAddFunction(modifiers.getName(), modifiers.getValue(), modifiers.isBonus()));
                    player.getGameStats().addEffect(this, functions);
                }
            }
        }

    }

    public void end(Player player) {
        functions.clear();
        player.getGameStats().endEffect(this);
    }

    public void onLevelUp() {
        this.level++;
        this.exp = 0;
    }

    public void addexp() {
        this.exp++;
    }
}
