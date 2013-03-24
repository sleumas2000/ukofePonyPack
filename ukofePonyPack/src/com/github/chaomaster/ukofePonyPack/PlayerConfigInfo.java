/**
 * Copyright (C) 2013 chao-master
 * 
 * This file is part of ukofePonyPack.
 * 
 *     ukofePonyPack is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     ukofePonyPack is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with ukofePonyPack.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.chaomaster.ukofePonyPack;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PlayerConfigInfo {
	PonyType type = PonyType.UNSET;
	Date expire;
	boolean forever = false;

	PlayerConfigInfo(PonyType t) {
		this.type = t;
		this.forever = true;
	}

	PlayerConfigInfo(PonyType t, int d) {
		this.type = t;
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(5, d);
		this.expire = cal.getTime();
	}
	
	PlayerConfigInfo(PonyType t,int d,boolean f){
		this.type = t;
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(5, d);
		this.expire = cal.getTime();
		this.forever = f;
	}

	PlayerConfigInfo() {
		this.expire = new Date(0L);
	}

	PlayerConfigInfo(Map<String, Object> values) {
		this.type = (values.containsKey("type") ? PonyType
				.valueOf((String) values.get("type")) : PonyType.UNSET);
		this.expire = (values.containsKey("expire") ? (Date) values
				.get("expire") : new Date());
		this.forever = (values.containsKey("forever") ? ((Boolean) values
				.get("forever")).booleanValue() : false);
	}

	public HashMap<String, Object> map() {
		HashMap<String, Object> r = new HashMap<String, Object>();
		r.put("type", this.type.name());
		r.put("expire", this.expire);
		r.put("forever", Boolean.valueOf(this.forever));
		return r;
	}

	public boolean hasExpired() {
		if(this.type == PonyType.ERROR){return true;} //TODO maybe remove
		if(this.forever){return false;}
		return new Date().after(this.expire);
	}

	public String formatExpire() {
		if (this.forever)
			return "never expires";
		if (!hasExpired()) {
			SimpleDateFormat ef = new SimpleDateFormat("EEE d/M HH:mm");
			return "expires on " + ef.format(this.expire);
		}
		return "has already expired";
	}
}