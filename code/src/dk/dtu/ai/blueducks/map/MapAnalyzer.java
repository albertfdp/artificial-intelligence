/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.map;

import java.util.ArrayList;
import java.util.Collection;
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
			nbc = normalizedScores;
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
	
	
	private boolean isEndOfDeadEnd(List<Cell> cells) {
		for (Cell cell : cells) {			
			if (cell != null && graph.degree(cell) > 2)
				return true;
		}
		return false;
	}
	
	public Set<List<Cell>> getDeadEnds() {
		Map<Cell, Double> nbc = getNormalizedBetweenessCentrality();
		Set<Cell> endOfDeadEndCells = new HashSet<Cell>();
		for (Cell cell : graph.getVertices()) {
			if (nbc.get(cell) == 0) {
				endOfDeadEndCells.add(cell);
			}
		}
		
		Set<List<Cell>> deadEndsCells = new HashSet<List<Cell>>();
		for (final Cell endOfDeadEndCell : endOfDeadEndCells) {
			List<Cell> deadEndCells = new ArrayList<Cell>();
			List<Cell> neighbours = endOfDeadEndCell.getCellNeighbours();
			Cell currentCell = endOfDeadEndCell;
			while (!isEndOfDeadEnd(neighbours)) {
				for (Cell neighbour : neighbours) {
					if (neighbour != null) {
						double dCurrent = dd.getDistance(endOfDeadEndCell, currentCell).doubleValue();
						double dNext = dd.getDistance(endOfDeadEndCell, neighbour).doubleValue();
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
			deadEndsCells.add(deadEndCells);
		}		
		return deadEndsCells;
	}
		
	public Set<Set<Cell>> getNeighbourGoals(List<Cell> goals) {
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
		
		Set<Set<Cell>> uniqueGoals = mergeListsCells(groupsGoals);
		
		return uniqueGoals;
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
	
	
//	public List<Cell> getGoalsRow(Cell goal, List<Cell> goals) {
//		List<Cell> row = new ArrayList<Cell>();
//		
//		List<Cell> goalNeighbours = new ArrayList<Cell>();
//		for (Cell cell : goal.getCellNeighbours()) {
//			if (goals.contains(cell))
//				goalNeighbours.add(cell);
//		}
//		
//		row.add(goal);
//		for (Cell cell : goalNeighbours) {
//			if (!row.contains(cell))
//				row.add(cell);
//		}
//		
//		Collections.sort(row, new Comparator<Cell>() {
//
//			@Override
//			public int compare(Cell o1, Cell o2) {
//				
//				Map<Cell, Double> nbc = LevelMap.getInstance().getBetweenessCentrality();
//				double bc1 = nbc.get(o1).doubleValue();
//				double bc2 = nbc.get(o2).doubleValue();
//				
//				return bc1 < bc2 ? -1 : bc1 > bc2 ? 1 : 0;
//			}
//			
//		});
//		return row;
//	}
	
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
