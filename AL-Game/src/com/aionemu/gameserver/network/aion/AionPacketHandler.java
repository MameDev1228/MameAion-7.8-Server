/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 *  Aion-Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion-Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details. *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion-Lightning.
 *  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.administration.DeveloperConfig;
import com.aionemu.gameserver.configs.network.NetworkConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.packet.PacketAuditService;
import com.aionemu.gameserver.services.packet.ProtocolTraceService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;

/**
 * @author -Nemesiss-
 * @author Luno
 * @author GiGatR00n
 */
public class AionPacketHandler {

	/**
	 * logger for this class
	 */
	private static final Logger log = LoggerFactory.getLogger(AionPacketHandler.class);
	private static final Object UNKNOWN_PACKET_LOG_LOCK = new Object();
	private static final SimpleDateFormat UNKNOWN_PACKET_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private Map<Integer, AionClientPacket> packetsPrototypes = new HashMap<Integer, AionClientPacket>();

	/**
	 * Reads one packet from given ByteBuffer
	 *
	 * @param data
	 * @param client
	 * @return AionClientPacket object from binary data
	 */
	public AionClientPacket handle(ByteBuffer data, AionConnection client) {
		State state = client.getState();
		if (data == null || data.remaining() < 5) {
			invalidPacket(state, data, client, "too short for 7.x opcode header");
			return null;
		}
		int id = data.getShort() & 0xffff;
		/* Second opcode / 7.x client header bytes. */
		if (data.remaining() < 3) {
			invalidPacket(state, data, client, "missing secondary opcode/header bytes for opcode 0x" + String.format("%04X", id));
			return null;
		}
		data.position(data.position() + 3);

		return getPacket(state, id, data, client);
	}

	public void addPacketPrototype(AionClientPacket packetPrototype) {
		packetsPrototypes.put(packetPrototype.getOpcode(), packetPrototype);
	}

	private AionClientPacket getPacket(State state, int id, ByteBuffer buf, AionConnection con) {
		AionClientPacket prototype = packetsPrototypes.get(id);

		if (prototype == null) {
			unknownPacket(state, id, buf, con);
			return null;
		}

		ProtocolTraceService.getInstance().recordClient(con, prototype.getPacketName(), id, state, buf, "known_opcode");

		/**
		 * Display Packets Name + Hex-Bytes in Chat Window
		 */
		Player player = con.getActivePlayer();

		if (con.getState().equals(State.IN_GAME) && player != null && player.getAccessLevel() >= DeveloperConfig.SHOW_PACKETS_INCHAT_ACCESSLEVEL) {
			if (isPacketFilterd(DeveloperConfig.FILTERED_PACKETS_INCHAT, prototype.getPacketName())) {
				if (DeveloperConfig.SHOW_PACKET_BYTES_INCHAT) {
					String PckName = String.format("0x%04X : %s", id, prototype.getPacketName());
					PacketSendUtility.sendMessage(player, "********************************************");
					PacketSendUtility.sendMessage(player, PckName);
					PacketSendUtility.sendMessage(player, Util.toHexStream(getByteBuffer(buf, DeveloperConfig.TOTAL_PACKET_BYTES_INCHAT)));
					buf.position(5);

				}
				else if (DeveloperConfig.SHOW_PACKET_NAMES_INCHAT) {
					String PckName = String.format("0x%04X : %s", id, prototype.getPacketName());
					PacketSendUtility.sendMessage(player, PckName);
				}
			}
		}
		AionClientPacket res = prototype.clonePacket();
		res.setBuffer(buf);
		res.setConnection(con);

		Player activePlayer = con.getActivePlayer();
		if (con.getState().equals(State.IN_GAME) && activePlayer != null && activePlayer.getPlayerAccount().getMembership() == 10) {
			PacketSendUtility.sendMessage(activePlayer, "0x" + Integer.toHexString(res.getOpcode()).toUpperCase() + " : " + res.getPacketName());
		}
		return res;
	}

	private boolean isPacketFilterd(String filterlist, String PacketName) {

		// If FilterList was empty, all packets will be shown.
		if (filterlist == null || filterlist.equalsIgnoreCase("*"))
			return true;

		String[] Parts = null;
		Parts = filterlist.trim().split(",");

		for (String p : Parts) {
			if (p.trim().equalsIgnoreCase(PacketName)) {
				return true;
			}
		}
		return false;
	}

