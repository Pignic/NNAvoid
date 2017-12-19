package com.pignic.nnavoid;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.neuroph.nnet.MultiLayerPerceptron;

import com.pignic.basicapp.BaseApplication;
import com.pignic.basicapp.BaseApplication.Engine;
import com.pignic.basicapp.InputHandler;
import com.pignic.basicapp.InputHandler.Action;
import com.pignic.basicapp.InputHandler.Binding;
import com.pignic.nnavoid.io.Report;
import com.pignic.nnavoid.pojo.Arena;
import com.pignic.nnavoid.pojo.Cell;
import com.pignic.nnavoid.pojo.Cell.STATE;
import com.pignic.nnavoid.pojo.FixtureUserData;
import com.pignic.nnavoid.pojo.Playground;
import com.pignic.nnavoid.utils.MathUtil;;

public class Application extends Engine implements ContactListener {

	private enum Fitness {

		CollidePlayer(-30), CollideWall(-30);

		float value;

		Fitness(final float value) {
			this.value = value;
		}
	}

	private final static long simulationTime = 20000;

	private final static int sqrtCellPerSimulation = 13;

	private static final int TOP_PLAYER = 8;

	public static final float WORLD_RATIO = 10;

	public static void main(final String[] args) {
		EventQueue.invokeLater(() -> {
			final Application app = new Application();
			app.graph = new Graph();
			new BaseApplication(800, 600, app).startMainLoop(60);
			new BaseApplication(800, 600, app.graph).startMainLoop(10);
		});
	}

	private Cell bestCell = null;

	private int collisionCount = 0;

	private int currentGeneration = 1;

	private Cell debugCell;

	private Graph graph;

	private final float maxFitness = 0;

	private double mutationRate = 0.5;

	private final Map<Cell, MultiLayerPerceptron> neuralNetworksArena = new HashMap<Cell, MultiLayerPerceptron>();

	private Report report;

	private boolean requestReset = false;

	private boolean showCommand = false;

	private Playground simulation;

	private final double[] weights =
			//
			//
			// new double[] { -0.7004352829966382, -5.56073278340762, 3.0466008499001282, -6.481144246386253,
			// 4.048488977606282, 1.0400597307942103, -5.18340751145029, -14.566588151481863, -1.8495623165449508,
			// 4.705443625414061, 8.728111291753804, -3.2406590776765203, -7.326751776580791, -6.346537204297653,
			// -4.26799712355353, -0.2500993987516241, 0.9305901582980922, 10.254043588175874, 0.6433041605416842,
			// -2.8920424095647395, 7.250110579237381, -5.491449856167274, -6.452170040981614, 1.1703076589318453,
			// 1.2892605984435386, -3.904192330933389, 4.42671740407375, -4.8614845654030825, -0.41735048115637774,
			// -6.410471780887386, -6.726193695547144, -3.9426636815229497, 2.1239866113808215,
			// 0.14340034838341997, 11.088491346609427, -7.174494926556096, 0.9227220776098053, 3.289155701461227,
			// -0.5001932995827534, 1.6697373411518832, 6.185803267617047, 5.111637708496167, -1.1221349342230396,
			// 5.076936719717062, -6.01306302280531, -11.065217032047551, -5.05261533760113, -5.733833166088258,
			// -6.401385745136402, 0.8518662339952138, -0.3019599873082043, -2.580509817621659, 6.176607957922067,
			// -4.349670394919092, 0.06886822663297476, -0.5039968899047554, -4.1783800582463595,
			// 2.8135705506184543, 1.4936050412989792, -6.818240825767003, 4.5843998777403225, 8.697424486346256,
			// -6.757363227307538, -8.19029724243502, 0.4914041470450539, 3.805754733108682, -2.5503809049729558,
			// 2.2507138242637903, 2.506842776853438, -0.14805239442964618, 2.2277510185958067,
			// 0.47601561494035843, -4.5260895485357935, -9.79779264366902, -1.8622336694265764,
			// 0.29193061345302135, -1.7424789207443323, 7.917449066186222, -0.5705606029804171,
			// -4.434720815072955, 2.2678842064670515, -2.427089068797641, 4.6368308039599295, -2.729189747401011,
			// 1.3725825053634848, -0.17662921763411307, 8.828021719805118, -1.6316515355994767,
			// -3.372093869263553, -0.13856610763094077, -3.790725515133203, 1.9620617789323656,
			// 0.008103760589546719, -1.3312647556633115, -3.133138355128958, -0.6684074502101726,
			// -4.388561559418778, 1.9271509979403878, 1.2341268192743178, -10.69679214549318, 4.680337121132323,
			// 6.81274330577638, -3.6487775425328013, -1.972578280337774, 7.470844842394962, 1.6981533337064947,
			// -8.377131646010564, -2.3667468416550856, -0.9867728115371385, -0.747228943695534,
			// -0.7996859644526146, 2.0296088505178, 7.157255351256833, 1.078952142826555, 6.112536001342936,
			// 5.598633412090848, -10.08432760758162, 9.432257438350355, 2.5239259472568896, -7.966328573299945,
			// -1.7150081988349564, -0.8111036781420211, 8.618498868792988, -1.4185000827793133, 2.481040823975293,
			// -0.7983390853791881, -8.261472380991744, -19.05553356827401, -10.694979806232826,
			// 9.843651248733211 }
			null
	//
	;

