package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerTransformDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TRANSFORM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_PLAYER_APPEARANCE;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.TransformType;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransformEffect")
public abstract class TransformEffect extends EffectTemplate
{
    @XmlAttribute
    protected int model;
	
    @XmlAttribute
    protected TransformType type = TransformType.NONE;
	
    @XmlAttribute
    protected int panelid;
	
    @XmlAttribute
    protected int itemId;
	
    @XmlAttribute
    protected int skillId;
	
    @XmlAttribute
    protected AbnormalState state = AbnormalState.BUFF;
	
    private int modelId;
	
    @Override
    public void applyEffect(Effect effect) {
        effect.addToEffectedController();
        if (state != null) {
            effect.getEffected().getEffectController().setAbnormal(state.getId());
            effect.setAbnormal(state.getId());
        }
    }
	
    public void endEffect(Effect effect, AbnormalState state) {
        final Creature effected = effect.getEffected();
        int newModel = 0;
		int oldPanelid = 0;
        int oldEquipment = 0;
        int oldSkillId = 0;
        TransformType transformType = TransformType.PC;
        if (this.state != null) {
            effected.getEffectController().unsetAbnormal(this.state.getId());
        } if ((effected instanceof Player)) {
            for (Effect tmp : effected.getEffectController().getAbnormalEffects()) {
                for (EffectTemplate template : tmp.getEffectTemplates()) {
                    if ((template instanceof TransformEffect)) {
                        if (((TransformEffect) template).getTransformId() != this.model) {
                            newModel = ((TransformEffect) template).getTransformId();
                            transformType = ((TransformEffect) template).getTransformType();
                            oldPanelid = ((TransformEffect) template).getPanelId();
                            oldEquipment = ((TransformEffect) template).getItemId();
                            oldSkillId = ((TransformEffect) template).getSkillId();
                            break;
                        }
                    }
                }
            }
            effected.getTransformModel().setModelId(newModel);
            effected.getTransformModel().setPanelId(oldPanelid);
            effected.getTransformModel().setItemId(oldEquipment);
            effected.getTransformModel().setTransformType(transformType);
            effected.getTransformModel().setSkillId(oldSkillId);
            effected.getTransformModel().setTransformId(0);
			effected.getEffectController().removeEffect(4769); //Storm Surge.
			((Player) effected).setInvisibleTransform(false);
			DAOManager.getDAO(PlayerTransformDAO.class).deletePlTransfo(effected.getObjectId());
            PacketSendUtility.broadcastPacketAndReceive(effected, new SM_TRANSFORM(effected, oldPanelid, false));
            if (((effected instanceof Player)) &&
			    ((transformType == TransformType.PC) || (transformType == TransformType.NONE) ||
				 (transformType == TransformType.FORM1) || (transformType == TransformType.FORM2) ||
				 (transformType == TransformType.FORM3) || (transformType == TransformType.FORM4) ||
				 (transformType == TransformType.FORM5) || (transformType == TransformType.AVATAR))) {
                ((Player) effected).setTransformed(false);
            }
        } else if ((effected instanceof Summon)) {
            effected.getTransformModel().setModelId(0);
            PacketSendUtility.broadcastPacketAndReceive(effected, new SM_TRANSFORM(effected, 0, false));
        } else if ((effected instanceof Npc)) {
            effected.getTransformModel().setModelId(effected.getObjectTemplate().getTemplateId());
            PacketSendUtility.broadcastPacketAndReceive(effected, new SM_TRANSFORM(effected, 0, false));
        }
    }
	
