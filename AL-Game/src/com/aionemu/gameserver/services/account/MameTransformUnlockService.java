package com.aionemu.gameserver.services.account;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.dao.AccountTransformDAO;
import com.aionemu.gameserver.dao.ServerVariablesDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.account.AccountTransfo;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.transform_book.TransformBookTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TRANSFORM_LIST;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Unlocks every non-ultimate transformation card for private-server baseline play.
 *
 * The heavy parts are moved off the enter-world path and protected by an
 * account-scoped version flag, because otherwise every login scanned the whole
 * transform book and could perform many single-row DB writes.
 */
public final class MameTransformUnlockService {

    private static final Logger log = LoggerFactory.getLogger(MameTransformUnlockService.class);
    private static final MameTransformUnlockService INSTANCE = new MameTransformUnlockService();
    private static final int ULTIMATE_GRADE = 5;
    private static final String KEY_PREFIX = "mame_tf_nu:";
    private static volatile List<Integer> nonUltimateCardIds;

    public static MameTransformUnlockService getInstance() {
        return INSTANCE;
    }

    private MameTransformUnlockService() {
    }

    public void onPlayerLogin(final Player player) {
        if (!CustomConfig.MAME_TRANSFORM_UNLOCK_NON_ULTIMATE || player == null) {
            return;
        }
        // Do not block the login/enter-world pipeline. The list is account scoped,
        // so a few seconds delay before the UI refresh is fine.
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                unlockAfterLogin(player);
            }
        }, 2500);
    }

    private void unlockAfterLogin(Player player) {
        if (player == null || !player.isOnline() || player.getPlayerAccount() == null) {
            return;
        }
        List<Integer> targetIds = getNonUltimateCardIds();
        if (targetIds.isEmpty()) {
            return;
        }

        int accountId = player.getPlayerAccount().getId();
        String key = KEY_PREFIX + accountId;
        int version = targetIds.size();
        if (DAOManager.getDAO(ServerVariablesDAO.class).load(key) == version) {
            return;
        }

        int added = unlockNonUltimate(player, targetIds);
        DAOManager.getDAO(ServerVariablesDAO.class).store(key, version);
        if (added > 0) {
            player.getTransformList().updateTransformationsList();
            PacketSendUtility.sendPacket(player, new SM_TRANSFORM_LIST(0, player));
        }
        log.info("[MAME-TRANSFORM] checked=" + targetIds.size() + " unlocked=" + added + " account=" + accountId + " player=" + player.getName());
    }

    private int unlockNonUltimate(Player player, List<Integer> targetIds) {
        int added = 0;
        for (int cardId : targetIds) {
            if (player.getTransformList().hasTransformation(cardId)) {
                continue;
            }
            AccountTransfo transfo = new AccountTransfo(cardId, 1);
            if (DAOManager.getDAO(AccountTransformDAO.class).addTransfo(player.getPlayerAccount(), transfo)) {
                added++;
            }
        }
        return added;
    }

    private static List<Integer> getNonUltimateCardIds() {
        List<Integer> cached = nonUltimateCardIds;
        if (cached != null) {
            return cached;
        }
        synchronized (MameTransformUnlockService.class) {
            cached = nonUltimateCardIds;
            if (cached != null) {
                return cached;
            }
            List<Integer> ids = new ArrayList<Integer>();
            for (TransformBookTemplate template : DataManager.TRANSFORM_BOOK_DATA.getAllBooks().valueCollection()) {
                if (template != null && template.getGrade() != ULTIMATE_GRADE) {
                    ids.add(template.getId());
                }
            }
            Collections.sort(ids);
            nonUltimateCardIds = Collections.unmodifiableList(ids);
            return nonUltimateCardIds;
        }
    }
}
