/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.planner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import dk.dtu.ai.blueducks.goals.Goal;
import dk.dtu.ai.blueducks.heuristics.Heuristic;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.LevelMap;

public class AStarSearch {


	Logger log = Logger.getLogger(AStarSearch.class.getSimpleName());

	// distance between a cell and its neighbor
	static final double DISTANCE_ONE = 1.0;

	private LevelMap map;
	private Heuristic<? extends AStarNode, ? extends Goal> heuristic;
	// private List<PathCell> path;
	private HashMap<AStarNode, AStarNode> path;
	private Set<AStarNode> closedSet;
	private TreeSet<AStarNode> openSet;
	// score from beginning of path till current position
	private HashMap<AStarNode, Double> scorePartial;
	// estimated score from cell till goal
	private HashMap<AStarNode, Double> scoreEstimated;

	/**
	 * Instantiates a new planner.
	 * 
	 * @param map the map
	 * @param heuristic the heuristic
	 */
	public AStarSearch(LevelMap map, Heuristic<? extends AStarNode, ? extends Goal> heuristic) {
		super();
		this.map = map;
		this.heuristic = heuristic;
	}

	public List<AStarNode> getBestPath(AStarNode begin, Goal end) {

		log.finest("Starting Path Planning from " + begin + " to " + end);
		// empty set for already explored cells
		closedSet = new HashSet<>();
		scorePartial = new HashMap<>();
		scoreEstimated = new HashMap<>();
		openSet = new TreeSet<>();
		path = new HashMap<>();

		// PathCell beginning = new PathCell (beginCell, null);
		path.put(begin, null);

		// in the "to explore" set there is in the beginning just the cell with which we begin
		openSet.add(begin);
		scorePartial.put(begin, (double) 0);
		scoreEstimated.put(begin, heuristic.getHeuristicValue(begin, end));

		while (!openSet.isEmpty()) {
			AStarNode current = openSet.first();
			if (current.equals(end))
				return computePath(path, end);
			closedSet.add(current);
			openSet.remove(current);

			for (AStarNode entity : current.getNeighbours()) {
				double tentativeScore = scorePartial.get(current) + DISTANCE_ONE;

				if (closedSet.contains(entity)) {
					if (tentativeScore >= scorePartial.get(entity)) {
						continue;
					}
				}

				if (!openSet.contains(entity) || tentativeScore < scorePartial.get(entity)) {
					// PathCell newPathCell = new PathCell(cell, current);
					path.put(entity, current);
					// update partial score from start to neighbor cell
					scorePartial.remove(entity);
					scorePartial.put(entity, tentativeScore);

					// update estimated score till goal for neighbor cell
					scoreEstimated.remove(entity);
					scoreEstimated.put(entity,
							scorePartial.get(entity) + heuristic.getHeuristicValue(entity, end));

					if (!openSet.contains(entity)) {
						openSet.add(entity);
					}
				}

				// TODO - re-think how to return path. Maybe using a HashMap(with Cell and parent)
			}
		}
		return computePath(path, end);
	}

	private List<AStarNode> computePath(HashMap<AStarNode, AStarNode> path, AStarNode end) {
		List<AStarNode> computedPath = new ArrayList<AStarNode>();
		AStarNode lastEntity = end;

		// going back to recompute the path
		while (path.get(lastEntity) != null) {
			computedPath.add(lastEntity);
			lastEntity = path.get(lastEntity);
		}

		// adding cell before the null cell
		computedPath.add(lastEntity);

		return computedPath;
	}
	// private class PathCell {
	// private Cell current;
	// private Cell previous;
	//
	// private PathCell(Cell current, Cell previous) {
	// this.current = current;
	// this.previous = previous;
	// }
	// }
}
