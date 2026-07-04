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
package ai.instance.benirunerkEstate;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;

/****/
/** Author Rinzler (Encom)
/****/
 
@AIName("IDF8_House_Party")
public class IDF8_House_PartyAI2 extends NpcAI2
{
    @Override
    protected void handleDialogStart(Player player) {
        PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
    }
	
	@Override
    public boolean onDialogSelect(final Player player, int dialogId, int questId, int extendedRewardIndex) {
		if (dialogId == 10000) {
			switch (getNpcId()) {
				case 838344:
				case 838345:
				    startHouseParty();
				break;
			}
        }
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
		getOwner().getController().onDelete();
        return true;
    }
	
	private void startHouseParty() {
		//IDF8_House_Dev_M_Fi_80_Ae.
		spawn(658676, 627.3351f, 494.4604f, 169.5000f, (byte) 105);
        spawn(658676, 647.5353f, 432.4962f, 169.5000f, (byte) 29);
        spawn(658676, 636.3904f, 489.4637f, 169.5000f, (byte) 90);
		spawn(658676, 667.5422f, 493.4539f, 169.5000f, (byte) 89);
		//IDF8_House_BattleDev_M_Fi_80_Ae.
		spawn(658679, 632.3008f, 481.2546f, 169.5000f, (byte) 108);
		spawn(658679, 629.7439f, 483.1811f, 169.5000f, (byte) 107);
		spawn(658679, 651.7638f, 445.6856f, 169.5000f, (byte) 10);
		spawn(658679, 632.0842f, 484.4377f, 169.5000f, (byte) 100);
		spawn(658679, 633.0773f, 441.8217f, 169.5000f, (byte) 12);
		spawn(658679, 652.6893f, 481.2226f, 169.5000f, (byte) 109);
		spawn(658679, 632.3163f, 440.0254f, 169.5000f, (byte) 10);
		//IDF8_House_BattleDev_F_Fi_80_Ae.
		spawn(658680, 667.5401f, 444.3724f, 169.5000f, (byte) 21);
		spawn(658680, 630.9537f, 442.0666f, 169.5000f, (byte) 8);
		spawn(658680, 665.9076f, 443.7281f, 169.5000f, (byte) 17);
		spawn(658680, 651.2293f, 443.7338f, 169.5000f, (byte) 10);
		spawn(658680, 650.3036f, 446.1769f, 169.5000f, (byte) 7);
		spawn(658680, 667.9622f, 443.0138f, 169.5000f, (byte) 23);
		spawn(658680, 665.8298f, 481.3081f, 169.5000f, (byte) 107);
		spawn(658680, 667.1731f, 482.9460f, 169.5000f, (byte) 108);
		spawn(658680, 665.5043f, 482.9025f, 169.5000f, (byte) 108);
    }
}