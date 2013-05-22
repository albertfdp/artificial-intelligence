/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.heuristics;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.goals.DeliverBoxGoal;
import dk.dtu.ai.blueducks.goals.Goal;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.CellEdge;
import dk.dtu.ai.blueducks.map.LevelMap;
import dk.dtu.ai.blueducks.map.MapAnalyzer;

/**
 * The Heuristic used for the GoalPlanner to pick a goal.
 */
public class GoalPlannerHeuristic {
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(LevelMap.class.getSimpleName());

	public static float getHeuristicValue(Agent agent, Goal goal) {

		DeliverBoxGoal dbg=(DeliverBoxGoal) goal;
		Cell agentCell = LevelMap.getInstance().getCellForAgent(agent);
		Cell boxCell = LevelMap.getInstance().getCurrentState().getCellForBox(dbg.getWhat());
		Cell goalCell = dbg.getTo();

		float distance = 100000;
		if (boxCell != null) { 
			distance = LevelMap.getInstance().getDijkstraDistance(agentCell, boxCell);
			distance += LevelMap.getInstance().getDijkstraDistance(boxCell, goalCell);
		}
		
		if (LevelMap.getInstance().getLockedCells().contains(boxCell)) {
			distance += 100000;
		}
		
		List<Cell> goalNeighbours = goalCell.getCellNeighbours();
		
		for (Cell neighbour : goalNeighbours) {
			Map<Cell, Double> nbc = MapAnalyzer.getInstance().getNormalizedBetweenessCentrality();
						
			if (neighbour != null && (MapAnalyzer.getInstance().getDegreeCentrality().get(goalCell) == 2)
					&& LevelMap.getInstance().isGoal(neighbour) && (nbc.get(neighbour) < nbc.get(goalCell))) {
				distance += 1000;
			}
		}
		
		// TODO: Add more stuff
		return distance;
	}
}