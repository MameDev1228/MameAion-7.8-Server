package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.services.player.LumielTransformService;
import javolution.util.FastMap;

import java.util.Map;

public class CM_LUMIEL_TRANSFORM extends AionClientPacket {

    private int actionId;
    private int lumielId;
    private int matCount;
    private Player player;
    int objectId;
    long count;
    private Map<Integer, Long> matrials = new FastMap<Integer, Long>();

    public CM_LUMIEL_TRANSFORM(int opcode, AionConnection.State state, AionConnection.State... restStates) {
        super(opcode, state, restStates);
    }

    @Override
    protected void readImpl() {
        this.player = getConnection().getActivePlayer();
        this.actionId = readH(); //2 put item
        switch (actionId) {
            case 1:
                break;
            case 2: //put
                this.lumielId = readD(); //lumiel Id
                this.matCount =  readH(); //material count
                for (int i = 0; i < matCount; i++) {
                    this.objectId = readD();
                    this.count = readQ();
                    this.matrials.put(this.objectId, this.count);
                }
                break;
            case 3: //reward genrate
                this.lumielId = readD();
                break;
            case 4: //win reward
                this.lumielId = readD();
                break;
        }

    }

    @Override
    protected void runImpl() {
        switch (this.actionId) {
            case 1:
                LumielTransformService.getInstance().sendLumielPacket(this.player);
                break;
            case 2:
                LumielTransformService.getInstance().onRewardPoints(this.player, this.lumielId, this.matrials);
                break;
            case 3:
                LumielTransformService.getInstance().onGenerateReward(this.player, this.lumielId);
                break;
            case 4:
                LumielTransformService.getInstance().onRewardPlayer(this.player, this.lumielId);
                break;
        }
    }
}
