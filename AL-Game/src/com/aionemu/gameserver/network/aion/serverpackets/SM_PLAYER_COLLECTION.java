package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.collection.PlayerCollection;
import com.aionemu.gameserver.model.gameobjects.player.collection.PlayerCollectionInfos;
import com.aionemu.gameserver.model.templates.collection.CollectionType;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_PLAYER_COLLECTION extends AionServerPacket {

    private final Player player;

    public SM_PLAYER_COLLECTION(Player player) {
        this.player = player;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        PlayerCollection playerCollection = this.player.getPlayerCollection();

        //common
        writeH(playerCollection.getCollectionInfos().get(CollectionType.COMMON).getLevel());
        writeD(playerCollection.getCollectionInfos().get(CollectionType.COMMON).getExp());
        //ancien
        writeH(playerCollection.getCollectionInfos().get(CollectionType.ANCIENT).getLevel());
        writeD(playerCollection.getCollectionInfos().get(CollectionType.ANCIENT).getExp());
        //relic
        writeH(playerCollection.getCollectionInfos().get(CollectionType.RELIC).getLevel());
        writeD(playerCollection.getCollectionInfos().get(CollectionType.RELIC).getExp());
        //event
        writeH(playerCollection.getCollectionInfos().get(CollectionType.EVENT).getLevel());
        writeD(playerCollection.getCollectionInfos().get(CollectionType.EVENT).getExp());
        //for later ?
        writeH(0);
        writeD(50);

    }
}
