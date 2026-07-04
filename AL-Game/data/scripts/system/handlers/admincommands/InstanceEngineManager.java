package admincommands;

import java.util.concurrent.CountDownLatch;

import com.aionemu.gameserver.instance.InstanceEngine;
import com.aionemu.gameserver.model.GameEngine;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

public class InstanceEngineManager extends AdminCommand
{
	private static final String COMMAND_START = "start";
	private static final String COMMAND_STOP = "stop";
	private static final String COMMAND_RESTART = "restart";
	
	public InstanceEngineManager() {
		super("instance");
	}
	
	@Override
	public void execute(final Player player, String... params) {
		final GameEngine[] parallelEngines = { InstanceEngine.getInstance() };
		final CountDownLatch progressLatch = new CountDownLatch(parallelEngines.length);
		if (params.length == 0) {
			showHelp(player);
			return;
		} if (COMMAND_STOP.equalsIgnoreCase(params[0]) || COMMAND_START.equalsIgnoreCase(params[0]) || COMMAND_RESTART.equalsIgnoreCase(params[0])) {
			if (COMMAND_START.equalsIgnoreCase(params[0])) {
				InstanceEngine.getInstance().load(progressLatch);
				PacketSendUtility.sendMessage(player, "InstanceEngine loaded successfully!");
			} if (COMMAND_STOP.equalsIgnoreCase(params[0])) {
				InstanceEngine.getInstance().shutdown();
				PacketSendUtility.sendMessage(player, "InstanceEngine shutdown successfully!");
			} if (COMMAND_RESTART.equalsIgnoreCase(params[0])) {
				InstanceEngine.getInstance().shutdown();
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						InstanceEngine.getInstance().load(progressLatch);
						PacketSendUtility.sendMessage(player, "InstanceEngine reloaded successfully!");
					}
				}, 5000);
			}
		}
	}
	
	protected void showHelp(Player player) {
		PacketSendUtility.sendMessage(player, "AdminCommand //instance start|stop");
	}
}
