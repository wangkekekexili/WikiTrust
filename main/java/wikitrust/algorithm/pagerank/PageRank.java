package wikitrust.algorithm.pagerank;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.OptionalDouble;


/**
 * PageRank
 * 
 * @author Ke Wang
 *
 */
public class PageRank {
	
	public static double PRECISION = 0.000001;
	public static double ALPHA = 0.85;
	
	public static Map<Integer, Double> runPageRank(List<Integer> nodeIds, List<int[]> links) {

		// create a list of nodes for each node id
		Map<Integer, Node> nodeMap = new HashMap<Integer, Node>();
		for (Integer i : nodeIds) {
			Node newNode = new Node(i);
			newNode.setValue(1.0/nodeIds.size());
			nodeMap.put(i, newNode);
		}
		
		// scan links (edges)
		for (int[] link : links) {
			int nodeFrom = link[0];
			int nodeTo = link[1];
			Edge newEdge = new Edge(nodeMap.get(nodeFrom), nodeMap.get(nodeTo));
			nodeMap.get(nodeFrom).addOutgoingEdge(newEdge);
			nodeMap.get(nodeTo).addIncmongEdge(newEdge);
		}
		
		OptionalDouble maxDifference = OptionalDouble.empty();
		
		while (maxDifference.isPresent()==false || maxDifference.getAsDouble()>PRECISION) {
			maxDifference = OptionalDouble.of(0);
			nodeMap.forEach((nodeId, node)->{
				node.resetTempValue();
			});
			nodeMap.forEach((nodeId, node)->{
				int numberOfOutgoingEdges = node.getOutgoingEdges().size();
				if (numberOfOutgoingEdges == 0) {
					double temp = node.getValue() / (nodeMap.size());
					nodeMap.forEach((nodeId2, node2)->{
						node2.addTempValue(temp);
					});
				} else {
					double temp = node.getValue() / numberOfOutgoingEdges;
					node.getOutgoingEdges().forEach(edge->{
						Node anotherEnd = edge.getAnotherNode(node);
						anotherEnd.addTempValue(temp);
					});
				}
			});
			
			// calculate new weight
			for (Entry<Integer, Node> entry : nodeMap.entrySet()) {
				Node n = entry.getValue();
				n.setTempValue(n.getTempValue()*ALPHA + (1-ALPHA)/nodeMap.size());
				double difference = Math.abs(n.getTempValue() - n.getValue());
				if (maxDifference.isPresent()==false || difference>maxDifference.getAsDouble()) {
					maxDifference = OptionalDouble.of(difference);
				}
				n.setValue(n.getTempValue());
			}
			
		}
		
		// generate final result and return
		Map<Integer, Double> finalResult = new HashMap<>();
		nodeMap.forEach((nodeId, node)->{
			finalResult.put(nodeId, node.getValue());
		});
		
		return finalResult;
	}
}