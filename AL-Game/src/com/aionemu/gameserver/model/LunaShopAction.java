package com.aionemu.gameserver.model;

import gnu.trove.map.hash.TIntObjectHashMap;

public enum LunaShopAction
{
    INSTANCE_TELEPORT(0),
    CRAFT(2),
    CRAFTBOX(3),
    BUY_ITEMS(4),
    SIEGE(5),
    TELEPORT(6),
    TELEPORT_2(7),
    WARDROBE(8),
    WARDROBE_SLOT(9),
    WARDROBE_ITEM(10),
    CHANGE_OUTFIT(11),
    TREASURE(12),
    TAKI_ADVENTURE(14),
    DICE_ROLL(15),
    DICE_REWARD(16),
    UNKNOWN(255);
	
    private static TIntObjectHashMap<LunaShopAction> lunaActions;
	
    static {
        lunaActions = new TIntObjectHashMap<LunaShopAction>();
        for (LunaShopAction action : values()) {
            lunaActions.put(action.getActionId(), action);
        }
    }
	
    private int actionId;
	
    private LunaShopAction(int actionId) {
        this.actionId = actionId;
    }
	
    public int getActionId() {
        return actionId;
    }
	
    public static LunaShopAction getActionById(int actionId) {
        LunaShopAction action = lunaActions.get(actionId);
        return action != null ? action : UNKNOWN;
    }
}