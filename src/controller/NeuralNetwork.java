package controller;

import java.util.ArrayList;
import java.util.List;

public class NeuralNetwork {

	private class Neuron {

		private final Neuron[] connections;
		private final double[] weights;
		private double output;

		public Neuron(Neuron[] connections) {

			this.connections = connections;

			this.weights = new double[connections.length];
			for (int i = 0; i < weights.length; i++) {
				weights[i] = Math.random() * 2 - 1;
			}
		}

		public void calculateOutput() {

			double sum = 0;
			for (int i = 0; i < connections.length; i++) {
				sum += connections[i].getOutput() * weights[i];
			}
			output = activitionFunction(sum);
		}

		private double activitionFunction(double x) {
			return Math.tanh(x);
		}

		public double getOutput() {
			return output;
		}

		public void setOutput(double output) {
			this.output = output;
		}

	}

	private final List<List<Neuron>> layers;

	public NeuralNetwork(int... numberOfLayers) {

		this.layers = new ArrayList<>();

		List<Neuron> previousLayer = new ArrayList<>();
		for (int numberOfLayer : numberOfLayers) {

			List<Neuron> newLayer = new ArrayList<>();
			for (int i = 0; i < numberOfLayer; i++) {
				Neuron newElement = new Neuron(
						previousLayer.toArray(new Neuron[0]));
				newLayer.add(newElement);
			}
			layers.add(newLayer);
			previousLayer = newLayer;
		}
	}

	public void setInputs(double[] inputs) {

		final List<Neuron> firstLayer = layers.get(0);

		if (inputs.length != firstLayer.size()) {
			throw new IllegalStateException(
					"The inputs parameter size doesn't match the number of neurons in the first layer!");
		}

		for (int i = 0; i < inputs.length; i++) {
			firstLayer.get(i).setOutput(inputs[i]);
		}
	}

	public double[] getOutputs() {

		for (int i = 1; i < layers.size(); i++) {
			List<Neuron> layer = layers.get(i);
			layer.forEach(n -> n.calculateOutput());
		}

		List<Double> outputs = new ArrayList<>();
		for (Neuron neuron : layers.get(layers.size() - 1)) {
			outputs.add(neuron.getOutput());
		}

		return outputs.stream().mapToDouble(Double::doubleValue).toArray();
	}
}
