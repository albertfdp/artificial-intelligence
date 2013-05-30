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
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.goals.ClearBoxGoal;
import dk.dtu.ai.blueducks.goals.ClearPathGoal;
import dk.dtu.ai.blueducks.goals.DeliverBoxGoal;
import dk.dtu.ai.blueducks.goals.Goal;
import dk.dtu.ai.blueducks.goals.TopLevelClearAgentGoal;
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
		if (goal instanceof DeliverBoxGoal) {
			if (LevelMap.getInstance().getAgentsList().size() > 1) {
				return multiAgentHeuristic(agent, goal);
			}
			return chooseNotBlockingGoal(agent, goal);
		} else if(goal instanceof ClearPathGoal)
			return clearBoxGoalHeuristic(agent, goal);
		else if(goal instanceof TopLevelClearAgentGoal)
			return clearAgentGoalHeuristic(agent,goal);
		return Float.MAX_VALUE;
	}
		
	private static float multiAgentHeuristic(Agent agent, Goal goal) {
		float h = 0;
		
		float a0 = 1; // distance agent box goal
		float a1 = 1; // betweenness box
		//float a2 = 0.01f; // distance other boxes
		float a3 = 200; // betweenness goal cell
		float a4 = 0; // locking goal
		float a5 = 0; // undoing a goal
		boolean moreAgentsColor = false;
		
		if (agent.getCurrentGoal()!=null && agent.getCurrentGoal().equals(goal))
			return Heuristic.PENALTY_DELIVER_BOX_GOAL;
		
		DeliverBoxGoal deliverBoxGoal = (DeliverBoxGoal) goal;
				
		// cell where the agent is located now
		Cell agentCell = LevelMap.getInstance().getCellForAgent(agent);
		
		// cell of the box to be delivered
		Cell boxCell = LevelMap.getInstance().getCurrentState().getCellForBox(deliverBoxGoal.getWhat());
		
		// cell of the goal
		Cell goalCell = deliverBoxGoal.getTo();
		
		float distanceAgentBox = LevelMap.getInstance().getDistance(agentCell, boxCell);
		float distanceBoxGoal = LevelMap.getInstance().getDistance(boxCell, goalCell);
		
		for (Agent otherAgent : LevelMap.getInstance().getAgentsList()) {
			if (agent.getColor().equals(otherAgent.getColor()) && !otherAgent.equals(agent)) {
				moreAgentsColor = true;
				break;
			}
		}
		if (moreAgentsColor) {
			a0 = 20;
		}
		
		Map<Cell, Double> nbc = MapAnalyzer.getNormalizedBetweennessCentrality();
		
		float betweennessBox = nbc.get(boxCell).floatValue();
		float betweennessGoal = nbc.get(goalCell).floatValue();
		
		// check if resolving this goal, we lock other goals
		Set<Set<Cell>> groupsOfGoals = MapAnalyzer.getNeighbourGoals();
		for (Set<Cell> group : groupsOfGoals) {
			if (group.contains(goalCell)) {
				// check if there are other goals with less betweennes unresolved
				for (Cell groupGoal : group) {
					if ((nbc.get(groupGoal).floatValue() < betweennessGoal) 
							&& !LevelMap.getInstance().getLockedCells().contains(groupGoal)){
						a4 = 1;
						break;
					}
				}
			}
		}
		
		// check that we are not undoing one solved goal
		if (LevelMap.getInstance().getLockedCells().contains(boxCell)) {
			a5 = 1;
		}
		
		h = a0 * (distanceAgentBox + distanceBoxGoal) + a1 * betweennessBox 
				+ a3 * betweennessGoal + a4 * Heuristic.PENALTY_LOCK_GOAL
				+ a5 * Heuristic.PENALTY_UNDO_GOAL;
				
		return h + Heuristic.PENALTY_DELIVER_BOX_GOAL;
	}
	
	private static float clearAgentGoalHeuristic(Agent agent, Goal goal) {
		return 0;
	}

	private static float clearBoxGoalHeuristic(Agent agent, Goal goal) {
		float h = 0;
		//h += 
		Cell cellAgent = LevelMap.getInstance().getCellForAgent(agent);
		if(goal instanceof ClearBoxGoal) {
			ClearBoxGoal cbg = (ClearBoxGoal) goal;
			Cell boxCell = LevelMap.getInstance().getCurrentState().getCellForBox(cbg.getBox());
			h += LevelMap.getInstance().getDistance(cellAgent, boxCell);
		}
		//works only if the goal is instance of ClearBoxGoal
		return h;
	}
	
	private static float FOSAMAStersHeuristic(Agent agent, Goal goal) {
		
		float h = 0;
		
		float a0 = 1; // distance agent box goal
		float a1 = 0; // betweenness box
		float a3 = 0; // betweenness goal cell
		float a4 = 0; // locking goal
		float a5 = 0; // undoing a goal
		
		if (agent.getCurrentGoal()!=null && agent.getCurrentGoal().equals(goal))
			return Heuristic.PENALTY_DELIVER_BOX_GOAL;
		
		DeliverBoxGoal deliverBoxGoal = (DeliverBoxGoal) goal;
				
		// cell where the agent is located now
		Cell agentCell = LevelMap.getInstance().getCellForAgent(agent);
		
		// cell of the box to be delivered
		Cell boxCell = LevelMap.getInstance().getCurrentState().getCellForBox(deliverBoxGoal.getWhat());
		
		// cell of the goal
		Cell goalCell = deliverBoxGoal.getTo();
		
		float distanceAgentBox = LevelMap.getInstance().getDistance(agentCell, boxCell);
		float distanceBoxGoal = LevelMap.getInstance().getDistance(boxCell, goalCell);
		
		Map<Cell, Double> nbc = MapAnalyzer.getNormalizedBetweennessCentrality();
		
		float betweennessBox = nbc.get(boxCell).floatValue();
		float betweennessGoal = nbc.get(goalCell).floatValue();
		
		// check if resolving this goal, we lock other goals
		Set<Set<Cell>> groupsOfGoals = MapAnalyzer.getNeighbourGoals();
		for (Set<Cell> group : groupsOfGoals) {
			if (group.contains(goalCell)) {
				// check if there are other goals with less betweennes unresolved
				for (Cell groupGoal : group) {
					if ((nbc.get(groupGoal).floatValue() < betweennessGoal) 
							&& !LevelMap.getInstance().getLockedCells().contains(groupGoal)){
						a4 = 1;
						break;
					}
				}
			}
		}	
		
		if (deliverBoxGoal.getWhat().getId() == 'A') {
			a0 = 1;
		}
		
		// check that we are not undoing one solved goal
		if (LevelMap.getInstance().getLockedCells().contains(boxCell)) {
			a5 = 1;
		}
		
		if(distanceAgentBox == Integer.MAX_VALUE)
			return Integer.MAX_VALUE;
		
		h = a0 * (distanceAgentBox + distanceBoxGoal) + a1 * betweennessBox
				+ a3 * betweennessGoal + a4 * Heuristic.PENALTY_LOCK_GOAL
				+ a5 * Heuristic.PENALTY_UNDO_GOAL;
				
		return h + Heuristic.PENALTY_DELIVER_BOX_GOAL;
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
		
		if (agent.getCurrentGoal()!=null && agent.getCurrentGoal().equals(goal))
			return Heuristic.PENALTY_DELIVER_BOX_GOAL;
		
		DeliverBoxGoal deliverBoxGoal = (DeliverBoxGoal) goal;
				
		// cell where the agent is located now
		Cell agentCell = LevelMap.getInstance().getCellForAgent(agent);
		
		// cell of the box to be delivered
		Cell boxCell = LevelMap.getInstance().getCurrentState().getCellForBox(deliverBoxGoal.getWhat());
		
		// cell of the goal
		Cell goalCell = deliverBoxGoal.getTo();
		
		float distanceAgentBox = LevelMap.getInstance().getDistance(agentCell, boxCell);
		float distanceBoxGoal = LevelMap.getInstance().getDistance(boxCell, goalCell);
		
		Map<Cell, Double> nbc = MapAnalyzer.getNormalizedBetweennessCentrality();
		
		float betweennessBox = nbc.get(boxCell).floatValue();
		float betweennessGoal = nbc.get(goalCell).floatValue();
		
		// check if resolving this goal, we lock other goals
		Set<Set<Cell>> groupsOfGoals = MapAnalyzer.getNeighbourGoals();
		for (Set<Cell> group : groupsOfGoals) {
			if (group.contains(goalCell)) {
				// check if there are other goals with less betweennes unresolved
				for (Cell groupGoal : group) {
					if ((nbc.get(groupGoal).floatValue() < betweennessGoal) 
							&& !LevelMap.getInstance().getLockedCells().contains(groupGoal)){
						a4 = 1;
						break;
					}
				}
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
				
		return h + Heuristic.PENALTY_DELIVER_BOX_GOAL;
	}
	
}
