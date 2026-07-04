package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.commons.database.dao.DAOManager;

import com.aionemu.gameserver.configs.main.*;
import com.aionemu.gameserver.dao.*;
import com.aionemu.gameserver.model.Gender;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerAppearance;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ACCOUNT_TYPE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CREATE_CHARACTER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUIT_RESPONSE;
import com.aionemu.gameserver.services.NameRestrictionService;
import com.aionemu.gameserver.services.player.PlayerService;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.List;

public class CM_CREATE_CHARACTER extends AionClientPacket
{
    private PlayerAppearance playerAppearance;
    private PlayerCommonData playerCommonData;
    private int create_protocol_47;
    private int classId;
	 private int isJumping;
	
    private static Logger log = LoggerFactory.getLogger(CM_CREATE_CHARACTER.class);
	
    public CM_CREATE_CHARACTER(int opcode, State state, State... restStates) {
        super(opcode, state, restStates);
    }
	
    @Override
    protected void readImpl() {
        readD();
        readS();
        playerCommonData = new PlayerCommonData(IDFactory.getInstance().nextId());
        String name = Util.convertName(readS());
        playerCommonData.setName(name);
        readB(52 - (name.length() * 2 + 2));
        playerCommonData.setGender(readD() == 0 ? Gender.MALE : Gender.FEMALE);
        playerCommonData.setRace(readD() == 0 ? Race.ELYOS : Race.ASMODIANS);
        playerCommonData.setPlayerClass(PlayerClass.getPlayerClassById((byte) readD()));
		if (!playerCommonData.getPlayerClass().isStartingClass()) {
            playerCommonData.setLevel(80);
			playerCommonData.setNpcExpands(18);
			playerCommonData.setAdvencedStigmaSlotSize(9);
        } else {
            playerCommonData.setLevel(1);
			playerCommonData.setNpcExpands(0);
        } if (getConnection().getAccount().getMembership() >= MembershipConfig.STIGMA_SLOT_QUEST) {
            playerCommonData.setAdvencedStigmaSlotSize(9);
        }
        playerAppearance = new PlayerAppearance();
        playerAppearance.setVoice(readD());
        playerAppearance.setSkinRGB(readD());
        playerAppearance.setHairRGB(readD());
        playerAppearance.setEyeRGB(readD());
        playerAppearance.setLipRGB(readD());
        playerAppearance.setFace(readC());
        playerAppearance.setHair(readC());
        playerAppearance.setDeco(readC());
        playerAppearance.setTattoo(readC());
        playerAppearance.setFaceContour(readC());
        playerAppearance.setExpression(readC());
        playerAppearance.setPupilShape(readC());
        playerAppearance.setRemoveMane(readC());
        playerAppearance.setRightEyeRGB(readD());
        playerAppearance.setEyeLashShape(readC());
        readC();
        playerAppearance.setJawLine(readC());
        playerAppearance.setForehead(readC());
        playerAppearance.setEyeHeight(readC());
        playerAppearance.setEyeSpace(readC());
        playerAppearance.setEyeWidth(readC());
        playerAppearance.setEyeSize(readC());
        playerAppearance.setEyeShape(readC());
        playerAppearance.setEyeAngle(readC());
        playerAppearance.setBrowHeight(readC());
        playerAppearance.setBrowAngle(readC());
        playerAppearance.setBrowShape(readC());
        playerAppearance.setNose(readC());
        playerAppearance.setNoseBridge(readC());
        playerAppearance.setNoseWidth(readC());
        playerAppearance.setNoseTip(readC());
        playerAppearance.setCheek(readC());
        playerAppearance.setLipHeight(readC());
        playerAppearance.setMouthSize(readC());
        playerAppearance.setLipSize(readC());
        playerAppearance.setSmile(readC());
        playerAppearance.setLipShape(readC());
        playerAppearance.setJawHeigh(readC());
        playerAppearance.setChinJut(readC());
        playerAppearance.setEarShape(readC());
        playerAppearance.setHeadSize(readC());
        playerAppearance.setNeck(readC());
        playerAppearance.setNeckLength(readC());
        playerAppearance.setShoulderSize(readC());
        playerAppearance.setTorso(readC());
        playerAppearance.setChest(readC());
        playerAppearance.setWaist(readC());
        playerAppearance.setHips(readC());
        playerAppearance.setArmThickness(readC());
        playerAppearance.setHandSize(readC());
        playerAppearance.setLegThickness(readC());
        playerAppearance.setFootSize(readC());
        playerAppearance.setFacialRate(readC());
        readC();
        playerAppearance.setArmLength(readC());
        playerAppearance.setLegLength(readC());
        playerAppearance.setShoulders(readC());
        playerAppearance.setFaceShape(readC());
        playerAppearance.setPupilSize(readC());
        playerAppearance.setUpperTorso(readC());
        playerAppearance.setForeArmThickness(readC());
        playerAppearance.setHandSpan(readC());
        playerAppearance.setCalfThickness(readC());
        readC();
        readC();
        readC();
        playerAppearance.setHeight(readF());
        create_protocol_47 = readC();
        isJumping = readC();
    }
	
