/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.planner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import dk.dtu.ai.blueducks.heuristics.Heuristic;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.LevelMap;

public class AStarSearch {

	// distance between a cell and its neighbor
	static final double DISTANCE_ONE = 1.0;

	private LevelMap map;
	private Heuristic<Cell, Cell> heuristic;
	private List<Cell> path;
	private Set<Cell> closedSet;
	private TreeSet<Cell> openSet;
	// score from beginning of path till current position
	private HashMap<Cell, Double> scorePartial;
	// estimated score from cell till goal
	private HashMap<Cell, Double> scoreEstimated;

	/**
	 * Instantiates a new planner.
	 * 
	 * @param map the map
	 * @param heuristic the heuristic
	 */
	public AStarSearch(LevelMap map, Heuristic<Cell, Cell> heuristic) {
		super();
		this.map = map;
		this.heuristic = heuristic;
	}

	public List<Cell> getBestPath(Cell beginCell, Cell endCell) {
		// empty set for already explored cells
		closedSet = new HashSet<>();
		scorePartial = new HashMap<>();
		scoreEstimated = new HashMap<>();
		path.add(beginCell);

		// in the "to explore" set there is in the beginning just the cell with which we begin
		openSet = new TreeSet<>();
		openSet.add(beginCell);
		scorePartial.put(beginCell, (double) 0);
		scoreEstimated.put(beginCell, heuristic.getHeuristicValue(beginCell, endCell));

		while (!openSet.isEmpty()) {
			Cell current = openSet.first();
			if (current.equals(endCell))
				return path;
			closedSet.add(current);
			openSet.remove(current);

			for (Cell cell : current.getNeighbours()) {
				double tentativeScore = scorePartial.get(current) + DISTANCE_ONE;

				if (closedSet.contains(cell)) {
					if (tentativeScore >= scorePartial.get(cell)) {
						continue;
					}
				}

				if (!openSet.contains(cell) || tentativeScore < scorePartial.get(cell)) {
					path.add(current);
					// update partial score from start to neighbor cell
					scorePartial.remove(cell);
					scorePartial.put(cell, tentativeScore);

					// update estimated score till goal for neighbor cell
					scoreEstimated.remove(cell);
					scoreEstimated.put(cell,
							scorePartial.get(cell) + heuristic.getHeuristicValue(cell, endCell));

					if (!openSet.contains(cell)) {
						openSet.add(cell);
					}
				}
				
				// TODO - re-think how to return path. Maybe using a HashMap(with Cell and parent)
			}
		}
		return path;
	}
}
