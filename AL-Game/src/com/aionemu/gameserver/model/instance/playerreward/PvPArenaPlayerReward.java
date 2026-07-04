package com.aionemu.gameserver.model.instance.playerreward;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.InstanceBuff;

public class PvPArenaPlayerReward extends InstancePlayerReward
{
	private int position;
	private int timeBonus;
	private float timeBonusModifier;
	//<Abyss Points>
	private int basicAP;
	private int rankingAP;
	private int scoreAP;
	//<Glory Points>
	private int basicGP;
	private int rankingGP;
	private int scoreGP;
	//<Kinah>
	private int basicKinah;
	private int rankingKinah;
	private int scoreKinah;
	private long logoutTime;
	private boolean isRewarded = false;
	private InstanceBuff boostMorale;
	
	public PvPArenaPlayerReward(Integer object, int timeBonus, byte buffId) {
		super(object);
		super.addPoints(13000);
		this.timeBonus = timeBonus;
		timeBonusModifier = ((float) this.timeBonus / (float) 660000);
		boostMorale = new InstanceBuff(buffId);
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	public int getTimeBonus() {
		return timeBonus > 0 ? timeBonus : 0;
	}
	
	public void updateLogOutTime() {
		logoutTime = System.currentTimeMillis();
	}
	
	public void updateBonusTime() {
		int offlineTime = (int) (System.currentTimeMillis() - logoutTime);
		timeBonus -= offlineTime * timeBonusModifier;
	}
	
	public boolean isRewarded() {
		return isRewarded;
	}
	
	public void setRewarded() {
		isRewarded = true;
	}
	
	//<Abyss Points>
	public int getBasicAP() {
		return basicAP;
	}
	
	public int getRankingAP() {
		return rankingAP;
	}
	
	public int getScoreAP() {
		return scoreAP;
	}
	
	public void setBasicAP(int ap) {
		this.basicAP = ap;
	}
	
	public void setRankingAP(int ap) {
		this.rankingAP = ap;
	}
	
	public void setScoreAP(int ap) {
		this.scoreAP = ap;
	}
	
	//<Glory Points>
	public int getBasicGP() {
		return basicGP;
	}
	
	public int getRankingGP() {
		return rankingGP;
	}
	
	public int getScoreGP() {
		return scoreGP;
	}
	
	public void setBasicGP(int gp) {
		this.basicGP = gp;
	}
	
	public void setRankingGP(int gp) {
		this.rankingGP = gp;
	}
	
	public void setScoreGP(int gp) {
		this.scoreGP = gp;
	}
	
	//<Kinah>
	public void setBasicKinah(int basicKinah) {
		this.basicKinah = basicKinah;
	}
	
	public void setRankingKinah(int rankingKinah) {
		this.rankingKinah = rankingKinah;
	}
	
	public void setScoreKinah(int scoreKinah) {
		this.scoreKinah = scoreKinah;
	}
	
	public int getBasicKinah() {
		return basicKinah;
	}
	
	public int getRankingKinah() {
		return rankingKinah;
	}
	
	public int getScoreKinah() {
		return scoreKinah;
	}
	
	public float getParticipation() {
		return (float) getTimeBonus() / timeBonus;
	}
	
	public int getScorePoints() {
		return timeBonus + getPoints();
	}
	
	public boolean hasBoostMorale() {
		return boostMorale.hasInstanceBuff();
	}
	
	public void applyBoostMoraleEffect(Player player) {
		boostMorale.applyEffect(player, 20000);
	}
	
	public void endBoostMoraleEffect(Player player) {
		boostMorale.endEffect(player);
	}
	
	public int getRemaningTime() {
		int time = boostMorale.getRemaningTime();
		if (time >= 0 && time < 20) {
			return 20 - time;
		}
		return 0;
	}
}