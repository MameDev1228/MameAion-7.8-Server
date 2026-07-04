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
package admincommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.gameobjects.player.PlayerSweep;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DAEVANION_SKILL_ENCHANT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOGIN_EVENT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UNK_17C;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author Krz on décembre 2018
 */
public class Dev extends AdminCommand
{
    public Dev() {
        super("dev");
    }

    @Override
    public void execute(Player player, String... params) {
       //PacketSendUtility.sendPacket(player, new SM_LOGIN_EVENT());
        //RandomBonus bonus = DataManager.ITEM_RANDOM_BONUSES.getRndBonusById(1119);
        //RandomAttr attr1 = bonus.getRandomAttr().(Rnd.get(1, 9));
        //RandomAttr attr1 = bonus.getRandomAttr().get(Rnd.get(0, bonus.getRandomAttr().size() - 1));
        //for(RandomAttr rnd : bonus.getRandomAttr()) {

            //player.sendMessage("Attr stat Min : " + attr1.getMinValue() + " Max : " + attr1.getMaxValue() + " stat name : " + attr1.getStatName());
        //}

        PacketSendUtility.sendPacket(player, new SM_DAEVANION_SKILL_ENCHANT(2, 5129, 1));

        PacketSendUtility.sendPacket(player, new SM_UNK_17C());

    }

    @Override
    public void onFail(Player player, String message) {
    }

    private void sendPacket(Player player, final String variable, final int floor) {
    }

    public PlayerCommonData getCommonData(Player player){
        return player.getCommonData();
    }

    public PlayerSweep getPlayerSweep(Player player){
        return player.getPlayerShugoSweep();
    }
}
