package com.aionemu.gameserver.model.stats.container;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.actions.PlayerMode;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.stats.calc.AdditionStat;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.templates.item.ArmorType;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.WeaponType;
import com.aionemu.gameserver.model.templates.ride.RideInfo;
import com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.taskmanager.tasks.PacketBroadcaster.BroadcastMode;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class PlayerGameStats extends CreatureGameStats<Player>
{
	private int cachedSpeed;
	private int cachedAttackSpeed;

	public PlayerGameStats(Player owner) {
		super(owner);
	}

	@Override
	protected void onStatsChange() {
		super.onStatsChange();
		updateStatsAndSpeedVisually();
	}

	public void updateStatsAndSpeedVisually() {
		updateStatsVisually();
		checkSpeedStats();
	}

	public void updateStatsVisually() {
		owner.addPacketBroadcastMask(BroadcastMode.UPDATE_STATS);
	}

	/**
	 * 7.x item templates often contain the same modern combat value twice:
	 * once in <weapon_stats ... physical_power_boost=...> and once as an
	 * equip <modifier>. The equip listener already applies modifiers, so using
	 * weapon_stats as the base unconditionally double-counts Physical/Magical
	 * Attack and Defense in the profile and in damage formulas.
	 *
	 * Keep weapon_stats only as a fallback for old/incomplete templates that do
	 * not expose the value as a modifier.
	 */
	private int getWeaponStatsFallback(Item item, StatEnum stat, int weaponStatsValue) {
		if (item == null || item.getItemTemplate() == null || weaponStatsValue == 0) {
			return 0;
		}
		return hasTemplateModifier(item.getItemTemplate(), stat) ? 0 : weaponStatsValue;
	}

	private boolean hasTemplateModifier(ItemTemplate template, StatEnum stat) {
		if (template == null || template.getModifiers() == null) {
			return false;
		}
		for (StatFunction function : template.getModifiers()) {
			if (function != null && function.getName() == stat) {
				return true;
			}
		}
		return false;
	}

	private void checkSpeedStats() {
		int current = getMovementSpeed().getCurrent();
		int currentAttackSpeed = getAttackSpeed().getCurrent();
		if (current != cachedSpeed || currentAttackSpeed != cachedAttackSpeed) {
			owner.addPacketBroadcastMask(BroadcastMode.UPDATE_SPEED);
		}
		cachedSpeed = current;
		cachedAttackSpeed = currentAttackSpeed;
	}

	@Override
	public Stat2 getMaxHp() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.MAXHP, pst.getMaxHp());
	}

	@Override
	public Stat2 getMaxMp() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.MAXMP, pst.getMaxMp());
	}

	public Stat2 getMaxDp() {
		return getStat(StatEnum.MAXDP, 4000);
	}

	public Stat2 getFlyTime() {
		return getStat(StatEnum.FLY_TIME, 60); //...1 Min
	}

	public Stat2 getAllSpeed() {
		return getStat(StatEnum.ALLSPEED, 7500);
	}

    @Override
	public Stat2 getPhysicPowerBoost() {
		int base = 0;
		Item mainHandWeapon = owner.getEquipment().getMainHandWeapon();
		if (mainHandWeapon != null && mainHandWeapon.getItemTemplate().getWeaponStats() != null) {
			base += getWeaponStatsFallback(mainHandWeapon, StatEnum.PHYSICAL_POWER_BOOST, mainHandWeapon.getItemTemplate().getWeaponStats().getPhysicalPowerBoost());
		}
		return getStat(StatEnum.PHYSICAL_POWER_BOOST, base);
	}

	@Override
	public Stat2 getPhysicPowerBoostResist() {
		int base = 0;
		Item mainHandWeapon = owner.getEquipment().getMainHandWeapon();
		if (mainHandWeapon != null && mainHandWeapon.getItemTemplate().getWeaponStats() != null) {
			base += getWeaponStatsFallback(mainHandWeapon, StatEnum.PHYSICAL_POWER_BOOST_RESIST, mainHandWeapon.getItemTemplate().getWeaponStats().getPhysicalPowerBoostResist());
		}
		return getStat(StatEnum.PHYSICAL_POWER_BOOST_RESIST, base);
	}

	@Override
	public Stat2 getPhysicDamageBoost() {
		return getStat(StatEnum.PHYSICAL_DAMAGE_BOOST, 0);
	}

	@Override
	public Stat2 getPhysicDamageBoostResist() {
		return getStat(StatEnum.PHYSICAL_DAMAGE_BOOST_RESIST, 0);
	}

    @Override
	public Stat2 getMagicPowerBoost() {
		int base = 0;
		Item mainHandWeapon = owner.getEquipment().getMainHandWeapon();
		if (mainHandWeapon != null && mainHandWeapon.getItemTemplate().getWeaponStats() != null) {
			base += getWeaponStatsFallback(mainHandWeapon, StatEnum.MAGICAL_POWER_BOOST, mainHandWeapon.getItemTemplate().getWeaponStats().getMagicalPowerBoost());
		}
		return getStat(StatEnum.MAGICAL_POWER_BOOST, base);
	}

	@Override
	public Stat2 getMagicPowerBoostResist() {
		int base = 0;
		Item mainHandWeapon = owner.getEquipment().getMainHandWeapon();
		if (mainHandWeapon != null && mainHandWeapon.getItemTemplate().getWeaponStats() != null) {
			base += getWeaponStatsFallback(mainHandWeapon, StatEnum.MAGICAL_POWER_BOOST_RESIST, mainHandWeapon.getItemTemplate().getWeaponStats().getMagicalPowerBoostResist());
		}
		return getStat(StatEnum.MAGICAL_POWER_BOOST_RESIST, base);
	}

	@Override
	public Stat2 getMagicDamageBoost() {
		return getStat(StatEnum.MAGICAL_DAMAGE_BOOST, 0);
	}

	@Override
	public Stat2 getMagicDamageBoostResist() {
		return getStat(StatEnum.MAGICAL_DAMAGE_BOOST_RESIST, 0);
	}

	@Override
	public Stat2 getHealBoost() {
		return getStat(StatEnum.HEAL_BOOST, 107);
	}

	@Override
	public Stat2 getPvePowerBoost() {
		return getStat(StatEnum.PVE_POWER_BOOST, 0);
	}

	@Override
	public Stat2 getPvePowerBoostResist() {
		return getStat(StatEnum.PVE_POWER_BOOST_RESIST, 0);
	}

	@Override
	public Stat2 getPvpPowerBoost() {
		return getStat(StatEnum.PVP_POWER_BOOST, 0);
	}

	@Override
	public Stat2 getPvpPowerBoostResist() {
		return getStat(StatEnum.PVP_POWER_BOOST_RESIST, 0);
	}

	@Override
	public Stat2 getAttackSpeed() {
		int base = 1500;
		Equipment equipment = owner.getEquipment();
		Item mainHandWeapon = equipment.getMainHandWeapon();
		if (mainHandWeapon != null) {
			base = mainHandWeapon.getItemTemplate().getWeaponStats().getAttackSpeed();
			Item offWeapon = owner.getEquipment().getOffHandWeapon();
			if (offWeapon != null) {
				base += offWeapon.getItemTemplate().getWeaponStats().getAttackSpeed() / 4;
			}
		}
		Stat2 aSpeed = getStat(StatEnum.ATTACK_SPEED, base);
		return aSpeed;
	}

	@Override
    public Stat2 getBCastingTime() {
        int base = 0;
        int casterClass = owner.getPlayerClass().getClassId();
        if (casterClass == 7 || //Sorcerer.
            casterClass == 8 || //Spirit-Master.
            casterClass == 16) { //Songweaver.
            base = 800;
        }
        return getStat(StatEnum.BOOST_CASTING_TIME, base);
    }

	@Override
	public Stat2 getConcentration() {
		return getStat(StatEnum.CONCENTRATION, 0);
	}

	@Override
	public Stat2 getRootResistance() {
		return getStat(StatEnum.ROOT_RESISTANCE, 0);
	}

	@Override
	public Stat2 getSnareResistance() {
		return getStat(StatEnum.SNARE_RESISTANCE, 0);
	}

	@Override
	public Stat2 getBindResistance() {
		return getStat(StatEnum.BIND_RESISTANCE, 0);
	}

	@Override
	public Stat2 getFearResistance() {
		return getStat(StatEnum.FEAR_RESISTANCE, 0);
	}

	@Override
	public Stat2 getSleepResistance() {
		return getStat(StatEnum.SLEEP_RESISTANCE, 0);
	}

	@Override
	public Stat2 getPDef() {
		return getStat(StatEnum.PHYSICAL_POWER_BOOST_RESIST, 0);
	}

	@Override
	public Stat2 getMResist() {
		return getStat(StatEnum.MAGICAL_RESIST, 0);
	}

	@Override
	public Stat2 getMBResist() {
		return getStat(StatEnum.MAGIC_SKILL_BOOST_RESIST, 0);
	}

	@Override
	public Stat2 getMovementSpeed() {
		Stat2 movementSpeed;
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		if (owner.isInPlayerMode(PlayerMode.RIDE)) {
			RideInfo ride = owner.ride;
			int runSpeed = (int) pst.getRunSpeed() * 1000;
			if (owner.isInState(CreatureState.FLYING)) {
				movementSpeed = new AdditionStat(StatEnum.FLY_SPEED, runSpeed, owner);
				movementSpeed.addToBonus((int) (ride.getFlySpeed() * 1000) - runSpeed);
			} else {
				float speed = owner.isInSprintMode() ? ride.getSprintSpeed() : ride.getMoveSpeed();
				movementSpeed = new AdditionStat(StatEnum.SPEED, runSpeed, owner);
				movementSpeed.addToBonus((int) (speed * 1000) - runSpeed);
			}
		} else if (owner.isInFlyingState()) {
			movementSpeed = getStat(StatEnum.FLY_SPEED, Math.round(pst.getFlySpeed() * 1000));
		} else if (owner.isInState(CreatureState.FLIGHT_TELEPORT) && !owner.isInState(CreatureState.RESTING)) {
			movementSpeed = getStat(StatEnum.SPEED, 12000);
		} else if (owner.isInState(CreatureState.WALKING)) {
			movementSpeed = getStat(StatEnum.SPEED, Math.round(pst.getWalkSpeed() * 1000));
		} else if (getAllSpeed().getBonus() != 0) {
			movementSpeed = getStat(StatEnum.SPEED, getAllSpeed().getCurrent());
		} else {
			movementSpeed = getStat(StatEnum.SPEED, Math.round(pst.getRunSpeed() * 1000));
		}
		return movementSpeed;
	}

	@Override
	public Stat2 getAttackRange() {
		int base = 1500;
		Equipment equipment = owner.getEquipment();
		Item mainHandWeapon = equipment.getMainHandWeapon();
		Item offHandWeapon = equipment.getOffHandWeapon();
		if (mainHandWeapon != null) {
			base = mainHandWeapon.getItemTemplate().getWeaponStats().getAttackRange();
			if (!mainHandWeapon.getItemTemplate().isTwoHandWeapon() && mainHandWeapon != null && offHandWeapon != null && offHandWeapon.getItemTemplate().getArmorType() != ArmorType.SHIELD) {
				if (mainHandWeapon.getItemTemplate().getWeaponStats().getAttackRange() != offHandWeapon.getItemTemplate().getWeaponStats().getAttackRange()) {
					if (mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.DAGGER_1H && offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.DAGGER_1H) {
						base = 1500;
					} else if (mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.DAGGER_1H && offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.SWORD_1H) {
						base = 1500;
					} else if (mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.SWORD_1H && offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.DAGGER_1H) {
						base = 1500;
					} else if (mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.DAGGER_1H && offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.MACE_1H) {
						base = 1500;
					} else if (mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.MACE_1H && offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.DAGGER_1H) {
						base = 1500;
					} else if (mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.MACE_1H && offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.SWORD_1H) {
						base = 1500;
					} else if (mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.MACE_1H && offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.MACE_1H) {
						base = 1500;
					} else if (mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.SWORD_1H && offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.MACE_1H) {
						base = 1500;
					} else {
						if (mainHandWeapon != null && offHandWeapon != null && offHandWeapon.getItemTemplate().getArmorType() != ArmorType.SHIELD) {
							base = mainHandWeapon.getItemTemplate().getWeaponStats().getAttackRange();
							//log.info("[Error] PlayerGameStats] mainHandWeapon ["+mainHandWeapon.getItemTemplate().getItemType()+"] offHandWeapon ["+offHandWeapon.getItemTemplate().getItemType()+"]");
						}
					}
				}
			}
		}
		return getStat(StatEnum.ATTACK_RANGE, base);
	}

	@Override
	public Stat2 getMDef() {
		return getStat(StatEnum.MAGICAL_POWER_BOOST_RESIST, 0);
	}

	@Override
	public Stat2 getPower() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.POWER, pst.getPower());
	}

	@Override
	public Stat2 getHealth() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.HEALTH, pst.getHealth());
	}

	@Override
	public Stat2 getAccuracy() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.ACCURACY, pst.getAccuracy());
	}

	@Override
	public Stat2 getAgility() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.AGILITY, pst.getAgility());
	}

	@Override
	public Stat2 getKnowledge() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.KNOWLEDGE, pst.getKnowledge());
	}

	@Override
	public Stat2 getWill() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.WILL, pst.getWill());
	}

	@Override
	public Stat2 getEvasion() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.EVASION, pst.getEvasion());
	}

	@Override
	public Stat2 getParry() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		int base = pst.getParry();
		Item mainHandWeapon = owner.getEquipment().getMainHandWeapon();
		if (mainHandWeapon != null) {
			base += mainHandWeapon.getItemTemplate().getWeaponStats().getParry();
		}
		return getStat(StatEnum.PARRY, base);
	}

	@Override
	public Stat2 getBlock() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.BLOCK, pst.getBlock());
	}

	@Override
	public Stat2 getMainHandPAttack() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		int base = pst.getMainHandAttack();
		Equipment equipment = owner.getEquipment();
		Item mainHandWeapon = equipment.getMainHandWeapon();
		if (mainHandWeapon != null) {
			if (mainHandWeapon.getItemTemplate().getAttackType().isMagical()) {
				return new AdditionStat(StatEnum.MAIN_HAND_POWER, 0, owner);
			}
			base = mainHandWeapon.getItemTemplate().getWeaponStats().getMeanDamage();
		}
		Stat2 stat = getStat(StatEnum.PHYSICAL_ATTACK, base);
		return getStat(StatEnum.MAIN_HAND_POWER, stat);
	}

	public Stat2 getOffHandPAttack() {
		Equipment equipment = owner.getEquipment();
		Item offHandWeapon = equipment.getOffHandWeapon();
		if (offHandWeapon != null && offHandWeapon.getItemTemplate().isWeapon()) {
			int base = offHandWeapon.getItemTemplate().getWeaponStats().getMeanDamage();
			base *= 0.98;
			Stat2 stat = getStat(StatEnum.PHYSICAL_ATTACK, base);
			return getStat(StatEnum.OFF_HAND_POWER, stat);
		}
		return new AdditionStat(StatEnum.OFF_HAND_POWER, 0, owner);
	}

	@Override
    public Stat2 getPCritical() {
        PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), (owner.getLevel()));
        int base = pst.getMainHandCritRate();
        Equipment equipment = owner.getEquipment();
        Item mainHandWeapon = equipment.getMainHandWeapon();
        if (mainHandWeapon != null) {
            base = mainHandWeapon.getItemTemplate().getWeaponStats().getPhysicalCritical();
        } else if (mainHandWeapon != null && mainHandWeapon.hasFusionedItem()) {
            base += mainHandWeapon.getItemTemplate().getWeaponStats().getPhysicalCritical() + mainHandWeapon.getFusionedItemTemplate().getWeaponStats().getPhysicalCritical();
        }
        return getStat(StatEnum.PHYSICAL_CRITICAL, base);
    }

	public Stat2 getOffHandPCritical() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), (owner.getLevel()));
		int base = pst.getMainHandCritRate();
        Equipment equipment = owner.getEquipment();
        Item offHandWeapon = equipment.getOffHandWeapon();
        if (offHandWeapon != null && offHandWeapon.getItemTemplate().isWeapon()) {
            base = offHandWeapon.getItemTemplate().getWeaponStats().getPhysicalCritical();
            return getStat(StatEnum.PHYSICAL_CRITICAL, base);
        }
        return new AdditionStat(StatEnum.OFF_HAND_CRITICAL, 0, owner);
    }

	@Override
    public Stat2 getPAccuracy() {
        PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
        int base = pst.getMainHandAccuracy();
        Equipment equipment = owner.getEquipment();
        Item mainHandWeapon = equipment.getMainHandWeapon();
        if (mainHandWeapon != null) {
            base += mainHandWeapon.getItemTemplate().getWeaponStats().getPhysicalAccuracy();
        }
        return getStat(StatEnum.PHYSICAL_ACCURACY, base);
    }

	public Stat2 getOffHandPAccuracy() {
        Equipment equipment = owner.getEquipment();
        Item offHandWeapon = equipment.getOffHandWeapon();
        if (offHandWeapon != null && offHandWeapon.getItemTemplate().isWeapon()) {
            PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
            int base = pst.getMainHandAccuracy();
            base += offHandWeapon.getItemTemplate().getWeaponStats().getPhysicalAccuracy();
            return getStat(StatEnum.PHYSICAL_ACCURACY, base);
        }
        return new AdditionStat(StatEnum.OFF_HAND_ACCURACY, 0, owner);
    }

	@Override
    public Stat2 getMAttack() {
        PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		int base = pst.getMainHandAttack();
        Equipment equipment = owner.getEquipment();
        Item mainHandWeapon = equipment.getMainHandWeapon();
        if (mainHandWeapon != null) {
            if (!mainHandWeapon.getItemTemplate().getAttackType().isMagical()) {
                return new AdditionStat(StatEnum.MAGICAL_ATTACK, 0, owner);
            }
            base = mainHandWeapon.getItemTemplate().getWeaponStats().getMeanDamage();
        }
        return getStat(StatEnum.MAGICAL_ATTACK, base);
    }

	@Override
    public Stat2 getMainHandMAttack() {
        PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		int base = pst.getMainHandAttack();
        Equipment equipment = owner.getEquipment();
        Item mainHandWeapon = equipment.getMainHandWeapon();
        if (mainHandWeapon != null) {
            if (!mainHandWeapon.getItemTemplate().getAttackType().isMagical()) {
                return new AdditionStat(StatEnum.MAIN_HAND_MAGICAL_POWER, 0, owner);
            }
            base = mainHandWeapon.getItemTemplate().getWeaponStats().getMeanDamage();
        }
        Stat2 stat = getStat(StatEnum.MAGICAL_ATTACK, base);
        return getStat(StatEnum.MAIN_HAND_MAGICAL_POWER, stat);
    }

	@Override
    public Stat2 getOffHandMAttack() {
        int base = 0;
        Equipment equipment = owner.getEquipment();
        Item offHandWeapon = equipment.getOffHandWeapon();
        if (offHandWeapon != null && offHandWeapon.getItemTemplate().isWeapon()) {
            base = offHandWeapon.getItemTemplate().getWeaponStats().getMeanDamage();
            base *= 0.82;
            Stat2 stat = getStat(StatEnum.MAGICAL_ATTACK, base);
            return getStat(StatEnum.OFF_HAND_MAGICAL_POWER, stat);
        }
        return new AdditionStat(StatEnum.OFF_HAND_MAGICAL_POWER, 0, owner);
    }

	@Override
    public Stat2 getMBoost() {
        int base = 0;
        Item mainHandWeapon = owner.getEquipment().getMainHandWeapon();
        if (mainHandWeapon != null) {
            base += mainHandWeapon.getItemTemplate().getWeaponStats().getBoostMagicalSkill();
        }
        return getStat(StatEnum.BOOST_MAGICAL_SKILL, base);
    }

	@Override
	public Stat2 getMAccuracy() {
        PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(this.owner.getPlayerClass(), this.owner.getLevel());
        int base = pst.getMagicAccuracy();
        Item mainHandWeapon = this.owner.getEquipment().getMainHandWeapon();
        if (mainHandWeapon != null) {
            base += mainHandWeapon.getItemTemplate().getWeaponStats().getMagicalAccuracy();
        }
        Stat2 stat = getStat(StatEnum.MAGICAL_ACCURACY, base);
        int HAGI = this.owner.getGameStats().getStat(StatEnum.AGILITY, 0).getCurrent();
        int MAccuracyCalculation = Math.round(2286 * HAGI / (376.0F + HAGI));
        stat.addToBonus(MAccuracyCalculation);
        return stat;
    }

    public Stat2 getOffHandMAccuracy() {
        Equipment equipment = this.owner.getEquipment();
        Item offHandWeapon = equipment.getOffHandWeapon();
        if ((offHandWeapon != null) && (offHandWeapon.getItemTemplate().isWeapon()) && (!offHandWeapon.getItemTemplate().isTwoHandWeapon())) {
            PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(this.owner.getPlayerClass(), this.owner.getLevel());
            int base = pst.getMagicAccuracy();
            base += offHandWeapon.getItemTemplate().getWeaponStats().getMagicalAccuracy();
            return getStat(StatEnum.MAGICAL_ACCURACY, base);
        }
        return new AdditionStat(StatEnum.OFF_HAND_MAGICAL_ACCURACY, 0, this.owner);
    }

	@Override
	public Stat2 getMCritical() {
		return getStat(StatEnum.MAGICAL_CRITICAL, 50);
	}

	@Override
	public Stat2 getHpRegenRate() {
		int base = owner.getLevel() + 3;
		if (owner.isInState(CreatureState.RESTING)) {
			base *= 8;
		}
		base *= getHealth().getCurrent() / 100f;
		return getStat(StatEnum.REGEN_HP, base);
	}

	@Override
	public Stat2 getMpRegenRate() {
		int base = owner.getLevel() + 8;
		if (owner.isInState(CreatureState.RESTING)) {
			base *= 8;
		}
		base *= getWill().getCurrent() / 100f;
		return getStat(StatEnum.REGEN_MP, base);
	}

	@Override
	public void updateStatInfo() {
		PacketSendUtility.sendPacket(owner, new SM_STATS_INFO(owner));
	}

	@Override
	public void updateSpeedInfo() {
		PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.START_EMOTE2, 0, 0), true);
	}
}