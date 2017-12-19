package com.pignic.nnavoid.pojo;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;

public class Arena extends Playground {

	public Arena(final World world, final Vec2 size, final Cell... shooters) {
		super(world, size, new Vec2(), shooters);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Body build(final World world) {
		final BodyDef bodyDef = new BodyDef();
		bodyDef.position = new Vec2(0, 0);
		bodyDef.angle = 0;
		bodyDef.type = BodyType.STATIC;
		final Body body = world.createBody(bodyDef);
		// left
		createWall(body, new Vec2(-size.x / 2f, 0), new Vec2(wallThickness, size.y));
		// right
		createWall(body, new Vec2(size.x / 2f, 0), new Vec2(wallThickness, size.y));
		// top
		createWall(body, new Vec2(0, size.y / 2f), new Vec2(size.x, wallThickness));
		// bottom
		createWall(body, new Vec2(0, -size.y / 2f), new Vec2(size.x, wallThickness));
		return body;
	}

	private Fixture createWall(final Body body, final Vec2 center, final Vec2 size) {
		final PolygonShape wall = new PolygonShape();
		wall.setAsBox(size.x / 2f, size.y / 2f, center, 0);
		final Fixture fixture = body.createFixture(wall, 1);
		fixture.setFilterData(Playground.wallFilter);
		fixture.setUserData(new WallFUD());
		return fixture;
	}

	@Override
	public Vec2 getRelativeLocation(final Vec2 position) {
		Vec2 relativeLocation = new Vec2();
		final Vec2 left = new Vec2(-(size.x - wallThickness) / 2f, position.y);
		final Vec2 right = new Vec2((size.x - wallThickness) / 2f, position.y);
		final Vec2 top = new Vec2(position.x, -(size.y - wallThickness) / 2f);
		final Vec2 bottom = new Vec2(position.x, (size.y - wallThickness) / 2f);
		final float toLeft = left.sub(position).length();
		final float toRight = right.sub(position).length();
		final float toTop = top.sub(position).length();
		final float toBottom = bottom.sub(position).length();
		if (toLeft < toRight && toLeft < toTop && toLeft < toBottom) {
			// Left
			relativeLocation = left.sub(position);
		} else if (toRight < toTop && toRight < toBottom) {
			// Right
			relativeLocation = right.sub(position);
		} else if (toTop < toBottom) {
			// Top
			relativeLocation = top.sub(position);
		} else {
			// Bottom
			relativeLocation = bottom.sub(position);
		}
		return relativeLocation;
	}

}
