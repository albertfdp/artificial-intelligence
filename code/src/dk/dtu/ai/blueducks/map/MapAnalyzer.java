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
	
	private static final Logger logger = Logger.getLogger(MapAnalyzer.class.getSimpleName());
	
	public static Graph<Cell, CellEdge> graph;
	
	private static DijkstraShortestPath<Cell, CellEdge> dd;
	
	private static Map<Cell, Double> normalizedBetweennessCentrality;
	
	private static Map<Cell, Integer> degreeCentrality;
	
	private static Set<List<Cell>> deadEnds;
	
	private static Set<Set<Cell>> neighbourGoals;
	
	private static Map<Cell, Map<Cell, Number>> distances;
	
	private static int MapHashCode = 0;
	
	public MapAnalyzer() {
		distances = new HashMap<Cell, Map<Cell, Number>>();
	}
	
	public void doAnalysis() {
		MapHashCode = LevelMap.getInstance().hashCode();
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
		computeDegreeCentrality();
		//computeDeadEnds();
		computeNeighbourGoals();
	}
		
	public static int getMapHashCode() {
		return MapHashCode;
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
		normalizedBetweennessCentrality = new HashMap<Cell, Double>();
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
		boolean hasNeighbours = false;
		for (Cell cell : cells) {			
			if (graph.degree(cell) > 2)
				return true;
		}
		return !hasNeighbours;
	}
	
	private void computeDeadEnds() {
 		logger.info("Computing dead ends ...");
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
			neighbours.removeAll(Collections.singleton(null)); // remove nulls
			Cell currentCell = endOfDeadEndCell;
			
			double dCurrent = 0;
			double dNext = 0;
			

			boolean isInEmptyBlock = false;
			while (!isEndOfDeadEnd(neighbours) && !isInEmptyBlock) {
				for (Cell neighbour : neighbours) {
					if (endOfDeadEndCells.contains(neighbour)) {
						isInEmptyBlock = true;
						break;
					} else {
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
		logger.info("Dead ends computed ...");
	}
		
	private void computeNeighbourGoals() {
		logger.info("Computing groups of goals ...");
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
		logger.info("Groups of goals computed ...");
	}
	
	private Set<Set<Cell>> mergeListsCells(Set<Set<Cell>> groups) {
		Set<Set<Cell>> merged = new HashSet<Set<Cell>>();
		for (Set<Cell> groupA : groups) {
			boolean alreadyMerged = false;
			for (Set<Cell> mergedGroups : merged) {
				if (mergedGroups.containsAll(groupA)) {
					alreadyMerged = true;
					break;
				}
			}
			if (alreadyMerged)
				continue;
			Set<Cell> union = new HashSet<Cell>(groupA);
			for (Set<Cell> groupB : groups) {
				Set<Cell> intersection = new HashSet<Cell>(union);
				intersection.retainAll(groupB);
				if (intersection.size() > 0) {
					union.addAll(groupB);
				} 
			}
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

	public static Set<Set<Cell>> getNeighbourGoals() {
		return neighbourGoals;
	}

}
