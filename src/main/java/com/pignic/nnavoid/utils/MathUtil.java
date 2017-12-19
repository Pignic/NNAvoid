package com.pignic.nnavoid.utils;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jbox2d.common.Vec2;

import com.pignic.basicapp.Vector2D;

public class MathUtil {

	private static final Random rand = new Random();

	public static List<Point> combinations(final int range) {
		final List<Point> combinations = new ArrayList<Point>();
		for (int i = 0; i < range; ++i) {
			for (int j = i + 1; j < range; ++j) {
				combinations.add(new Point(i, j));
			}
		}
		return combinations;
	}

	public static float getAngle(final Vec2 vector) {
		return (float) new Vector2D(vector.x, vector.y).getAngle();
	}

	public static float getAngle(final Vec2 center, final Vec2 next, final Vec2 previous) {
		float angle = (float) (Math.atan2(next.y - center.y, next.x - center.x)
				- Math.atan2(previous.y - center.y, previous.x - center.x));
		if (angle >= Math.PI) {
			angle -= 2 * Math.PI;
		} else if (angle <= -Math.PI) {
			angle += 2 * Math.PI;
		}
		return angle;
	}

	public static Vec2 getClosestPoint(final Vec2 a, final Vec2 b, final Vec2 test) {
		final float testAngle = (float) (getAngle(b, a, new Vec2(0, b.y)) + Math.PI / 2f);
		final Vec2 testb = new Vec2(Math.round(test.x + Math.cos(testAngle) * 100f),
				Math.round(test.y + Math.sin(testAngle) * 100f));
		final float x = ((b.x - a.x) * (test.x * testb.y - testb.x * test.y)
				- (testb.x - test.x) * (a.x * b.y - b.x * a.y))
				/ ((a.x - b.x) * (test.y - testb.y) - (a.y - b.y) * (test.x - testb.x));
		final float y = ((test.y - testb.y) * (a.x * b.y - b.x * a.y)
				- (a.y - b.y) * (test.x * testb.y - testb.x * test.y))
				/ ((a.x - b.x) * (test.y - testb.y) - (a.y - b.y) * (test.x - testb.x));
		return new Vec2(x, y);
	}

	/**
	 * x = angle, y = length
	 */
	public static Vec2 getPolar(final Vec2 vector) {
		return new Vec2((float) Math.atan2(vector.y, vector.x), (float) Math.hypot(vector.x, vector.y));
	}

	public static double[] mutate(final Double[] inputs, final double mutationRate) {
		final double[] mutated = new double[inputs.length];
		for (int i = 0; i < inputs.length; ++i) {
			mutated[i] = inputs[i] + (Math.random() - 0.5) * mutationRate;
		}
		return mutated;
	}

	public static float normalizeAngle(final float angle) {
		return (float) Math.atan2(Math.sin(angle), Math.cos(angle));
	}

	public static <T> T pickOne(final T... collection) {
		return collection[rand.nextInt(collection.length)];
	}

	public static int sumTo0(final int n) {
		int sum = n;
		for (int i = n - 1; i > 0; --i) {
			sum += i;
		}
		return sum;
	}

	public static Vector2D vec2ToVector2D(final Vec2 v) {
		return new Vector2D(v.x, v.y);
	}

	public static Vec2 vector2DToVec2(final Vector2D v) {
		return new Vec2((float) v.x, (float) v.y);
	}

	public double[] merge(final Double[] a, final Double[] b, final double mutationRate) {
		final double[] merged = new double[Math.min(a.length, b.length)];
		for (int i = 0; i < merged.length; ++i) {
			merged[i] = (a[i] + b[i]) / 2.0 + (Math.random() - 0.5) * (mutationRate / 2d);
		}
		return merged;
	}
}
