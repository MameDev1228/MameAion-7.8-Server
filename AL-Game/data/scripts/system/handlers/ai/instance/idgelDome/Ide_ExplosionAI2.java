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
package ai.instance.idgelDome;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.poll.*;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Ide_Explosion")
public class Ide_ExplosionAI2 extends NpcAI2
{
	private Future<?> ideExplosionTask;
	
	@Override
	public void think() {
	}
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		SkillEngine.getInstance().getSkill(getOwner(), 21559, 60, getTarget()).useNoAnimationSkill(); //Ide Explosion.
		nextIdeExplosion();
	}
	
	private void nextIdeExplosion() {
		ideExplosionTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				ideExplosion();
			}
		}, 3000, 10000);
	}
	
	private void ideExplosion() {
		switch (Rnd.get(1, 30)) {
			case 1:
			    spawn(857985, 223.8631f, 204.6591f, 79.8621f, (byte) 105);
			break;
            case 2:
			    spawn(857985, 207.2701f, 199.0392f, 79.8621f, (byte) 73);
			break;
            case 3:
			    spawn(857985, 206.8560f, 229.6816f, 79.9752f, (byte) 34);
			break;
            case 4:
			    spawn(857985, 205.7647f, 259.5381f, 85.1724f, (byte) 0);
			break;
            case 5:
			    spawn(857985, 233.0871f, 274.5522f, 89.2910f, (byte) 81);
			break;
            case 6:
			    spawn(857985, 251.2565f, 291.0344f, 89.3029f, (byte) 97);
			break;
            case 7:
			    spawn(857985, 271.6235f, 292.8755f, 89.3107f, (byte) 86);
			break;
            case 8:
			    spawn(857985, 288.6084f, 283.6652f, 92.9667f, (byte) 16);
			break;
            case 9:
			    spawn(857985, 302.4931f, 297.1147f, 88.7278f, (byte) 15);
			break;
            case 10:
			    spawn(857985, 276.5026f, 271.6816f, 92.9425f, (byte) 75);
			break;
            case 11:
			    spawn(857985, 297.7512f, 268.5453f, 89.3028f, (byte) 65);
			break;
            case 12:
			    spawn(857985, 295.5736f, 244.9519f, 89.3052f, (byte) 52);
			break;
            case 13:
			    spawn(857985, 278.0393f, 227.7699f, 89.3028f, (byte) 38);
			break;
            case 14:
			    spawn(857985, 257.2781f, 225.3783f, 89.3028f, (byte) 26);
			break;
            case 15:
			    spawn(857985, 241.2174f, 234.5789f, 92.9718f, (byte) 15);
			break;
            case 16:
			    spawn(857985, 252.9236f, 246.7642f, 92.9425f, (byte) 14);
			break;
            case 17:
			    spawn(857985, 231.1372f, 250.6710f, 89.2928f, (byte) 34);
			break;
            case 18:
			    spawn(857985, 323.3003f, 258.8278f, 85.1724f, (byte) 62);
			break;
            case 19:
			    spawn(857985, 323.0904f, 286.1350f, 79.8621f, (byte) 91);
			break;
            case 20:
			    spawn(857985, 306.0208f, 312.5088f, 79.8621f, (byte) 44);
			break;
            case 21:
			    spawn(857985, 322.5616f, 319.3589f, 79.8621f, (byte) 14);
			break;
            case 22:
			    spawn(857985, 226.3969f, 220.6507f, 88.7278f, (byte) 75);
			break;
            case 23:
			    spawn(857985, 275.4004f, 279.7122f, 85.3750f, (byte) 110);
			break;
            case 24:
			    spawn(857985, 259.2716f, 281.0453f, 85.3750f, (byte) 94);
			break;
            case 25:
			    spawn(857985, 242.9404f, 268.5405f, 85.2837f, (byte) 110);
			break;
            case 26:
			    spawn(857985, 243.7539f, 249.4746f, 85.3750f, (byte) 12);
			break;
            case 27:
			    spawn(857985, 255.9392f, 236.4897f, 85.3750f, (byte) 112);
			break;
            case 28:
			    spawn(857985, 273.3356f, 238.5667f, 85.3395f, (byte) 36);
			break;
            case 29:
			    spawn(857985, 285.7655f, 251.5583f, 85.3750f, (byte) 53);
			break;
            case 30:
			    spawn(857985, 284.5052f, 270.1414f, 85.3750f, (byte) 72);
			break;
		}
		getOwner().getController().onDelete();
	}
	
	@Override
    public AIAnswer ask(AIQuestion question) {
        switch (question) {
            case CAN_ATTACK_PLAYER:
                return AIAnswers.POSITIVE;
            default:
                return AIAnswers.NEGATIVE;
        }
    }
	
	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		ideExplosionTask.cancel(true);
	}
	
    @Override
	public boolean isMoveSupported() {
		return false;
	}
}