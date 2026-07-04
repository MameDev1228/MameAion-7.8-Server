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
package ai;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AttackIntention;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.handler.*;
import com.aionemu.gameserver.ai2.manager.SkillAttackManager;
import com.aionemu.gameserver.ai2.poll.AIAnswer;
import com.aionemu.gameserver.ai2.poll.AIAnswers;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.model.skill.NpcSkillEntry;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.concurrent.Future;

@AIName("aggressive")
public class AggressiveNpcAI2 extends GeneralNpcAI2
{
	private Future<?> sendPacketTask;
	
	private void cancelTask() {
        if (sendPacketTask != null && !sendPacketTask.isCancelled()) {
            sendPacketTask.cancel(true);
        }
    }
	
	@Override
	public void think() {
		ThinkEventHandler.onThink(this);
	}
	
	@Override
	protected void handleDied() {
		DiedEventHandler.onDie(this);
	}
	
	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		switch (getNpcId()) {
			//Evergale Canyon 7.x
			case 654840:
			case 654841:
			case 654842:
			case 654843:
			//The Veilenthrone 6.x
			case 656410:
			case 656411:
			case 656412:
			case 656413:
			//Illumiel Brawl 6.x
			case 656658:
			case 656659:
			//Poeta 7.x
			case 651878:
			//Ishalgen 7.x
			case 651806:
			//Heiron 7.x
			case 212008:
			//Silentera Canyon 7.x
			case 858870:
			case 858871:
			case 858872:
			//Lakrum 7.x
			case 655120:
			case 655121:
			case 655122:
			case 655123:
			case 655124:
			//Demaha 7.x
			case 658554:
			case 858116:
			case 858117:
			case 858118:
			case 858119:
			case 858120:
			case 858121:
			case 858122:
			case 858124:
			    World.getInstance().doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(final Player player) {
						sendPacketTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
							@Override
							public void run() {
								if (player.getWorldId() == getOwner().getWorldId()) {
									PacketSendUtility.sendPacket(player, new SM_FLAG_UPDATE(getOwner()));
									getOwner().getController().onDelete();
								}
							}
						}, 1000, 2000);
					}
				});
			break;
		}
	}
	
	@Override
	protected void handleAttack(Creature creature) {
		AttackEventHandler.onAttack(this, creature);
	}
	
	@Override
    protected void handleCreatureSee(Creature creature) {
        CreatureEventHandler.onCreatureSee(this, creature);
    }
	
    @Override
    protected void handleCreatureMoved(Creature creature) {
        CreatureEventHandler.onCreatureMoved(this, creature);
    }
	
	@Override
	protected void handleCreatureAggro(Creature creature) {
		if (canThink()) {
		    AggroEventHandler.onAggro(this, creature);
		}
	}
	
	@Override
	protected void handleFinishAttack() {
		AttackEventHandler.onFinishAttack(this);
	}
	
	@Override
	protected void handleAttackComplete() {
		AttackEventHandler.onAttackComplete(this);
	}
	
	@Override
    protected void handleTargetGiveup() {
        TargetEventHandler.onTargetGiveup(this);
    }
	
    @Override
    protected void handleTargetChanged(Creature creature) {
        TargetEventHandler.onTargetChange(this, creature);
    }
	
	@Override
	protected boolean handleGuardAgainstAttacker(Creature attacker) {
		return AggroEventHandler.onGuardAgainstAttacker(this, attacker);
	}
	
	@Override
	protected boolean handleCreatureNeedsSupport(Creature creature) {
		return AggroEventHandler.onCreatureNeedsSupport(this, creature);
	}
	
	protected void creatureNeedHelp(int distance) {
		Creature firstTarget = getAggroList().getMostHated();
		for (VisibleObject object : getKnownList().getKnownObjects().values()) {
			if (object instanceof Npc && isInRange(object, distance)) {
				Npc npc = (Npc) object;
				if ((npc != null) && !npc.getLifeStats().isAlreadyDead()) {
					npc.getAi2().onCreatureEvent(AIEventType.CREATURE_AGGRO, firstTarget);
				}
			}
		}
	}
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		switch (getNpcId()) {
			//Lakrum.
			case 655758:
			case 655759:
			case 655760:
			case 655761:
			case 655762:
			case 655763:
			case 655765:
			case 655767:
			case 655768:
			case 655769:
			case 655770:
		    case 655771:
			case 655772:
			case 655773:
			case 655774:
			case 655775:
			case 655777:
			case 655779:
			case 655780:
			case 655781:
			case 655782:
			case 655783:
			case 655784:
			case 655787:
			case 655789:
			case 655790:
			case 655791:
			case 655792:
			case 655795:
			case 655797:
			case 656093:
			case 656094:
			case 656095:
			case 656096:
			case 656098:
			case 656099:
			case 656100:
			case 656101:
			//Crimson Katalam 7.x.
			case 209560:
			case 209561:
			case 209563:
			case 209565:
			case 209566:
			case 209568:
			case 209570:
			case 209571:
			case 209574:
			case 209575:
			case 209577:
			case 209579:
			case 209580:
			case 209582:
			case 209584:
			case 209585:
			case 209591:
			case 209593:
			case 209594:
			case 209596:
			case 209622:
			case 209623:
			case 209625:
			case 209626:
			case 209630:
			case 209631:
			case 209633:
			case 209634:
			case 801313:
			case 801314:
			case 658882:
			case 658883:
			case 658884:
			case 658885:
			case 658886:
			case 658887:
			case 887228:
			case 887233:
			case 887243:
			case 887248:
			case 887258:
			case 887263:
			case 887273:
			case 887278:
			case 887288:
			case 887293:
			case 887426:
			case 887427:
			case 887428:
			case 887429:
			case 887430:
			case 887431:
			case 887432:
			case 887433:
			case 887434:
			case 887438:
			case 887439:
			case 887440:
			case 887441:
			case 887442:
			case 887443:
			case 887444:
			case 887445:
			case 887450:
			case 887451:
			case 887452:
			case 887453:
			case 887454:
			case 887455:
			case 887456:
			case 887457:
			case 887462:
			case 887463:
			case 887464:
			case 887465:
			case 887466:
			case 887467:
			case 887468:
			case 887469:
			case 887474:
			case 887475:
			case 887476:
			case 887477:
			case 887478:
			case 887479:
			case 887480:
			case 887481:
			//Crimson Danaria [81th Base]
			case 661447:
			case 661448:
			case 661449:
			case 661450:
			case 661451:
			case 661452:
			case 661453:
			case 661454:
			case 661211:
			case 661212:
			case 661213:
			case 661214:
			case 661215:
			case 661216:
			case 661378:
			case 661379:
			case 661380:
			case 661381:
			case 661382:
			case 661383:
			case 661384:
			case 661385:
			//Heiron PVP Guard 7.x
			case 204649:
			case 207568:
			case 207571:
			case 207572:
			case 207574:
			case 207575:
			case 207577:
			case 207578:
			case 207580:
			case 207581:
			case 207661:
			case 207744:
			case 207747:
			case 207751:
			case 207754:
			case 207755:
			case 207758:
			case 207761:
			case 207765:
			case 207768:
			case 207769:
			case 207772:
			case 207775:
			case 207845:
			case 207847:
			case 207856:
			case 207946:
			case 207947:
			case 209492:
			case 209493:
			case 209494:
			case 799624:
			case 799625:
			case 653021:
			case 653022:
			//Beluslan PVP Guard 7.x
			case 207598:
			case 207599:
			case 207601:
			case 207602:
			case 207604:
			case 207608:
			case 207610:
			case 207611:
			case 207778:
			case 207779:
			case 207782:
			case 207786:
			case 207788:
			case 207789:
			case 207790:
			case 207793:
			case 207800:
			case 207803:
			case 207804:
			case 207806:
			case 207807:
			case 207810:
			case 207871:
			case 209996:
			case 209997:
			case 209998:
			case 209999:
			case 833713:
			//Inggison PVP Guard 7.x
			case 209018:
			case 209024:
			case 209051:
			case 209052:
			case 209053:
			case 209054:
			case 209055:
			case 209056:
			case 209057:
			case 209059:
			case 209061:
			case 209065:
			case 209066:
			case 209067:
			case 209071:
			case 209074:
			case 209100:
			case 209101:
			case 209119:
			case 209124:
			case 209125:
			case 209131:
			case 209132:
			case 209142:
			case 209144:
			case 209145:
			case 209146:
			case 209150:
			case 209151:
			case 209153:
			case 840131:
			case 840132:
			case 840133:
			case 840134:
			case 840136:
			case 840137:
			case 840139:
			case 840140:
			case 840141:
			case 840142:
			case 840146:
			case 840147:
			case 840162:
			case 840164:
			case 840166:
			case 840168:
			//Inggison Guard Quest 7.x
			case 839965:
			case 839966:
			case 839967:
			case 839968:
			case 839969:
			//Inggison Gateway 7.x
			case 840060:
			case 840061:
			case 840062:
			case 840064:
			case 840067:
			case 840068:
			case 840069:
			case 840071:
			case 840074:
			case 840075:
			case 840076:
			case 840078:
			case 840081:
			case 840082:
			case 840083:
			case 840085:
			case 840088:
			case 840089:
			case 840090:
			case 840092:
			//Gelkmaros PVP Guard 7.x
			case 209251:
			case 209254:
			case 209255:
			case 209256:
			case 209260:
			case 209261:
			case 209262:
			case 209265:
			case 209266:
			case 209268:
			case 209271:
			case 209272:
			case 209301:
			case 209313:
			case 209324:
			case 209332:
			case 209344:
			case 209350:
			case 209351:
			case 209353:
			case 840212:
			case 840213:
			case 840214:
			case 840217:
			case 840218:
			case 840219:
			case 840220:
			case 840233:
			case 840234:
			case 840237:
			case 840239:
			//Gelkmaros Guard Quest 7.x
			case 839978:
			case 839979:
			case 839980:
			case 839981:
			case 839982:
			//Gelkmaros Gateway 7.x
			case 840095:
			case 840096:
			case 840097:
			case 840099:
			case 840102:
			case 840103:
		    case 840104:
			case 840106:
			case 840109:
			case 840110:
			case 840111:
			case 840113:
			case 840116:
			case 840117:
			case 840118:
			case 840120:
			case 840123:
			case 840124:
			case 840125:
			case 840127:
			//Silentera Canyon 7.x
			case 799385:
			case 799386:
			case 799387:
			case 799388:
			case 799389:
			case 799390:
			case 799391:
			case 799392:
			case 799393:
			case 799394:
			case 799395:
			case 799396:
			case 799397:
			case 799398:
			case 799399:
			case 799400:
			//Divine Fortress 7.x
			case 884968:
			case 885061:
			case 885070:
			case 885071:
			case 885076:
			case 884969:
			case 885081:
			case 885090:
			case 885091:
			case 885096:
			//Temple Of Scale 7.x
			case 257057:
			case 257060:
			case 257063:
			case 257066:
			case 257069:
			case 257058:
			case 257061:
			case 257064:
			case 257067:
			case 257070:
			//Artifact 2012
			case 257102:
			case 257132:
			case 257135:
			case 257138:
			case 257141:
			case 257144:
			case 257107:
			case 257133:
			case 257136:
			case 257139:
			case 257142:
			case 257145:
			//Artifact 2013
			case 257147:
			case 257177:
			case 257180:
			case 257183:
			case 257186:
			case 257189:
			case 257152:
			case 257178:
			case 257181:
			case 257184:
			case 257187:
			case 257190:
			//Artifact 2014
			case 252464:
			case 256695:
			case 256698:
			case 256701:
			case 256704:
			case 252469:
			case 256696:
			case 256699:
			case 256702:
			case 256705:
			//Altar Of Avarice 7.x
			case 257357:
			case 257360:
			case 257363:
			case 257366:
			case 257369:
			case 257358:
			case 257361:
			case 257364:
			case 257367:
			case 257370:
			//Vorgaltem Citadel 7.x
			case 257657:
			case 257660:
			case 257663:
			case 257666:
			case 257669:
			case 257658:
			case 257661:
			case 257664:
			case 257667:
			case 257670:
			//Artifact 3012
			case 257702:
			case 257732:
			case 257735:
			case 257738:
			case 257741:
			case 257744:
			case 257707:
			case 257733:
			case 257736:
			case 257739:
			case 257742:
			case 257745:
			//Artifact 3013
			case 257747:
			case 257777:
			case 257780:
			case 257783:
			case 257786:
			case 257789:
			case 257752:
			case 257778:
			case 257781:
			case 257784:
			case 257787:
			case 257790:
			//Artifact 3014
			case 252434:
			case 256719:
			case 256722:
			case 252725:
			case 256728:
			case 252439:
			case 256720:
			case 256723:
			case 256726:
			case 256729:
			//Crimsom Temple 7.x
			case 257957:
			case 257960:
			case 257963:
			case 257966:
			case 257969:
			case 257958:
			case 257961:
			case 257964:
			case 257967:
			case 257970:
			//Silona Fortress 7.x
			case 272515:
			case 272520:
			case 272525:
			case 272530:
			case 272535:
			case 272540:
			case 272545:
			case 272550:
			case 272771:
			case 272595:
			case 272600:
			case 272605:
			case 272610:
			case 272615:
			case 272620:
			case 272625:
			case 272630:
			case 272776:
			//Pradeth Fortress 7.x
			case 273015:
			case 273020:
			case 273030:
			case 273035:
			case 273040:
			case 273045:
			case 273095:
			case 273100:
			case 273110:
			case 273115:
			case 273120:
			case 273125:
			    conquerorPassion();
			break;
		} switch (getNpcId()) {
			case 653764:
			case 653771:
			case 653773:
			case 653774:
			case 653775:
			case 653799:
			case 653885:
			case 653886:
			case 653961:
			case 653962:
			case 653963:
			case 653966:
		        typeA();
			break;
		} switch (getNpcId()) {
			case 653762:
			case 653802:
			case 653808:
			case 653964:
		        typeB();
			break;
		} switch (getNpcId()) {
			case 653784:
			case 653795:
			case 653798:
		        typeC();
			break;
		} switch (getNpcId()) {
			case 653763:
			case 653766:
			case 653770:
			case 653807:
		        typeD();
			break;
		} switch (getNpcId()) {
			case 653197:
			case 653198:
			case 653199:
			case 653200:
			case 653201:
			case 653202:
			case 653205:
			case 653206:
			case 653207:
			case 653208:
			case 653209:
			case 653216:
			case 653217:
			case 653221:
		        mindControl();
			break;
		}
		//Herelym Mine 6.x
		switch (getNpcId()) {
			case 656314:
			case 656315:
			case 656316:
			case 656317:
			case 656334:
			case 656335:
			case 656336:
		        soulPrison();
			break;
		}
		//Idgel Dome-Illumiel Brawl 6.x
		switch (getNpcId()) {
			case 654750:
			case 656825:
			case 656826:
			case 656827:
			case 656828:
			    ironScale();
			break;
		} switch (getNpcId()) {
			//Heiron 7.x
			case 653142:
			//Beluslan 7.x
			case 652465:
			//Crismon Katalam 7.x
			case 230297:
			case 230298:
			case 230299:
			case 230300:
			case 231200:
			case 231202:
			case 231203:
			case 231204:
			case 231205:
			case 231211:
			case 231212:
			case 231213:
			case 231220:
			case 231221:
			case 231222:
			case 231223:
			case 231238:
			case 231239:
			case 231240:
			case 231241:
			case 231242:
			case 231243:
			//Crimson Katalam [Base] 7.x
			case 658738:
			case 658749:
			case 658760:
			case 658771:
			case 658782:
			case 658793:
			case 658804:
			case 658815:
			case 658826:
			case 658941:
			case 658952:
			case 658963:
			case 658974:
			case 658985:
			case 658996:
			case 659007:
			case 659018:
			case 659029:
			case 659040:
			case 659051:
			case 659062:
			case 659073:
			case 659084:
			case 659095:
			case 659106:
			case 658888:
			case 658889:
			case 658890:
			case 661286:
			case 661287:
			case 661288:
			case 659197:
			case 659198:
			case 659199:
			case 659200:
			case 659201:
			case 659202:
			case 659203:
			case 659204:
			case 659205:
			case 659206:
			case 659207:
			case 659208:
			case 659209:
			case 659210:
			case 659211:
			case 659212:
			case 659213:
			case 659214:
			case 659215:
			case 659216:
			case 659217:
			case 659218:
			case 659219:
			case 659220:
			case 659221:
			case 887238:
			case 887253:
			case 887268:
			case 887283:
			case 887298:
			case 887434:
			case 887435:
			case 887436:
			case 887437:
			case 887446:
			case 887447:
			case 887448:
			case 887449:
			case 887458:
			case 887459:
			case 887460:
			case 887461:
			case 887470:
			case 887471:
			case 887472:
			case 887473:
			case 887482:
			case 887483:
			case 887484:
			case 887485:
			//Crimson Danaria 7.x
			case 231334:
			case 231335:
			case 231336:
			case 231337:
			case 231338:
			case 231339:
			case 231340:
			case 231341:
			case 231360:
			case 231361:
			case 231362:
			case 231363:
			case 231364:
			case 231365:
			case 231366:
			case 231367:
			case 231515:
			case 231529:
			case 231530:
			case 231531:
			case 231532:
			case 231533:
			case 231534:
			case 231541:
			case 231542:
			case 231543:
			case 231544:
			case 231545:
			case 231546:
			//Crimson Danaria [81th Base]
			case 661217:
			case 661218:
			case 661219:
			case 661386:
			case 661387:
			case 661388:
			case 661389:
			case 858743:
			//Temple Of Scales 7.x
			case 257059:
			case 257062:
			case 257065:
			case 257068:
			case 257071:
			//Artifact 2012
			case 257112:
			case 257134:
			case 257143:
			case 257146:
			//Artifact 2013
			case 257157:
			case 257179:
			case 257188:
			case 257191:
			//Artifact 2014
			case 252474:
			case 256697:
			case 256700:
			case 256703:
			case 256706:
			//Altar Of Avarice 7.x
			case 257359:
			case 257362:
			case 257365:
			case 257368:
			case 257371:
			//Vorgaltem Citadel 7.x
			case 257659:
			case 257662:
			case 257665:
			case 257668:
			case 257671:
			//Artifact 3012
			case 257712:
			case 257734:
			case 257743:
			case 257746:
			//Artifact 3013
			case 257757:
			case 257779:
			case 257788:
			case 257791:
			//Artifact 3014
			case 252444:
			case 256721:
			case 256724:
			case 256727:
			case 256730:
			//Crimsom Temple 7.x
			case 257959:
			case 257962:
			case 257965:
			case 257968:
			case 257971:
			//Silona Fortress 7.x
			case 272675:
			case 272680:
			case 272685:
			case 272690:
			case 272695:
			case 272700:
			case 272705:
			case 272710:
			case 272786:
			case 272823:
			case 272826:
			//Pradeth Fortress 7.x
			case 661455:
			case 661456:
			case 661457:
			case 661458:
			case 858744:
			//Inggison Dreadgion 7.x
			case 661664:
			case 661674:
			case 661675:
			case 661676:
			case 661677:
			case 661678:
			case 661679:
			case 661680:
			case 661681:
			case 661682:
			case 661683:
			case 661684:
			case 661685:
			case 661686:
			case 661687:
			case 661688:
			case 661689:
			case 661690:
			case 661691:
			case 661692:
			case 661693:
			case 662103:
			case 662104:
			case 662105:
			case 662106:
			case 661730:
			case 661731:
			case 661733:
			case 661734:
			case 661736:
			case 661737:
			case 661738:
			case 661739:
			case 661740:
			case 661741:
			//Gelkmaros Dreadgion 7.x
			case 661803:
			case 661813:
			case 661814:
			case 661815:
			case 661816:
			case 661817:
			case 661818:
			case 661819:
			case 661820:
			case 661821:
			case 661822:
			case 661823:
			case 661824:
			case 661825:
			case 661826:
			case 661827:
			case 661828:
			case 661829:
			case 661830:
			case 661831:
			case 661832:
			case 662107:
			case 662108:
			case 662109:
			case 662110:
			case 661869:
			case 661870:
			case 661872:
			case 661873:
			case 661875:
			case 661876:
			case 661877:
			case 661878:
			case 661879:
			case 661880:
			//Unstable Danuar Mysticarium 7.x
			case 230065:
			case 230066:
			case 230067:
			case 230068:
			case 230069:
			case 230070:
			case 230077:
			case 230078:
			case 230079:
			case 230080:
			case 230081:
			case 230082:
			case 230083:
			    beritraFavor();
			break;
		} switch (getNpcId()) {
			case 230322:
			case 230323:
			case 230328:
		    case 230329:
			case 651702:
			    survivalInstinct();
			break;
		} switch (getNpcId()) {
			//Primeth Forge.
			case 650008:
			case 650009:
			case 650010:
			case 650011:
			case 650025:
		    case 654728:
			case 655210:
			case 655211:
			//The Veilenthrone.
			case 656426:
			case 656427:
			case 656428:
			case 656430:
			case 656432:
			case 656433:
			case 656434:
			case 656435:
			case 656436:
			case 656437:
			case 656438:
			case 656439:
			case 656441:
			case 656443:
			case 656444:
			case 656445:
			case 656446:
			case 656447:
			case 656448:
			case 656449:
			case 656450:
			case 656451:
			case 656452:
			case 656453:
			case 656455:
			case 656456:
			case 656457:
			case 656458:
			case 656459:
			case 656461:
			case 656462:
			case 656463:
			case 656465:
			case 656469:
			case 656470:
			case 656471:
			case 656485:
			case 656488:
			case 656585:
			case 656586:
			//Divine Fortress [1011]
			case 884940:
			case 884945:
			case 884950:
			case 884955:
			case 884986:
			case 884991:
			case 884996:
			case 885001:
			case 885038:
			case 885039:
			case 885040:
			case 885041:
			case 885046:
			case 885050:
			case 885051:
			case 885056:
			//Reshanta Artifact [1014]
			case 884778:
			case 884783:
			case 884788:
			case 884793:
			case 884798:
			//Reshanta Artifact [1016]
			case 884622:
			case 884627:
			case 884632:
			case 884637:
			case 884642:
			//Reshanta Artifact [1017]
			case 884544:
			case 884549:
			case 884554:
			case 884559:
			case 884564:
			//Reshanta Artifact [1018]
			case 884700:
			case 884705:
			case 884710:
			case 884715:
			case 884720:
			//Reshanta Artifact [1019]
			case 884856:
			case 884861:
			case 884866:
			case 884871:
			case 884876:
			//Lakrum Outpost [1517]
			case 885746:
			case 885915:
			case 885916:
			case 885917:
			case 885918:
			//Lakrum Outpost [1518]
			case 885761:
			case 885927:
			case 885928:
			case 885929:
			case 885930:
			//Lakrum Outpost [1519]
			case 885776:
			case 885939:
			case 885940:
			case 885941:
			case 885942:
			//Lakrum Outpost Killer.
			case 886295:
			case 886296:
			case 886297:
			//Lakrum 6.x
			case 655094:
			case 655095:
			case 655096:
			case 655097:
			case 655098:
			case 655103:
			case 655104:
			case 655105:
			case 655106:
			case 655107:
			case 656106:
			case 656107:
			case 656108:
			case 656109:
			case 656110:
			//Minium Vault 7.x
			case 661325:
			case 661326:
			case 661327:
		    case 661328:
			case 661329:
			case 661357:
			    ereshkigalFury();
			break;
		} switch (getNpcId()) {
			//Ishalgen 7.x
		    case 203505:
			case 203506:
			case 203520:
			case 203521:
			    griffonBlessing();
			break;
		} switch (getNpcId()) {
			//DCA Mini Cannon.
			case 230388:
				bombardmentOfTheOuterFortressGate();
			break;
		} switch (getNpcId()) {
			//Esoterrace 7.x
			case 655512:
			case 655513:
			case 655514:
			case 655516:
			case 655517:
			case 655519:
			case 655520:
			case 655539:
			case 655540:
			case 655541:
				suddenShriek();
			break;
		} switch (getNpcId()) {
			//IDCatacombs_Rudra 7.x
			case 663017:
			case 663019:
			case 663021:
			case 663023:
			case 663024:
			case 663026:
			case 663029:
			case 858940:
			case 858990:
				prefixDebuff2();
				prefixDebuff3();
			break;
		} switch (getNpcId()) {
			//Evergale Canyon 7.x
			case 654840:
			case 654841:
			case 654842:
			case 654843:
			//The Veilenthrone 6.x
			case 656410:
			case 656411:
			case 656412:
			case 656413:
			//Illumiel Brawl 6.x
			case 656658:
			case 656659:
			//Poeta 7.x
			case 651878:
			//Ishalgen 7.x
			case 651806:
			//Heiron 7.x
			case 212008:
			//Silentera Canyon 7.x
			case 858870:
			case 858871:
			case 858872:
			//Lakrum 7.x
			case 655120:
			case 655121:
			case 655122:
			case 655123:
			case 655124:
			//Demaha 7.x
			case 658554:
			case 858116:
			case 858117:
			case 858118:
			case 858119:
			case 858120:
			case 858121:
			case 858122:
			case 858124:
			    World.getInstance().doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(final Player player) {
						sendPacketTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
							@Override
							public void run() {
								if (player.getWorldId() == getOwner().getWorldId()) {
									if (getOwner().isSpawned()) {
										PacketSendUtility.sendPacket(player, new SM_FLAG_INFO(1, getOwner()));
									}
								}
							}
						}, 1000, 2000);
					}
				});
			break;
		}
	}
	
	private void typeA() {
	    SkillEngine.getInstance().getSkill(getOwner(), 22987, 60, getOwner()).useNoAnimationSkill(); //Warrior Type.
	}
	private void typeB() {
	    SkillEngine.getInstance().getSkill(getOwner(), 22988, 60, getOwner()).useNoAnimationSkill(); //Assassin Type.
	}
	private void typeC() {
	    SkillEngine.getInstance().getSkill(getOwner(), 22989, 60, getOwner()).useNoAnimationSkill(); //Mage Type.
	}
	private void typeD() {
	    SkillEngine.getInstance().getSkill(getOwner(), 22990, 60, getOwner()).useNoAnimationSkill(); //Special Type.
	}
	private void conquerorPassion() {
		SkillEngine.getInstance().getSkill(getOwner(), 20665, 60, getOwner()).useNoAnimationSkill(); //Conqueror's Passion.
	}
	private void darkLordBlessing() {
		SkillEngine.getInstance().getSkill(getOwner(), 22664, 60, getOwner()).useNoAnimationSkill(); //Dark Lord's Blessing.
	}
	private void ereshkigalFury() {
	    SkillEngine.getInstance().getSkill(getOwner(), 22682, 60, getOwner()).useNoAnimationSkill(); //Ereshkigal's Fury.
	}
	private void mindControl() {
	    SkillEngine.getInstance().getSkill(getOwner(), 19226, 60, getOwner()).useNoAnimationSkill(); //Mind Control.
	}
	private void soulPrison() {
	    SkillEngine.getInstance().getSkill(getOwner(), 17434, 60, getOwner()).useNoAnimationSkill(); //Soul Prison.
	}
	private void ironScale() {
	    SkillEngine.getInstance().getSkill(getOwner(), 21744, 60, getOwner()).useNoAnimationSkill(); //Iron Scale.
	}
	private void beritraFavor() {
	    SkillEngine.getInstance().getSkill(getOwner(), 21135, 60, getOwner()).useNoAnimationSkill(); //Beritra's Favor.
	}
	private void survivalInstinct() {
	    SkillEngine.getInstance().getSkill(getOwner(), 20656, 60, getOwner()).useNoAnimationSkill(); //Survival Instinct.
	}
	private void suddenShriek() {
	    SkillEngine.getInstance().getSkill(getOwner(), 19340, 60, getOwner()).useNoAnimationSkill(); //Sudden Shriek.
	}
	private void reianSpirit() {
	    SkillEngine.getInstance().getSkill(getOwner(), 20597, 60, getOwner()).useNoAnimationSkill(); //Reian Spirit.
	}
	private void griffonBlessing() {
	    SkillEngine.getInstance().getSkill(getOwner(), 18353, 60, getOwner()).useNoAnimationSkill(); //Griffon's Blessing.
	}
	private void prefixDebuff2() {
	    SkillEngine.getInstance().getSkill(getOwner(), 20875, 60, getOwner()).useNoAnimationSkill();
	}
	private void prefixDebuff3() {
	    SkillEngine.getInstance().getSkill(getOwner(), 20876, 60, getOwner()).useNoAnimationSkill();
	}
	private void bombardmentOfTheOuterFortressGate() {
	    SkillEngine.getInstance().getSkill(getOwner(), 17240, 60, getOwner()).useNoAnimationSkill(); //Bombardment Of The Outer Fortress Gate.
	}
	
	@Override
	protected AIAnswer pollInstance(AIQuestion question) {
		switch (question) {
	        case CAN_SPAWN_ON_DAYTIME_CHANGE:
			    return AIAnswers.POSITIVE;
			case SHOULD_DECAY:
			    return AIAnswers.POSITIVE;
			case SHOULD_RESPAWN:
			    return AIAnswers.POSITIVE;
			case SHOULD_REWARD:
			    return AIAnswers.POSITIVE;
			case SHOULD_REWARD_AP:
			    return AIAnswers.POSITIVE;
			case SHOULD_REWARD_GP:
			    return AIAnswers.POSITIVE;
			case CAN_RESIST_ABNORMAL:
			    return AIAnswers.POSITIVE;
			case CAN_ATTACK_PLAYER:
			    return AIAnswers.POSITIVE;
			default:
				return null;
		}
	}
	
    @Override
	public AttackIntention chooseAttackIntention() {
		VisibleObject currentTarget = getTarget();
		Creature mostHated = getAggroList().getMostHated();
		if (mostHated == null || mostHated.getLifeStats().isAlreadyDead()) {
			return AttackIntention.FINISH_ATTACK;
		} if (currentTarget == null || !currentTarget.getObjectId().equals(mostHated.getObjectId())) {
			onCreatureEvent(AIEventType.TARGET_CHANGED, mostHated);
			return AttackIntention.SWITCH_TARGET;
		} if (getOwner().getObjectTemplate().getAttackRange() == 0) {
			NpcSkillEntry skill = getOwner().getSkillList().getRandomSkill();
			if (skill != null) {
				skillId = skill.getSkillId();
				skillLevel = skill.getSkillLevel();
				return AttackIntention.SKILL_ATTACK;
			}
		} else {
			NpcSkillEntry skill = SkillAttackManager.chooseNextSkill(this);
			if (skill != null) {
				skillId = skill.getSkillId();
				skillLevel = skill.getSkillLevel();
				return AttackIntention.SKILL_ATTACK;
			}
		}
		return AttackIntention.SIMPLE_ATTACK;
	}
}