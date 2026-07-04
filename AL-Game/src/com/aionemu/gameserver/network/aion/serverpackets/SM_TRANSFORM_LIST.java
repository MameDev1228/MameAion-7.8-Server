package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.account.AccountTransfo;
import com.aionemu.gameserver.model.account.AccountTransformList;
import com.aionemu.gameserver.model.account.TransformCollection;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import javolution.util.FastList;

import java.util.List;
import java.util.Map;

public class SM_TRANSFORM_LIST extends AionServerPacket {
    private int action;
    private AccountTransformList transformList;
    private int cardId;
    private int result;
    private Player player;
    private int code;
    List<Integer> deleteList = new FastList<Integer>();
    List<AccountTransfo> createdTransform = new FastList<AccountTransfo>();

    public SM_TRANSFORM_LIST(int action, Player player) {
        this.action = action;
        this.transformList = player.getTransformList();
        this.player = player;

    }

    public SM_TRANSFORM_LIST(int action, Player player, int cardId, int result, int code) {
        this.action = action;
        this.transformList = player.getTransformList();
        this.cardId = cardId;
        this.result = result;
        this.player = player;
        this.code = code;
    }

    public SM_TRANSFORM_LIST(List<AccountTransfo> createdTransform, int result, int code) {
        this.action = 1;
        this.result = result;
        this.code = code;
        this.createdTransform = createdTransform;
    }

    public SM_TRANSFORM_LIST(int action, int cardId) {
        this.action = action;
        this.cardId = cardId;
    }

    public SM_TRANSFORM_LIST(int action, List<Integer> deleteList) {
        this.action = action;
        this.deleteList = deleteList;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeH(action);
        switch (action) {
            case 0: //list
                writeD(player.getLastUsedTransformation()); //last used transform
                writeC(0); //unk
                if (transformList != null && transformList.getTransformations().size() != 0) {
                    writeH(transformList.getTransformations().size());
                    for (AccountTransfo transfo : transformList.getTransformations()) {
                        writeD(transfo.getCardId());
                        writeD(transfo.getCount());
                    }
                } else {
                    writeH(0);
                }
                break;
            case 1: //contract result
                    writeH(this.result); //result 1 for contract 3 for create
                switch (result) {
                    case 1:
                        int count = 0;
                        for (AccountTransfo transfo : this.createdTransform) {
                            writeD(transfo.getCardId());
                            count++;
                        }
                        for (int i = 0; i < 10 - count; i++) {
                            writeD(0);
                        }
                        break;
                    case 3:
                        writeD(this.cardId);
                        writeD(0);
                        writeD(0);
                        writeD(0);
                        writeD(0);
                        writeD(0);
                        writeD(0);
                        writeD(0);
                        writeD(0);
                        writeD(0);
                        break;
                    }
                    writeD(this.code);
                break;
            case 2: //delete result
                writeH(this.deleteList.size()); //1
                for (Integer trans : this.deleteList) {
                    writeD(trans);
                    writeD(1); //count
                }
                break;
            case 3: //collection
                if (this.player.getTransformCollections().size() == 0) {
                    writeB(new byte[DataManager.TRANSFORM_COLLECTION_DATA.size()*4]);
                } else {
                    for (TransformCollection collection : this.player.getTransformCollections().values()) {
                        writeD(collection.getId());
                    }
                    int diff = DataManager.TRANSFORM_COLLECTION_DATA.size() - this.player.getTransformCollections().size();
                    for (int i = 0; i < diff; i++) {
                        writeD(0);
                    }
                }
                break;
        }
    }
}