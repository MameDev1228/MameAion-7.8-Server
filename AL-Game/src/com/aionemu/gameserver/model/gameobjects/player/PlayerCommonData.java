/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.gameobjects.player;

import com.aionemu.gameserver.GameServer;
import com.aionemu.gameserver.configs.main.AdvCustomConfig;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.Gender;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.team.legion.LegionJoinRequestState;
import com.aionemu.gameserver.model.templates.BoundRadius;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.XPLossEnum;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PlayerCommonData extends VisibleObjectTemplate {
    static Logger log = LoggerFactory.getLogger(PlayerCommonData.class);
    private final int playerObjId;
    private Race race;
    private String name;
    private PlayerClass playerClass;
    private int level = 0;
    private long exp = 0;
    private long expRecoverable = 0;
    private Gender gender;
    private Timestamp lastOnline = new Timestamp(Calendar.getInstance().getTime().getTime() - 20);
    private Timestamp lastStamp = new Timestamp(Calendar.getInstance().getTime().getTime() - 20);
    private boolean online;
    private String note;
    private WorldPosition position;
    private int questExpands = 0;
    private int npcExpands = AdvCustomConfig.CUBE_SIZE;
    private int warehouseSize = 0;
    private int advencedStigmaSlotSize = 0;
    private int titleId = -1;
    private int bonusTitleId = -1;
    private int dp = 0;
    private int mailboxLetters;
    private int soulSickness = 0;
    private boolean noExp = false;
    private long reposteCurrent;
    private long reposteMax = 23000000;
    private int mentorFlagTime;
    private int worldOwnerId;
    private BoundRadius boundRadius;
    private long lastTransferTime;
    private int joinRequestLegionId = 0;
    private LegionJoinRequestState joinRequestState = LegionJoinRequestState.NONE;
    private int lunaConsumePoint;
    private int muni_keys;
    private int consumeCount = 0;
    private int wardrobeSlot;
    private PlayerUpgradeArcade upgradeArcade;
    //Berdin's Star.
    private long berdinStar;
    private long berdinStarMax = 1125000000;
    private boolean BerdinStarBoost = false;
    //Abyss Favor.
    private long abyssFavor;
    private long abyssFavorMax = 1000000;
    private boolean AbyssFavorBoost = false;
    //Crucible Spire.
    private int floor;
    //Minions.
    private int minionEnergy;
    private int lastMinion;
    private Timestamp minionFunctionTime;
    //Dimensional Hourglass.
    private int worldPlayTime;
    //Shugo Sweep.
    private int freeDice = 0;
    private int goldenDice = 0;
    private int lastDiceSlot = 0;
    private int lastDiceReward = 0;
    private Timestamp diceTime;
    private int diceTemplateId = 1;
    private int resetBoard = 0;

    public PlayerCommonData(int objId) {
        this.playerObjId = objId;
    }

    public int getPlayerObjId() {
        return playerObjId;
    }

    public long getExp() {
        return this.exp;
    }

    public int getQuestExpands() {
        return this.questExpands;
    }

    public void setQuestExpands(int questExpands) {
        this.questExpands = questExpands;
    }

    public void setNpcExpands(int npcExpands) {
        this.npcExpands = npcExpands;
    }

    public int getNpcExpands() {
        return npcExpands;
    }

    /**
     * @return the advencedStigmaSlotSize
     */
    public int getAdvencedStigmaSlotSize() {
        return advencedStigmaSlotSize;
    }

    /**
     * @param advencedStigmaSlotSize the advencedStigmaSlotSize to set
     */
    public void setAdvencedStigmaSlotSize(int advencedStigmaSlotSize) {
        this.advencedStigmaSlotSize = advencedStigmaSlotSize;
    }

    public long getExpShown() {
        return this.exp;
    }

    public long getExpNeed() {
        return DataManager.PLAYER_EXPERIENCE_TABLE.getExpTemplate(this.level + 1).getExp();
    }

    /**
     * calculate the lost experience must be called before setexp
     *
     * @author Jangan
     */
    public void calculateExpLoss() {
        long expLost = 0;
        int recoverable = 0;
        if (this.getExpShown() <= 0) {
            expLost = 0;
        } else {
            expLost = (this.getExpShown() * 10) /100;
            recoverable = (int) Math.round(expLost / 2);
            long allExpLost = recoverable + this.expRecoverable;
            if (this.getExpShown() - expLost <= 0) {
                this.exp = 0;
            } else {
                this.exp = this.exp - expLost;
            } if (this.expRecoverable > getExpNeed()) {
                this.expRecoverable = getExpNeed();
            } else {
                if(this.expRecoverable + allExpLost >= getExpNeed()) {
                    this.expRecoverable = getExpNeed();
                } else {
                    this.expRecoverable = allExpLost;
                }
            } if (this.getPlayer() != null) {
                PacketSendUtility.sendPacket(getPlayer(), new SM_STATUPDATE_EXP(getExpShown(), getExpRecoverable(), getExpNeed(), this.getCurrentReposteEnergy(), this.getMaxReposteEnergy(), this.getBerdinStar()));
            }
        }
    }

    public void setRecoverableExp(long expRecoverable) {
        this.expRecoverable = expRecoverable;
    }

    public void resetRecoverableExp() {
        long el = this.expRecoverable;
        this.expRecoverable = 0;
        this.setExp(this.exp + el);
    }

    public long getExpRecoverable() {
        return this.expRecoverable;
    }

    /**
     * @param value
     */
    public void addExp(long value, int npcNameId) {
        this.addExp(value, null, npcNameId, "");
    }

    public void addExp(long value, RewardType rewardType) {
        this.addExp(value, rewardType, 0, "");
    }

    public void addExp(long value, RewardType rewardType, int npcNameId) {
        this.addExp(value, rewardType, npcNameId, "");
    }

    public void addExp(long value, RewardType rewardType, String name) {
        this.addExp(value, rewardType, 0, name);
    }

    public void addExp(long value, RewardType rewardType, int npcNameId, String name) {
        if (this.noExp) {
            return;
		}
        long reward = value;
        if ((getPlayer() != null) && (rewardType != null)) {
            reward = rewardType.calcReward(getPlayer(), value);
        }
        long repose = 0;
		if ((isReadyForReposteEnergy()) && (getCurrentReposteEnergy() > 0)) {
            if (rewardType != null && rewardType == RewardType.HUNTING || rewardType == RewardType.GROUP_HUNTING || rewardType == RewardType.QUEST) {
                repose = (long) ((reward / 100f) * 60);
				addReposteEnergy(-repose);
            }
        }
		long berdinStarBoost = 0;
		if ((isReadyForBerdinStar()) && (getBerdinStar() > 0)) {
		    if (rewardType != null && rewardType == RewardType.HUNTING || rewardType == RewardType.GROUP_HUNTING || rewardType == RewardType.QUEST) {
                berdinStarBoost = (long) ((reward / 100f) * 50);
                deleteBerdinStar(berdinStarBoost);
            }
        }
        reward = reward + repose + berdinStarBoost;
        setExp(reward);
        if ((getPlayer() != null) && (rewardType != null)) {
            switch (rewardType) {
                case HUNTING:
                case CRAFTING:
                case GATHERING:
                case GROUP_HUNTING:
                    if (npcNameId == 0) {
                        PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP2(reward));
                    } else {
                        PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP_DESC(new DescriptionId(npcNameId * 2 + 1), reward));
                        if (repose > 0) {
                            PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP_DESC(new DescriptionId(2805577), repose));
                        } if (berdinStarBoost > 0) {
                            PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP_DESC(new DescriptionId(2806671), berdinStarBoost));
                        }
                    }
                break;
				case QUEST:
                    if (npcNameId == 0) {
                        PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP2(reward));
                    } else if (repose > 0) {
                        PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP_VITAL_BONUS_DESC(new DescriptionId(npcNameId * 2 + 1), reward, repose));
                    } else {
                        PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP_DESC(new DescriptionId(npcNameId * 2 + 1), reward));
                    }
                break;
                case PVP_KILL:
                    if (repose > 0) {
                        PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP_VITAL_BONUS(name, reward, repose));
                    } else {
                        PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP(name, reward));
                    }
                break;
            }
        }
    }

    public boolean isReadyForReposteEnergy() {
        return this.level >= 10;
    }

    public void addReposteEnergy(long add) {
        if (!this.isReadyForReposteEnergy()) {
            return;
        }
        reposteCurrent += add;
        if (reposteCurrent < 0) {
            reposteCurrent = 0;
        } else if (reposteCurrent > getMaxReposteEnergy()) {
            reposteCurrent = getMaxReposteEnergy();
        }
    }

    public void updateMaxReposte() {
        if (!isReadyForReposteEnergy()) {
            reposteCurrent = 0;
            reposteMax = 0;
        } else {
            reposteMax = (long) (getExpNeed() * 0.25f); //Retail 99%
        }
    }

    public void removeExp(long exp) {
        if (this.exp - exp <= 0) {
            this.exp = 0;
        } else {
            this.exp = this.exp - exp;
        }
        PacketSendUtility.sendPacket(this.getPlayer(), new SM_STATUPDATE_EXP(getExpShown(), getExpRecoverable(), getExpNeed(), this.getCurrentReposteEnergy(), this.getMaxReposteEnergy(), this.getBerdinStar()));
    }

    public void setCurrentReposteEnergy(long value) {
        reposteCurrent = value;
    }

    public long getCurrentReposteEnergy() {
        return isReadyForReposteEnergy() ? this.reposteCurrent : 0;
    }

    public long getMaxReposteEnergy() {
        return isReadyForReposteEnergy() ? this.reposteMax : 0;
    }

    public void setExp(long reward) {
        int oldLvl = this.level; //current Level
        long restExp = 0;
        long totalExp = this.exp + reward;
        long requiredExp = DataManager.PLAYER_EXPERIENCE_TABLE.getExpTemplate(oldLvl + 1).getExp();
        long startMaxExp = DataManager.PLAYER_EXPERIENCE_TABLE.getExpTemplate(10).getExp();
        long maxExp = DataManager.PLAYER_EXPERIENCE_TABLE.getExpTemplate(81).getExp();
        if (getPlayerClass() != null && getPlayerClass().isStartingClass() && this.getLevel() == 9 && this.getExp() + reward >= startMaxExp) {
            this.exp = startMaxExp;
        } else if (this.getLevel() == 80 && this.getExp() + reward >= maxExp) {
            this.exp = maxExp;
        } else {
            if (totalExp >= requiredExp) {
                if (totalExp > requiredExp) {
                    restExp = totalExp - requiredExp;
                    this.exp = restExp;
                    this.level++;
                    upgradePlayerData();
                    updateMaxReposte();
                }
            } else {
                this.exp = totalExp;
            }
        } if (this.getPlayer() != null) {
            PacketSendUtility.sendPacket(this.getPlayer(), new SM_STATUPDATE_EXP(getExpShown(), getExpRecoverable(), getExpNeed(), this.getCurrentReposteEnergy(), this.getMaxReposteEnergy(), this.getBerdinStar()));
        }
    }

    private void upgradePlayerData() {
        Player player = getPlayer();
        if (player != null) {
            player.getController().upgradePlayer();
        }
    }

    public void setNoExp(boolean value) {
        this.noExp = value;
    }

    public boolean getNoExp() {
        return noExp;
    }

    /**
     * @return Race as from template
     */
    public final Race getRace() {
        return race;
    }

    public Race getOppositeRace() {
        return race == Race.ELYOS ? Race.ASMODIANS : Race.ELYOS;
    }

    /**
     * @return the mentorFlagTime
     */
    public int getMentorFlagTime() {
        return mentorFlagTime;
    }

    public boolean isHaveMentorFlag() {
        return mentorFlagTime > System.currentTimeMillis() / 1000;
    }

    /**
     * @param mentorFlagTime the mentorFlagTime to set
     */
    public void setMentorFlagTime(int mentorFlagTime) {
        this.mentorFlagTime = mentorFlagTime;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PlayerClass getPlayerClass() {
        return playerClass;
    }

    public void setPlayerClass(PlayerClass playerClass) {
        this.playerClass = playerClass;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public WorldPosition getPosition() {
        return position;
    }

    public Timestamp getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(Timestamp timestamp) {
        lastOnline = timestamp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        if (level <= DataManager.PLAYER_EXPERIENCE_TABLE.getMaxLevel()) {
            this.level = level;
            upgradePlayerData();
        }
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getTitleId() {
        return titleId;
    }

    public void setTitleId(int titleId) {
        this.titleId = titleId;
    }

    public int getBonusTitleId() {
        return bonusTitleId;
    }

    public void setBonusTitleId(int bonusTitleId) {
        this.bonusTitleId = bonusTitleId;
    }

    /**
     * This method should be called exactly once after creating object of this class
     *
     * @param position
     */
    public void setPosition(WorldPosition position) {
        if (this.position != null) {
            throw new IllegalStateException("position already set");
        }
        this.position = position;
    }

    /**
     * Gets the cooresponding Player for this common data. Returns null if the player is not online
     *
     * @return Player or null
     */
    public Player getPlayer() {
        if (online && getPosition() != null) {
            return World.getInstance().findPlayer(playerObjId);
        }
        return null;
    }

    public void addDp(int dp) {
        setDp(this.dp + dp);
    }

    /**
     * //TODO move to lifestats -> db save?
     *
     * @param dp
     */
    public void setDp(int dp) {
        if (getPlayer() != null) {
            if (playerClass.isStartingClass())
                return;

            int maxDp = getPlayer().getGameStats().getMaxDp().getCurrent();
            this.dp = dp > maxDp ? maxDp : dp;

            PacketSendUtility.broadcastPacket(getPlayer(), new SM_DP_INFO(playerObjId, this.dp), true);
            getPlayer().getGameStats().updateStatsAndSpeedVisually();
            PacketSendUtility.sendPacket(getPlayer(), new SM_STATUPDATE_DP(this.dp));
        } else {
            log.debug("CHECKPOINT : getPlayer in PCD return null for setDP " + isOnline() + " " + getPosition());
        }
    }

    public int getDp() {
        return this.dp;
    }

    @Override
    public int getTemplateId() {
        return 100000 + race.getRaceId() * 2 + gender.getGenderId();
    }

    @Override
    public int getNameId() {
        return 0;
    }

    /**
     * @param warehouseSize the warehouseSize to set
     */
    public void setWarehouseSize(int warehouseSize) {
        this.warehouseSize = warehouseSize;
    }

    /**
     * @return the warehouseSize
     */
    public int getWarehouseSize() {
        return warehouseSize;
    }

    public void setMailboxLetters(int count) {
        this.mailboxLetters = count;
    }

    public int getMailboxLetters() {
        return mailboxLetters;
    }

    /**
     * @param boundRadius
     */
    public void setBoundingRadius(BoundRadius boundRadius) {
        this.boundRadius = boundRadius;
    }

    @Override
    public BoundRadius getBoundRadius() {
        return boundRadius;
    }

    public void setDeathCount(int count) {
        this.soulSickness = count;
    }

    public int getDeathCount() {
        return this.soulSickness;
    }

    public void setLastTransferTime(long value) {
        this.lastTransferTime = value;
    }

    public long getLastTransferTime() {
        return this.lastTransferTime;
    }

    public int getWorldOwnerId() {
        return worldOwnerId;
    }

    public void setWorldOwnerId(int worldOwnerId) {
        this.worldOwnerId = worldOwnerId;
    }

    public int getJoinRequestLegionId() {
        return joinRequestLegionId;
    }

    public void setJoinRequestLegionId(int joinRequestLegionId) {
        this.joinRequestLegionId = joinRequestLegionId;
    }

    public LegionJoinRequestState getJoinRequestState() {
        return joinRequestState;
    }

    public void setJoinRequestState(LegionJoinRequestState joinRequestState) {
        this.joinRequestState = joinRequestState;
    }

    public void setLunaConsumePoint(int point) {
        this.lunaConsumePoint = point;
    }

    public int getLunaConsumePoint() {
        return lunaConsumePoint;
    }

    public void setMuniKeys(int keys) {
        this.muni_keys = keys;
    }

    public int getMuniKeys() {
        return muni_keys;
    }

    public void setLunaConsumeCount(int count) {
        this.consumeCount = count;
    }

    public int getLunaConsumeCount() {
        return consumeCount;
    }

    public void setWardrobeSlot(int slot) {
        this.wardrobeSlot = slot;
    }

    public int getWardrobeSlot() {
        return wardrobeSlot;
    }

    public PlayerUpgradeArcade getUpgradeArcade() {
        if (upgradeArcade == null) {
            this.upgradeArcade = new PlayerUpgradeArcade();
        }
        return upgradeArcade;
    }

    public void setUpgradeArcade(PlayerUpgradeArcade upgradeArcade) {
        this.upgradeArcade = upgradeArcade;
    }

    /**
     * Berdin's Star
     */
    public boolean isReadyForBerdinStar() {
        return this.level >= 10;
    }

    public void addBerdinStar(long add) {
        if (!isReadyForBerdinStar()) {
            return;
        }
        berdinStar += add;
        if (this.berdinStar < 0) {
            berdinStar = 0;
        } else if (berdinStar > getMaxBerdinStar()) {
            berdinStar = getMaxBerdinStar();
        }
        checkBerdinStarPercent();
    }

    public void deleteBerdinStar(long del) {
        if (!isReadyForBerdinStar()) {
            return;
        }
        berdinStar -= del;
        if (this.berdinStar < 0) {
            berdinStar = 0;
        } else if (berdinStar > getMaxBerdinStar()) {
            berdinStar = getMaxBerdinStar();
        }
        checkBerdinStarPercent();
    }

    public void setBerdinStar(long value) {
        berdinStar = value;
        checkBerdinStarPercent();
    }

    public long getBerdinStar() {
        return isReadyForBerdinStar() ? berdinStar : 0;
    }

    public long getMaxBerdinStar() {
        return isReadyForBerdinStar() ? berdinStarMax : 0;
    }

    public void checkBerdinStarPercent() {
        if (this.getPlayer() != null) {
            if (this.isReadyForBerdinStar()) {
                int percent = (int) (berdinStar * 100f / getMaxBerdinStar());
                if (!BerdinStarBoost && percent > 50) {
                    BerdinStarBoost = true;
                    //Berdin's Favor's charge level surpassed 0% and the additional EXP effect was activated.
                    PacketSendUtility.sendPacket(this.getPlayer(), new SM_SYSTEM_MESSAGE(1403399, 50));
                } else if (BerdinStarBoost && percent < 50) {
                    BerdinStarBoost = false;
                    //Berdin's Favor's charge level fell below 0% and the additional EXP effect was removed.
                    PacketSendUtility.sendPacket(this.getPlayer(), new SM_SYSTEM_MESSAGE(1403400, 50));
                } else if (berdinStar <= 0) {
                    //You’ve used up all your Berdin's Favor.
                    PacketSendUtility.sendPacket(this.getPlayer(), new SM_SYSTEM_MESSAGE(1403401));
                }
            }
        }
    }

    /**
     * Abyss Favor
     */
    public boolean isReadyForAbyssFavor() {
        return this.level >= 10;
    }

    public void addAbyssFavor(long add) {
        if (!isReadyForAbyssFavor()) {
            return;
        }
        abyssFavor += add;
        if (this.abyssFavor < 0) {
            abyssFavor = 0;
        } else if (abyssFavor > getMaxAbyssFavor()) {
            abyssFavor = getMaxAbyssFavor();
        }
        checkAbyssFavorPercent();
    }

    public void deleteAbyssFavor(long del) {
        if (!isReadyForAbyssFavor()) {
            return;
        }
        abyssFavor -= del;
        if (this.abyssFavor < 0) {
            abyssFavor = 0;
        } else if (abyssFavor > getMaxAbyssFavor()) {
            abyssFavor = getMaxAbyssFavor();
        }
        checkAbyssFavorPercent();
    }

    public void setAbyssFavor(long value) {
        abyssFavor = value;
        checkAbyssFavorPercent();
    }

    public long getAbyssFavor() {
        return isReadyForAbyssFavor() ? abyssFavor : 0;
    }

    public long getMaxAbyssFavor() {
        return isReadyForAbyssFavor() ? abyssFavorMax : 0;
    }

    public void checkAbyssFavorPercent() {
        if (this.getPlayer() != null) {
            if (this.isReadyForAbyssFavor()) {
                int percent = (int) (abyssFavor * 100f / getMaxAbyssFavor());
                if (!AbyssFavorBoost && percent > 50) {
                    AbyssFavorBoost = true;
                    //Abyssal Favor charge has surpassed 0% and the additional Abyss Point effect was activated.
                    PacketSendUtility.sendPacket(this.getPlayer(), new SM_SYSTEM_MESSAGE(1404029, 50));
                    PacketSendUtility.sendPacket(this.getPlayer(), new SM_ABYSS_FAVOR());
                } else if (AbyssFavorBoost && percent < 50) {
                    AbyssFavorBoost = false;
                    //Abyssal Favor charge fell below 0% and the additional Abyss Point effect was removed.
                    PacketSendUtility.sendPacket(this.getPlayer(), new SM_SYSTEM_MESSAGE(1404030, 50));
                    PacketSendUtility.sendPacket(this.getPlayer(), new SM_ABYSS_FAVOR());
                } else if (abyssFavor <= 0) {
                    //Abyssal Favor depleted.
                    PacketSendUtility.sendPacket(this.getPlayer(), new SM_SYSTEM_MESSAGE(1404031));
                    PacketSendUtility.sendPacket(this.getPlayer(), new SM_ABYSS_FAVOR());
                }
            }
        }
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getFloor() {
        return floor;
    }

    public void setMinionEnergy(int energy) {
        this.minionEnergy = energy;
    }

    public int getMinionEnergy() {
        return minionEnergy;
    }

    public void setLastMinion(int id) {
        this.lastMinion = id;
    }

    public int getLastMinion() {
        return lastMinion;
    }

    public Timestamp getMinionFunctionTime() {
        return minionFunctionTime;
    }

    public void setMinionFunctionTime(Timestamp minionFunctionTime) {
        this.minionFunctionTime = minionFunctionTime;
    }

    public int getGoldenDice() {
        return goldenDice;
    }

    public void setGoldenDice(int dice) {
        this.goldenDice = dice;
    }

    public int getResetBoard() {
        return resetBoard;
    }

    public void setResetBoard(int reset) {
        this.resetBoard = reset;
    }

    public int getFreeDice() {
        return freeDice;
    }

    public void setFreeDice(int freeDice) {
        this.freeDice = freeDice;
    }

    public int getLastDiceSlot() {
        return lastDiceSlot;
    }

    public void setLastDiceSlot(int lastDiceSlot) {
        this.lastDiceSlot = lastDiceSlot;
    }

    public int getLastDiceReward() {
        return lastDiceReward;
    }

    public void setLastDiceReward(int lastDiceReward) {
        this.lastDiceReward = lastDiceReward;
    }

    public Timestamp getDiceTime() {
        return diceTime;
    }

    public void setDiceTime(Timestamp diceTime) {
        this.diceTime = diceTime;
    }

    public int getDiceTemplateId() {
        return diceTemplateId;
    }

    public void setDiceTemplateId(int diceTemplateId) {
        this.diceTemplateId = diceTemplateId;
    }

    public void setWorldPlayTime(int playTime) {
        this.worldPlayTime = playTime;
    }

    public int getWorldPlayTime() {
        return worldPlayTime;
    }
}