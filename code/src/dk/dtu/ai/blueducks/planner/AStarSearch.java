/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.planner;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import dk.dtu.ai.blueducks.goals.Goal;
import dk.dtu.ai.blueducks.heuristics.Heuristic;
import dk.dtu.ai.blueducks.map.State;

public class AStarSearch {

	private static final Logger log = Logger.getLogger(AStarSearch.class.getSimpleName());

	// distance between a cell and its neighbor
	private static final float DISTANCE_ONE = 1.0f;

	@SuppressWarnings("unchecked")
	public static <NodeType extends AStarNode, GoalType extends Goal> List<NodeType> getBestPath(
			NodeType begin, GoalType end, Heuristic<NodeType, GoalType> heuristic) {

		Set<NodeType> closedSet;
		PriorityQueue<NodeType> openSet;

		if (log.isLoggable(Level.FINE))
			log.fine("Starting Path Planning from " + begin + " for " + end);
		// empty set for already explored cells
		closedSet = new HashSet<>(100000);
		openSet = new PriorityQueue<>(100000);

		// in the "to explore" set there is in the beginning just the cell with which we begin
		openSet.add(begin);
		begin.g = 0;
		begin.f = heuristic.getHeuristicValue(begin, end, null);
		int loopCount = 0;

		while (!openSet.isEmpty()) {
			if (openSet.size() > 10000) {
				Iterator<NodeType> it = openSet.iterator();
				int i = 0;
				while (it.hasNext() && (i++) < 6000)
					it.next();
				while (it.hasNext()){
					it.next();
					it.remove();
				}

			}
			if (log.isLoggable(Level.INFO)) {
				loopCount++;
				if ((loopCount % 5000) == 0)
					log.info("Expanded " + loopCount + " states. OpenSet size: " + openSet.size());
			}
			NodeType current = openSet.peek();
			if (current.satisfiesGoal(end))
				return AStarSearch.<NodeType> computePath(current);
			closedSet.add(current);
			openSet.remove(current);
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
					entity.f = entity.g + heuristic.getHeuristicValue(entity, end, current);

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
