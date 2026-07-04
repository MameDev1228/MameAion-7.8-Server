package com.aionemu.gameserver.model.account;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.minion.MinionBuff;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatRateFunction;
import com.aionemu.gameserver.model.templates.minion.MinionAttr;
import com.aionemu.gameserver.model.templates.transform_book.CollectionAttr;
import com.aionemu.gameserver.model.templates.transform_book.TransformCollectionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TransformlCollectionBuff implements StatOwner {

    private TransformCollectionTemplate template;
    private List<IStatFunction> functions = new ArrayList<IStatFunction>();
    Logger log = LoggerFactory.getLogger(TransformlCollectionBuff.class);

    public void apply(Player player, TransformCollectionTemplate template) {
        if (template == null) {
            return;
        }
        CollectionAttr attribute = null;
        if (player.isMagicalTypeClass()) {
            attribute = template.getMagicalAttr();
        } else {
            attribute = template.getPhysicalAttr();
        }
        functions.add(new StatRateFunction(attribute.getName(), attribute.getValue(), true));
        player.setBonus(true);
        player.getGameStats().addEffect(this, functions);
    }

    public void end(Player player) {
        functions.clear();
        player.setBonus(false);
        player.getGameStats().endEffect(this);
    }
}
