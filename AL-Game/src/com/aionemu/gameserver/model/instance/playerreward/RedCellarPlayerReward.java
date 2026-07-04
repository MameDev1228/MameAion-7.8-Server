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
package com.aionemu.gameserver.model.instance.playerreward;

/****/
/** Author Rinzler (Encom)
/****/

public class RedCellarPlayerReward extends InstancePlayerReward
{
	private int gemstoneShardBox;
	private int shiningGemstoneShardBox;
	private int dazzlingGemstoneShardBox;
	private boolean isRewarded = false;
	
	public RedCellarPlayerReward(Integer object) {
		super(object);
	}
	
	public boolean isRewarded() {
		return isRewarded;
	}
	
	public void setRewarded() {
		isRewarded = true;
	}
	
	public int getGemstoneShardBox() {
		return gemstoneShardBox;
	}
	public void setGemstoneShardBox(int gemstoneShardBox) {
		this.gemstoneShardBox = gemstoneShardBox;
	}
	
	public int getShiningGemstoneShardBox() {
		return shiningGemstoneShardBox;
	}
	public void setShiningGemstoneShardBox(int shiningGemstoneShardBox) {
		this.shiningGemstoneShardBox = shiningGemstoneShardBox;
	}
	
	public int getDazzlingGemstoneShardBox() {
		return dazzlingGemstoneShardBox;
	}
	public void setDazzlingGemstoneShardBox(int dazzlingGemstoneShardBox) {
		this.dazzlingGemstoneShardBox = dazzlingGemstoneShardBox;
	}
}