	public final World world = new World(new Vec2());

	public Application() {
		world.setContactListener(this);
		final Timer timer = new Timer();
		try {
			report = new Report(new File(this.getClass().getClassLoader().getResource("out").getFile()));
		} catch (final IOException e) {
			report = null;
			e.printStackTrace();
		}
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				requestReset = true;
			}
		}, simulationTime, simulationTime);
	}

	/**
	 * Average two dna by averaging each info from A or B. Apply a mutation of 10% of the mutation rate.
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	private double[] average(final Double[] a, final Double[] b) {
		final double[] merged = new double[Math.min(a.length, b.length)];
		for (int i = 0; i < merged.length; ++i) {
			merged[i] = (a[i] + b[i]) / 2d + (Math.random() - 0.5) * (mutationRate / 10d);
		}
		return merged;
	}

	@Override
	public void beginContact(final Contact contact) {
		handleContact(contact);
	}

	private void buildArenaSimulation() {
		final Vec2 simulationSize = new Vec2(container.getWidth() / WORLD_RATIO, container.getHeight() / WORLD_RATIO);
		simulation = new Arena(world, simulationSize);
		final Vec2 spacing = new Vec2(simulationSize.x / (sqrtCellPerSimulation + 1),
				simulationSize.y / (sqrtCellPerSimulation + 1));
		for (int i = 0; i < sqrtCellPerSimulation; ++i) {
			for (int j = 0; j < sqrtCellPerSimulation; ++j) {
				final MultiLayerPerceptron network = new MultiLayerPerceptron(
						Arrays.asList(new Integer[] { 4, 8, 8, 2 }));
				final Cell cell = new Cell(world, simulation, new Vec2((i + 1) * spacing.x - simulationSize.x / 2f,
						(j + 1) * spacing.y - simulationSize.y / 2f), (float) (Math.random() * Math.PI * 2f));
				if (weights != null) {
					network.setWeights(weights);
				} else {
					network.randomizeWeights(-1, 1);
				}
				neuralNetworksArena.put(cell, network);
				simulation.addShooter(cell);
			}
		}
		debugCell = simulation.getShooters().get(0);
		graph.setNetwork(neuralNetworksArena.get(simulation.getShooters().get(0)));
		// debugCell.setDebug(true);
	}

	private void cellVSCell(final Fixture cell1Fixture, final Fixture cell2Fixture, final Contact contact) {
		final Cell cell1 = (Cell) cell1Fixture.getBody().getUserData();
		final Cell cell2 = (Cell) cell2Fixture.getBody().getUserData();
		if (cell1.isAlive() && cell2.isAlive()) {
			cell1.addFitness(Fitness.CollidePlayer.value);
			cell1.getHit();
			cell2.addFitness(Fitness.CollidePlayer.value);
			cell2.getHit();
			++collisionCount;
		} else {
			contact.setEnabled(false);
		}
	}

	private void cellVSWall(final Fixture shooterFixture, final Fixture wallFixture, final Contact contact) {
		final Cell shooter = (Cell) shooterFixture.getBody().getUserData();
		if (shooter.isAlive()) {
			shooter.addFitness(Fitness.CollideWall.value);
			shooter.getHit();
			if (shooter.getState() == STATE.created) {

			} else {
				++collisionCount;
			}
		}
	}

	public String doubleArrayToString(final Double[] array) {
		String result = "";
		for (final Double value : array) {
			result += value.toString() + " , ";
		}
		return result;
	}

	@Override
	public void endContact(final Contact contact) {

	}

	public Vec2 getCoordinateToScreen(final Vec2 coordinates) {
		return MathUtil.vector2DToVec2(container.getCoordinateToScreen(coordinates.x, coordinates.y));
	}

	public float getZoomLevel() {
		return container.getZoomLevel();
	}

	void handleContact(final Contact contact) {
		final Fixture a = contact.getFixtureA();
		final Fixture b = contact.getFixtureB();
		final FixtureUserData fudA = (FixtureUserData) a.getUserData();
		final FixtureUserData fudB = (FixtureUserData) b.getUserData();
		if (fudA == null || fudB == null) {
			return;
		}
		if (fudA.getType() == FixtureUserData.FixtureUserDataType.FUD_CELL
				&& fudB.getType() == FixtureUserData.FixtureUserDataType.FUD_WALL) {
			cellVSWall(a, b, contact);
		} else if (fudA.getType() == FixtureUserData.FixtureUserDataType.FUD_WALL
				&& fudB.getType() == FixtureUserData.FixtureUserDataType.FUD_CELL) {
			cellVSWall(b, a, contact);
		} else if (fudA.getType() == FixtureUserData.FixtureUserDataType.FUD_CELL
				&& fudB.getType() == FixtureUserData.FixtureUserDataType.FUD_CELL) {
			cellVSCell(a, b, contact);
		}
	}

	@Override
	public void init() {
		container.setScale(WORLD_RATIO);
		buildArenaSimulation();

		container.addKeyListener(new InputHandler(new Binding('z', new Action() {
			@Override
			public Object call() throws Exception {
				debugCell.setMoveCommand(isPressed() ? 0.5f : 0);
				return null;
			}
		}), new Binding('s', new Action() {
			@Override
			public Object call() throws Exception {
				debugCell.setMoveCommand(isPressed() ? -0.5f : 0);
				return null;
			}
		}), new Binding('q', new Action() {
			@Override
			public Object call() throws Exception {
				debugCell.setRotateCommand(isPressed() ? -0.5f : 0);
				return null;
			}
		}), new Binding('d', new Action() {
			@Override
			public Object call() throws Exception {
				debugCell.setRotateCommand(isPressed() ? 0.5f : 0);
				return null;
			}
		}), new Binding('f', new Action() {
			@Override
			public final Object call() throws Exception {
				return container.toggleFullSreen();
			}
		}), new Binding('r', new Action() {
			@Override
			public Object call() throws Exception {
				requestReset = isPressed();
				return null;
			}
		}), new Binding('p', new Action() {
			@Override
			public Object call() throws Exception {
				mutationRate += isPressed() ? 0.01 : 0;
				return null;
			}
		}), new Binding('m', new Action() {
			@Override
			public Object call() throws Exception {
				mutationRate -= isPressed() ? 0.01 : 0;
				return null;
			}
		}), new Binding('a', new Action() {
			@Override
			public Object call() throws Exception {
				Cell.debugDraw = isPressed() ? !Cell.debugDraw : Cell.debugDraw;
				return null;
			}
		}), new Binding('c', new Action() {
			@Override
			public Object call() throws Exception {
				showCommand = isPressed() ? !showCommand : showCommand;
				return null;
			}
		}), new Binding('e', new Action() {
			@Override
			public Object call() throws Exception {
				debugCell.setDebug(isPressed() ? !debugCell.isDebug() : debugCell.isDebug());
				return null;
			}
		})));
	}

	/**
	 * Merge two dna by mixing, picking randomly info from A or B. Apply a mutation of 10% of the mutation rate.
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	private double[] merge(final Double[] a, final Double[] b) {
		final double[] merged = new double[Math.min(a.length, b.length)];
		for (int i = 0; i < merged.length; ++i) {
			merged[i] = MathUtil.pickOne(a[i], b[i]) + (Math.random() - 0.5) * (mutationRate / 10d);
		}
		return merged;
	}

	/**
	 * Apply a mutation on the weights
	 *
	 * @param inputs
	 * @return
	 */
	private double[] mutate(final Double[] inputs) {
		final double[] mutated = new double[inputs.length];
		for (int i = 0; i < inputs.length; ++i) {
			mutated[i] = inputs[i] + (Math.random() - 0.5) * mutationRate;
		}
		return mutated;
	}

	@Override
	public void postSolve(final Contact contact, final ContactImpulse impulse) {
		return;
	}

	@Override
	public void preSolve(final Contact contact, final Manifold oldManifold) {
		return;
	}

	@Override
	public void render(final Graphics2D g) {
		simulation.render(g, this);
		int i = 0;
		g.setColor(Color.RED);
		g.drawString("C to show commands", 5, i += 15);
		if (Cell.debugDraw) {
			g.drawString("FPS: " + Integer.toString(container.getFps()), 5, i += 15);
			g.drawString("Generation: " + Integer.toString(currentGeneration), 5, i += 15);
			g.drawString("Best fitness: " + Float.toString(bestCell != null ? bestCell.getFitness() : 0), 5, i += 15);
			g.drawString("Collision: " + collisionCount, 5, i += 15);
			g.drawString("Mutation rate: " + mutationRate, 5, i += 15);
		}
		if (showCommand) {
			g.drawString("ZQSD to control debug cell", 5, i += 15);
			g.drawString("F: toggle fullscreen", 5, i += 15);
			g.drawString("R: skip simulation", 5, i += 15);
			g.drawString("P: increase mutation rate", 5, i += 15);
			g.drawString("M: decrease mutation rate", 5, i += 15);
			g.drawString("A: toggle debug draw", 5, i += 15);
			g.drawString("E: take control of debug cell", 5, i += 15);
			g.drawString("Mouse wheel: zoom", 5, i += 15);
			g.drawString("Mouse drag: move point of view", 5, i += 15);
			g.drawString("---Legend---", 5, i += 15);
			g.drawString("RED: New cell", 5, i += 15);
			g.drawString("GREEN: top " + TOP_PLAYER + " cells", 5, i += 15);
			g.drawString("ORANGE: Mutated from top ranking", 5, i += 15);
			g.drawString("CYAN: Merged from top ranking", 5, i += 15);
			g.drawString("BLUE: Averaged from top ranking", 5, i += 15);
		}
	}

	private void reset() {
		++currentGeneration;
		if (bestCell != null) {
			bestCell.setBestCell(false);
		} else {
			bestCell = debugCell;
		}

		bestCell = updateNetworks(neuralNetworksArena);
		graph.setNetwork(neuralNetworksArena.get(bestCell));
		if (bestCell != null) {
			bestCell.setBestCell(true);
			Cell.resetSensors();
			System.out.println(
					"-----Gen " + currentGeneration + " --------------------------------------------------------");
			System.out.println(doubleArrayToString(neuralNetworksArena.get(bestCell).getWeights()));
		}
		System.out.println(collisionCount + " collisions------------------------------------------------------------");
		collisionCount = 0;
		requestReset = false;
	}

	private void stepNetworks(final long ms, final Map<Cell, MultiLayerPerceptron> networks) {
		for (final Entry<Cell, MultiLayerPerceptron> set : networks.entrySet()) {
			set.getValue().setInput(set.getKey().getOutput());
			set.getValue().calculate();
			set.getKey().setInput(set.getValue().getOutput());
			set.getKey().update(ms);
		}
	}

	@Override
	public void update(final long ms) {
		world.step(60f / 1000f, 6, 3);
		if (requestReset) {
			reset();
		} else {
			stepNetworks(ms, neuralNetworksArena);
		}
	}

	private Cell updateNetworks(final Map<Cell, MultiLayerPerceptron> neuralNetworks) {
		final List<Cell> cells = new ArrayList<Cell>();
		for (final Entry<Cell, MultiLayerPerceptron> set : neuralNetworks.entrySet()) {
			cells.add(set.getKey());
		}
		if (cells.size() <= 0) {
			return null;
		}
		cells.sort((o1, o2) -> Math.round(o2.getFitness() * 1000f - o1.getFitness() * 1000f));
		if (report != null) {
			report.write(currentGeneration, maxFitness, collisionCount,
					cells.subList(0, TOP_PLAYER).toArray(new Cell[0]));
		}
		for (int i = 0; i < TOP_PLAYER; ++i) {
			System.out.println("cell (" + cells.get(i).id + ") #" + i + " fitness : " + cells.get(i).getFitness()
					+ " collisions: " + cells.get(i).getCollisionCount());
			cells.get(i).reset();
			cells.get(i).setState(STATE.kept);
		}
		System.out.println();
		final List<Point> mergeCombinations = MathUtil.combinations(TOP_PLAYER);
		for (int i = 0; i < mergeCombinations.size(); ++i) {
			final Cell cell = cells.get(i + TOP_PLAYER);
			final Cell cellA = cells.get(mergeCombinations.get(i).x);
			final Cell cellB = cells.get(mergeCombinations.get(i).y);
			// Merge
			neuralNetworks.get(cell)
					.setWeights(merge(neuralNetworks.get(cellA).getWeights(), neuralNetworks.get(cellB).getWeights()));
			cell.reset();
			cell.setState(STATE.merged);
		}
		for (int i = 0; i < mergeCombinations.size(); ++i) {
			final Cell cell = cells.get(i + TOP_PLAYER + mergeCombinations.size());
			final Cell cellA = cells.get(mergeCombinations.get(i).x);
			final Cell cellB = cells.get(mergeCombinations.get(i).y);
			// Averaged
			neuralNetworks.get(cell).setWeights(
					average(neuralNetworks.get(cellA).getWeights(), neuralNetworks.get(cellB).getWeights()));
			cell.reset();
			cell.setState(STATE.averaged);
		}
		for (int i = TOP_PLAYER + mergeCombinations.size() * 2; i < cells.size(); ++i) {
			final Cell cell = cells.get(i);
			if (i < cells.size() - 5) {
				// Mutate
				neuralNetworks.get(cell).setWeights(mutate(neuralNetworks.get(cells.get(i % TOP_PLAYER)).getWeights()));
				cell.setState(STATE.mutated);
			} else {
				// Randomize
				neuralNetworks.get(cell).randomizeWeights(-1, 1);
				cell.setState(STATE.created);
			}
			cell.reset();
		}
		return cells.get(0);
	}

}
