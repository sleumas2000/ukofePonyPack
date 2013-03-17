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
		cal.add(5, 1);
		this.expire = cal.getTime();
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