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
package com.aionemu.gameserver.configs.shedule;

import com.aionemu.commons.utils.xml.JAXBUtil;
import org.apache.commons.io.FileUtils;

import javax.xml.bind.annotation.*;
import java.io.File;
import java.util.List;

/**
 * @author Rinzler (Encom)
 */

@XmlRootElement(name = "dynamic_schedule")
@XmlAccessorType(XmlAccessType.FIELD)
public class DynamicSchedule
{
	@XmlElement(name = "dynamic", required = true)
	private List<Dynamic> dynamicsList;
	
	public List<Dynamic> getDynamicsList() {
		return dynamicsList;
	}
	
	public void setFightsList(List<Dynamic> dynamicList) {
		this.dynamicsList = dynamicList;
	}
	
	public static DynamicSchedule load() {
		DynamicSchedule ds;
		try {
			String xml = FileUtils.readFileToString(new File("./config/shedule/dynamic_schedule.xml"));
			ds = (DynamicSchedule) JAXBUtil.deserialize(xml, DynamicSchedule.class);
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize dynamic rift", e);
		}
		return ds;
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "dynamic")
	public static class Dynamic {
		@XmlAttribute(required = true)
		private int id;
		
		@XmlElement(name = "riftTime", required = true)
		private List<String> riftTimes;
		
		public int getId() {
			return id;
		}
		
		public void setId(int id) {
			this.id = id;
		}
		
		public List<String> getRiftTimes() {
			return riftTimes;
		}
		
		public void setRiftTimes(List<String> riftTimes) {
			this.riftTimes = riftTimes;
		}
	}
}