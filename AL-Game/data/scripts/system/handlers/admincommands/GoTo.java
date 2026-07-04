package admincommands;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMap;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.WorldMapType;

public class GoTo extends AdminCommand
{
	public GoTo() {
		super("goto");
	}
	
	@Override
	public void execute(Player player, String... params) {
		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(player, "syntax //goto <location>");
			return;
		}
		StringBuilder sbDestination = new StringBuilder();
		for(String p: params) {
			sbDestination.append(p + " ");
		}
		String destination = sbDestination.toString().trim();
		
		if (destination.equalsIgnoreCase("Sanctum"))
			goTo(player, WorldMapType.SANCTUM.getId(), 1322, 1511, 568);
		else if (destination.equalsIgnoreCase("Cloister"))
			goTo(player, WorldMapType.CLOISTER_OF_KAISINEL.getId(), 2155, 1567, 1205);
		else if (destination.equalsIgnoreCase("Poeta"))
			goTo(player, WorldMapType.POETA.getId(), 829, 1231, 118);
		else if (destination.equalsIgnoreCase("Heiron"))
			goTo(player, WorldMapType.HEIRON.getId(), 2540, 343, 411);
		else if  (destination.equalsIgnoreCase("Pandaemonium"))
			goTo(player, WorldMapType.PANDAEMONIUM.getId(), 1679, 1400, 195);
		else if (destination.equalsIgnoreCase("Convent"))
			goTo(player, WorldMapType.CONVENT_OF_MARCHUTAN.getId(), 1557, 1429, 266);
		else if (destination.equalsIgnoreCase("Ishalgen"))
			goTo(player, WorldMapType.ISHALGEN.getId(), 579, 2445, 279);
		else if (destination.equalsIgnoreCase("Beluslan"))
			goTo(player, WorldMapType.BELUSLAN.getId(), 398, 400, 222);
		else if (destination.equalsIgnoreCase("academy"))
			goTo(player, 110070000, 459, 251, 128);
		else if (destination.equalsIgnoreCase("priory"))
			goTo(player, 120080000, 577, 250, 94);
		
		//INSTANCE
		else if (destination.equalsIgnoreCase("lower") || destination.equalsIgnoreCase("Lower Udas Temple"))
			goTo(player, 300160000, 1325, 780, 112);
		else if (destination.equalsIgnoreCase("taloc") || destination.equalsIgnoreCase("Taloc's Hollow"))
			goTo(player, 300190000, 200, 214, 1099);
		else if (destination.equalsIgnoreCase("haramel") || destination.equalsIgnoreCase("Haramel"))
			goTo(player, 300200000, 176, 21, 144);
		else if (destination.equalsIgnoreCase("kromede") || destination.equalsIgnoreCase("Kromede Trial"))
			goTo(player, 300230000, 248, 244, 189);
		else if (destination.equalsIgnoreCase("esoterrace") || destination.equalsIgnoreCase("Esoterrace"))
			goTo(player, 300250000, 333, 437, 326);
		else if (destination.equalsIgnoreCase("discipline") || destination.equalsIgnoreCase("Arena Of Discipline")) //2.x
			goTo(player, 300360000, 707, 1779, 165);
		else if (destination.equalsIgnoreCase("water2") || destination.equalsIgnoreCase("Steel Rake Fortress [PVP]")) //7.x
			goTo(player, 300370000, 324, 348, 148);
		else if (destination.equalsIgnoreCase("harmony") || destination.equalsIgnoreCase("Arena Of Harmony")) //3.x
			goTo(player, 300450000, 500, 371, 211);
		else if (destination.equalsIgnoreCase("mysticarium") || destination.equalsIgnoreCase("Unstable Danuar Mysticarium")) //7.x
			goTo(player, 300480000, 179, 122, 231);
			
		else if (destination.equalsIgnoreCase("rudra1") || destination.equalsIgnoreCase("IDCatacombs_Rudra")) //7.x
			goTo(player, 300910000, 1320, 707, 243);
		else if (destination.equalsIgnoreCase("rudra2") || destination.equalsIgnoreCase("IDCatacombs_Rudra_E")) //7.x
			goTo(player, 300920000, 1320, 707, 243);
			
