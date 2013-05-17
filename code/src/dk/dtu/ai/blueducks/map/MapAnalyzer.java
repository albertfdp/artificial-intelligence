/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

public class MapAnalyzer {
	
	/** The analyzer. */
	private static MapAnalyzer analyzer;
	
	private static final Logger log = Logger.getLogger(MapLoader.class.getSimpleName());
	
	private static Graph<Cell, String> graph;
	
	private MapAnalyzer() {
		MapAnalyzer.graph = this.getGraph(LevelMap.getInstance().getCells());
	}
	
	public static MapAnalyzer getInstance() {
		if (MapAnalyzer.analyzer == null) {
			MapAnalyzer.analyzer = new MapAnalyzer();
		}
		return MapAnalyzer.analyzer;
	}
	
	private Graph<Cell, String> getGraph(List<Cell> cells) {
		Graph<Cell, String> graph = new UndirectedSparseGraph<Cell, String>();
		
		for (Cell cell : cells) {
			graph.addVertex(cell);
		}
		
		for (Cell cell : cells) {
			for (Cell neighbour : cell.getCellNeighbours()) {
				if (neighbour != null) {
					graph.addEdge(cell.toString() + "-" + neighbour.toString() + "-" + 
							cell.getDirection(neighbour), cell, neighbour);
				}
			}
		}
		return graph;
	}
	
	public Map<Cell, Integer> getDegreeCentrality() {
		Map<Cell, Integer> degrees = new HashMap<Cell, Integer>();
		for (Cell cell : graph.getVertices()) {
			degrees.put(cell, graph.degree(cell));
		}
		return degrees;
	}
	
	public Map<Cell, Double> getNormalizedBetweenessCentrality() {
		Map<Cell, Double> scores = getBetweenessCentrality();
		double highest = 0.0;
		for (Entry<Cell, Double> entry : scores.entrySet()) {
			if (entry.getValue() > highest)
				highest = entry.getValue();
		}
		Map<Cell, Double> normalizedScores = new HashMap<Cell, Double>();
		for (Entry<Cell, Double> entry : scores.entrySet()) {
			log.info("SCORE: " + entry.getKey().toString() + " " + entry.getValue() / highest);
			normalizedScores.put(entry.getKey(), entry.getValue() / highest);
		}
		return normalizedScores;
	}
	
	public Map<Cell, Double> getBetweenessCentrality() {
		
		Map<Cell, Double> scores = new HashMap<Cell, Double>();
		
		BetweennessCentrality<Cell, String> bc = new BetweennessCentrality<Cell, String>(graph);
		bc.setRemoveRankScoresOnFinalize(false);
		bc.evaluate();
		for (Cell c : graph.getVertices()) {
			scores.put(c, bc.getVertexRankScore(c));
		}
		return scores;
	}
	
	public Map<Cell, Number> getDistances(Cell cell) {
		DijkstraShortestPath<Cell, String> dd = new DijkstraShortestPath<Cell, String>(graph);
		return dd.getDistanceMap(cell);
	}

}
