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

package com.aionemu.commons.utils;

/**
 * @author KID, -Nemesiss-
 */
public class NetworkUtils {

	/**
	 * check if IP address match pattern
	 * 
	 * @param pattern
	 *            *.*.*.* , 192.168.1.0-255 , *
	 * @param address
	 *            - 192.168.1.1<BR>
	 *            <code>address = 10.2.88.12  pattern = *.*.*.*   result: true<BR>
	 *                address = 10.2.88.12  pattern = *   result: true<BR>
	 *                address = 10.2.88.12  pattern = 10.2.88.12-13   result: true<BR>
	 *                address = 10.2.88.12  pattern = 10.2.88.13-125   result: false<BR></code>
	 * @return true if address match pattern
	 */
	public static boolean checkIPMatching(String pattern, String address) {
		if (pattern == null || address == null)
			return false;

		pattern = pattern.trim();
		address = address.trim();
		if (pattern.equals("*.*.*.*") || pattern.equals("*"))
			return true;

		String[] mask = pattern.split("\\.");
		String[] ip_address = address.split("\\.");
		if (mask.length != ip_address.length)
			return false;

		for (int i = 0; i < mask.length; i++) {
			if (mask[i].equals("*"))
				continue;
			else if (mask[i].contains("-")) {
				int min = Integer.parseInt(mask[i].split("-")[0]);
				int max = Integer.parseInt(mask[i].split("-")[1]);
				int ip = Integer.parseInt(ip_address[i]);
				if (ip < min || ip > max)
					return false;
			} else if (parseOctet(mask[i]) == parseOctet(ip_address[i])) {
				continue;
			} else
				return false;
		}
		return true;
	}

	private static int parseOctet(String octet) {
		return Integer.parseInt(octet.trim());
	}
}
