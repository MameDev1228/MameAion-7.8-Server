/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 *  Aion-Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion-Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details. *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion-Lightning.
 *  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.npc.AbyssNpcType;
import com.aionemu.gameserver.model.templates.npc.NpcUiType;
import com.aionemu.gameserver.model.templates.tribe.Tribe;

/**
 * @author Cheatkiller
 * @author GiGatR00n v4.7.5.x
 */
public class TribeRelationService {

	private static final Logger log = LoggerFactory.getLogger(TribeRelationService.class);
	private static final Set<String> BAD_TRIBE_WARNED = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

	private static boolean hasValidTribes(Creature creature1, Creature creature2, String context) {
		return hasValidTribe(creature1, "left", context) && hasValidTribe(creature2, "right", context);
	}

	private static boolean hasValidTribe(Creature creature, String side, String context) {
		if (creature == null) {
			warnBadTribe(context, side, null, "creature is null");
			return false;
		}
		TribeClass tribe = creature.getTribe();
		if (tribe == null) {
			warnBadTribe(context, side, creature, "tribe is null");
			return false;
		}
		if (DataManager.TRIBE_RELATIONS_DATA.getTribeData(tribe) == null) {
			warnBadTribe(context, side, creature, "tribe relation data is missing for " + tribe);
			return false;
		}
		return true;
	}

	private static void warnBadTribe(String context, String side, Creature creature, String reason) {
		String key = context + ':' + side + ':' + reason + ':' + (creature != null ? creature.getObjectId() : 0);
		if (!BAD_TRIBE_WARNED.add(key)) {
			return;
		}
		if (creature instanceof Npc) {
			Npc npc = (Npc) creature;
			log.warn("[TRIBE_TRACE] context={} side={} reason={} objectId={} npcId={} name={} worldId={} x={} y={} z={}", new Object[] {
				context, side, reason, npc.getObjectId(), npc.getNpcId(), safeName(npc), npc.getWorldId(), npc.getX(), npc.getY(), npc.getZ() });
		}
		else if (creature != null) {
			log.warn("[TRIBE_TRACE] context={} side={} reason={} objectId={} type={} name={} worldId={}", new Object[] {
				context, side, reason, creature.getObjectId(), creature.getClass().getSimpleName(), safeName(creature), creature.getWorldId() });
		}
		else {
			log.warn("[TRIBE_TRACE] context={} side={} reason={}", new Object[] { context, side, reason });
		}
	}

	private static String safeName(Creature creature) {
		try {
			return creature != null ? String.valueOf(creature.getName()) : "null";
		}
		catch (Exception e) {
			return "name-error";
		}
	}

	public static boolean isAggressive(Creature creature1, Creature creature2) {
		if (!hasValidTribes(creature1, creature2, "isAggressive")) {
			return false;
		}
		Tribe tribe1 = DataManager.TRIBE_RELATIONS_DATA.getTribeData(creature1.getTribe());
		Tribe tribe2 = DataManager.TRIBE_RELATIONS_DATA.getTribeData(creature2.getTribe());

		if (tribe1.getAggro().isEmpty() && tribe2.getAggro().isEmpty()) {
			switch (creature1.getBaseTribe()) {
				case GUARD_DARK:
					switch (creature2.getBaseTribe()) {
						case PC:
						case GUARD:
						case GENERAL:
						case GUARD_DRAGON:
							return true;
						default:
							break;
					}
					break;
				case GUARD:
					switch (creature2.getBaseTribe()) {
						case PC_DARK:
						case GUARD_DARK:
						case GENERAL_DARK:
						case GUARD_DRAGON:
							return true;
						default:
							break;

					}
					break;
				case GUARD_DRAGON:
					switch (creature2.getBaseTribe()) {
						case PC_DARK:
						case PC:
						case GUARD:
						case GUARD_DARK:
						case GENERAL_DARK:
						case GENERAL:
							return true;
						default:
							break;

					}
					break;
				default:
					break;
			}
		}
		return DataManager.TRIBE_RELATIONS_DATA.isAggressiveRelation(creature1.getTribe(), creature2.getTribe());
	}

	public static boolean isFriend(Creature creature1, Creature creature2) {
		if (!hasValidTribes(creature1, creature2, "isFriend")) {
			return false;
		}
		if (creature1.getTribe() == creature2.getTribe()) // OR BASE ????
		{
			return true;
		}
		switch (creature1.getBaseTribe()) {
			case USEALL:
			case FIELD_OBJECT_ALL:
				return true;
			case GENERAL_DARK:
				switch (creature2.getBaseTribe()) {
					case PC_DARK:
					case GUARD_DARK:
						return true;
					default:
						break;
				}
				break;
			case GENERAL:
				switch (creature2.getBaseTribe()) {
					case PC:
					case GUARD:
						return true;
					default:
						break;

				}
				break;
			case FIELD_OBJECT_LIGHT:
				switch (creature2.getBaseTribe()) {
					case PC:
						return true;
					default:
						break;

				}
			case FIELD_OBJECT_DARK:
				switch (creature2.getBaseTribe()) {
					case PC_DARK:
						return true;
					default:
						break;

				}
				break;
			case GUARD_DARK:
				switch (creature2.getBaseTribe()) {
					case PC_DARK:
						return true;
					default:
						break;
				}
				break;
			case GUARD:
				switch (creature2.getBaseTribe()) {
					case PC:
						return true;
					default:
						break;

				}
				break;
			default:
				break;
		}
		return DataManager.TRIBE_RELATIONS_DATA.isFriendlyRelation(creature1.getTribe(), creature2.getTribe());
	}

