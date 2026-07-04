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
package instance;

import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(300160000)
public class Lower_Udas_Temple extends GeneralInstanceHandler
{
	private Race lowerUdasRace;
	
	@Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
    }
	
	@Override
    public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
		if (player.getRace() == Race.ELYOS) {
			ClassChangeService.onUpdateQuest62110(player);
			ClassChangeService.onUpdateGuide63801(player);
		} else {
			ClassChangeService.onUpdateQuest72110(player);
			ClassChangeService.onUpdateGuide73801(player);
		} if (lowerUdasRace == null) {
			lowerUdasRace = player.getRace();
			travelerBag();
			hiddenSwitch();
		}
    }
	
	private void hiddenSwitch() {
		final int hiddenSwitch = lowerUdasRace == Race.ASMODIANS ? 701534 : 700604;
		spawn(hiddenSwitch, 806.1687f, 886.72736f, 149.53448f, (byte) 60);
	}
	
	private void travelerBag() {
		final int travelerBag = lowerUdasRace == Race.ASMODIANS ? 702615 : 730229;
		spawn(travelerBag, 739.9965f, 873.97766f, 153.00752f, (byte) 25);
	}
	
	@Override
	public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 653581: //Zhanim The Librarian.
				QuestState qs60305 = player.getQuestStateList().getQuestState(60305);
				QuestState qs70305 = player.getQuestStateList().getQuestState(70305);
				if (qs60305 != null && qs60305.getStatus() == QuestStatus.START && qs60305.getQuestVarById(0) == 4) {
					ClassChangeService.onUpdateMission60305(player);
				} else if (qs70305 != null && qs70305.getStatus() == QuestStatus.START && qs70305.getQuestVarById(0) == 5) {
					ClassChangeService.onUpdateMission70305(player);
				}
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						player.getController().updateZone();
						player.getController().updateNearbyQuests();
					}
				});
			break;
			case 653470: //Debilkarim The Maker.
				spawn(836792, 560.74164f, 1298.1951f, 187.89362f, (byte) 0); //Lower Udas Exit.
			break;
		}
	}
}