package com.aionemu.gameserver.services;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.controllers.attack.AggroInfo;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.drop.Drop;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.TemporaryPlayerTeam;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * MameAion field progression rewards.
 * - Lakrum common mob independent bonus drops.
 * - Crimson/North Katalam base capture rewards.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public final class MameFieldRewardService {

    private static final int LAKRUM_WORLD_ID = 800050000;
    private static final int CRIMSON_KATALAM_WORLD_ID = 800030000;
    private static final long KATALAM_BASE_KINAH = 25_000_000L;

    private static final BonusDrop[] LAKRUM_BASIC_DROPS = new BonusDrop[] {
        new BonusDrop(166033102, 25.0f, 1),
        new BonusDrop(166023102, 25.0f, 1),
        new BonusDrop(166075000,  5.0f, 1),
        new BonusDrop(188071448, 30.0f, 1),
        new BonusDrop(188074436, 40.0f, 1),
        new BonusDrop(190095008, 15.0f, 1),
        new BonusDrop(190090040,  1.0f, 1)
    };

    private static final RewardItem[] KATALAM_BASE_REWARDS = new RewardItem[] {
        new RewardItem(166033102, 10),
        new RewardItem(166023102, 10),
        new RewardItem(190095008, 3),
        new RewardItem(166075000, 2)
    };

    private MameFieldRewardService() {
    }

    /**
     * Adds independent Lakrum bonus drops. Each rule rolls independently, so several items can drop at once.
     */
    public static int addLakrumBasicDrops(Npc npc, Set<DropItem> droppedItems, int index, int winnerObjId) {
        if (npc == null || droppedItems == null || npc.getWorldId() != LAKRUM_WORLD_ID) {
            return index;
        }
        if (isLakrumWorldRaid(npc.getNpcId())) {
            return index;
        }
        for (BonusDrop rule : LAKRUM_BASIC_DROPS) {
            if (rollPercent(rule.chance)) {
                droppedItems.add(createDropItem(index++, winnerObjId, npc.getObjectId(), rule.itemId, rule.count));
            }
        }
        return index;
    }

    private static boolean isLakrumWorldRaid(int npcId) {
        switch (npcId) {
            case 655120: // Scout Kabar
            case 655121: // Scout Paltan
            case 655122: // Inspector Kephrata
            case 655123: // Guard Captain Haznish
            case 655124: // Mad King Laurent
            case 655240: // Berserk Anomos
                return true;
            default:
                return false;
        }
    }

    /**
     * Rewards the capturing party/team and same-race individual contributors for Crimson/North Katalam bases.
     */
    public static void rewardCrimsonKatalamBaseCapture(Npc boss, int baseId, Race capturedRace, AionObject winner) {
        if (boss == null || boss.getWorldId() != CRIMSON_KATALAM_WORLD_ID || capturedRace == null || !capturedRace.isPlayerRace()) {
            return;
        }
        if (baseId < 701 || baseId > 713) {
            return;
        }

        Set<Integer> rewardedObjectIds = new LinkedHashSet<Integer>();
        Set<Player> rewardPlayers = new LinkedHashSet<Player>();

        addWinnerPlayers(rewardPlayers, rewardedObjectIds, winner, capturedRace);
        addContributorPlayers(rewardPlayers, rewardedObjectIds, boss, capturedRace);

        for (Player player : rewardPlayers) {
            giveKatalamBaseReward(player, baseId);
        }
    }

    private static void addWinnerPlayers(Set<Player> players, Set<Integer> ids, AionObject winner, Race race) {
        if (winner instanceof Player) {
            Player player = (Player) winner;
            if (player.isInTeam()) {
                addTeamMembers(players, ids, player.getCurrentTeam(), race);
            } else {
                addPlayer(players, ids, player, race);
            }
        } else if (winner instanceof TemporaryPlayerTeam) {
            addTeamMembers(players, ids, (TemporaryPlayerTeam) winner, race);
        } else if (winner instanceof Creature) {
            Creature creature = (Creature) winner;
            Creature master = creature.getMaster();
            if (master instanceof Player) {
                addWinnerPlayers(players, ids, master, race);
            }
        }
    }

    private static void addTeamMembers(Set<Player> players, Set<Integer> ids, TemporaryPlayerTeam team, Race race) {
        if (team == null) {
            return;
        }
        Collection<Player> members = team.getOnlineMembers();
        for (Player member : members) {
            addPlayer(players, ids, member, race);
        }
    }

    private static void addContributorPlayers(Set<Player> players, Set<Integer> ids, Npc boss, Race race) {
        for (AggroInfo aggro : boss.getAggroList().getFinalDamageList(false)) {
            if (aggro == null || aggro.getDamage() <= 0 || !(aggro.getAttacker() instanceof Creature)) {
                continue;
            }
            Creature attacker = (Creature) aggro.getAttacker();
            Creature master = attacker.getMaster();
            if (master instanceof Player) {
                addPlayer(players, ids, (Player) master, race);
            } else if (attacker instanceof Player) {
                addPlayer(players, ids, (Player) attacker, race);
            }
        }
    }

    private static void addPlayer(Set<Player> players, Set<Integer> ids, Player player, Race race) {
        if (player == null || player.getRace() != race) {
            return;
        }
        if (ids.add(player.getObjectId())) {
            players.add(player);
        }
    }

    private static void giveKatalamBaseReward(Player player, int baseId) {
        for (RewardItem reward : KATALAM_BASE_REWARDS) {
            ItemService.addItem(player, reward.itemId, reward.count);
        }
        player.getInventory().increaseKinah(KATALAM_BASE_KINAH);
        PacketSendUtility.sendMessage(player, "[MameAion] 北カタラム基地 " + baseId + " 占領報酬を獲得しました。ギーナ 25,000,000 と支援アイテムを支給しました。");
    }

    private static boolean rollPercent(float chancePercent) {
        return Rnd.get() * 100.0f < chancePercent;
    }

    private static DropItem createDropItem(int index, int playerObjId, int npcObjId, int itemId, long count) {
        DropItem item = new DropItem(new Drop(itemId, 1, 1, 100, false, false));
        item.setIndex(index);
        item.setPlayerObjId(playerObjId);
        item.setNpcObj(npcObjId);
        item.setCount(count);
        return item;
    }

    private static final class BonusDrop {
        private final int itemId;
        private final float chance;
        private final long count;

        private BonusDrop(int itemId, float chance, long count) {
            this.itemId = itemId;
            this.chance = chance;
            this.count = count;
        }
    }

    private static final class RewardItem {
        private final int itemId;
        private final long count;

        private RewardItem(int itemId, long count) {
            this.itemId = itemId;
            this.count = count;
        }
    }
}
