/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

public class MapAnalyzer {
	
	/** The analyzer. */
	private static MapAnalyzer analyzer;
	
	private final static int MAX_NUMBER_VERTEX = 60 * 60;
	
	private static final Logger logger = Logger.getLogger(MapLoader.class.getSimpleName());
	
	public static Graph<Cell, CellEdge> graph;
	
	private static DijkstraShortestPath<Cell, CellEdge> dd;
	
	private static Map<Cell, Double> normalizedBetweennessCentrality;
	
	private static Map<Cell, Integer> degreeCentrality;
	
	private static Set<List<Cell>> deadEnds;
	
	private static Set<List<Cell>> neighbourGoals;
	
	private static Map<Cell, Map<Cell, Number>> distances;
	
	public MapAnalyzer() {
		distances = new HashMap<Cell, Map<Cell, Number>>();
	}
	
	public void doAnalysis() {
		MapAnalyzer.graph = this.computeGraph(LevelMap.getInstance().getCells());
		if (MapAnalyzer.graph.getVertexCount() < MAX_NUMBER_VERTEX) {
			// precompute dijkstra and betweenness centrality
			logger.info("Using betweennes centrality and dijkstra distances for improving performance ... ");
			computeNormalizedBetweenessCentrality();
			computeDijkstraShortestPath();
		} else {
			logger.info("Using manhattan distances for improving computing time ... ");
			setDefaultBetweennessCentrality();
			computeManhattanDistances();
		}
		computeDeadEnds();
		computeNeighbourGoals();
	}
	
	public static MapAnalyzer getInstance() {
		if (MapAnalyzer.analyzer == null) {
			MapAnalyzer.analyzer = new MapAnalyzer();
		}
		return MapAnalyzer.analyzer;
	}
	
