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
package ai.instance.altarOfAscension;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.manager.EmoteManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Weakened_Mortasha_Normal")
public class Weakened_Mortasha_NormalAI2 extends AggressiveNpcAI2
{
	private boolean canThink = true;
	private int curentPercent = 100;
	private List<Integer> percents = new ArrayList<Integer>();
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	@Override
	public void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{50});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		curentPercent = hpPercentage;
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 50:
					    smokescreen();
					break;
				}
				percents.remove(percent);
				break;
			}
		}
	}
	
	private void smokescreen() {
		canThink = false;
		getOwner().getController().abortCast();
		EmoteManager.emoteStopAttacking(getOwner());
		getOwner().getController().cancelCurrentSkill();
		//This is only a spark….
		sendMsg(1502848, getObjectId(), false, 2000);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				SkillEngine.getInstance().getSkill(getOwner(), 20229, 60, getTarget()).useNoAnimationSkill(); //Smokescreen.
			}
		}, 2000);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				weakenedMortashaVanish();
				getOwner().getController().onDelete();
				switch (Rnd.get(1, 7)) {
					case 1:
						desertRuinsExcavationSite();
						spawn(858715, 2732.9126f, 2679.0360f, 628.1217f, (byte) 72);
					break;
					case 2:
						scaleshadowPrisonCamp();
						spawn(858715, 2938.8025f, 1910.8958f, 673.2500f, (byte) 92);
					break;
					case 3:
						burningPath();
						spawn(858715, 2585.7322f, 1818.4229f, 695.4014f, (byte) 119);
					break;
					case 4:
						circleOfRebirth();
						spawn(858715, 2765.9385f, 1131.9886f, 777.6271f, (byte) 89);
					break;
					case 5:
						ashwindCliff();
						spawn(858715, 2210.2832f, 1616.7418f, 742.6250f, (byte) 93);
					break;
					case 6:
						yanohasTerritory();
						spawn(858715, 2124.6416f, 2862.5298f, 667.9198f, (byte) 103);
					break;
					case 7:
						boilingGeyser();
						spawn(858715, 2619.9563f, 2220.3723f, 680.8722f, (byte) 33);
					break;
				}
			}
		}, 8000);
	}
	
	private void weakenedMortashaVanish() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//The ascended entity has disappeared.
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_IDF8_Dragon_Altar_MSG_10, 0);
				}
			}
		});
	}
	private void yanohasTerritory() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//Power of an ascended entity detected in the Desert Ruins Excavation Site.
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_IDF8_Dragon_Altar_MSG_01, 5000);
				}
			}
		});
	}
	private void desertRuinsExcavationSite() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//Power of an ascended entity detected in the Desert Ruins Excavation Site.
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_IDF8_Dragon_Altar_MSG_02, 5000);
				}
			}
		});
	}
	private void boilingGeyser() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//Power of an ascended entity detected in the Desert Ruins Excavation Site.
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_IDF8_Dragon_Altar_MSG_03, 5000);
				}
			}
		});
	}
	private void ashwindCliff() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//Power of an ascended entity detected in the Ashwind Cliff.
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_IDF8_Dragon_Altar_MSG_04, 5000);
				}
			}
		});
	}
	private void circleOfRebirth() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//Power of an ascended entity detected in the Circle of Rebirth.
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_IDF8_Dragon_Altar_MSG_05, 5000);
				}
			}
		});
	}
	private void scaleshadowPrisonCamp() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//Power of an ascended entity detected in the Scaleshadow Prison Camp.
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_IDF8_Dragon_Altar_MSG_06, 5000);
				}
			}
		});
	}
	private void burningPath() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//Power of an ascended entity detected in the Burning Path.
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_IDF8_Dragon_Altar_MSG_07, 5000);
				}
			}
		});
	}
	
	@Override
    protected void handleDespawned() {
        super.handleDespawned();
		percents.clear();
    }
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		addPercent();
	}
	
	@Override
    protected void handleBackHome() {
        super.handleBackHome();
		addPercent();
		canThink = true;
		curentPercent = 100;
    }
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}