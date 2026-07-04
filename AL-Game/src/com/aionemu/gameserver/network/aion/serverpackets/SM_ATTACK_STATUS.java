package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_ATTACK_STATUS extends AionServerPacket
{
	private Creature attacked;
    private Creature attacker;
    private TYPE type;
    private int skillId;
    private int value;
    private int logId;
	
	public static enum TYPE {
		NATURAL_HP(3),
		USED_HP(4),
		REGULAR(5),
		ABSORBED_HP(6),
		DAMAGE(7),
		HP(7),
		PROTECTDMG(8),
		DELAYDAMAGE(10),
		FALL_DAMAGE(17),
		HEAL_MP(19),
		ABSORBED_MP(20),
		MP(21),
		NATURAL_MP(22),
		ATTACK(23),
		FP_RINGS(24),
		FP(25),
		NATURAL_FP(26),
		AUTO_HEAL_FP(27);
		
		private int value;
		
		private TYPE(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return this.value;
		}
	}
	
	public static enum LOG {
		SPELLATK(1),
        HEAL(3),
        MPHEAL(4),
        SKILLLATKDRAININSTANT(23),
        SPELLATKDRAININSTANT(24),
        POISON(25),
        BLEED(26),
        PROCATKINSTANT(93),
        DELAYEDSPELLATKINSTANT(97),
        SPELLATKDRAIN(130),
        FPHEAL(133),
        REGULARHEAL(170),
        REGULAR(189),
		CHANNELLING(200),
        ATTACK(204);
		
		private int value;
		
		private LOG(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return this.value;
		}
	}
	
	public SM_ATTACK_STATUS(Creature attacked, Creature attacker, TYPE type, int skillId, int value, LOG log) {
        this.attacked = attacked;
        this.attacker = attacker;
        this.type = type;
        this.skillId = skillId;
        this.value = value;
        this.logId = log.getValue();
    }
	
	public SM_ATTACK_STATUS(Creature attacked, Creature attacker, TYPE type, int skillId, int value) {
        this(attacked, attacker, type, skillId, value, LOG.REGULAR);
    }
	
    public SM_ATTACK_STATUS(Creature attacked, Creature attacker, int value) {
        this(attacked, attacker, TYPE.REGULAR, 0, value, LOG.REGULAR);
    }
	
	protected void writeImpl(AionConnection con) {
        if (this.type.getValue() == 5 || this.type.getValue() == 7 || this.type.getValue() == 10) {
			writeD(this.attacked.getObjectId().intValue());
			writeD(this.attacker.getObjectId().intValue());
		} else {
			writeD(this.attacker.getObjectId().intValue());
			writeD(0x00);
		} switch (this.type) {
            case ATTACK:
            case DAMAGE:
            case DELAYDAMAGE:
                writeD(-this.value);
            break;
            default:
            writeD(this.value);
        }
        writeC(this.type.getValue());
        if (this.type.getValue() == 19 || this.type.getValue() == 20 || this.type.getValue() == 21 || this.type.getValue() == 22) {
            writeC(this.attacked.getLifeStats().getMpPercentage());
        } else {
            writeC(this.attacked.getLifeStats().getHpPercentage());
        }
        writeH(this.skillId);
        writeH(0);
		if (this.skillId != 0) {
            writeH(this.logId);
        } else {
            writeH(LOG.ATTACK.getValue());
        }
    }
}