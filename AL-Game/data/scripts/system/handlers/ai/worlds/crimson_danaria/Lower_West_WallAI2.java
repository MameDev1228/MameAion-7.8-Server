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
package ai.world.crimson_danaria;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Lower_West_Wall")
public class Lower_West_WallAI2 extends NpcAI2
{
	private boolean canThink = true;
	private AtomicBoolean isHome = new AtomicBoolean(true);
	private List<Integer> percents = new ArrayList<Integer>();
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isHome.compareAndSet(true, false)) {
			lowerWestSideAttacked();
		}
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{50});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				percents.remove(percent);
				canThink = false;
				lowerWestSideDamaged();
			}
			break;
		}
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
	    isHome.set(true);
		getOwner().getEffectController().removeAllEffects();
    }
	
	@Override
	protected void handleDied() {
		super.handleDied();
		percents.clear();
		lowerWestSideDie();
		getOwner().getEffectController().removeAllEffects();
	}
	
	private void lowerWestSideAttacked() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//The lower west side of the castle wall is under attack.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_LDF5b_6021_Wall_01_Attacked, 0);
			}
		});
	}
	private void lowerWestSideDamaged() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//A powerful explosion seriously damaged the lower west side of the castle wall.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_LDF5b_6021_Wall_01_Damaged, 0);
			}
		});
	}
	private void lowerWestSideDie() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//The lower west side of the castle wall was destroyed.
				PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_LDF5b_6021_Wall_01_Die, 0);
			}
		});
	}
	
	@Override
	public boolean isMoveSupported() {
		return false;
	}
}