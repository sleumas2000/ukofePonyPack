/*This file is part of the ukofePonyPack.

    The ukofePonyPack is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    The ukofePonyPack is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with The ukofePonyPack.  If not, see <http://www.gnu.org/licenses/>.*/

package com.github.chaomaster.ukofePonyPack;

public enum PonyType {
	ERROR("A Human due to an error"),
	UNSET("A Human as it is unset"),
	NONE("A Human"),
	PEGASUS("A Pegasus"),
	UNICORN("An Unicorn"),
	EARTH("A Earth pony"),
	ALICORN("An Alicorn"),
	ZEBRA("A Zebra");

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