	private Graph<Cell, CellEdge> computeGraph(List<Cell> cells) {
		logger.info("Constructing graph ....");
		Graph<Cell, CellEdge> graph = new UndirectedSparseGraph<Cell, CellEdge>();
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
	
	public void computeDegreeCentrality() {
		degreeCentrality = new HashMap<Cell, Integer>();
		for (Cell cell : graph.getVertices()) {
			degreeCentrality.put(cell, graph.degree(cell));
		}
	}
	
	/**
	 * Sets the betweenness centrality to 1 for all cells.
	 */
	public void setDefaultBetweennessCentrality() {
		for (Cell cell : graph.getVertices()) {
			normalizedBetweennessCentrality.put(cell, 1.00);
		}
	}
	
	public void computeNormalizedBetweenessCentrality() {
		normalizedBetweennessCentrality = new HashMap<Cell, Double>();
		Map<Cell, Double> scores = getBetweenessCentrality();
		double highest = 0.0;
		for (Entry<Cell, Double> entry : scores.entrySet()) {
			if (entry.getValue() > highest)
				highest = entry.getValue();
		}
		for (Entry<Cell, Double> entry : scores.entrySet()) {
			normalizedBetweennessCentrality.put(entry.getKey(), entry.getValue() / highest);
		}
	}
	
	
	public Map<Cell, Double> getBetweenessCentrality() {
		Map<Cell, Double> scores = new HashMap<Cell, Double>();
		logger.info("Computing betweenness centrality");
		BetweennessCentrality<Cell, CellEdge> bc = new BetweennessCentrality<Cell, CellEdge>(graph);
		bc.setRemoveRankScoresOnFinalize(false);
		bc.evaluate();
		for (Cell c : graph.getVertices()) {
			scores.put(c, bc.getVertexRankScore(c));
		}
		return scores;
	}
	
	public void computeManhattanDistances() {
		for (Cell cellA : graph.getVertices()) {
			distances.put(cellA, getManhattanDistances(cellA));
		}
	}
	
	private Map<Cell, Number> getManhattanDistances(Cell cellA) {
		Map<Cell, Number> distanceMap = new HashMap<Cell, Number>();
		for (Cell cellB : graph.getVertices()) {
			distanceMap.put(cellB, getManhattanDistance(cellA, cellB));
		}
		return distanceMap;
	}
	
	private Number getManhattanDistance(Cell cellA, Cell cellB) {
		return (Number) (Math.abs(cellA.x - cellB.x) + Math.abs(cellA.y - cellB.y));
	}
	
	
	private boolean isEndOfDeadEnd(List<Cell> cells) {
		for (Cell cell : cells) {			
			if (cell != null && graph.degree(cell) > 2)
				return true;
		}
		return false;
	}
	
	private void computeDeadEnds() {
		Set<Cell> endOfDeadEndCells = new HashSet<Cell>();
		for (Cell cell : graph.getVertices()) {
			if (normalizedBetweennessCentrality.get(cell) == 0) {
				endOfDeadEndCells.add(cell);
			}
		}
		
		deadEnds = new HashSet<List<Cell>>();
		for (final Cell endOfDeadEndCell : endOfDeadEndCells) {
			List<Cell> deadEndCells = new ArrayList<Cell>();
			List<Cell> neighbours = endOfDeadEndCell.getCellNeighbours();
			Cell currentCell = endOfDeadEndCell;
			
			double dCurrent = 0;
			double dNext = 0;
			
			while (!isEndOfDeadEnd(neighbours)) {
				for (Cell neighbour : neighbours) {
					if (neighbour != null) {
						dCurrent = distances.get(endOfDeadEndCell).get(currentCell).doubleValue();
						dNext = distances.get(endOfDeadEndCell).get(neighbour).doubleValue();
						if (!deadEndCells.contains(neighbour))
							deadEndCells.add(neighbour);
						if (dCurrent < dNext) {
							currentCell = neighbour;
						}
					}
				}
				neighbours = currentCell.getCellNeighbours();
			}
			Collections.sort(deadEndCells, new Comparator<Cell>() {

				@Override
				public int compare(Cell o1, Cell o2) {
					double d1 = dd.getDistance(endOfDeadEndCell, o1).doubleValue();
					double d2 = dd.getDistance(endOfDeadEndCell, o2).doubleValue();
					
					return d1 < d2 ? -1 : d1 > d2 ? 1 : 0;
				}
				
			});
			deadEnds.add(deadEndCells);
		}
	}
		
	private void computeNeighbourGoals() {
		List<Cell> goals = LevelMap.getInstance().getAllGoals();
		Set<Set<Cell>> groupsGoals = new HashSet<Set<Cell>>();
		for (Cell goal : goals) {
			Set<Cell> neighbourGoals = new HashSet<Cell>();
			neighbourGoals.add(goal);
			for (Cell neighbourCell : goal.getCellNeighbours()) {
				if (goals.contains(neighbourCell))
					neighbourGoals.add(neighbourCell);
			}
			groupsGoals.add(neighbourGoals);
		}
		MapAnalyzer.neighbourGoals = mergeListsCells(groupsGoals);
	}
	
	private Set<List<Cell>> mergeListsCells(Set<Set<Cell>> groups) {

		Set<List<Cell>> merged = new HashSet<List<Cell>>();
		for (Set<Cell> groupA : groups) {
			boolean alreadyMerged = false;
			for (List<Cell> mergedGroups : merged) {
				if (mergedGroups.containsAll(groupA)) {
					alreadyMerged = true;
					break;
				}
			}
			if (alreadyMerged)
				continue;
			List<Cell> union = new ArrayList<Cell>(groupA);
			for (Set<Cell> groupB : groups) {
				Set<Cell> intersection = new HashSet<Cell>(union);
				intersection.retainAll(groupB);
				if (intersection.size() > 0) {
					for (Cell cellB : groupB) {
						if (!union.contains(cellB))
							union.add(cellB);
					}
					//union.addAll(groupB);
				} 
			}
			Collections.sort(union, new Comparator<Cell>() {

				@Override
				public int compare(Cell o1, Cell o2) {
					double b1 = normalizedBetweennessCentrality.get(o1).doubleValue();
					double b2 = normalizedBetweennessCentrality.get(o2).doubleValue();
					
					return b1 < b2 ? -1 : b1 > b2 ? 1 : 0;
				}
				
			});
			merged.add(union);
		}
		return merged;
	}
	
	private void computeDijkstraShortestPath() {
		MapAnalyzer.dd = new DijkstraShortestPath<Cell, CellEdge>(graph);
		for (Cell cellA : graph.getVertices()) {
			distances.put(cellA, dd.getDistanceMap(cellA));
		}
	}
	
	/**
	 * Gets the cached Map of distances between a cell and all other cells.
	 * Depending on the size of the map, will return a Manhattan or Dijkstra
	 * distance.
	 *
	 * @return the distances
	 */
	public static Map<Cell, Map<Cell, Number>> getDistances() {
		return distances;
	}
	
	public static Map<Cell, Double> getNormalizedBetweennessCentrality() {
		return normalizedBetweennessCentrality;
	}

	public static Map<Cell, Integer> getDegreeCentrality() {
		return degreeCentrality;
	}

	public static Set<List<Cell>> getNeighbourGoals() {
		return neighbourGoals;
	}

}
