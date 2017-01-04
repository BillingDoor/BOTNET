package cs.sii.control.command;

import java.util.List;
import java.util.Random;

import org.jgrapht.VertexFactory;

public class MyVertexFactory<T> implements VertexFactory<T> {

	private List<T> nodes;
	private Random rng;

	public MyVertexFactory(List<T> nodes, Random gen) {
		super();
		this.nodes = nodes;
		rng = gen;
	}

	@Override
	public T createVertex() {

		//return nodes.get(rng.nextInt(nodes.size() - 1));
		return nodes.remove(0);
	}

	public Integer nodesLenght() {
		return nodes.size();
	}
}
