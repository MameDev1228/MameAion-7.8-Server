/*
 * MameAion75 v68: 4.8-style cooldown packet port for CC2/EU7.7.
 * Keep cooldown state authoritative on the server, but serialize only concrete skillIds.
 */
package com.aionemu.gameserver.network.aion.serverpackets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skill.PlayerSkillEntry;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;

public class SM_SKILL_COOLDOWN extends AionServerPacket {
	private static final Logger log = LoggerFactory.getLogger(SM_SKILL_COOLDOWN.class);

	private final ArrayList<Cooldown> cooldowns = new ArrayList<Cooldown>();
	private final boolean notify;

	public SM_SKILL_COOLDOWN(int skillId, long expirationTimeMillis) {
		this(skillId, expirationTimeMillis, getTemplateDurationMillis(skillId));
	}

	public SM_SKILL_COOLDOWN(int skillId, long expirationTimeMillis, int durationMillis) {
		this.notify = true;
		if (skillId > 0) {
			cooldowns.add(new Cooldown(skillId, expirationTimeMillis, durationMillis));
		}
	}

	/**
	 * Legacy constructor kept for old call sites. Prefer the Player-aware constructor
	 * whenever possible, because it serializes only skills the client actually owns.
	 */
	public SM_SKILL_COOLDOWN(Map<Integer, Long> cooldownExpirationMillisByDelayId) {
		this.notify = true;
		if (cooldownExpirationMillisByDelayId == null) {
			return;
		}
		for (Map.Entry<Integer, Long> entry : cooldownExpirationMillisByDelayId.entrySet()) {
			ArrayList<Integer> skillsWithCooldown = DataManager.SKILL_DATA.getSkillsForDelayId(entry.getKey());
			if (skillsWithCooldown == null || skillsWithCooldown.isEmpty()) {
				continue;
			}
			for (Integer skillId : skillsWithCooldown) {
				if (skillId == null || skillId <= 0) {
					continue;
				}
				SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
				if (template == null) {
					continue;
				}
				cooldowns.add(new Cooldown(skillId, entry.getValue(), calculateDurationMillis(template)));
			}
		}
		sortForClient();
	}

	/**
	 * 4.8-proven path: convert cooldown delayIds to concrete skillIds owned by this player.
	 * This avoids CC2/EU7.7 clearing unrelated cooldown icons from over-broad delayId groups.
	 */
	public SM_SKILL_COOLDOWN(Player player, Map<Integer, Long> cooldownExpirationMillisByDelayId, boolean notify) {
		this.notify = notify;
		if (player == null || cooldownExpirationMillisByDelayId == null || player.getSkillList() == null) {
			return;
		}
		for (PlayerSkillEntry skill : player.getSkillList().getAllSkills()) {
			if (skill == null) {
				continue;
			}
			SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skill.getSkillId());
			if (template == null) {
				continue;
			}
			int delayId = template.getDelayId();
			Long expirationTime = cooldownExpirationMillisByDelayId.get(delayId);
			if (expirationTime == null) {
				continue;
			}
			cooldowns.add(new Cooldown(skill.getSkillId(), expirationTime, calculateDurationMillis(template, skill.getSkillLevel())));
		}
		sortForClient();
	}

	/** Creates a skill cooldown reset packet for the supplied delayIds. */
	public SM_SKILL_COOLDOWN(Player player, Collection<Integer> resettableDelayIds) {
		this(player, toResetMap(resettableDelayIds), true);
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeH(cooldowns.size());
		writeC(notify ? 1 : 0);
		for (Cooldown cooldown : cooldowns) {
			writeH(cooldown.skillId);
			writeD(cooldown.getRemainingSeconds());
			writeD(cooldown.getDurationMillis());
		}
	}

	private void sortForClient() {
		Collections.sort(cooldowns, new Comparator<Cooldown>() {
			@Override
			public int compare(Cooldown a, Cooldown b) {
				return Integer.compare(a.getDurationMillis(), b.getDurationMillis());
			}
		});
	}

	private static Map<Integer, Long> toResetMap(Collection<Integer> resettableDelayIds) {
		HashMap<Integer, Long> resetMap = new HashMap<Integer, Long>();
		if (resettableDelayIds != null) {
			for (Integer delayId : resettableDelayIds) {
				if (delayId != null && delayId > 0) {
					resetMap.put(delayId, 0L);
				}
			}
		}
		return resetMap;
	}

	private static int calculateDurationMillis(SkillTemplate template) {
		return calculateDurationMillis(template, 0);
	}

	private static int calculateDurationMillis(SkillTemplate template, int skillLevel) {
		if (template == null || template.getCooldown() <= 0) {
			return 0;
		}
		int cooldown = skillLevel > 0 ? template.getCooldownForLevel(skillLevel) : template.getCooldown();
		return Math.max(0, cooldown * 100);
	}

	private static int getTemplateDurationMillis(int skillId) {
		SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		return calculateDurationMillis(template);
	}

	private static final class Cooldown {
		private final int skillId;
		private final long expirationTimeMillis;
		private final int durationMillis;

		private Cooldown(int skillId, long expirationTimeMillis, int durationMillis) {
			this.skillId = skillId;
			this.expirationTimeMillis = expirationTimeMillis;
			this.durationMillis = Math.max(0, durationMillis);
		}

		private int getRemainingSeconds() {
			if (expirationTimeMillis <= 0) {
				return 0;
			}
			long rawLeft = (expirationTimeMillis - System.currentTimeMillis()) / 1000L;
			if (rawLeft <= 0) {
				return 0;
			}
			int maxLeft = durationMillis > 0 ? Math.max(1, durationMillis / 1000 + 5) : 0;
			if (maxLeft > 0 && rawLeft > maxLeft) {
				log.warn("[MAME-COOLDOWN][CLAMP_PACKET] skill=" + skillId + " rawLeftSec=" + rawLeft + " maxLeftSec=" + maxLeft + " durationMillis=" + durationMillis);
				rawLeft = maxLeft;
			}
			return rawLeft > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) rawLeft;
		}

		private int getDurationMillis() {
			return durationMillis;
		}
	}
}