	private ByteBuffer getByteBuffer(ByteBuffer buf, int count) {

		count = (count <= buf.capacity()) ? count : buf.capacity();
		ByteBuffer tmpBuffer = buf.asReadOnlyBuffer();
		tmpBuffer.position(5);
		tmpBuffer.limit(count);

		// Create an empty ByteBuffer with a Requested Capacity.
		ByteBuffer PckBuffer = ByteBuffer.allocate(count);
		try {
			do {
				PckBuffer.put(tmpBuffer.get());
			}
			while (tmpBuffer.remaining() > 0);
		}
		catch (Exception e) {
			// e.printStackTrace();
		}
		PckBuffer.position(0);
		return PckBuffer;
	}

	/**
	 * Logs unknown packet.
	 *
	 * @param state
	 * @param id
	 * @param data
	 */
	private void unknownPacket(State state, int id, ByteBuffer data, AionConnection con) {
		if (!NetworkConfig.DISPLAY_UNKNOWNPACKETS) {
			return;
		}

		ByteBuffer fullPacket = data.asReadOnlyBuffer();
		fullPacket.position(0);
		String hex = Util.toHex(fullPacket);
		String playerInfo = getConnectionInfo(con);
		ProtocolTraceService.getInstance().recordClient(con, "UNKNOWN_C2S", id, state, data, "unregistered_opcode");
		String message = String.format("Unknown packet received from Aion client: 0x%04X, state=%s, %s%n%s", id, state.toString(), playerInfo, hex);
		log.warn(message);
		appendUnknownPacketFile(id, state, playerInfo, hex);
		PacketAuditService.getInstance().logUnknown(id, state, con, data);
	}

	private void invalidPacket(State state, ByteBuffer data, AionConnection con, String reason) {
		ByteBuffer fullPacket = data != null ? data.asReadOnlyBuffer() : null;
		String hex = "<no buffer>";
		if (fullPacket != null) {
			fullPacket.position(0);
			hex = Util.toHex(fullPacket);
		}
		String playerInfo = getConnectionInfo(con);
		ProtocolTraceService.getInstance().recordClient(con, "INVALID_C2S", 0, state, data, reason);
		ProtocolTraceService.getInstance().dump(con, "INVALID_C2S_" + reason, null);
		String message = String.format("Invalid packet received from Aion client: reason=%s, state=%s, %s%n%s", reason, state.toString(), playerInfo, hex);
		log.warn(message);
		appendPacketFile("invalid_packets.log", 0, state, playerInfo, reason, hex);
		PacketAuditService.getInstance().logInvalid(state, con, reason, data);
	}

	private String getConnectionInfo(AionConnection con) {
		if (con == null) {
			return "connection=null";
		}
		Player player = con.getActivePlayer();
		String playerName = player != null ? player.getName() : "-";
		int objectId = player != null ? player.getObjectId() : 0;
		String accountName = con.getAccount() != null ? con.getAccount().getName() : "-";
		return "ip=" + con.getIP() + ", account=" + accountName + ", player=" + playerName + ", objectId=" + objectId;
	}

	private void appendUnknownPacketFile(int id, State state, String playerInfo, String hex) {
		appendPacketFile("unknown_packets.log", id, state, playerInfo, null, hex);
	}

	private void appendPacketFile(String fileName, int id, State state, String playerInfo, String reason, String hex) {
		synchronized (UNKNOWN_PACKET_LOG_LOCK) {
			File dir = new File("log/packets");
			if (!dir.exists() && !dir.mkdirs()) {
				return;
			}
			File file = new File(dir, fileName);
			try (FileWriter writer = new FileWriter(file, true)) {
				writer.write("[" + UNKNOWN_PACKET_DATE.format(new Date()) + "] ");
				if (reason == null) {
					writer.write(String.format("opcode=0x%04X state=%s %s%n", id, state.toString(), playerInfo));
				} else {
					writer.write(String.format("state=%s reason=%s %s%n", state.toString(), reason, playerInfo));
				}
				writer.write(hex);
				writer.write(System.lineSeparator());
				writer.write(System.lineSeparator());
			} catch (IOException e) {
				log.warn("Failed to write packet audit log file " + fileName, e);
			}
		}
	}
}
