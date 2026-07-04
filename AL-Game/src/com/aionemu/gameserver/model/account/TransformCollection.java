package com.aionemu.gameserver.model.account;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.minion.MinionBuff;
import com.aionemu.gameserver.model.templates.transform_book.TransformCollectionTemplate;

public class TransformCollection {

    private final int id;
    private final TransformCollectionTemplate template;
    private final TransformlCollectionBuff cb;

    public TransformCollection(int id) {
        this.id = id;
        this.template = DataManager.TRANSFORM_COLLECTION_DATA.getTransformCollectionById(this.id);
        this.cb = new TransformlCollectionBuff();
    }

    public int getId() {
        return id;
    }

    public TransformlCollectionBuff getCb() {
        return cb;
    }
}
