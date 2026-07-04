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
package ai.housing;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldPosition;
import com.aionemu.gameserver.world.knownlist.Visitor;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("agrint")
public class AgrintAI2 extends AggressiveNpcAI2
{
	private boolean canThink = true;
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
	}
	
	@Override
    protected void handleSpawned() {
        super.handleSpawned();
		switch (getNpcId()) {
			case 218850: //Spring Agrint.
			    announceSpringAgrint();
			break;
			case 218851: //Summer Agrint.
			    announceSummerAgrint();
			break;
			case 218852: //Fall Agrint.
			    announceFallAgrint();
			break;
			case 218853: //Winter Agrint.
				announceWinterAgrint();
			break;
		}
    }
	
	@Override
	protected void handleDied() {
		switch (getNpcId()) {
			//AGRINT ORIEL.
			case 218850: //Spring Agrint.
				//Spring has sprung, you'll be undone!
				sendMsg(1500548, getObjectId(), false, 0);
				//Sprouts, awake!
				sendMsg(1500549, getObjectId(), false, 6000);
				rndSpawn(218866, 2); //Vernal Umbronite.
				rndSpawn(218867, 2); //Sprout Umbronite.
				getOwner().getEffectController().removeAllEffects();
			break;
			case 218851: //Summer Agrint.
			    //The sun sear and scorch your sorry souls.
				sendMsg(1500550, getObjectId(), false, 0);
				//Sun's light, blaze forth!
				sendMsg(1500551, getObjectId(), false, 6000);
				rndSpawn(218868, 2); //Sweltering Umbronite.
				rndSpawn(218869, 2); //Rain Umbronite.
				getOwner().getEffectController().removeAllEffects();
			break;
			case 218852: //Fall Agrint.
			    //My gloom envelop you.
				sendMsg(1500552, getObjectId(), false, 0);
				//Leaves flurry and winds blow!
				sendMsg(1500553, getObjectId(), false, 6000);
				rndSpawn(218870, 2); //Maple Umbronite.
				rndSpawn(218871, 2); //Dusk Umbronite.
				getOwner().getEffectController().removeAllEffects();
			break;
			case 218853: //Winter Agrint.
			    //Fingers of cold creep up your spine...
				sendMsg(1500554, getObjectId(), false, 0);
				//An icy chill will freeze your soul.
				sendMsg(1500555, getObjectId(), false, 6000);
				rndSpawn(218872, 2); //Ice Umbronite.
				rndSpawn(218873, 2); //Snowflower Umbronite.
				getOwner().getEffectController().removeAllEffects();
			break;
			//AGRINT PERNON.
			case 218862: //Spring Agrint.
				//Spring has sprung, you'll be undone!
				sendMsg(1500548, getObjectId(), false, 0);
				//Sprouts, awake!
				sendMsg(1500549, getObjectId(), false, 6000);
				rndSpawn(218882, 2); //Vernal Umbronite.
				rndSpawn(218883, 2); //Sprout Umbronite.
				getOwner().getEffectController().removeAllEffects();
			break;
			case 218863: //Summer Agrint.
			    //The sun sear and scorch your sorry souls.
				sendMsg(1500550, getObjectId(), false, 0);
				//Sun's light, blaze forth!
				sendMsg(1500551, getObjectId(), false, 6000);
				rndSpawn(218884, 2); //Sweltering Umbronite.
				rndSpawn(218885, 2); //Rain Umbronite.
				getOwner().getEffectController().removeAllEffects();
			break;
			case 218864: //Fall Agrint.
			    //My gloom envelop you.
				sendMsg(1500552, getObjectId(), false, 0);
				//Leaves flurry and winds blow!
				sendMsg(1500553, getObjectId(), false, 6000);
				rndSpawn(218886, 2); //Maple Umbronite.
				rndSpawn(218887, 2); //Dusk Umbronite.
				getOwner().getEffectController().removeAllEffects();
			break;
			case 218865: //Winter Agrint.
				//Fingers of cold creep up your spine...
				sendMsg(1500554, getObjectId(), false, 0);
				//An icy chill will freeze your soul.
				sendMsg(1500555, getObjectId(), false, 6000);
				rndSpawn(218888, 2); //Ice Umbronite.
				rndSpawn(218889, 2); //Snowflower Umbronite.
				getOwner().getEffectController().removeAllEffects();
			break;
		}
		super.handleDied();
	}
	
	private void announceSpringAgrint() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HF_SpringAgrintAppear);
			}
		});
	}
	private void announceSummerAgrint() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HF_SummerAgrintAppear);
			}
		});
	}
	private void announceFallAgrint() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HF_FallAgrintAppear);
			}
		});
	}
	private void announceWinterAgrint() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HF_WinterAgrintAppear);
			}
		});
	}
	
	private void rndSpawn(int npcId, int count) {
		for (int i = 0; i < count; i++) {
			SpawnTemplate template = rndSpawnInRange(npcId, 8);
			SpawnEngine.spawnObject(template, getPosition().getInstanceId());
		}
	}
	
	protected SpawnTemplate rndSpawnInRange(int npcId, float distance) {
		float direction = Rnd.get(0, 199) / 100f;
		float x = (float) (Math.cos(Math.PI * direction) * distance);
        float y = (float) (Math.sin(Math.PI * direction) * distance);
		return SpawnEngine.addNewSingleTimeSpawn(getPosition().getMapId(), npcId, getPosition().getX() + x, getPosition().getY() + y, getPosition().getZ(), getPosition().getHeading());
	}
	
	@Override
	public int modifyOwnerDamage(int damage) {
		return 1;
	}
	
	@Override
	public int modifyDamage(int damage) {
		return 1;
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}