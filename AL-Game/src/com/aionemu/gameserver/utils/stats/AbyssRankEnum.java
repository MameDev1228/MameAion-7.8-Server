package com.aionemu.gameserver.utils.stats;

import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum AbyssRankEnum
{
	//Ap Rank 7.x
	GRADE9_SOLDIER(1, 1800, 180, 0, 0, 0, 1802431, 0),
	GRADE8_SOLDIER(2, 2160, 216, 1200, 0, 0, 1802433, 0),
	GRADE7_SOLDIER(3, 2592, 259, 4220, 0, 0, 1802435, 0),
	GRADE6_SOLDIER(4, 3110, 311, 10990, 0, 0, 1802437, 0),
	GRADE5_SOLDIER(5, 3732, 373, 23500, 0, 0, 1802439, 0),
	GRADE4_SOLDIER(6, 4478, 447, 42780, 0, 0, 1802441, 0),
	GRADE3_SOLDIER(7, 5373, 537, 69700, 0, 0, 1802443, 0),
	GRADE2_SOLDIER(8, 6228, 622, 105600, 0, 0, 1802445, 0),
	GRADE1_SOLDIER(9, 7473, 1120, 150800, 0, 0, 1802447, 0),
	
	//Glory Rank 7.x
	STAR1_OFFICER(10, 8967, 1793, 0, 1244, 1000, 1802449, 400),
	STAR2_OFFICER(11, 10760, 2690, 0, 1368, 700, 1802451, 450),
	STAR3_OFFICER(12, 12912, 3873, 0, 1915, 500, 1802453, 600),
	STAR4_OFFICER(13, 15494, 5422, 0, 3064, 300, 1802455, 800),
	STAR5_OFFICER(14, 23241, 8134, 0, 5210, 100, 1802457, 1500),
	GENERAL(15, 26727, 10690, 0, 8335, 30, 1802459, 2000),
	GREAT_GENERAL(16, 30736, 12909, 0, 10002, 10, 1802461, 2500),
	COMMANDER(17, 35346, 15552, 0, 11503, 3, 1802463, 3000),
	SUPREME_COMMANDER(18, 40647, 18697, 0, 12437, 1, 1802465, 4000);
	
	static Logger log = LoggerFactory.getLogger(AbyssRankEnum.class);
	
	private int id;
	private int pointsGained;
	private int pointsLost;
	private int apRequired;
	private int gpRequired;
	private int quota;
	private int descriptionId;
	private int dailyReduceGp;

	/**
	 * @param id
	 * @param pointsGained
	 * @param pointsLost
	 * @param required
	 * @param quota
	 */
	private AbyssRankEnum(int id, int pointsGained, int pointsLost, int apRequired, int gpRequired, int quota, int descriptionId, int dailyReduceGp) {
		this.id = id;
		this.pointsGained = pointsGained;
		this.pointsLost = pointsLost;
		this.apRequired = apRequired;
		this.gpRequired = gpRequired;
		this.quota = quota;
		this.descriptionId = descriptionId;
		this.dailyReduceGp = dailyReduceGp;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the pointsLost
	 */
	public int getPointsLost() {
		return pointsLost;
	}

	/**
	 * @return the pointsGained
	 */
	public int getPointsGained() {
		return pointsGained;
	}

	/**
	 * @return AP required for Rank
	 */
	public int getApRequired() {
		return apRequired;
	}
	
	public int getGpRequired() {
		return gpRequired;
	}

	/**
	 * @return The quota is the maximum number of allowed player to have the rank
	 */
	public int getQuota() {
		return quota;
	}

	public int getDescriptionId() {
		return descriptionId;
	}
	
	public int getDailyReduceGp() {
		return dailyReduceGp;
	}

	public static DescriptionId getRankDescriptionId(Player player){
		int pRankId = player.getAbyssRank().getRank().getId();
		for (AbyssRankEnum rank : values()) {
			if (rank.getId() == pRankId) {
				int descId = rank.getDescriptionId();
				return (player.getRace() == Race.ELYOS) ? new DescriptionId(descId) : new DescriptionId(descId + 36);
			}
		}
		throw new IllegalArgumentException("No rank Description Id found for player: " + player);
	}

	/**
	 * @param id
	 * @return The abyss rank enum by his id
	 */
	public static AbyssRankEnum getRankById(int id) {
		for (AbyssRankEnum rank : values()) {
			if (rank.getId() == id) {
				return rank;
			}
		}
		throw new IllegalArgumentException("Invalid abyss rank provided " + id);
	}

	/**
	 * @param ap
	 * @return The abyss rank enum for his needed ap
	 */
	public static AbyssRankEnum getRankForAp(int ap) {
        AbyssRankEnum r = AbyssRankEnum.GRADE9_SOLDIER;
        for (AbyssRankEnum rank : values()) {
            if (rank.getApRequired() <= ap) {
                r = rank;
            } else {
                break;
            }
        }
        return r;
    }

	/**
	 * @param gp
	 * @return The abyss rankGp enum for his needed gp
	 */
	public static AbyssRankEnum getRankForGp(int gp) {
        AbyssRankEnum rgp = AbyssRankEnum.STAR1_OFFICER;
        for (AbyssRankEnum rank : values()) {
            if (rank.getGpRequired() <= gp) {
                rgp = rank;
            } else {
                break;
            }
        }
        return rgp;
    }
}