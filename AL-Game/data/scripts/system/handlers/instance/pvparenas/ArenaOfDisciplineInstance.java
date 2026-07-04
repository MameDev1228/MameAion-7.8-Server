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
package instance.pvparenas;

import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.playerreward.InstancePlayerReward;
import com.aionemu.gameserver.model.instance.playerreward.PvPArenaPlayerReward;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(300360000)
public class ArenaOfDisciplineInstance extends PvPArenaInstance
{
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		killBonus = 200;
		deathFine = -100;
		super.onInstanceCreate(instance);
	}
	
	@Override
	public void onGather(Player player, Gatherable gatherable) {
		if (!instanceReward.isStartProgress()) {
			return;
		}
		getPlayerReward(player.getObjectId()).addPoints(1250);
		sendPacket();
		int nameId = gatherable.getObjectTemplate().getNameId();
		DescriptionId name = new DescriptionId(nameId * 2 + 1);
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400237, name, 1250));
	}
	
	protected void reward() {
		int totalPoints = instanceReward.getTotalPoints();
		int size = instanceReward.getInstanceRewards().size();
		float totalAP = (1.0f * size) * 100;
		float totalGP = (1.0f * size) * 100;
		float totalKinah = (1.01f * size) * 100;
		for (InstancePlayerReward playerReward: instanceReward.getInstanceRewards()) {
			PvPArenaPlayerReward reward = (PvPArenaPlayerReward) playerReward;
			if (!reward.isRewarded()) {
				int score = reward.getScorePoints();
				float scoreRate = ((float) score / (float) totalPoints);
				int rank = instanceReward.getRank(score);
				float percent = reward.getParticipation();
				int basicAP = 100;
				int basicGP = 100;
				int basicKinah = 100;
				int rankingAP = 10000;
				int rankingGP = 250;
				int rankingKinah = 10000;
				if (size > 1) {
					rankingAP = rank == 0 ? 40000 : 10000;
					rankingGP = rank == 0 ? 1000 : 250;
					rankingKinah = rank == 0 ? 50000 : 10000;
				}
				int scoreAP = (int)(totalAP * scoreRate);
				int scoreGP = (int)(totalGP * scoreRate);
				int scoreKinah = (int)(totalKinah * scoreRate);
				//<Abyss Points>
				basicAP *= percent;
				rankingAP *= percent;
				reward.setBasicAP(basicAP);
				reward.setRankingAP(rankingAP);
				reward.setScoreAP(scoreAP);
				//<Glory Points>
				basicGP *= percent;
				rankingGP *= percent;
				reward.setBasicGP(basicGP);
				reward.setRankingGP(rankingGP);
				reward.setScoreGP(scoreGP);
				//<Kinah>
				basicKinah *= percent;
				rankingKinah *= percent;
				reward.setBasicKinah(basicKinah);
				reward.setRankingKinah(rankingKinah);
				reward.setScoreKinah(scoreKinah);
			}
		}
		super.reward();
	}
}