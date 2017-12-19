package com.pignic.nnshoot.utils;

import org.jbox2d.common.Vec2;

import com.pignic.nnavoid.utils.MathUtil;

public class MathUtilTest {

	public static float getRelativeAngle(final Vec2 src, final Vec2 dst, final float srcAngle) {
		return Math.abs(MathUtil.getAngle(dst.sub(src)) - srcAngle);
	}

	public static void main(final String... args) {

		// System.out.println(MathUtil.getAngle(new Vec2(1, 0)));
		// System.out.println(MathUtil.getAngle(new Vec2(0, 1)) - (float) Math.PI / 2f);
		// System.out.println(MathUtil.getAngle(new Vec2(-1, 0)) - (float) Math.PI);
		// System.out.println(Math.abs(MathUtil.getAngle(new Vec2(0, -1))) - (float) Math.PI / 2f);

		// System.out.println(getRelativeAngle(new Vec2(1, 1), new Vec2(2, 2), (float) Math.PI / 4f));
		// System.out.println(getRelativeAngle(new Vec2(1, 0), new Vec2(1, 1), 0) - (float) Math.PI / 2f);
		// System.out.println(getRelativeAngle(new Vec2(2, 2), new Vec2(2, 1), 0) - (float) Math.PI / 2f);
		// System.out.println(getRelativeAngle(new Vec2(2, 2), new Vec2(1, 2), (float) Math.PI));

		// System.out.println(MathUtil.getPolar(new Vec2(1, 0)).x);
		// System.out.println(MathUtil.getPolar(new Vec2(1, 1)).x);
		// System.out.println(MathUtil.getPolar(new Vec2(-1, 0)).x);
		// System.out.println(MathUtil.getPolar(new Vec2(0, -1)).x);

		// System.out.println(MathUtil.normalizeAngle((float) Math.PI * -2.5f));
		int as = 0;
		int bs = 0;
		char c;
		for (int i = 0; i < 10000; ++i) {
			c = MathUtil.pickOne('a', 'b');
			if (c == 'a') {
				++as;
			} else {
				++bs;
			}
		}
		System.out.println("a:" + as + " b:" + bs);
	}
}
