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
package ai.instance.perilousContaminatedUnderpath;

import ai.AggressiveNpcAI2;

import com.aionemu.gameserver.ai2.*;
import com.aionemu.gameserver.ai2.handler.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.*;
import com.aionemu.gameserver.world.knownlist.Visitor;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("IDEVENT_Def_Mob")
public class IDEVENT_Def_MobAI2 extends AggressiveNpcAI2
{
	@Override
	protected void handleDied() {
		super.handleDied();
		sendReward();
		DiedEventHandler.onDie(this);
	}
	
	private void sendReward() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (MathUtil.isIn3dRange(player, getOwner(), 100)) {
					switch (player.getWorldId()) {
						case 301631000: //Secret Hellpath 7.x
							ItemService.addItem(player, 182007405, 1); //Bright Aether.
						break;
						case 301632000: //Perilous Contaminated Underpath.
							ItemService.addItem(player, 186000470, 1); //War Points.
						break;
					}
				}
			}
		});
	}
}