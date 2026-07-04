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
package ai.instance.evergaleCanyon;

import ai.ActionItemNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.instancereward.InstanceReward;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Artifact_Core_Fragment_Asmodians")
public class Artifact_Core_Fragment_AsmodiansAI2 extends ActionItemNpcAI2
{
    private boolean isRewarded;
	protected int startBarAnimation = 1;
	protected int cancelBarAnimation = 2;
	
    @Override
    protected void handleDialogStart(Player player) {
        handleUseItemStart(player);
		InstanceReward<?> instance = getPosition().getWorldMapInstance().getInstanceHandler().getInstanceReward();
        if (instance != null && !instance.isStartProgress()) {
            return;
        }
        super.handleDialogStart(player);
    }
	
	protected void handleUseItemStart(final Player player) {
		final int delay = getTalkDelay();
		if (delay != 0) {
			final ItemUseObserver observer = new ItemUseObserver() {
				@Override
				public void abort() {
					player.getController().cancelTask(TaskId.ACTION_ITEM_NPC);
					PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.END_QUESTLOOT, 0, getObjectId()), true);
					PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getObjectId(), 0, cancelBarAnimation));
					player.getObserveController().removeObserver(this);
				}
			};
			player.getObserveController().attach(observer);
			PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getObjectId(), getTalkDelay(), startBarAnimation));
			PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_QUESTLOOT, 0, getObjectId()), true);
			player.getController().addTask(TaskId.ACTION_ITEM_NPC, ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.END_QUESTLOOT, 0, getObjectId()), true);
					PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getObjectId(), getTalkDelay(), cancelBarAnimation));
					player.getObserveController().removeObserver(observer);
					handleUseItemFinish(player);
				}
			}, delay));
		} else {
			handleUseItemFinish(player);
		}
	}
	
    @Override
    protected void handleUseItemFinish(Player player) {
        if (!isRewarded) {
            isRewarded = true;
			getOwner().getController().onDelete();
			AI2Actions.handleUseItemFinish(this, player);
			switch (getNpcId()) {
				case 835309: //Artifact Core Fragment.
					if (player.getCommonData().getRace() == Race.ELYOS) {
						announceTempleOfOriginE();
						WorldMapInstance instance1 = getPosition().getWorldMapInstance();
						deleteNpcs(instance1.getNpcs(835456));
					    spawn(835304, 744.87201f, 756.45233f, 338.42093f, (byte) 0, 6); //Temple Of Origin.
					    spawn(835455, 744.87201f, 756.45233f, 338.42093f, (byte) 0); //Temple Of Origin [Flag]
					    spawn(835238, 744.87201f, 756.45233f, 338.42093f, (byte) 0); //IDEternityWar_v03_Flag_L.
					} else if (player.getCommonData().getRace() == Race.ASMODIANS) {
						announceTempleOfOriginA();
						WorldMapInstance instance2 = getPosition().getWorldMapInstance();
						deleteNpcs(instance2.getNpcs(835455));
					    spawn(835309, 744.87201f, 756.45233f, 338.42093f, (byte) 0, 6); //Temple Of Origin.
						spawn(835456, 744.87201f, 756.45233f, 338.42093f, (byte) 0); //Temple Of Origin [Flag]
						spawn(835239, 744.87201f, 756.45233f, 338.42093f, (byte) 0); //IDEternityWar_v03_Flag_D.
					}
			    break;
				case 835310: //Artifact Core Fragment.
			        if (player.getCommonData().getRace() == Race.ELYOS) {
						announceFairyElb1();
						announceNorthernCaveE();
						WorldMapInstance instance3 = getPosition().getWorldMapInstance();
						deleteNpcs(instance3.getNpcs(835456));
						//Elb: <Canyon Fairy>
						spawn(654855, 206.65828f, 450.01440f, 295.48935f, (byte) 0);
						spawn(654855, 193.34024f, 436.78748f, 302.56314f, (byte) 0);
						spawn(654855, 219.44083f, 452.26108f, 295.20300f, (byte) 0);
						spawn(654855, 190.00842f, 448.16849f, 302.56314f, (byte) 0);
						spawn(654855, 223.94318f, 439.94461f, 302.56314f, (byte) 0);
					    spawn(835305, 212.27483f, 443.66403f, 294.31482f, (byte) 0, 11); //Northern Cave.
						spawn(835455, 212.27483f, 443.66403f, 294.31482f, (byte) 0); //Northern Cave [Flag]
						spawn(835240, 212.27483f, 443.66403f, 294.31482f, (byte) 0); //IDEternityWar_v04_Flag_L.
					} else if (player.getCommonData().getRace() == Race.ASMODIANS) {
						announceFairyElb1();
						announceNorthernCaveA();
						WorldMapInstance instance4 = getPosition().getWorldMapInstance();
						deleteNpcs(instance4.getNpcs(835455));
						//Elb: <Canyon Fairy>
						spawn(654856, 206.65828f, 450.01440f, 295.48935f, (byte) 0);
						spawn(654856, 193.34024f, 436.78748f, 302.56314f, (byte) 0);
						spawn(654856, 219.44083f, 452.26108f, 295.20300f, (byte) 0);
						spawn(654856, 190.00842f, 448.16849f, 302.56314f, (byte) 0);
						spawn(654856, 223.94318f, 439.94461f, 302.56314f, (byte) 0);
					    spawn(835310, 212.27483f, 443.66403f, 294.31482f, (byte) 0, 11); //Northern Cave.
						spawn(835456, 212.27483f, 443.66403f, 294.31482f, (byte) 0); //Northern Cave [Flag]
						spawn(835241, 212.27483f, 443.66403f, 294.31482f, (byte) 0); //IDEternityWar_v04_Flag_D.
					}
				break;
				case 835311: //Artifact Core Fragment.
			        if (player.getCommonData().getRace() == Race.ELYOS) {
						announceWallRuinsE();
						WorldMapInstance instance5 = getPosition().getWorldMapInstance();
						deleteNpcs(instance5.getNpcs(835456));
					    spawn(835306, 592.07892f, 640.55408f, 324.73904f, (byte) 0, 12); //Wall Ruins's.
						spawn(835455, 592.07892f, 640.55408f, 324.73904f, (byte) 0); //Wall Ruins's. [Flag]
						spawn(835242, 592.07892f, 640.55408f, 324.73904f, (byte) 0); //IDEternityWar_v05_Flag_L.
					} else if (player.getCommonData().getRace() == Race.ASMODIANS) {
						announceWallRuinsA();
						WorldMapInstance instance6 = getPosition().getWorldMapInstance();
						deleteNpcs(instance6.getNpcs(835455));
					    spawn(835311, 592.07892f, 640.55408f, 324.73904f, (byte) 0, 12); //Wall Ruins's.
						spawn(835456, 592.07892f, 640.55408f, 324.73904f, (byte) 0); //Wall Ruins's. [Flag]
						spawn(835243, 592.07892f, 640.55408f, 324.73904f, (byte) 0); //IDEternityWar_v05_Flag_D.
					}
				break;
				case 835312: //Artifact Core Fragment.
			        if (player.getCommonData().getRace() == Race.ELYOS) {
						announceCollapsedWallE();
						WorldMapInstance instance7 = getPosition().getWorldMapInstance();
						deleteNpcs(instance7.getNpcs(835456));
					    spawn(835307, 900.56952f, 637.95612f, 325.18738f, (byte) 0, 41); //Collapsed Wall's.
						spawn(835455, 900.56952f, 637.95612f, 325.18738f, (byte) 0); //Collapsed Wall's. [Flag]
						spawn(835244, 900.56952f, 637.95612f, 325.18738f, (byte) 0); //IDEternityWar_v06_Flag_L.
					} else if (player.getCommonData().getRace() == Race.ASMODIANS) {
						announceCollapsedWallA();
						WorldMapInstance instance8 = getPosition().getWorldMapInstance();
						deleteNpcs(instance8.getNpcs(835455));
					    spawn(835312, 900.56952f, 637.95612f, 325.18738f, (byte) 0, 41); //Collapsed Wall's.
						spawn(835456, 900.56952f, 637.95612f, 325.18738f, (byte) 0); //Collapsed Wall's. [Flag]
						spawn(835245, 900.56952f, 637.95612f, 325.18738f, (byte) 0); //IDEternityWar_v06_Flag_D.
					}
				break;
				case 835313: //Artifact Core Fragment.
			        if (player.getCommonData().getRace() == Race.ELYOS) {
						announceFairyElb2();
						announceSouthernCaveE();
						WorldMapInstance instance9 = getPosition().getWorldMapInstance();
						deleteNpcs(instance9.getNpcs(835456));
						//Elb: <Canyon Fairy>
						spawn(654857, 1246.7339f, 405.43893f, 312.49734f, (byte) 0);
						spawn(654857, 1235.1982f, 381.30444f, 312.49734f, (byte) 0);
						spawn(654857, 1240.6429f, 393.54608f, 312.49734f, (byte) 0);
						spawn(654857, 1260.6436f, 399.44736f, 312.49734f, (byte) 0);
						spawn(654857, 1258.8136f, 387.54639f, 312.49734f, (byte) 0);
						spawn(654857, 1246.9137f, 385.63171f, 312.49734f, (byte) 0);
					    spawn(835308, 1250.4025f, 396.76108f, 309.01529f, (byte) 0, 42); //Southern Cave.
						spawn(835455, 1250.4025f, 396.76108f, 309.01529f, (byte) 0); //Southern Cave [Flag]
						spawn(835246, 1250.4025f, 396.76108f, 309.01529f, (byte) 0); //IDEternityWar_v07_Flag_L.
					} else if (player.getCommonData().getRace() == Race.ASMODIANS) {
						announceFairyElb2();
						announceSouthernCaveA();
						WorldMapInstance instance10 = getPosition().getWorldMapInstance();
						deleteNpcs(instance10.getNpcs(835455));
						//Elb: <Canyon Fairy>
						spawn(654858, 1246.7339f, 405.43893f, 312.49734f, (byte) 0);
						spawn(654858, 1235.1982f, 381.30444f, 312.49734f, (byte) 0);
						spawn(654858, 1240.6429f, 393.54608f, 312.49734f, (byte) 0);
						spawn(654858, 1260.6436f, 399.44736f, 312.49734f, (byte) 0);
						spawn(654858, 1258.8136f, 387.54639f, 312.49734f, (byte) 0);
						spawn(654858, 1246.9137f, 385.63171f, 312.49734f, (byte) 0);
					    spawn(835313, 1250.4025f, 396.76108f, 309.01529f, (byte) 0, 42); //Southern Cave.
						spawn(835456, 1250.4025f, 396.76108f, 309.01529f, (byte) 0); //Southern Cave [Flag]
						spawn(835247, 1250.4025f, 396.76108f, 309.01529f, (byte) 0); //IDEternityWar_v07_Flag_L.
					}
				break;
			}
        }
    }
	
	private void announceTempleOfOriginE() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//The Elyos took possession of the fragment at the Temple of Origin.
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_IDEternity_War_pure_05);
				}
			}
		});
	}
	private void announceTempleOfOriginA() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//The Asmodians took possession of the fragment at the Temple of Origin.
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_IDEternity_War_pure_06);
				}
			}
		});
	}
	
	
	private void announceNorthernCaveE() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//The Elyos took possession of the fragment at the Northern Cave.
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_IDEternity_War_pure_07);
				}
			}
		});
	}
	private void announceNorthernCaveA() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//The Asmodians took possession of the fragment at the Northern Cave.
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_IDEternity_War_pure_08);
				}
			}
		});
	}
	private void announceFairyElb1() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//The fairy Elb is requesting help from inside the cave.
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_IDEternity_War_people_04);
				}
			}
		});
	}
	
	
	private void announceWallRuinsE() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//The Elyos took possession of the fragment at the Wall Ruins.
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_IDEternity_War_pure_01);
				}
			}
		});
	}
	private void announceWallRuinsA() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//The Asmodians took possession of the fragment at the Wall Ruins.
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_IDEternity_War_pure_02);
				}
			}
		});
	}
	
	
	private void announceCollapsedWallE() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//The Elyos took possession of the fragment at the Collapsed Wall.
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_IDEternity_War_pure_03);
				}
			}
		});
	}
	private void announceCollapsedWallA() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//The Asmodians took possession of the fragment at the Collapsed Wall.
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_IDEternity_War_pure_04);
				}
			}
		});
	}
	
	
	private void announceSouthernCaveE() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//The Elyos took possession of the fragment at the Southern Cave.
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_IDEternity_War_pure_09);
				}
			}
		});
	}
	private void announceSouthernCaveA() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//The Asmodians took possession of the fragment at the Southern Cave.
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_IDEternity_War_pure_10);
				}
			}
		});
	}
	private void announceFairyElb2() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//The fairy Elb is requesting help from inside the cave.
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_IDEternity_War_people_04);
				}
			}
		});
	}
	
	protected int getTalkDelay() {
		return getObjectTemplate().getTalkDelay() * 1000;
	}
	
	private void deleteNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			if (npc != null) {
				npc.getController().onDelete();
			}
		}
	}
	
	@Override
	public boolean isMoveSupported() {
		return false;
	}
}