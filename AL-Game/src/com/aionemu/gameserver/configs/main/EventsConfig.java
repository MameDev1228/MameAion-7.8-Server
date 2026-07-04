package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class EventsConfig
{
	@Property(key = "gameserver.event.enable", defaultValue = "false")
	public static boolean EVENT_ENABLED;
	@Property(key = "gameserver.enable.decor", defaultValue = "0")
    public static int ENABLE_DECOR;
	@Property(key = "gameserver.event.service.enable", defaultValue = "false")
	public static boolean ENABLE_EVENT_SERVICE;

	// MameAion: keep login clean while validating 7.7/CC2 stats/damage.
	@Property(key = "gameserver.boost.event.enable", defaultValue = "false")
	public static boolean ENABLE_BOOST_EVENT;
	@Property(key = "gameserver.service.security.buff.enable", defaultValue = "false")
	public static boolean ENABLE_SECURITY_SERVICE_BUFF;
	@Property(key = "gameserver.service.pc.cafe.buff.enable", defaultValue = "false")
	public static boolean ENABLE_PC_CAFE_LOGIN_BUFF;
	@Property(key = "gameserver.f2p.auto.benefits.enable", defaultValue = "false")
	public static boolean ENABLE_F2P_AUTO_BENEFITS;
	@Property(key = "gameserver.cash.buff.packet.enable", defaultValue = "false")
	public static boolean ENABLE_CASH_BUFF_PACKET;
	@Property(key = "gameserver.account.benefit.packets.enable", defaultValue = "false")
	public static boolean ENABLE_ACCOUNT_BENEFIT_PACKETS;
	
	//VIP Tickets.
	@Property(key = "gameserver.vip.tickets.enable", defaultValue = "false")
	public static boolean ENABLE_VIP_TICKETS;
	@Property(key = "gameserver.vip.tickets.time", defaultValue = "60")
	public static int VIP_TICKETS_PERIOD;
	
	//Event Awake [Event JAP]
	@Property(key = "gameserver.event.awake.enable", defaultValue = "false")
	public static boolean ENABLE_AWAKE_EVENT;
	@Property(key = "gameserver.event.seed.transformation.time", defaultValue = "60")
	public static int SEED_TRANSFORMATION_PERIOD;
	
	//Upgrade Arcade 4.7
	@Property(key="gameserver.upgrade.arcade.chance", defaultValue = "50")
	public static int EVENT_ARCADE_CHANCE;
	
	//Event Window.
	@Property(key = "gameserver.event.window.enable", defaultValue = "true")
	public static boolean ENABLE_EVENT_WINDOW;
}