		//2nd zone: 1326 1240 310
		//final boss: 835 1292 223
		
		
		else if (destination.equalsIgnoreCase("kamar") || destination.equalsIgnoreCase("Kamar Battlefield")) //7.x
			goTo(player, 301120000, 1374, 1455, 600);
		else if (destination.equalsIgnoreCase("dome") || destination.equalsIgnoreCase("Idgel Dome")) //4.x
			goTo(player, 301310000, 261, 262, 85);
		else if (destination.equalsIgnoreCase("drakenspire") || destination.equalsIgnoreCase("Drakenspire Depths")) //6.x
			goTo(player, 301390000, 340, 182, 1684);
		else if (destination.equalsIgnoreCase("vault") || destination.equalsIgnoreCase("The Shugo Emperor's Vault")) //4.7.x
			goTo(player, 301400000, 543, 294, 400);
		else if (destination.equalsIgnoreCase("underpath") || destination.equalsIgnoreCase("Contaminated Underpath")) //5.x
			goTo(player, 301630000, 230, 169, 164);
		else if (destination.equalsIgnoreCase("hellpath") || destination.equalsIgnoreCase("Secret Hellpath")) //7.x
			goTo(player, 301631000, 230, 169, 164);
		else if (destination.equalsIgnoreCase("underpath3") || destination.equalsIgnoreCase("Perilous Contaminated Underpath")) //5.x
			goTo(player, 301632000, 230, 169, 164);
		else if (destination.equalsIgnoreCase("factory") || destination.equalsIgnoreCase("Secret Munitions Factory")) //5.x
			goTo(player, 301640000, 407, 292, 198);
		else if (destination.equalsIgnoreCase("ashunatal") || destination.equalsIgnoreCase("Ashunatal Dredgion")) //5.x
			goTo(player, 301650000, 399, 169, 432);
		else if (destination.equalsIgnoreCase("farm") || destination.equalsIgnoreCase("Animal Farm Race")) //7.x
			goTo(player, 301700000, 1371, 1380, 375);
		else if (destination.equalsIgnoreCase("wicked") || destination.equalsIgnoreCase("Wicked Gracheni's Vault")) //7.x
			goTo(player, 301750000, 543, 294, 400);
		else if (destination.equalsIgnoreCase("evergale") || destination.equalsIgnoreCase("Evergale Canyon")) { //5.x
			if (player.getCommonData().getRace() == Race.ELYOS) {
				goTo(player, 302350000, 402, 751, 336);
			} else {
				goTo(player, 302350000, 1094, 752, 336);
			}
		}
		else if (destination.equalsIgnoreCase("primeth") || destination.equalsIgnoreCase("Primeth's Forge [Normal]")) //6.x
			goTo(player, 302430000, 467, 805, 811);
		else if (destination.equalsIgnoreCase("corridor") || destination.equalsIgnoreCase("Silentera Corridor")) //6.x
			goTo(player, 302440000, 506, 411, 327);
		else if (destination.equalsIgnoreCase("cubic") || destination.equalsIgnoreCase("Qubrinerk's Cubic Lab")) //6.x
			goTo(player, 302460000, 407, 292, 198);
		else if (destination.equalsIgnoreCase("mine") || destination.equalsIgnoreCase("Herelym Mine")) //6.x
			goTo(player, 302500000, 862, 530, 295);
		else if (destination.equalsIgnoreCase("mine1") || destination.equalsIgnoreCase("Herelym Mine Way 1")) //6.x
			goTo(player, 302500000, 640, 270, 295);
		else if (destination.equalsIgnoreCase("mine2") || destination.equalsIgnoreCase("Herelym Mine Way 2")) //6.x
			goTo(player, 302500000, 555, 683, 293);
		else if (destination.equalsIgnoreCase("mine3") || destination.equalsIgnoreCase("Herelym Mine Way 3")) //6.x
			goTo(player, 302500000, 674, 766, 294);
		else if (destination.equalsIgnoreCase("senekta") || destination.equalsIgnoreCase("The Veilenthrone")) //6.x
			goTo(player, 302510000, 862, 549, 150);
		else if (destination.equalsIgnoreCase("water") || destination.equalsIgnoreCase("Steel Rake Fortress")) //6.x
			goTo(player, 302520000, 950, 892, 120);
		else if (destination.equalsIgnoreCase("illumiel") || destination.equalsIgnoreCase("Illumiel Brawl")) //6.x
			goTo(player, 302530000, 278, 274, 92);
		else if (destination.equalsIgnoreCase("solo2") || destination.equalsIgnoreCase("Kumuki Cave [With Made In Abyss]")) //7.x
			goTo(player, 302540000, 176, 21, 144);
		else if (destination.equalsIgnoreCase("lab1") || destination.equalsIgnoreCase("Stellin Development Lab")) //7.x
			goTo(player, 302550000, 488, 251, 377);
		else if (destination.equalsIgnoreCase("mission1") || destination.equalsIgnoreCase("Regatus Headquarters")) //7.x
			goTo(player, 302560000, 217, 175, 229);
		else if (destination.equalsIgnoreCase("mission2") || destination.equalsIgnoreCase("Vishaka's Hideout")) //7.x
			goTo(player, 302570000, 940, 1533, 717);
		else if (destination.equalsIgnoreCase("mission3") || destination.equalsIgnoreCase("Secret Research Center")) //7.x
			goTo(player, 302580000, 84, 375, 309);
		else if (destination.equalsIgnoreCase("hall") || destination.equalsIgnoreCase("Hall Of Tenacity")) //7.x
			goTo(player, 302600000, 278, 256, 236);
		else if (destination.equalsIgnoreCase("lab2") || destination.equalsIgnoreCase("Stellin Development Lab [Hard]")) //7.x
			goTo(player, 302610000, 488, 251, 377);
		else if (destination.equalsIgnoreCase("lab3") || destination.equalsIgnoreCase("Stellin Development Lab [Easy]")) //7.x
			goTo(player, 302620000, 488, 251, 377);
		else if (destination.equalsIgnoreCase("primeth2") || destination.equalsIgnoreCase("Primeth's Forge [Hard]")) //7.x
			goTo(player, 302630000, 467, 805, 811);
		else if (destination.equalsIgnoreCase("minium") || destination.equalsIgnoreCase("Minium Vault")) //7.x
			goTo(player, 302641000, 528, 121, 176);
		else if (destination.equalsIgnoreCase("minium2") || destination.equalsIgnoreCase("Minium Vault Of Opportunity")) //7.x
			goTo(player, 302642000, 528, 121, 176);
		else if (destination.equalsIgnoreCase("legion") || destination.equalsIgnoreCase("Legion Banquet Hall")) //7.x
			goTo(player, 302650000, 252, 240, 206);
		else if (destination.equalsIgnoreCase("cellar") || destination.equalsIgnoreCase("The Red Cellar")) //7.x
			goTo(player, 302651000, 195, 256, 223);
		else if (destination.equalsIgnoreCase("estate1") || destination.equalsIgnoreCase("Benirunerk's Estate")) //7.x
			goTo(player, 302660000, 628, 461, 169);
		else if (destination.equalsIgnoreCase("scaleshadow") || destination.equalsIgnoreCase("Scaleshadow")) //7.x
			goTo(player, 302670000, 586, 664, 354);
		else if (destination.equalsIgnoreCase("spire") || destination.equalsIgnoreCase("Crucible Spire [Lower Level]")) //7.x
			goTo(player, 302680000, 223, 249, 241);
		else if (destination.equalsIgnoreCase("estate2") || destination.equalsIgnoreCase("Benirunerk's Estate [Easy]")) //7.x
			goTo(player, 302690000, 628, 461, 169);
		else if (destination.equalsIgnoreCase("drakenspire2") || destination.equalsIgnoreCase("Drakenspire Depths [Hard]")) //7.x
			goTo(player, 302700000, 340, 182, 1684);
		else if (destination.equalsIgnoreCase("shattered") || destination.equalsIgnoreCase("Shattered Abyssal Splinter")) //7.x
			goTo(player, 302710000, 204, 120, 197);
		else if (destination.equalsIgnoreCase("vale") || destination.equalsIgnoreCase("Chaotic Vale")) //7.x
			goTo(player, 302730000, 439, 433, 642);
		else if (destination.equalsIgnoreCase("vale2") || destination.equalsIgnoreCase("Chaotic Vale [Hard]")) //7.x
			goTo(player, 302740000, 439, 433, 642);
		else if (destination.equalsIgnoreCase("underpass") || destination.equalsIgnoreCase("IDUnderpass_B1_Q")) //7.x
			goTo(player, 302750000, 215, 256, 193);
		else if (destination.equalsIgnoreCase("altar1") || destination.equalsIgnoreCase("Altar Of Ascension")) //7.x
			goTo(player, 302810000, 2903, 2236, 723);
		else if (destination.equalsIgnoreCase("altar2") || destination.equalsIgnoreCase("Altar Of Ascension [Easy]")) //7.x
			goTo(player, 302820000, 2903, 2236, 723);
		else if (destination.equalsIgnoreCase("altar3") || destination.equalsIgnoreCase("Altar Of Ascension Of Opportunity")) //7.x
			goTo(player, 302830000, 2903, 2236, 723);
		else if (destination.equalsIgnoreCase("esoterrace2") || destination.equalsIgnoreCase("Esoterrace Of Opportunity")) //7.x
			goTo(player, 302850000, 333, 437, 326);
		else if (destination.equalsIgnoreCase("karamatis") || destination.equalsIgnoreCase("Karamatis"))
			goTo(player, 310010000, 221, 250, 206);
		else if (destination.equalsIgnoreCase("genetic") || destination.equalsIgnoreCase("Aetherogenetics Lab"))
			goTo(player, 310050000, 381, 230, 156);
		else if (destination.equalsIgnoreCase("sliver") || destination.equalsIgnoreCase("Sliver Of Darkness"))
			goTo(player, 310070000, 247, 249, 1392);
		else if (destination.equalsIgnoreCase("indratu") || destination.equalsIgnoreCase("Indratu Fortress"))
			goTo(player, 310090000, 562, 335, 1015);
		else if (destination.equalsIgnoreCase("ataxiar") || destination.equalsIgnoreCase("Ataxiar"))
			goTo(player, 320020000, 221, 250, 206);
		else if (destination.equalsIgnoreCase("space") || destination.equalsIgnoreCase("Space Of Destiny"))
			goTo(player, 320070000, 246, 246, 125);
		else if (destination.equalsIgnoreCase("firetemple") || destination.equalsIgnoreCase("Fire Temple"))
			goTo(player, 320100000, 148, 461, 141);
		else if (destination.equalsIgnoreCase("alquimia") || destination.equalsIgnoreCase("Alquimia Research Center"))
			goTo(player, 320110000, 603, 527, 200);
		else if (destination.equalsIgnoreCase("bakarma") || destination.equalsIgnoreCase("Bakarma Fortress")) //6.x
			goTo(player, 320170000, 562, 335, 1015);
		else if (destination.equalsIgnoreCase("genesis") || destination.equalsIgnoreCase("Genesis Arena")) //7.x
			goTo(player, 320180000, 93, 721, 52);
		
