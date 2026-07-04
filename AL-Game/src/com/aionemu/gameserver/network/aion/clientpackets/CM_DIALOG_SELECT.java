package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.configs.main.CustomConfig;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.actions.PlayerMode;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.player.MameBurningService;
import com.aionemu.gameserver.utils.PacketSendUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_DIALOG_SELECT extends AionClientPacket
{
	private int targetObjectId;
	private int dialogId;
	private int extendedRewardIndex;
	@SuppressWarnings("unused")
	private int lastPage;
	private int questId;
	private int unk;
	
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(CM_DIALOG_SELECT.class);
	
	public CM_DIALOG_SELECT(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		targetObjectId = readD();
		dialogId = readH();
		extendedRewardIndex = readH();
		unk = readH();
		lastPage = readH();
		questId = readD();
		readH();
	}
	
	@Override
	protected void runImpl() {
		final Player player = getConnection().getActivePlayer();
		QuestTemplate questTemplate = DataManager.QUEST_DATA.getQuestById(questId);
		QuestEnv env = new QuestEnv(null, player, questId, 0);
		if (player.isInPlayerMode(PlayerMode.RIDE)) {
			player.unsetPlayerMode(PlayerMode.RIDE);
		} if (player.isTrading()) {
			return;
		} if (player.isGM()) {
			PacketSendUtility.sendMessage(player, "<Quest Id>: " + this.questId);
			PacketSendUtility.sendMessage(player, "<Dialog Id>: " + this.dialogId);
		} if (targetObjectId == 0 || targetObjectId == player.getObjectId()) {
			if (questTemplate != null && !questTemplate.isCannotShare() && (dialogId == 1002 || dialogId == 20000)) {
				QuestService.startQuest(env);
				return;
			} if (QuestEngine.getInstance().onDialog(new QuestEnv(null, player, questId, dialogId))) {
				return;
			} if (questTemplate != null && questTemplate.isCanReport() &&
			   (dialogId == 108 || dialogId == 110 || dialogId == 111 || dialogId == 112 ||
			    dialogId == 113 || dialogId == 114 || dialogId == 115 || dialogId == 116 ||
				dialogId == 117 || dialogId == 118 || dialogId == 119 || dialogId == 120 ||
				dialogId == 121 || dialogId == 122 || dialogId == 123 || dialogId == 124)) {
				int bountyIndex = dialogId - 110;
				QuestService.bountyReward(env, bountyIndex);
				env.setExtendedRewardIndex(bountyIndex);
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(player.getObjectId(), 0, 0));
				PacketSendUtility.sendPacket(player, new SM_QUEST_COMPLETED_LIST(player.getQuestStateList().getAllFinishedQuests()));
				return;
			} if (questTemplate != null && questTemplate.isTutorial() && dialogId == 108) {
				QuestService.finishQuest(env, 0);
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(player.getObjectId(), 0, 0));
				PacketSendUtility.sendPacket(player, new SM_QUEST_COMPLETED_LIST(player.getQuestStateList().getAllFinishedQuests()));
				return;
			} if (player.getLevel() >= 9 && (CustomConfig.ENABLE_SIMPLE_2NDCLASS || MameBurningService.isWaitingForClassSelection(player))) {
				ClassChangeService.changeClassToSelection(player, dialogId);
			}
			return;
		}
		VisibleObject obj = player.getKnownList().getObject(targetObjectId);
		if (obj != null && obj instanceof Creature) {
			Creature creature = (Creature) obj;
			creature.getController().onDialogSelect(dialogId, player, questId, extendedRewardIndex, unk);
		}
	}
}