package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.skillengine.model.TransformType;

public class SM_TRANSFORM extends AionServerPacket
{
    private int state;
    private int modelId = 0;
    private int panelId = 0;
    private int itemId = 0;
    private int skillId = 0;
    private int transformId = 0;
	private Creature creature;
	private boolean applyEffect;
	
    public SM_TRANSFORM(Creature creature, boolean applyEffect) {
        this.creature = creature;
        this.state = creature.getState();
        this.modelId = creature.getTransformModel().getModelId();
        this.applyEffect = applyEffect;
    }
	
    public SM_TRANSFORM(Creature creature, int panelId, boolean applyEffect) {
        this.creature = creature;
        this.state = creature.getState();
        this.modelId = creature.getTransformModel().getModelId();
        this.panelId = panelId;
        this.applyEffect = applyEffect;
    }
	
    public SM_TRANSFORM(Creature creature, int panelId, boolean applyEffect, int itemId) {
        this.creature = creature;
        this.state = creature.getState();
        this.modelId = creature.getTransformModel().getModelId();
        this.panelId = panelId;
        this.applyEffect = applyEffect;
        this.itemId = itemId;
    }
	
    public SM_TRANSFORM(Creature creature, int panelId, boolean applyEffect, int itemId, int skillId) {
        this.creature = creature;
        this.state = creature.getState();
        this.modelId = creature.getTransformModel().getModelId();
        this.panelId = panelId;
        this.applyEffect = applyEffect;
        this.itemId = itemId;
        this.skillId = skillId;
    }
	
    @Override
    protected void writeImpl(AionConnection con) {
        boolean invisibleTransform = this.creature instanceof Player && ((Player) this.creature).isInvisibleTransform();
        writeD(this.creature.getObjectId());
		writeD(this.creature.getTransformModel().getModelId());
        writeH(this.state);
        writeF(0.25f);
        writeF(2.0f);
        writeC(!invisibleTransform && (this.applyEffect) && (this.creature.getTransformModel().getType() == TransformType.NONE) ? 1 : 0);
        writeD(this.creature.getTransformModel().getType().getId());
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(0);
		writeC(0);
        writeD(invisibleTransform ? 0 : this.creature.getTransformModel().getPanelId());
        writeD(invisibleTransform ? 0 : this.creature.getTransformModel().getItemId());
        writeC(0);
        writeH(invisibleTransform ? 0 : this.creature.getTransformModel().getSkillId());
        writeD(invisibleTransform ? 0 : this.creature.getTransformModel().getTransformId());
    }
}