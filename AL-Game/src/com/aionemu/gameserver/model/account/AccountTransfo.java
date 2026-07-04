package com.aionemu.gameserver.model.account;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.transform_book.TransformBookTemplate;

public class AccountTransfo
{
    private TransformBookTemplate template;
    private int cardId;
    private int count;
	
    public AccountTransfo(int cardId, int count) {
        this.template = DataManager.TRANSFORM_BOOK_DATA.getTransformBookById(cardId);
        this.cardId = cardId;
        this.count = count;
    }
	
    public TransformBookTemplate getTemplate() {
        return template;
    }
	
    public int getCardId() {
        return cardId;
    }
	
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}