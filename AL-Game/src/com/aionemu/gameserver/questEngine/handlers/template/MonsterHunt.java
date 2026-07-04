package com.aionemu.gameserver.questEngine.handlers.template;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.handlers.models.Monster;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

import javolution.util.FastMap;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MonsterHunt extends QuestHandler
{
	private final int questId;
	private final int itemId;
    private final Set<Integer> startNpcs = new HashSet<Integer>();
    private final Set<Integer> endNpcs = new HashSet<Integer>();
    private final FastMap<Monster, Set<Integer>> monsters;
    private final int startDialog;
    private final int endDialog;
    private final Set<Integer> aggroNpcs = new HashSet<Integer>();
	
    public MonsterHunt(int questId, List<Integer> startNpcIds, List<Integer> endNpcIds, FastMap<Monster, Set<Integer>> monsters, int startDialog, int endDialog, List<Integer> aggroNpcs, int itemId) {
        super(questId);
		this.questId = questId;
		this.itemId = itemId;
        this.startNpcs.addAll(startNpcIds);
        this.startNpcs.remove(0);
        if (endNpcIds == null) {
            this.endNpcs.addAll(startNpcs);
        } else {
            this.endNpcs.addAll(endNpcIds);
            this.endNpcs.remove(0);
        }
        this.monsters = monsters;
        this.startDialog = startDialog;
        this.endDialog = endDialog;
        if (aggroNpcs != null) {
            this.aggroNpcs.addAll(aggroNpcs);
            this.aggroNpcs.remove(0);
        }
    }
	
    @Override
    public void register() {
        for (Integer startNpc: startNpcs) {
            qe.registerQuestNpc(startNpc).addOnQuestStart(getQuestId());
            qe.registerQuestNpc(startNpc).addOnTalkEvent(getQuestId());
        } for (Set<Integer> monsterIds: monsters.values()) {
            for (Integer monsterId: monsterIds) {
                qe.registerQuestNpc(monsterId).addOnKillEvent(questId);
            }
        } for (Integer endNpc: endNpcs) {
            qe.registerQuestNpc(endNpc).addOnTalkEvent(getQuestId());
        } for (Integer aggroNpc: aggroNpcs) {
            qe.registerQuestNpc(aggroNpc).addOnAddAggroListEvent(getQuestId());
        }
    }
	
    @Override
    public boolean onDialogEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        int targetId = env.getTargetId();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
            if (startNpcs.isEmpty() || startNpcs.contains(targetId)) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, startDialog != 0 ? startDialog : 1011);
                } else {
					if (itemId != 0) {
						giveQuestItem(env, itemId, 1);
					}
					return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            for (Monster mi: monsters.keySet()) {
                int endVar = mi.getEndVar();
                int varId = mi.getVar();
                int total = 0;
                do {
                    int currentVar = qs.getQuestVarById(varId);
                    total += currentVar << ((varId - mi.getVar()) * 6);
                    endVar >>= 6;
                    varId++;
                } while (endVar > 0);
                if (mi.getEndVar() > total) {
                    return false;
                }
            } if (endNpcs.contains(targetId)) {
				if (endDialog != 0) {
					switch (env.getDialog()) {
						case USE_OBJECT: {
							return sendQuestDialog(env, endDialog);
						} case SELECT_REWARD: {
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							return sendQuestDialog(env, 5);
						}
						default:
						break;
					}
				} else {
					switch (env.getDialog()) {
						case START_DIALOG: {
							return sendQuestDialog(env, 1352);
						} case SELECT_REWARD: {
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							return sendQuestDialog(env, 5);
						}
						default:
						break;
					}
				}
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (endNpcs.contains(targetId)) {
                if (!aggroNpcs.isEmpty()) {
                    switch (env.getDialog()) {
                        case USE_OBJECT:
                            return sendQuestDialog(env, 10002);
                        case SELECT_REWARD:
						    removeQuestItem(env, itemId, 1);
                            return sendQuestDialog(env, 5);
                        default:
                            return sendQuestEndDialog(env);
                    }
                } else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }
	
    @Override
    public boolean onKillEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            for (Monster m: monsters.keySet()) {
                if (m.getNpcIds().contains(env.getTargetId())) {
                    int endVar = m.getEndVar();
                    int varId = m.getVar();
                    int total = 0;
                    do {
                        int currentVar = qs.getQuestVarById(varId);
                        total += currentVar << ((varId - m.getVar()) * 6);
                        endVar >>= 6;
                        varId++;
                    } while (endVar > 0);
                    total += 1;
                    if (total <= m.getEndVar()) {
                        if (!aggroNpcs.isEmpty()) {
                            qs.setStatus(QuestStatus.REWARD);
                            updateQuestStatus(env);
                        } else {
                            for (int varsUsed = m.getVar(); varsUsed < varId; varsUsed++) {
                                int value = total & 0x3F;
                                total >>= 6;
                                qs.setQuestVarById(varsUsed, value);
                            }
                            int var = qs.getQuestVarById(0);
                           	int var1 = qs.getQuestVarById(1);
                           	if (var == 0 && m.getVar() == 1 && varId == 2 && var1 == m.getEndVar()) {
                           		qs.setQuestVarById(0, 1);
                           		qs.setStatus(QuestStatus.REWARD);
                           		updateQuestStatus(env);
                           	} else {
                            	updateQuestStatus(env);
                            }
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
	
    @Override
    public boolean onAddAggroListEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
            QuestService.startQuest(env);
            return true;
        }
        return false;
    }
}