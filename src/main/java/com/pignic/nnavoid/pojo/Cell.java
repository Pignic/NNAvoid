package com.pignic.nnavoid.pojo;

import java.awt.Color;
import java.awt.Graphics2D;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;

import com.pignic.nnavoid.Application;
import com.pignic.nnavoid.gui.JBox2DPaint;
import com.pignic.nnavoid.utils.MathUtil;

public class Cell {

	public static class CellFUD extends FixtureUserData {
		public CellFUD() {
			super(FixtureUserDataType.FUD_CELL);
		}
	}

	public static enum STATE {
		averaged(Color.BLUE), created(Color.RED), kept(Color.GREEN), merged(Color.CYAN), mutated(Color.ORANGE);

		public Color color;

		STATE(final Color color) {
			this.color = color;
		}
	}

	public static boolean debugDraw = false;

	public static Filter filter = new Filter() {
		{
			groupIndex = 0;
		}
	};

	private static float highestMoveCommand = -1000;

	private static float highestRotateCommand = -1000;

	private static int idGenerator = 0;

	private static float lowestMoveCommand = 1000;

	private static float lowestRotateCommand = 1000;

	private static final float maxSpeed = 11;

	public static final float size = 4f / Application.WORLD_RATIO;

	public static float getHighestMoveCommand() {
		return highestMoveCommand;
	}

	public static float getHighestRotateCommand() {
		return highestRotateCommand;
	}

	public static float getLowestMoveCommand() {
		return lowestMoveCommand;
	}

	public static float getLowestRotateCommand() {
		return lowestRotateCommand;
	}

	private static int nextId() {
		return ++idGenerator;
	}

	public static void resetSensors() {
		lowestMoveCommand = 1000;
		highestMoveCommand = -1000;
		lowestRotateCommand = 1000;
		highestRotateCommand = -1000;
	}

	private boolean alive = true;

	private boolean bestCell = false;

	private final Body body;

	private Cell closestCell = null;

	private final Vec2 closestObstacle = new Vec2();

	private int collisionCount = 0;

	private boolean debug = false;

	private float fitness = 0;

	public final int id;

	private final float initialAngle;

	private final Vec2 initialPosition;

	private float lastDistanceFromTarget;

	private final Vec2 lastPosition = new Vec2();

	private int life = 1;

	private float moveCommand = 0;

	private int parentId = 0;

	private final Playground playground;

	private float rotateCommand = 0;

	private STATE state = STATE.created;

	private final Vec2 target = new Vec2();

	public Cell(final World world, final Playground playground, final Vec2 position, final float angle) {
		lastPosition.set(position);
		this.playground = playground;
		id = nextId();
		initialPosition = position;
		initialAngle = angle;
		final BodyDef bodyDef = new BodyDef();
		bodyDef.position = initialPosition;
		bodyDef.angle = initialAngle;
		bodyDef.type = BodyType.DYNAMIC;
		body = world.createBody(bodyDef);
		final CircleShape shape = new CircleShape();
		shape.m_radius = size;
		final Fixture fixture = body.createFixture(shape, 1);
		fixture.setFilterData(Cell.filter);
		fixture.setUserData(new CellFUD());
		body.setUserData(this);
		resetTarget();
	}

	public float addFitness(final float fitness) {
		this.fitness += fitness;
		return this.fitness;
	}

	public int collide() {
		return ++collisionCount;
	}

	private Cell computeClosestCell() {
		float closestDistance = 10000;
		closestCell = null;
		for (final Cell cell : playground.getShooters()) {
			if (cell != this && cell.isAlive()) {
				final float distance = cell.getBody().getPosition().sub(getBody().getPosition()).length();
				if (distance < closestDistance) {
					closestDistance = distance;
					closestCell = cell;
				}
			}
		}
		return closestCell;
	}

	public Body getBody() {
		return body;
	}

	public int getCollisionCount() {
		return collisionCount;
	}

	public float getFitness() {
		return fitness;
	}

	public int getHit() {
		if (debug) {
			return life;
		}
		// --life;
		if (life <= 0) {
			alive = false;
		}
		++collisionCount;
		return life;
	}

	public int getId() {
		return id;
	}

	public int getLife() {
		return life;
	}

	public float getMoveCommand() {
		return moveCommand;
	}

	public double[] getOutput() {
		final int outSize = 4;
		int i = 0;
		final double[] outputs = new double[outSize];
		computeClosestCell();
		final Vec2 closestWall = playground.getRelativeLocation(body.getPosition());
		final Vec2 dir = new Vec2((float) Math.cos(body.getAngle()), (float) Math.sin(body.getAngle()));
		if (closestCell != null) {
			Vec2 relpos = closestCell.body.getPosition().sub(body.getPosition());
			if (closestWall.length() < relpos.length()) {
				relpos = closestWall;
			}
			closestObstacle.x = MathUtil
					.normalizeAngle((float) (Math.atan2(relpos.y, relpos.x) - Math.atan2(dir.y, dir.x)));
			closestObstacle.y = relpos.length();
		} else {
			closestObstacle.x = MathUtil
					.normalizeAngle((float) (Math.atan2(closestWall.y, closestWall.x) - Math.atan2(dir.y, dir.x)));
			closestObstacle.y = closestWall.length();
			System.out.println(closestWall.length());
		}
		// Relative polar vector to the closest obstacle (cell or wall). The angle of the polar vector is relative to
		// the angle of the current cell.
		outputs[i++] = closestObstacle.x;
		outputs[i++] = closestObstacle.y;

		final Vec2 relpos = target.sub(body.getPosition());
		// Relative polar vector to the target. The angle of the polar vector is relative to the angle of the current
		// cell.
		outputs[i++] = MathUtil.normalizeAngle((float) (Math.atan2(relpos.y, relpos.x) - Math.atan2(dir.y, dir.x)));
		outputs[i++] = relpos.length();
		return outputs;
	}

