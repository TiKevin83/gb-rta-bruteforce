package stringflow.rta.gen1.astar;

import java.util.Comparator;

public class NodeSorter implements Comparator<Node> {

	public int compare(Node n0, Node n1) {
		if(n1.getFCost() < n0.getFCost())  {
			return 1;
		} else if(n1.getFCost() > n0.getFCost()) {
			return -1;
		}
		return 0;
	}
}