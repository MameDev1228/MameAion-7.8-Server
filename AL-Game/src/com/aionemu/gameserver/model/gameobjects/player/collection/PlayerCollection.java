package com.aionemu.gameserver.model.gameobjects.player.collection;

import com.aionemu.gameserver.model.templates.collection.CollectionType;
import javolution.util.FastList;
import javolution.util.FastMap;

import java.util.List;
import java.util.Map;

public class PlayerCollection {

    private Map<CollectionType, PlayerCollectionInfos> collectionInfos = new FastMap<CollectionType, PlayerCollectionInfos>();
    private List<PlayerCollectionEntry> completeCollection = new FastList<PlayerCollectionEntry>();
    private Map<Integer, PlayerCollectionEntry> playerCollectionEntry = new FastMap<Integer, PlayerCollectionEntry>();

    public List<PlayerCollectionEntry> getCompleteCollection() {
        return completeCollection;
    }

    public void setCompleteCollection(List<PlayerCollectionEntry> completeCollection) {
        this.completeCollection = completeCollection;
    }

    public Map<Integer, PlayerCollectionEntry> getPlayerCollectionEntry() {
        return playerCollectionEntry;
    }

    public void setPlayerCollectionEntry(Map<Integer, PlayerCollectionEntry> playerCollectionEntry) {
        this.playerCollectionEntry = playerCollectionEntry;
    }

    public Map<CollectionType, PlayerCollectionInfos> getCollectionInfos() {
        return collectionInfos;
    }

    public void setCollectionInfos(Map<CollectionType, PlayerCollectionInfos> collectionInfos) {
        this.collectionInfos = collectionInfos;
    }
}
