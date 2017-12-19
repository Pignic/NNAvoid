package com.pignic.nnavoid.pojo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.World;

import com.pignic.nnavoid.Application;
import com.pignic.nnavoid.gui.JBox2DPaint;

public abstract class Playground {

	public static class GroundFUD extends FixtureUserData {
		public GroundFUD() {
			super(FixtureUserDataType.FUD_GROUND);
		}
	}

	public static class WallFUD extends FixtureUserData {
		public WallFUD() {
			super(FixtureUserDataType.FUD_WALL);
		}
	}

	public static Filter wallFilter = new Filter() {
		{
			groupIndex = 0;
		}
	};

	protected Body body;

	protected final Vec2 offset;

	protected final List<Cell> shooters = new ArrayList<Cell>();

	protected final Vec2 size;

	protected final float wallThickness = 2;

	public Playground(final World world, final Vec2 size, final Vec2 offset, final Cell... shooters) {
		this.size = new Vec2(size);
		this.shooters.addAll(Arrays.asList(shooters));
		this.offset = new Vec2(offset);
		body = build(world);
	}

	public Playground addShooter(final Cell... shooters) {
		this.shooters.addAll(Arrays.asList(shooters));
		return this;
	}

	protected abstract Body build(World world);

	public Vec2 getOffset() {
		return offset;
	}

	public abstract Vec2 getRelativeLocation(final Vec2 position);

	public List<Cell> getShooters() {
		return shooters;
	}

	public float getWallThickness() {
		return wallThickness;
	}

	public void render(final Graphics2D g, final Application app) {
		JBox2DPaint.fillBody(g, app, body, Color.GRAY);
		for (final Cell shooter : shooters) {
			shooter.render(g, app);
		}
	}

	public List<Cell> reset() {
		for (final Cell shooter : shooters) {
			shooter.reset();
		}
		return shooters;
	}
}
