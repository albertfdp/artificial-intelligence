/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.heuristics;

import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.goals.DeliverBoxGoal;
import dk.dtu.ai.blueducks.goals.Goal;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.LevelMap;

/**
 * The Heuristic used for the GoalPlanner to pick a goal.
 */
public class GoalPlannerHeuristic {

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
		
		// TODO: Add more stuff
		return distance;
	}
}