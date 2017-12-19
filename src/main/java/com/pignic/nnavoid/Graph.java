package com.pignic.nnavoid;

import java.awt.Color;
import java.awt.Graphics2D;
import java.text.DecimalFormat;

import org.neuroph.core.Layer;
import org.neuroph.core.Neuron;
import org.neuroph.nnet.MultiLayerPerceptron;

import com.pignic.basicapp.BaseApplication.Engine;

public class Graph extends Engine {

	private static DecimalFormat decimalFormat = new DecimalFormat("0.0000");

	private MultiLayerPerceptron network;

	public Graph() {

	}

	public MultiLayerPerceptron getNetwork() {
		return network;
	}

	@Override
	public void init() {

	}

	@Override
	public void render(final Graphics2D g) {
		final int width = network.getLayersCount();
		int height = 0;
		for (int i = 0; i < width; ++i) {
			final int currentHeight = network.getLayerAt(i).getNeuronsCount();
			if (currentHeight > height) {
				height = currentHeight;
			}
		}
		final float marginX = container.getWidth() / (width + 1);
		g.setColor(Color.RED);
		for (int i = 0; i < width; ++i) {
			final Layer layer = network.getLayerAt(i);
			final float x = (i + 1) * marginX;
			final float marginY = container.getHeight() / (layer.getNeuronsCount() + 1f);
			for (int j = 0; j < layer.getNeuronsCount(); ++j) {
				final Neuron neuron = layer.getNeuronAt(j);
				g.setColor(neuron.getOutput() > 0.5 ? Color.GREEN : Color.RED);
				g.fillArc((int) x, (int) ((j + 1) * marginY) - 20, 40, 40, 0, 360);
				g.setColor(Color.WHITE);
				g.drawString(decimalFormat.format(neuron.getOutput()), x, (j + 1) * marginY + 5);
			}
		}
	}

	public void setNetwork(final MultiLayerPerceptron network) {
		this.network = network;
	}

	@Override
	public void update(final long ms) {
	}

}
