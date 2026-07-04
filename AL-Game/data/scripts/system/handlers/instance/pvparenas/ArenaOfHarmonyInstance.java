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
import com.aionemu.gameserver.model.instance.playerreward.HarmonyGroupReward;
import com.aionemu.gameserver.world.WorldMapInstance;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(300450000)
public class ArenaOfHarmonyInstance extends HarmonyArenaInstance
{
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		killBonus = 1000;
		deathFine = -150;
		super.onInstanceCreate(instance);
	}
	
	@Override
	protected void reward() {
		float totalScoreAP = (1.0f * 3) * 100;
		float totalScoreGP = (1.0f * 3) * 100;
		float totalScoreKinah = (1.0f * 3) * 100;
		int totalPoints = instanceReward.getTotalPoints();
		for (HarmonyGroupReward group : instanceReward.getGroups()) {
			int score = group.getPoints();
			int rank = instanceReward.getRank(score);
			float percent = group.getParticipation();
			float scoreRate = ((float) score / (float) totalPoints);
			//////////////////
			int basicAP = 100;
			int rankingAP = 0;
			basicAP *= percent;
			//////////////////
			int basicGP = 100;
			int rankingGP = 0;
			basicGP *= percent;
			//////////////////
			int basicKinah = 5000;
			int rankingKinah = 0;
			basicKinah *= percent;
			//////////////////
			int scoreAP = (int) (totalScoreAP * scoreRate);
			int scoreGP = (int) (totalScoreGP * scoreRate);
			int scoreKinah = (int) (totalScoreKinah * scoreRate);
			switch (rank) {
				case 0:
					rankingAP = 50000;
					rankingGP = 1000;
					rankingKinah = 50000;
				break;
				case 1:
					rankingAP = 25000;
					rankingGP = 750;
					rankingKinah = 25000;
				break;
				case 2:
					rankingAP = 10000;
					rankingGP = 500;
					rankingKinah = 10000;
				break;
			}
			rankingAP *= percent;
			rankingGP *= percent;
			rankingKinah *= percent;
			group.setBasicAP(basicAP);
			group.setRankingAP(rankingAP);
			group.setScoreAP(scoreAP);
			group.setBasicGP(basicGP);
			group.setRankingGP(rankingGP);
			group.setScoreGP(scoreGP);
			group.setBasicKinah(basicKinah);
			group.setRankingKinah(rankingKinah);
			group.setScoreKinah(scoreKinah);
		}
		super.reward();
	}
}