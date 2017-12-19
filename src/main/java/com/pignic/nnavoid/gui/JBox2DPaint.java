package com.pignic.nnavoid.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;

import com.pignic.nnavoid.Application;

public class JBox2DPaint {

	private static float worldRatio = 10;

	public static void drawBody(final Graphics2D g2d, final Application app, final Body body,
			final Color... drawColor) {
		paintBody(g2d, app, body, true, drawColor);
	}

	public static void fillBody(final Graphics2D g2d, final Application app, final Body body,
			final Color... fillColor) {
		paintBody(g2d, app, body, false, fillColor);
	}

	public static void paintBody(final Graphics2D g2d, final Application app, final Body body, final boolean draw,
			final Color... color) {
		final Vec2 screenCoord = app.getCoordinateToScreen(body.getPosition());
		Fixture curentFixture = body.getFixtureList();
		Color current = color[0];
		int currentColorIndex = 0;
		while (curentFixture != null) {
			if (curentFixture.getShape().getType().equals(ShapeType.POLYGON)) {
				final PolygonShape shape = (PolygonShape) curentFixture.getShape();
				final int[] xs = new int[shape.getVertexCount()];
				final int[] ys = new int[shape.getVertexCount()];
				for (int i = 0; i < shape.getVertexCount(); ++i) {
					xs[i] = Math.round(screenCoord.x + shape.getVertices()[i].x * app.getZoomLevel() * worldRatio);
					ys[i] = Math.round(screenCoord.y + shape.getVertices()[i].y * app.getZoomLevel() * worldRatio);
				}
				final Polygon p = new Polygon(xs, ys, shape.getVertexCount());
				g2d.rotate(body.getAngle(), screenCoord.x, screenCoord.y);
				if (draw) {
					g2d.setColor(current);
					g2d.drawPolygon(p);
				} else {
					g2d.setColor(current);
					g2d.fillPolygon(p);
				}
				g2d.rotate(-body.getAngle(), screenCoord.x, screenCoord.y);
			} else if (curentFixture.getShape().getType().equals(ShapeType.CIRCLE)) {
				final CircleShape shape = (CircleShape) curentFixture.getShape();
				final int halfSize = Math.round(shape.getRadius() * app.getZoomLevel() * worldRatio);
				if (draw) {
					g2d.setColor(current);
					g2d.drawOval(Math.round(screenCoord.x + shape.m_p.x * app.getZoomLevel() * worldRatio) - halfSize,
							Math.round(screenCoord.y + shape.m_p.y * app.getZoomLevel() * worldRatio) - halfSize,
							halfSize * 2, halfSize * 2);
				} else {
					g2d.setColor(current);
					g2d.fillOval(Math.round(screenCoord.x + shape.m_p.x * app.getZoomLevel() * worldRatio) - halfSize,
							Math.round(screenCoord.y + shape.m_p.y * app.getZoomLevel() * worldRatio) - halfSize,
							halfSize * 2, halfSize * 2);
				}

			}
			curentFixture = curentFixture.getNext();
			current = color[++currentColorIndex % color.length];
		}
	}

	public static void setWorldRatio(final float worldRatio) {
		JBox2DPaint.worldRatio = worldRatio;
	}
}
