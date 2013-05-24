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
	
	public static Graph<Cell, CellEdge> graph;
	
	private static DijkstraShortestPath<Cell, CellEdge> dd;
	
	private static Map<Cell, Double> nbc;
	
	private MapAnalyzer() {
		MapAnalyzer.graph = this.computeGraph(LevelMap.getInstance().getCells());
	}
	
	public static MapAnalyzer getInstance() {
		if (MapAnalyzer.analyzer == null) {
			MapAnalyzer.analyzer = new MapAnalyzer();
		}
		return MapAnalyzer.analyzer;
	}
	
	private Graph<Cell, CellEdge> computeGraph(List<Cell> cells) {
		Graph<Cell, CellEdge> graph = new UndirectedSparseGraph<Cell, CellEdge>();
		log.info("Constructing graph ....");
		for (Cell cell : cells) {
			graph.addVertex(cell);
		}
		
		for (Cell cell : cells) {
			for (Cell neighbour : cell.getCellNeighbours()) {
				if (neighbour != null) {
					graph.addEdge(new CellEdge(cell, neighbour), cell, neighbour);
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
	
	public Map<Cell, Double> getDefaultBetweennessCentrality() {
		if (nbc == null) {
			Map<Cell, Double> scores = new HashMap<Cell, Double>();
			for (Cell cell : graph.getVertices()) {
				scores.put(cell, 1.00);
			}
			return scores;
		}
		return nbc;
	}
	
	public Map<Cell, Double> getNormalizedBetweenessCentrality() {
		if (nbc == null) {
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
		return nbc;
	}
	
	public Map<Cell, Double> getBetweenessCentrality() {
		
		Map<Cell, Double> scores = new HashMap<Cell, Double>();
		log.info("Computing betweenness centrality");
		BetweennessCentrality<Cell, CellEdge> bc = new BetweennessCentrality<Cell, CellEdge>(graph);
		bc.setRemoveRankScoresOnFinalize(false);
		bc.evaluate();
		for (Cell c : graph.getVertices()) {
			scores.put(c, bc.getVertexRankScore(c));
		}
		return scores;
	}
	
	public Map<Cell, Number> getManhattanDistances(Cell cellA) {
		Map<Cell, Number> distanceMap = new HashMap<Cell, Number>();
		for (Cell cellB : graph.getVertices()) {
			distanceMap.put(cellB, getManhattanDistance(cellA, cellB));
		}
		return distanceMap;
	}
	
	public Number getManhattanDistance(Cell cellA, Cell cellB) {
		return (Number) (Math.abs(cellA.x - cellB.x) + Math.abs(cellA.y - cellB.y));
	}
	
	public Map<Cell, Number> getDistances(Cell cell) {
		if (MapAnalyzer.dd == null)
			log.info("Computing Dijkstra Shortest Path ...");
			MapAnalyzer.dd = new DijkstraShortestPath<Cell, CellEdge>(graph);
		return dd.getDistanceMap(cell);
	}
	
	public List<CellEdge> getPath(Cell cellA, Cell cellB) {
		if (MapAnalyzer.dd == null)
			log.info("Computing Dijkstra Shortest Path ...");
			MapAnalyzer.dd = new DijkstraShortestPath<Cell, CellEdge>(graph);
		return dd.getPath(cellA, cellB);
	}

}
