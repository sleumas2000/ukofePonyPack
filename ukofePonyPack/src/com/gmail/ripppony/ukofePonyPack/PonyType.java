package com.gmail.ripppony.ukofePonyPack;

public enum PonyType {
	ERROR("A Human due to an error"), UNSET("A Human as it is unset"), NONE(
			"A Human"), PEGASUS("A Pegasus"), UNICORN("An Unicorn"), EARTH(
			"A Earth pony"), ALICORN("An Alicorn");

	private String _message;

	private PonyType(String message) {
		this._message = message;
	}

	public PonyType ignErr() {
		if (this == ERROR) {
			return NONE;
		}
		return this;
	}

	public PonyType ignUn() {
		if ((this == ERROR) || (this == UNSET)) {
			return NONE;
		}
		return this;
	}

	public boolean isUnset() {
		return (this == UNSET) || (this == ERROR);
	}

	public String getMessage() {
		return this._message;
	}
}