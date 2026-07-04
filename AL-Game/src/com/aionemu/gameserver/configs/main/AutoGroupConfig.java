/*
 * This file is part of Encom. **ENCOM FUCK OTHER SVN**
 *
 *  Encom is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Encom is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with Encom.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class AutoGroupConfig
{
	@Property(key = "gameserver.autogroup.enable", defaultValue = "true")
	public static boolean AUTO_GROUP_ENABLED;
	
	//IDGEL DOME
	@Property(key = "gameserver.idgel.dome.timer", defaultValue = "120")
	public static long IDGEL_TIMER;
	@Property(key = "gameserver.idgel.dome.timer2", defaultValue = "360")
	public static long IDGEL_TIMER_2;
	@Property(key = "gameserver.idgel.dome.enable", defaultValue = "true")
	public static boolean IDGEL_ENABLED;
	@Property(key = "gameserver.idgel.dome.schedule.midday", defaultValue = "0 0 12 ? * TUE,THU,SUN *")
	public static String IDGEL_SCHEDULE_MIDDAY;
	@Property(key = "gameserver.idgel.dome.schedule.night", defaultValue = "0 0 20 ? * TUE,THU,SUN *")
	public static String IDGEL_SCHEDULE_NIGHT;
	
	//ILLUMIEL BRAWL 6.x
	@Property(key = "gameserver.illumiel.timer", defaultValue = "240")
	public static long ILLUMIEL_TIMER;
	@Property(key = "gameserver.illumiel.timer2", defaultValue = "420")
	public static long ILLUMIEL_TIMER_2;
	@Property(key = "gameserver.illumiel.enable", defaultValue = "true")
	public static boolean ILLUMIEL_ENABLED;
	@Property(key = "gameserver.illumiel.schedule.midday", defaultValue = "0 0 11 ? * MON-SUN *")
	public static String ILLUMIEL_SCHEDULE_MIDDAY;
	@Property(key = "gameserver.illumiel.schedule.night", defaultValue = "0 0 19 ? * MON-SUN *")
	public static String ILLUMIEL_SCHEDULE_NIGHT;
	
	//KAMAR BATTLEFIELD
	@Property(key = "gameserver.kamar.timer", defaultValue = "120")
	public static long KAMAR_TIMER;
	@Property(key = "gameserver.kamar.timer2", defaultValue = "360")
	public static long KAMAR_TIMER_2;
	@Property(key = "gameserver.kamar.enable", defaultValue = "true")
	public static boolean KAMAR_ENABLED;
	@Property(key = "gameserver.kamar.schedule.midday", defaultValue = "0 0 12 ? * SAT *")
	public static String KAMAR_SCHEDULE_MIDDAY;
	@Property(key = "gameserver.kamar.schedule.night", defaultValue = "0 0 20 ? * SAT *")
	public static String KAMAR_SCHEDULE_NIGHT;
	
	//ASHUNATAL DREDGION
	@Property(key = "gameserver.ashunatal.timer", defaultValue = "120")
	public static long ASHUNATAL_TIMER;
	@Property(key = "gameserver.ashunatal.timer2", defaultValue = "360")
	public static long ASHUNATAL_TIMER_2;
	@Property(key = "gameserver.ashunatal.enable", defaultValue = "true")
	public static boolean ASHUNATAL_ENABLED;
	@Property(key = "gameserver.ashunatal.schedule.midday", defaultValue = "0 0 12 ? * MON,WED,SUN *")
	public static String ASHUNATAL_SCHEDULE_MIDDAY;
	@Property(key = "gameserver.ashunatal.schedule.night", defaultValue = "0 0 20 ? * MON,WED,SUN *")
	public static String ASHUNATAL_SCHEDULE_NIGHT;
}