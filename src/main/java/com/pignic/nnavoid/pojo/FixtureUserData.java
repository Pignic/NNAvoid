package com.pignic.nnavoid.pojo;

public class FixtureUserData {

	public static enum FixtureUserDataType {
		FUD_GAP, FUD_GROUND, FUD_PROJECTILE, FUD_CELL, FUD_WALL
	}

	FixtureUserDataType m_type;

	protected FixtureUserData(final FixtureUserDataType type) {
		m_type = type;
	}

	public FixtureUserDataType getType() {
		return m_type;
	}
}
