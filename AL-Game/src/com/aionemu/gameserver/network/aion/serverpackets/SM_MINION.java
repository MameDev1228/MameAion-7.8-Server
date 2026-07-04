package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.Minion;
import com.aionemu.gameserver.model.gameobjects.Pet;
import com.aionemu.gameserver.model.gameobjects.player.MinionCommonData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

import java.sql.Timestamp;
import java.util.Collection;

/**
 * @author Ranastic
 */

public class SM_MINION extends AionServerPacket
{
	int action;
	int subaction;
	int minionObjId;
	int minionId;
	int masterObjId;
	int energy;
	boolean bool;
	private boolean isActing;
	private int lootNpcId;
	String name;
	private Timestamp expiredTimeMillis;
	private MinionCommonData commonData;
	private Collection<MinionCommonData> minions;
	private int dopeAction;
	private int dopeSlot;
	private int itemObjectId;
	private int code;
	private Player player;
	private long timeLeft;

	private int slot;
	private int slot2;

	public SM_MINION(int action) {
		this.action = action;
	}

	public SM_MINION(int action, int energy, boolean auto) {
		this.action = action;
		this.energy = energy;
		this.bool = auto;
	}

	public SM_MINION(int action, int minionObjId) {
		this.action = action;
		this.minionObjId = minionObjId;
	}

	public SM_MINION(int action, MinionCommonData commonData) {
		this.action = action;
		this.commonData = commonData;
	}

	public SM_MINION(int action, MinionCommonData commonData, int code) {
		this.action = action;
		this.commonData = commonData;
		this.code = code;
	}

	public SM_MINION(int action, String name, int minionObjId, int minionId, int masterObjId) {
		this.action = action;
		this.name = name;
		this.minionObjId = minionObjId;
		this.minionId = minionId;
		this.masterObjId = masterObjId;
	}

	public SM_MINION(int action, Collection<MinionCommonData> minions) {
		this.action = action;
		this.minions = minions;
	}

	public SM_MINION(int action, Player player) {
		this.action = action;
		this.player = player;
	}

	public SM_MINION(boolean isLooting) {
		this.action = 9;
		this.isActing = isLooting;
		this.subaction = 1;
	}

	public SM_MINION(boolean isLooting, int npcId) {
		this(isLooting);
		this.action = 9;
		this.lootNpcId = npcId;
		this.subaction = 1;
	}

	public SM_MINION(int dopeAction, int itemId, int slot) {
		this(dopeAction, true);
		itemObjectId = itemId;
		dopeSlot = slot;
		this.action = 9;
		this.subaction = 1;
	}

	public SM_MINION(int action, int dopeAction, int itemId, int slot) {
		this(dopeAction, true);
		this.action = action;
		itemObjectId = itemId;
		dopeSlot = slot;
		this.subaction = 0;
	}

	public SM_MINION(Player player, int action) {
		this.action = action;
		this.player = player;
	}

	public SM_MINION(int dopeAction, boolean isBuffing) {
		this.action = 14;
		this.dopeAction = dopeAction;
		this.isActing = isBuffing;
		this.subaction = 0;
	}

	public SM_MINION(Timestamp expire, boolean isAuto) {
		this.action = 9;
		this.expiredTimeMillis = expire;
		this.bool = isAuto;
	}
	public SM_MINION(int action, long timeLeft) {
		this.action = action;
		this.timeLeft = timeLeft;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeH(action);
		switch(action) {
			case 0:
				writeH(10);
				writeD(2500000);
				writeD(0);
				writeD(2);
				writeD(5000);
				writeD(0);
				writeD(10000);
				writeD(0);
				writeD(5000);
				writeD(0);
				writeD(5000);
				writeD(0);
				writeD(10000);
				writeD(0);
				writeH(4);
				break;
			case 1://list
				writeC(0x00);
				writeH(minions.size());
				for (MinionCommonData mcd : minions) {
					writeD(mcd.getObjectId());
					writeD(0);
					writeD(0);
					writeD(mcd.getMasterObjectId());
					writeD(mcd.getMinionId());
					writeS(mcd.getName());
					writeQ(mcd.getBirthday());
					writeQ(mcd.getGrowthPoints());//Growth Points
					writeC(!mcd.isLocked() ? 1 : 0);//lock ??
					int[] scrollBag = mcd.getDopingBag().getScrollsUsed();
					if (scrollBag.length == 0) {
						writeB(new byte[24]);
					} else {
						writeD(scrollBag.length > 1 ? scrollBag[0] : 0); //food slot 0
						writeD(scrollBag.length > 2 ? scrollBag[1] : 0); //drink slot 1
						writeD(scrollBag.length > 3 ? scrollBag[2] : 0); //scroll slot 2
						writeD(scrollBag.length > 4 ? scrollBag[3] : 0); //scroll slot 3
						writeD(scrollBag.length > 5 ? scrollBag[4] : 0); //scroll slot 4
						writeD(scrollBag.length > 6 ? scrollBag[5] : 0); //scroll slot 5
					}
					writeC(0);
				}
				break;
			case 2://adopt
				writeD(code); //0x03 combine fail ?? //adopt type ??
				writeD(0);
				writeH(0);
				writeD(commonData.getObjectId());
				writeD(0);
				writeD(0);
				writeD(commonData.getMasterObjectId());
				writeD(commonData.getMinionId());
				writeS(commonData.getName());
				writeQ(commonData.getBirthday());
				writeQ(commonData.getGrowthPoints());//Growth Points
				writeC(!commonData.isLocked() ? 1 : 0);//lock ??
				writeB(new byte[24]);
				writeC(0);
				break;
			case 3://delete ??
				writeH(code);
				writeD(commonData.getObjectId());
				break;
			case 4://rename
				writeD(commonData.getObjectId());
				writeS(commonData.getName());
				break;
			case 5://lock
				writeD(commonData.getObjectId());
				writeC(!commonData.isLocked() ? 1 : 0);
				break;
			case 6://summon
				if (commonData == null) {
					return;
				}
				writeS(commonData.getName());
				writeD(commonData.getObjectId());
				writeD(commonData.getMinionId());
				writeD(commonData.getMasterObjectId());
				break;
			case 7://unsummon
				if (commonData == null) {
					return;
				}
				writeD(commonData.getObjectId());
				writeC(21);
				break;
			case 8://growth
				if (commonData == null) {
					return;
				}
				writeD(commonData.getObjectId());
				writeQ(commonData.getGrowthPoints()); //points
				break;
			case 9://minions Fuction setting
				writeC(subaction);
				if(subaction == 1) {
					if (lootNpcId > 0) {
						writeC(isActing ? 1 : 2);
						writeD(lootNpcId);
					} else {
						writeC(0);
						writeC(isActing ? 1 : 0);
					}
				} else if (subaction == 0) {
					writeC(dopeAction);
					switch (dopeAction) {
						case 0:
							writeD(minionObjId);
							writeD(itemObjectId);
							writeD(dopeSlot);
							break;
						case 1:
							writeD(minionObjId);
							writeD(dopeSlot);
							break;
						case 3:
							writeD(minionObjId);
							writeD(itemObjectId);
							break;
					}
				}
				break;
			case 11://function ??
				//writeQ(this.player.getCommonData().getMinionFunctionTime().getTime());
				break;
		    /*case 11://maybe combine
			    writeC(1);
			    writeD(980013);
		    break;*/
			case 13://energy
				writeD(con.getActivePlayer().getCommonData().getMinionEnergy());
				writeC(1);
				break;
			case 14://Auto Fuction
				writeC(0);
				break;
		}
	}
}