		//**Prison**//
		else if (destination.equalsIgnoreCase("prisone") || destination.equalsIgnoreCase("Prison Elyos"))
			goTo(player, 510010000, 256, 256, 49);
		else if (destination.equalsIgnoreCase("prisona") || destination.equalsIgnoreCase("Prison Asmos"))
			goTo(player, 520010000, 256, 256, 49);
		
		//**Zones Race**//
		else if (destination.equalsIgnoreCase("inggison"))
			goTo(player, 210050000, 1335, 276, 590);
		else if (destination.equalsIgnoreCase("gelkmaros"))
			goTo(player, 220070000, 1763, 2911, 554);
		else if (destination.equalsIgnoreCase("danaria")) //7.x
			goTo(player, 800040000, 2544, 1702, 141);
		else if (destination.equalsIgnoreCase("demaha")) //7.x
			goTo(player, 800060000, 900, 1630, 713);
		else if (destination.equalsIgnoreCase("silentera2")) //7.x
			goTo(player, 800070000, 639, 892, 358);
		else if (destination.equalsIgnoreCase("spacegap")) //7.x
			goTo(player, 610010000, 255, 255, 299);
		else if (destination.equalsIgnoreCase("secret")) //7.x
			goTo(player, 600081000, 630, 1080, 432);
		else if (destination.equalsIgnoreCase("hangout")) //7.x
			goTo(player, 800010000, 351, 295, 54);
		else if (destination.equalsIgnoreCase("oriel"))
			goTo(player, 700010000, 1261, 1845, 98);
		else if (destination.equalsIgnoreCase("pernon"))
			goTo(player, 710010000, 1069, 1539, 98);
		
