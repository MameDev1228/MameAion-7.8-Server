package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.services.player.PlayerCollectionService;

public class CM_COLLECTION_REGISTER extends AionClientPacket {

    public int id;
    public int index;
    public int objectId;
    public int count;

    public CM_COLLECTION_REGISTER(int opcode, AionConnection.State state, AionConnection.State... restStates) {
        super(opcode, state, restStates);
    }

    @Override
    protected void readImpl() {
        this.id = readD();
        this.index = readC();
        this.objectId = readD();
        this.count = readD();
    }

    @Override
    protected void runImpl() {
        Player player = getConnection().getActivePlayer();
        PlayerCollectionService.getInstance().registerCollection(player, this.id, this.index, this.objectId, this.count);
    }
}
