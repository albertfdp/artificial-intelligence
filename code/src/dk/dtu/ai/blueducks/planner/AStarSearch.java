/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.planner;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.logging.Logger;

import dk.dtu.ai.blueducks.goals.Goal;
import dk.dtu.ai.blueducks.heuristics.Heuristic;

public class AStarSearch {

	private static final Logger log = Logger.getLogger(AStarSearch.class.getSimpleName());

	// distance between a cell and its neighbor
	private static final float DISTANCE_ONE = 1.0f;

	@SuppressWarnings("unchecked")
	public static <NodeType extends AStarNode, GoalType extends Goal> List<NodeType> getBestPath(
			NodeType begin, GoalType end, Heuristic<NodeType, GoalType> heuristic) {

		Set<NodeType> closedSet;
		PriorityQueue<NodeType> openSet;

		log.finest("Starting Path Planning from " + begin + " to " + end);
		// empty set for already explored cells
		closedSet = new HashSet<>();
		openSet = new PriorityQueue<>();

		// in the "to explore" set there is in the beginning just the cell with which we begin
		openSet.add(begin);
		begin.g = 0;
		begin.f = heuristic.getHeuristicValue(begin, end);

		while (!openSet.isEmpty()) {
			NodeType current = openSet.peek();
			if (current.satisfiesGoal(end))
				return AStarSearch.<NodeType> computePath(current);
			closedSet.add(current);
			openSet.remove(current);
			log.info("THe state: " + current);
			for (AStarNode _entity : current.getNeighbours()) {
				NodeType entity = (NodeType) _entity;
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

	@SuppressWarnings("unchecked")
	private static <NodeType extends AStarNode> List<NodeType> computePath(NodeType finalState) {
		LinkedList<NodeType> path = new LinkedList<>();

		while (finalState != null) {
			path.addFirst(finalState);
			finalState = (NodeType) finalState.getPreviousNode();
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
