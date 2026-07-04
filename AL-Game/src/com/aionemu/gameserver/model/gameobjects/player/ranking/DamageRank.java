package com.aionemu.gameserver.model.gameobjects.player.ranking;

import com.aionemu.gameserver.model.gameobjects.PersistentState;

/**
 * Created by Wnkrz on 24/07/2017.
 */

public class DamageRank
{
    private int rank;
    private int bestRank;
    private int lowRank;
    private int currentScore;
    private int lastScore;
    private int bestScore;
    private PersistentState persistentState;
	
    public DamageRank(int rank, int bestRank, int low_rank, int current_score, int last_score, int best_score){
        this.rank = rank;
        this.bestRank = bestRank;
        this.lowRank = low_rank;
        this.currentScore = current_score;
        this.lastScore = last_score;
        this.bestScore = best_score;
    }
	
    public int getRank() {
        return rank;
    }
    public int getBestRank() {
        return bestRank;
    }
    public int getLowRank() {
        return lowRank;
    }
    public int getCurrentScore() {
        return currentScore;
    }
    public int getLastScore() {
        return lastScore;
    }
    public int getBestScore() {
        return bestScore;
    }
    public void setRank(int r) {
        this.rank = r;
    }
    public void setBestRank(int r) {
        this.bestRank = r;
    }
    public void  setLowRank(int r) {
        this.lowRank = r;
    }
    public void setCurrentScore(int r) {
        this.currentScore = r;
    }
    public void setLastScore(int r) {
        this.lastScore = r;
    }
    public void setBestScore(int r) {
        this.bestScore = r;
    }
	
    public PersistentState getPersistentState() {
        return persistentState;
    }
	
    public void setPersistentState(PersistentState persistentState) {
        if (persistentState != PersistentState.UPDATE_REQUIRED || this.persistentState != PersistentState.NEW) {
            this.persistentState = persistentState;
		}
    }
}