	public static boolean isSupport(Creature creature1, Creature creature2) {
		if (!hasValidTribes(creature1, creature2, "isSupport")) {
			return false;
		}
		// switch (creature1.getBaseTribe()) {
		// case GUARD_DARK:
		// switch (creature2.getBaseTribe()) {
		// case PC_DARK:
		// return true;
		// }
		// break;
		// case GUARD:
		// switch (creature2.getBaseTribe()) {
		// case PC:
		// return true;
		//
		// }
		// break;
		// }
		return DataManager.TRIBE_RELATIONS_DATA.isSupportRelation(creature1.getTribe(), creature2.getTribe());
	}

	public static boolean isInvulnerable(Creature creature1, Creature creature2) {
		if (!hasValidTribes(creature1, creature2, "isInvulnerable")) {
			return false;
		}
		switch (creature1.getTribe()) {
			case IDFORTRESS_VRITRA:
				switch (creature2.getBaseTribe()) {
					case PC:
					case PC_DARK:
						if (creature1 instanceof Npc) {
							Npc targetNpc = (Npc) creature1;
							if (creature2.getWorldId() != 301310000 && targetNpc.getObjectTemplate().getNpcUiType().equals(NpcUiType.NONE)) {
								return true;
							}
						}
					default:
						break;
				}
				break;
			case IDFORTRESS_SWITCH_LIGHT:
			case IDKAMAR_PROTECTGUARD_LIGHT:
				switch (creature2.getBaseTribe()) {
					case PC_DARK:
						return true;
					default:
						break;
				}
				break;
			case IDFORTRESS_SWITCH_DARK:
			case IDKAMAR_PROTECTGUARD_DARK:
				switch (creature2.getBaseTribe()) {
					case PC:
						return true;
					default:
						break;
				}
				break;
			default:
				break;
		}
		return false;
	}

	public static boolean isNone(Creature creature1, Creature creature2) {
		if (!hasValidTribes(creature1, creature2, "isNone")) {
			return false;
		}
		if (DataManager.TRIBE_RELATIONS_DATA.isAggressiveRelation(creature1.getTribe(), creature2.getTribe()) || creature1 instanceof Npc && checkSiegeRelation((Npc) creature1, creature2) || DataManager.TRIBE_RELATIONS_DATA.isHostileRelation(creature1.getTribe(), creature2.getTribe()) || DataManager.TRIBE_RELATIONS_DATA.isNeutralRelation(creature1.getTribe(), creature2.getTribe())) {
			return false;
		}
		switch (creature1.getBaseTribe()) {
			case GENERAL_DRAGON:
				return true;
			case GENERAL:
			case FIELD_OBJECT_LIGHT:
				switch (creature2.getBaseTribe()) {
					case PC_DARK:
						return true;
					default:
						break;
				}
				break;
			case GENERAL_DARK:
			case FIELD_OBJECT_DARK:
				switch (creature2.getBaseTribe()) {
					case PC:
						return true;
					default:
						break;

				}
				break;
			default:
				break;
		}
		return DataManager.TRIBE_RELATIONS_DATA.isNoneRelation(creature1.getTribe(), creature2.getTribe());
	}

	public static boolean isNeutral(Creature creature1, Creature creature2) {
		if (!hasValidTribes(creature1, creature2, "isNeutral")) {
			return false;
		}
		return DataManager.TRIBE_RELATIONS_DATA.isNeutralRelation(creature1.getTribe(), creature2.getTribe());
	}

	public static boolean isHostile(Creature creature1, Creature creature2) {
		if (!hasValidTribes(creature1, creature2, "isHostile")) {
			return false;
		}
		if (creature1 instanceof Npc && checkSiegeRelation((Npc) creature1, creature2)) {
			return true;
		}
		switch (creature1.getBaseTribe()) {
			case MONSTER:
				switch (creature2.getBaseTribe()) {
					case PC_DARK:
					case PC:
						return true;
					default:
						break;
				}
				break;
			default:
				break;
		}
		return DataManager.TRIBE_RELATIONS_DATA.isHostileRelation(creature1.getTribe(), creature2.getTribe());
	}

	public static boolean checkSiegeRelation(Npc npc, Creature creature) {
		if (!hasValidTribes(npc, creature, "checkSiegeRelation")) {
			return false;
		}
		return npc.getObjectTemplate().getAbyssNpcType() != AbyssNpcType.ARTIFACT && npc.getObjectTemplate().getAbyssNpcType() != AbyssNpcType.NONE && ((npc.getBaseTribe() == TribeClass.GENERAL && creature.getTribe() == TribeClass.PC_DARK) || (npc.getBaseTribe() == TribeClass.GENERAL_DARK && creature.getTribe() == TribeClass.PC) || npc.getBaseTribe() == TribeClass.GENERAL_DRAGON);
	}
}
