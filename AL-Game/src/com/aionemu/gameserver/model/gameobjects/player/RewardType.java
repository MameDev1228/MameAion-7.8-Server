package com.aionemu.gameserver.model.gameobjects.player;

import com.aionemu.gameserver.model.stats.container.StatEnum;

public enum RewardType
{
	AP_PLAYER {
		@Override
		public long calcReward(Player player, long reward) {
			float statRate = player.getGameStats().getStat(StatEnum.AP_BOOST, 100).getCurrent() / 100f;
			return (long) (reward * player.getRates().getApPlayerGainRate() * statRate);
		}
	},
	AP_NPC {
		@Override
		public long calcReward(Player player, long reward) {
			float statRate = player.getGameStats().getStat(StatEnum.AP_BOOST, 100).getCurrent() / 100f;
			return (long) (reward * player.getRates().getApNpcRate() * statRate);
		}
	},
	HUNTING {
		@Override
		public long calcReward(Player player, long reward) {
			float statRate = player.getGameStats().getStat(StatEnum.BOOST_HUNTING_XP_RATE, 100).getCurrent() / 100f;
			return (long) (reward * statRate);
		}
	},
	GROUP_HUNTING {
		@Override
		public long calcReward(Player player, long reward) {
			float statRate = player.getGameStats().getStat(StatEnum.BOOST_GROUP_HUNTING_XP_RATE, 100).getCurrent() / 100f;
			return (long) (reward * statRate);
		}
	},
	PVP_KILL {
		@Override
		public long calcReward(Player player, long reward) {
			return (reward);
		}
	},
	QUEST {
		@Override
		public long calcReward(Player player, long reward) {
			return reward;
		}
	},
	CRAFTING {
		@Override
		public long calcReward(Player player, long reward) {
			return reward;
		}
	},
	GATHERING {
		@Override
		public long calcReward(Player player, long reward) {
			return reward;
		}
	};
	public abstract long calcReward(Player player, long reward);
}