    public void startEffect(Effect effect, AbnormalState effectId) {
		final Creature effected = effect.getEffected();
		boolean transparentTransform = isTransparentTransform(effect);
		int activePanelId = transparentTransform ? 0 : this.panelid;
		int activeItemId = transparentTransform ? 0 : this.itemId;
		int activeTransformId = 0;
		int activeSkillId = transparentTransform ? 0 : effect.getSkillId();
		TransformType activeTransformType = transparentTransform ? TransformType.PC : effect.getTransformType();
		if (isSpecialTransform(effect.getSkillId()) || transparentTransform) {
			modelId = 0;
		} else {
			modelId = model;
			try {
				activeTransformId = DataManager.TRANSFORM_BOOK_DATA.getTransformId(this.skillId);
			} catch (Exception e) {
				activeTransformId = 0;
			}
		}
        effected.getTransformModel().setModelId(modelId);
        effected.getTransformModel().setPanelId(activePanelId);
        effected.getTransformModel().setItemId(activeItemId);
        // Transparent transformation is stats-only. Do not expose transform skill id in visual packets;
        // the 7.x client can rebuild a visible model from skillId even if model/panel are zero.
        effected.getTransformModel().setSkillId(activeSkillId);
        effected.getTransformModel().setTransformType(activeTransformType);
        effected.getTransformModel().setTransformId(activeTransformId);
        PacketSendUtility.broadcastPacketAndReceive(effected, new SM_TRANSFORM(effected, true));
        if (effected instanceof Player) {
            Player player = (Player) effected;
            player.setTransformed(true);
            player.setTransformedModelId(modelId);
            player.setTransformedPanelId(activePanelId);
            player.setTransformedItemId(activeItemId);
            player.setTransformedSkillId(activeSkillId);
            player.setInvisibleTransform(transparentTransform);
            if (!transparentTransform) {
                DAOManager.getDAO(PlayerTransformDAO.class).storePlTransfo(effected.getObjectId(), activePanelId, activeItemId);
            } else {
                // Invisible transform scroll must apply stats only. Do not persist a visual model, and clear
                // stale transform appearance data from an older normal transformation if it exists.
                DAOManager.getDAO(PlayerTransformDAO.class).deletePlTransfo(effected.getObjectId());
            }
        }
        super.startEffect(effect);
    }
	
    public TransformType getTransformType() {
        return type;
    }
	
    public int getTransformId() {
        return model;
    }
	
    public int getPanelId() {
        return panelid;
    }
	
    public int getItemId() {
        return itemId;
    }
	
    public int getSkillId() {
        return skillId;
    }
	
    private boolean isTransparentTransform(Effect effect) {
        int skillId = effect.getSkillId();
        // Transparent potion skills 5607-5657 are explicit invisible-transform skills in 7.x data.
        if (skillId >= 5607 && skillId <= 5657) {
            return true;
        }
        // Transformation scroll UI applies the same TransformBook skill for normal and transparent scrolls.
        // TransformService marks the player before casting when the consumed item is 190099001/190099002.
        return effect.getEffected() instanceof Player && ((Player) effect.getEffected()).isInvisibleTransform();
    }

    private boolean isSpecialTransform(int skillId) {
        switch (skillId) {
			//Slayer Form I.
			case 3370:
			case 3371:
			//Slayer Of Darkness.
			case 4878:
			case 4879:
			//Mau Blessing.
			case 4875:
			case 4881:
			//(Advanced) Mau's Glory & Mau's Honor.
			case 5981:
			case 5982:
			case 5983:
			case 5984:
			case 5985:
			case 5986:
			case 5987:
			case 5988:
			//Transformation Panel.
			case 5053:
            case 5065:
            case 5066:
            case 5069:
            case 5070:
            case 5071:
            case 5072:
            case 5073:
            case 5074:
            case 5075:
            case 5076:
            case 5077:
            case 5078:
            case 5079:
            case 5080:
			case 5300:
			case 5301:
			case 5302:
			case 5303:
			case 5305:
			case 5306:
			//Transformation Panel 7.x
			case 5687:
			case 5688:
			case 5689:
			case 5690:
			case 5691:
			case 5692:
			case 5693:
			//Potion Transformation 7.x
			case 5607:
			case 5608:
			case 5609:
			case 5610:
			case 5611:
			case 5612:
			case 5613:
			case 5614:
			case 5615:
			case 5616:
			case 5617:
			case 5618:
			case 5619:
			case 5620:
			case 5621:
			case 5622:
			case 5623:
			case 5624:
			case 5625:
			case 5626:
			case 5627:
			case 5628:
			case 5629:
			case 5630:
			case 5631:
			case 5632:
			case 5633:
			case 5634:
			case 5635:
			case 5636:
			case 5637:
			case 5638:
			case 5639:
			case 5640:
			case 5641:
			case 5642:
			case 5643:
			case 5644:
			case 5645:
			case 5646:
			case 5647:
			case 5648:
			case 5649:
			case 5650:
			case 5651:
			case 5652:
			case 5653:
			case 5654:
			case 5655:
			case 5656:
			case 5657:
			case 5678:
			case 5866:
			//Genesis Arena 7.x
			case 5304:
			case 5916:
			case 5917:
                return true;
        }
        return false;
    }
}