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
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import dk.dtu.ai.blueducks.goals.Goal;
import dk.dtu.ai.blueducks.heuristics.Heuristic;
import dk.dtu.ai.blueducks.map.LevelMap;

public class AStarSearch<NodeType extends AStarNode, GoalType extends Goal> {

	Logger log = Logger.getLogger(AStarSearch.class.getSimpleName());

	// distance between a cell and its neighbor
	static final float DISTANCE_ONE = 1.0f;

	private LevelMap map;
	private Heuristic<AStarNode, Goal> heuristic;
	private Set<AStarNode> closedSet;
	private PriorityQueue<AStarNode> openSet;

	/**
	 * Instantiates a new planner.
	 * 
	 * @param map the map
	 * @param heuristic the heuristic
	 */
	public AStarSearch(LevelMap map, Heuristic<AStarNode, Goal> heuristic) {
		super();
		this.map = map;
		this.heuristic = heuristic;
	}

	public List<AStarNode> getBestPath(NodeType begin, GoalType end) {

		log.finest("Starting Path Planning from " + begin + " to " + end);
		// empty set for already explored cells
		closedSet = new HashSet<>();
		openSet = new PriorityQueue<>();

		// in the "to explore" set there is in the beginning just the cell with which we begin
		openSet.add(begin);
		begin.g = 0;
		begin.f = heuristic.getHeuristicValue(begin, end);

		while (!openSet.isEmpty()) {
			AStarNode current = openSet.peek();
			if (current.equals(end))
				return computePath(current);
			closedSet.add(current);
			openSet.remove();

			for (AStarNode entity : current.getNeighbours()) {
				float tentativeScore = current.g + DISTANCE_ONE;

				if (closedSet.contains(entity)) {
					if (tentativeScore >= entity.g) {
						continue;
					}
				}

				if (!openSet.contains(entity) || tentativeScore < entity.g) {
					// update partial score from start to neighbor cell
					entity.g = tentativeScore;

					// update estimated score till goal for neighbor cell
					entity.f = entity.g + heuristic.getHeuristicValue(entity, end);

					if (!openSet.contains(entity)) {
						openSet.add(entity);
					}
				}

				// TODO - re-think how to return path. Maybe using a HashMap(with Cell and parent)
			}
		}
		return null;
	}

	private List<AStarNode> computePath(AStarNode finalState) {
		LinkedList<AStarNode> path = new LinkedList<>();

		while (finalState != null) {
			path.addFirst(finalState);
			finalState = finalState.getPreviousNode();
		}

		return path;
	}
	// private List<AStarNode> computePath(HashMap<AStarNode, AStarNode> path, AStarNode end) {
	// List<AStarNode> computedPath = new ArrayList<AStarNode>();
	// AStarNode lastEntity = end;
	//
	// // going back to recompute the path
	// while (path.get(lastEntity) != null) {
	// computedPath.add(lastEntity);
	// lastEntity = path.get(lastEntity);
	// }
	//
	// // adding cell before the null cell
	// computedPath.add(lastEntity);
	//
	// return computedPath;
	// }
}
