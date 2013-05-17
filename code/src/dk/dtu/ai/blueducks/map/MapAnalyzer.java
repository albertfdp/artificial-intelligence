package dk.dtu.ai.blueducks.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

public class MapAnalyzer {
	
	private static final Logger log = Logger.getLogger(MapLoader.class.getSimpleName());
	
	private static Graph<Cell, String> getGraph(List<Cell> cells) {
		
		Graph<Cell, String> g = new UndirectedSparseGraph<Cell, String>();
		
		for (Cell cell : cells) {
			g.addVertex(cell);
		}
		
		for (Cell cell : cells) {
			for (Cell neighbour : cell.getCellNeighbours()) {
				if (neighbour != null) {
					g.addEdge(cell.toString() + "-" + neighbour.toString() + "-" + 
							cell.getDirection(neighbour), cell, neighbour);
				}
			}
		}
		return g;
	}
	
	public static Map<Cell, Integer> getDegreeCentrality() {
		Map<Cell, Integer> degrees = new HashMap<Cell, Integer>();
		Graph<Cell, String> g = getGraph(LevelMap.getInstance().getCells());
		for (Cell cell : g.getVertices()) {
			degrees.put(cell, g.degree(cell));
		}
		return degrees;
	}
	
	public static Map<Cell, Double> getBetweenessCentrality() {
		
		Map<Cell, Double> scores = new HashMap<Cell, Double>();
		
		Graph<Cell, String> g = getGraph(LevelMap.getInstance().getCells());
		
		BetweennessCentrality<Cell, String> bc = new BetweennessCentrality<Cell, String>(g);
		bc.setRemoveRankScoresOnFinalize(false);
		bc.evaluate();
		for (Cell c : g.getVertices()) {
			scores.put(c, bc.getVertexRankScore(c));
			log.info("SCORE\t" + c.toString() + "\t" + bc.getVertexRankScore(c));
		}
		return scores;
	}
	
	public static int getDistance(Cell cellA, Cell cellB) {
		Graph<Cell, String> g = getGraph(LevelMap.getInstance().getCells());
		DijkstraShortestPath<Cell, String> dd = new DijkstraShortestPath<Cell, String>(g);
		return dd.getDistance(cellA, cellB).intValue();
	}
	

}
