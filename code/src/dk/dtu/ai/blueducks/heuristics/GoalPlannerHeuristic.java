/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.heuristics;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.goals.DeliverBoxGoal;
import dk.dtu.ai.blueducks.goals.Goal;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.LevelMap;
import dk.dtu.ai.blueducks.map.MapAnalyzer;

/**
 * The Heuristic used for the GoalPlanner to pick a goal.
 */
public class GoalPlannerHeuristic {
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(LevelMap.class.getSimpleName());

	public static float getHeuristicValue(Agent agent, Goal goal) {
		return chooseNotBlockingGoal(agent, goal);
		//return chooseClosestGoal(agent, goal);
	}
	
	private static float chooseClosestGoal(Agent agent, Goal goal) {
		float h = 0;
		
		float a0 = 1;
		float a1 = 1;
		
		DeliverBoxGoal deliverBoxGoal = (DeliverBoxGoal) goal;
		
		// cell where the agent is located now
		Cell agentCell = LevelMap.getInstance().getCellForAgent(agent);
		
		// cell of the box to be delivered
		Cell boxCell = LevelMap.getInstance().getCurrentState().getCellForBox(deliverBoxGoal.getWhat());
		
		// cell of the goal
		Cell goalCell = deliverBoxGoal.getTo();
		
		float distanceAgentBox = LevelMap.getInstance().getDistance(agentCell, boxCell);
		float distanceBoxGoal = LevelMap.getInstance().getDistance(boxCell, goalCell);
		
		h = a0 * distanceAgentBox + a1 * distanceBoxGoal;
		
		return h;
	}
	
	private static float chooseNotBlockingGoal(Agent agent, Goal goal) {
		
		float h = 0;
		
		float a0 = 1; // distance agent box goal
		float a1 = 1; // betweenness box
		float a2 = 0.01f; // distance other boxes
		float a3 = 200; // betweenness goal cell
		float a4 = 0; // locking goal
		float a5 = 0; // undoing a goal
		
		DeliverBoxGoal deliverBoxGoal = (DeliverBoxGoal) goal;
		
		if (deliverBoxGoal.getWhat().getId() == 'A') {
			a0 = 1;
		}
		
		// cell where the agent is located now
		Cell agentCell = LevelMap.getInstance().getCellForAgent(agent);
		
		// cell of the box to be delivered
		Cell boxCell = LevelMap.getInstance().getCurrentState().getCellForBox(deliverBoxGoal.getWhat());
		
		// cell of the goal
		Cell goalCell = deliverBoxGoal.getTo();
		
		float distanceAgentBox = LevelMap.getInstance().getDistance(agentCell, boxCell);
		float distanceBoxGoal = LevelMap.getInstance().getDistance(boxCell, goalCell);
		
		Map<Cell, Double> nbc = LevelMap.getInstance().getBetweenessCentrality();
		
		float betweennessBox = nbc.get(boxCell).floatValue();
		float betweennessGoal = nbc.get(goalCell).floatValue();
		
		
		// check if resolving this goal, locks other goals
		Set<Set<Cell>> groupsOfGoals = MapAnalyzer.getInstance().getNeighbourGoals(LevelMap.getInstance().getAllGoals());
		for (Set<Cell> groupOfGoals : groupsOfGoals) {
			if (groupOfGoals.contains(goalCell)) {
				boolean hasLargestBetweenness = true;
				for (Cell cellGroup : groupOfGoals) {
					if (nbc.get(cellGroup) > betweennessGoal)
						hasLargestBetweenness = false;
				}
				if (hasLargestBetweenness)
					a4 = 1;
			}
		}
		
		float distanceOtherBoxes = 0;
		for (Box box : LevelMap.getInstance().getBoxesList()) {
			if (box != deliverBoxGoal.getWhat()) {
				Cell otherBoxCell = LevelMap.getInstance().getCurrentState().getCellForBox(box);
				if (!LevelMap.getInstance().getLockedCells().contains(otherBoxCell))
					distanceOtherBoxes += LevelMap.getInstance().getDistance(otherBoxCell, goalCell);
			}
		}
		
		// check that we are not undoing one solved goal
		if (LevelMap.getInstance().getLockedCells().contains(boxCell)) {
			a5 = 1;
		}
		
		h = a0 * (distanceAgentBox + distanceBoxGoal) + a1 * betweennessBox + a2 * distanceOtherBoxes 
				+ a3 * betweennessGoal + a4 * Heuristic.PENALTY_LOCK_GOAL
				+ a5 * Heuristic.PENALTY_UNDO_GOAL;
		
		logger.info(deliverBoxGoal.toString() + " => " + h);
		
		return h;
	}
	
}