		//**Zones Both Race**//
		if (destination.equalsIgnoreCase("core")) {
			if (player.getCommonData().getRace() == Race.ELYOS) {
				goTo(player, 400070000, 1521, 1148, 2362);
			} else {
				goTo(player, 400070000, 1550, 1943, 2367);
			}
		} if (destination.equalsIgnoreCase("silentera")) {
			if (player.getCommonData().getRace() == Race.ELYOS) {
				goTo(player, 600010000, 504, 410, 327);
			} else {
				goTo(player, 600010000, 500, 1139, 332);
			}
		} if (destination.equalsIgnoreCase("eye")) {
			if (player.getCommonData().getRace() == Race.ELYOS) {
				goTo(player, 600040000, 754, 1340, 1201);
			} else {
				goTo(player, 600040000, 754, 196, 1201);
			}
		} if (destination.equalsIgnoreCase("kaldor")) {
			if (player.getCommonData().getRace() == Race.ELYOS) {
				goTo(player, 800020000, 1267, 1343, 194);
			} else {
				goTo(player, 800020000, 397, 1375, 164);
			}
		} if (destination.equalsIgnoreCase("katalam")) {
			if (player.getCommonData().getRace() == Race.ELYOS) {
				goTo(player, 800030000, 349, 2695, 143);
			} else {
				goTo(player, 800030000, 361, 383, 281);
			}
		} if (destination.equalsIgnoreCase("lakrum")) {
			if (player.getCommonData().getRace() == Race.ELYOS) {
				goTo(player, 800050000, 2670, 473, 323);
			} else {
				goTo(player, 800050000, 2916, 2484, 313);
			}
		}
		//Altar 7.x
        else if (destination.equalsIgnoreCase("5021"))
            goTo(player, 800060000, 578, 698, 723);
        else if (destination.equalsIgnoreCase("5022"))
            goTo(player, 800060000, 602, 2596, 641);
		else if (destination.equalsIgnoreCase("5023"))
            goTo(player, 800060000, 1477, 1345, 608);
		else if (destination.equalsIgnoreCase("5024"))
            goTo(player, 800060000, 2321, 1107, 752);
		else if (destination.equalsIgnoreCase("5025"))
            goTo(player, 800060000, 257, 1627, 756);
		else if (destination.equalsIgnoreCase("5026"))
            goTo(player, 800060000, 2464, 242, 648);
		else if (destination.equalsIgnoreCase("5027"))
            goTo(player, 800060000, 2814, 2318, 711);
		else if (destination.equalsIgnoreCase("5028"))
            goTo(player, 800060000, 1692, 2810, 631);
		else if (destination.equalsIgnoreCase("5029"))
            goTo(player, 800060000, 1473, 794, 565);
		else if (destination.equalsIgnoreCase("5030"))
            goTo(player, 800060000, 2180, 1933, 737);
		else if (destination.equalsIgnoreCase("5031"))
            goTo(player, 800060000, 1334, 2492, 560);
		else if (destination.equalsIgnoreCase("5032"))
            goTo(player, 800060000, 2748, 832, 841);
		//Siege 7.x
        else if (destination.equalsIgnoreCase("1011"))
            goTo(player, 400070000, 1526, 1390, 2313);
        else if (destination.equalsIgnoreCase("2011"))
            goTo(player, 210050000, 1747, 2174, 335);
        else if (destination.equalsIgnoreCase("2021"))
            goTo(player, 210050000, 859, 1918, 348);
		else if (destination.equalsIgnoreCase("3011"))
            goTo(player, 220070000, 1196, 869, 321);
        else if (destination.equalsIgnoreCase("3021"))
            goTo(player, 220070000, 1879, 1107, 337);
        else if (destination.equalsIgnoreCase("6011"))
            goTo(player, 800040000, 1603, 934, 53);
        else if (destination.equalsIgnoreCase("6021"))
            goTo(player, 800040000, 2560, 2564, 254);
		else
			PacketSendUtility.sendMessage(player, "Could not find the specified destination !");
	}
	
	private static void goTo(final Player player, int worldId, float x, float y, float z) {
		WorldMap destinationMap = World.getInstance().getWorldMap(worldId);
		if (destinationMap.isInstanceType()) {
			TeleportService2.teleportTo(player, worldId, getInstanceId(worldId, player), x, y, z);
		} else {
			TeleportService2.teleportTo(player, worldId, x, y, z);
		}
	}
	
	private static int getInstanceId(int worldId, Player player) {
		if (player.getWorldId() == worldId)	{
			WorldMapInstance registeredInstance = InstanceService.getRegisteredInstance(worldId, player.getObjectId());
			if (registeredInstance != null) {
				return registeredInstance.getInstanceId();
			}
		}
		WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(worldId);
		InstanceService.registerPlayerWithInstance(newInstance, player);
		return newInstance.getInstanceId();
	}
	
	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntax : //goto <location>");
	}
}