	public int getParentId() {
		return parentId;
	}

	public float getRotateCommand() {
		return rotateCommand;
	}

	public STATE getState() {
		return state;
	}

	public boolean isAlive() {
		return alive;
	}

	public boolean isBestShooter() {
		return bestCell;
	}

	public boolean isDebug() {
		return debug;
	}

	public void render(final Graphics2D g, final Application app) {
		final Vec2 screenCoord = app.getCoordinateToScreen(body.getPosition());
		JBox2DPaint.fillBody(g, app, body, !alive ? Color.DARK_GRAY : state.color);
		if (bestCell) {
			g.drawArc((int) screenCoord.x - 10, (int) screenCoord.y - 10, 20, 20, 0, 360);
		}
		if (debug) {
			final float angle = body.getAngle();
			g.setColor(Color.RED);
			g.drawArc((int) screenCoord.x - 15, (int) screenCoord.y - 15, 30, 30, 0, 360);
			g.setColor(Color.BLACK);
			g.drawLine((int) screenCoord.x, (int) screenCoord.y,
					(int) (screenCoord.x + Math.cos(angle) * 15 * app.getZoomLevel()),
					(int) (screenCoord.y + Math.sin(angle) * 15 * app.getZoomLevel()));
		}
		if (alive) {
			if (debugDraw) {
				final float angle = body.getAngle();
				g.setColor(Color.BLACK);
				g.drawLine((int) screenCoord.x, (int) screenCoord.y,
						(int) (screenCoord.x + Math.cos(angle) * 10 * app.getZoomLevel()),
						(int) (screenCoord.y + Math.sin(angle) * 10 * app.getZoomLevel()));
				g.setColor(Color.MAGENTA);
				final Vec2 closestObstacleCoord = new Vec2((float) Math.cos(closestObstacle.x + body.getAngle()),
						(float) Math.sin(closestObstacle.x + body.getAngle())).mul(closestObstacle.y)
								.mul(app.getZoomLevel() * Application.WORLD_RATIO);
				g.drawLine((int) screenCoord.x, (int) screenCoord.y, (int) (screenCoord.x + closestObstacleCoord.x),
						(int) (screenCoord.y + closestObstacleCoord.y));
				g.setColor(Color.GREEN);
				final Vec2 targetScreenCoord = app.getCoordinateToScreen(target);
				g.drawLine((int) screenCoord.x, (int) screenCoord.y, (int) targetScreenCoord.x,
						(int) targetScreenCoord.y);
			}
		}
	}

	public Cell reset() {
		body.setTransform(initialPosition, initialAngle);
		fitness = 0;
		moveCommand = 0;
		rotateCommand = 0;
		alive = true;
		life = 1;
		resetTarget();
		collisionCount = 0;
		return this;
	}

	private void resetTarget() {
		boolean isValid = false;
		final float targetDistance = 8;
		do {
			final double angle = Math.random() * Math.PI * 2d;
			target.set(body.getPosition().x + (float) (Math.cos(angle) * targetDistance),
					body.getPosition().y + (float) (Math.sin(angle) * targetDistance));
			lastDistanceFromTarget = target.sub(body.getPosition()).length();
			if (target.x > (-playground.size.x + playground.wallThickness * 2f) / 2f
					&& target.x < (playground.size.x - playground.wallThickness * 2f) / 2f
					&& target.y > (-playground.size.y + playground.wallThickness * 2f) / 2f
					&& target.y < (playground.size.y - playground.wallThickness * 2f) / 2f) {
				isValid = true;
			}
		} while (!isValid);
	}

	public void setBestCell(final boolean bestShooter) {
		bestCell = bestShooter;
	}

	public void setClosestShooter(final Cell closestShooter) {
		closestCell = closestShooter;
	}

	public void setCollisionCount(final int collisionCount) {
		this.collisionCount = collisionCount;
	}

	public void setDebug(final boolean debug) {
		rotateCommand = 0;
		moveCommand = 0;
		this.debug = debug;
	}

	public void setInput(final double[] values) {
		if (!debug) {
			rotateCommand = (float) (values[0] - 0.5);
			moveCommand = (float) (values[1] - 0.3);
			if (rotateCommand < lowestRotateCommand) {
				lowestRotateCommand = rotateCommand;
			} else if (rotateCommand > highestRotateCommand) {
				highestRotateCommand = rotateCommand;
			}
			if (moveCommand < lowestMoveCommand) {
				lowestMoveCommand = moveCommand;
			} else if (moveCommand > highestMoveCommand) {
				highestMoveCommand = moveCommand;
			}
		}
	}

	public void setMoveCommand(final float moveCommand) {
		this.moveCommand = moveCommand;
	}

	public void setParentId(final int parentId) {
		this.parentId = parentId;
	}

	public void setRotateCommand(final float rotateCommand) {
		this.rotateCommand = rotateCommand;
	}

	public void setState(final STATE state) {
		this.state = state;
	}

	public boolean update(final long ms) {
		if (alive) {
			body.setAngularVelocity((float) (rotateCommand * Math.PI * 3f));
			body.setLinearVelocity(new Vec2((int) (Math.cos(body.getAngle()) * moveCommand * maxSpeed),
					(int) (Math.sin(body.getAngle()) * moveCommand * maxSpeed)));
			final float distanceFromTarget = target.sub(body.getPosition()).length();
			fitness += lastDistanceFromTarget - distanceFromTarget;
			lastDistanceFromTarget = distanceFromTarget;
			if (distanceFromTarget <= size) {
				resetTarget();
			}
		}
		return false;
	}
}
