package com.pignic.nnavoid.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.pignic.nnavoid.pojo.Cell;

public class Report {

	private final static char delimiter = ',';

	private final File outFile;

	private final BufferedWriter writer;

	public Report(final File reportFolder) throws IOException {
		outFile = new File(reportFolder, System.currentTimeMillis() + ".csv");
		outFile.createNewFile();
		final FileOutputStream fos = new FileOutputStream(outFile);
		writer = new BufferedWriter(new OutputStreamWriter(fos));
	}

	public void write(final int generation, final float maxFitness, final int collisions, final Cell... cells) {
		try {
			writer.write(Integer.toString(generation));
			writer.write(delimiter);
			writer.write(Float.toString(maxFitness));
			writer.write(delimiter);
			writer.write(Integer.toString(collisions));
			writer.write(delimiter);
			for (final Cell cell : cells) {
				writer.write(Float.toString(cell.getFitness()));
				writer.write(delimiter);
			}
			writer.newLine();
			writer.flush();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