    @Override
    protected void runImpl() {
        AionConnection client = getConnection();
        Account account = client.getAccount();
        if (client.getActivePlayer() != null) {
            return;
        } if (create_protocol_47 == 1) {
            client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.RESPONSE_CREATE_NEW));
            return;
        } if (account.getMembership() >= MembershipConfig.CHARACTER_ADDITIONAL_ENABLE) {
            if (MembershipConfig.CHARACTER_ADDITIONAL_COUNT <= account.size()) {
                client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.RESPONSE_SERVER_LIMIT_EXCEEDED));
                IDFactory.getInstance().releaseId(playerCommonData.getPlayerObjId());
                return;
            }
        } else if (GSConfig.CHARACTER_LIMIT_COUNT <= account.size()) {
            client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.RESPONSE_SERVER_LIMIT_EXCEEDED));
            IDFactory.getInstance().releaseId(playerCommonData.getPlayerObjId());
            return;
        } if (!PlayerService.isFreeName(playerCommonData.getName())) {
            if (GSConfig.CHARACTER_CREATION_MODE == 2) {
                client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.RESPONSE_NAME_RESERVED));
            } else {
                client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.RESPONSE_NAME_ALREADY_USED));
            }
            IDFactory.getInstance().releaseId(playerCommonData.getPlayerObjId());
            return;
        } if (PlayerService.isOldName(playerCommonData.getName())) {
            client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.RESPONSE_NAME_ALREADY_USED));
            IDFactory.getInstance().releaseId(playerCommonData.getPlayerObjId());
            return;
        } if (!NameRestrictionService.isValidName(playerCommonData.getName())) {
            client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.RESPONSE_INVALID_NAME));
            IDFactory.getInstance().releaseId(playerCommonData.getPlayerObjId());
            return;
        } if (NameRestrictionService.isForbiddenWord(playerCommonData.getName())) {
            client.sendPacket(new SM_CREATE_CHARACTER(null, 9));
            IDFactory.getInstance().releaseId(playerCommonData.getPlayerObjId());
            return;
        } if (GSConfig.CHARACTER_CREATION_MODE == 0) {
            for (PlayerAccountData data : account.getSortedAccountsList()) {
                if (data.getPlayerCommonData().getRace() != playerCommonData.getRace()) {
                    client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.FAILED_TO_CREATE_THE_CHARACTER));
                    IDFactory.getInstance().releaseId(playerCommonData.getPlayerObjId());
                    return;
                }
            }
        }
        Player player;
		if (!playerCommonData.getPlayerClass().isStartingClass()) {
			player = PlayerService.newPlayerJumping(playerCommonData, playerAppearance, account);
		} else {
            player = PlayerService.newPlayer(playerCommonData, playerAppearance, account);
        } if (!PlayerService.storeNewPlayer(player, account.getName(), account.getId())) {
            client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.RESPONSE_DB_ERROR));
            IDFactory.getInstance().releaseId(playerCommonData.getPlayerObjId());
        } else {
            List<Item> equipment = DAOManager.getDAO(InventoryDAO.class).loadEquipment(player.getObjectId());
            PlayerAccountData accPlData = new PlayerAccountData(playerCommonData, null, playerAppearance, equipment, null);
            accPlData.setCreationDate(new Timestamp(System.currentTimeMillis()));
            PlayerService.storeCreationTime(player.getObjectId(), accPlData.getCreationDate());
            account.addPlayerAccountData(accPlData);
            client.sendPacket(new SM_CREATE_CHARACTER(accPlData, SM_CREATE_CHARACTER.RESPONSE_OK));
            client.sendPacket(new SM_ACCOUNT_TYPE(account));
			if (!playerCommonData.getPlayerClass().isStartingClass()) {
				DAOManager.getDAO(PlayerQuestListDAO.class).store(player);
				getConnection().close(new SM_QUIT_RESPONSE(), false);
			}
        }
    }
}