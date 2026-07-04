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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.List;

/**
 * @author Ranastic
 */

public class SM_LUNA_SHOP_LIST extends AionServerPacket
{
	private static final Logger log = LoggerFactory.getLogger(SM_LUNA_SHOP_LIST.class);
	private int actionId;
	private long points;
	private int keys;
	private int costId;
	private int entryCount;
	private int tableId;
	private List<Integer> elyosSpecialCraft;
	private List<Integer> asmoSpecialCraft;
	private List<Integer> randomDailyCraft;
	private int lunaDiceNum;
	private int lunaDiceItems;
	private int unk4;

	private int startTime = ((int) (Calendar.getInstance().getTimeInMillis() / 1000));
	private int endTime = (startTime + 86400);
	private int endTime2 = (startTime + (86400 * 7));

	public SM_LUNA_SHOP_LIST(int actionId) {
		this.actionId = actionId;
	}
	
	public SM_LUNA_SHOP_LIST(int action, int lunaDiceNum, int lunaDiceItems, String FctName) {
		this.actionId = action;
		this.lunaDiceNum = lunaDiceNum;
		this.lunaDiceItems = lunaDiceItems;
	}
	
	public SM_LUNA_SHOP_LIST(int actionId, long points) {
		this.actionId = actionId;
		this.points = points;
	}
	
	public SM_LUNA_SHOP_LIST(int actionId, int keys) {
		this.actionId = actionId;
		this.keys = keys;
	}
	
	public SM_LUNA_SHOP_LIST(List<Integer> elyosSpecialCraft, List<Integer> asmoSpecialCraft) {
		this.actionId = 2;
		this.tableId = 0;
		this.elyosSpecialCraft = elyosSpecialCraft;
		this.asmoSpecialCraft = asmoSpecialCraft;
	}
	
	public SM_LUNA_SHOP_LIST(List<Integer> randomDailyCraft) {
		this.actionId = 2;
		this.tableId = 1;
		this.randomDailyCraft = randomDailyCraft;
	}
	
	public SM_LUNA_SHOP_LIST(int actionId, int tableId, int costId) {
		this.actionId = actionId;
		this.tableId = tableId;
		this.costId = costId;
	}
	
	public SM_LUNA_SHOP_LIST(int actionId, int tableId, int costId, int unk4, int lol) {
		this.actionId = actionId;
		this.tableId = tableId;
		this.costId = costId;
		this.unk4 = unk4;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		Player player = con.getActivePlayer();
		writeC(actionId);//actionid
		switch (actionId) {
			case 0://luna point handler id
				writeQ(con.getAccount().getLuna());
			break;
			case 1://taki advanture update
				writeH(tableId);//size?
				writeD(costId);
				writeD(0); //free Cost
			break;
			case 2:
				writeC(tableId);//tabId
				switch (tableId) {
					case 0:
						writeD(startTime);// Start time
						writeD(0);
						writeD(endTime);// End Time
						writeD(0);
						switch (player.getRace()) {
							case ELYOS:
								writeH(elyosSpecialCraft.size());// size
								for (Integer elyos : elyosSpecialCraft) {
									writeD(elyos);//luna recipe id
								}
								break;
							case ASMODIANS:
								writeH(asmoSpecialCraft.size());// size
								for (Integer asmo : asmoSpecialCraft) {
									writeD(asmo);//luna recipe id
								}
								break;
						}
					break;
					case 1:
						writeD(startTime);
						writeD(0); //test
						writeD(endTime);
						writeD(0);
						writeH(randomDailyCraft.size());//size
						for (Integer daily : randomDailyCraft) {
							writeD(daily);//luna recipe id
						}
					break;
				}
			break;
			case 3:
				writeD(1);
				writeD(1);
			break;
			case 4://munirunerk's keys
				writeD(con.getActivePlayer().getMuniKeys());
			break;
			case 5://luna consume point spent
				writeD(con.getActivePlayer().getLunaConsumePoint());
			break;
			case 6://update taki's mission?
			break;
			case 7:
				writeC(0);
				writeH(100);
			break;
			case 8:
				writeH(lunaDiceNum);
				writeD(lunaDiceItems);
			break;
			case 9:
				writeC(-1);
				writeC(255);
			break;
			case 15:
				writeH(0);
				writeD(0);
			break;
		}
	}
}