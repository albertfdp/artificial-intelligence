package dk.dtu.ai.blueducks.planner;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.goals.*;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.LevelMap;

public class GoalSplitter {
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(LevelMap.class.getSimpleName());

	/**
	 * Split the goal into subgoals
	 * 
	 * @param goal initial goal
	 * @param agent agent to achive the goal
	 * @return List<Goal> of subgoals
	 */
	public List<Goal> getSubgoal(Goal goal, Agent agent) {
		List<Goal> subgoals = new ArrayList<Goal>();
		
		if (goal instanceof DeliverBoxGoal) {
			subgoals = splitDeliverBoxGoal((DeliverBoxGoal) goal, agent);
		} else if (goal instanceof ClearPathGoal) {
			subgoals = splitClearPathGoal((ClearPathGoal) goal, agent);
		}
		return subgoals;
	}
	
	private List<Goal> splitClearPathGoal(ClearPathGoal cpg, Agent agent) {
		List<Goal> subgoals = new ArrayList<Goal>();
		
		Cell agentCell = LevelMap.getInstance().getCellForAgent(agent);
		
		// move to all the boxes that are in the path, and move them to the closest cleared cell
		List<Cell> cellsToBeClean = cpg.getCells();
		
		// get the box that the agent wants to clean
		Box box = cpg.getBox();
		Cell boxCell = LevelMap.getInstance().getCurrentState().getCellForBox(box);
		
		// find the closest free cell to the box which is not in the cellsToBeClean
		// FIXME: how to
		Cell goalBoxCell;
		Cell goalAgentCell;
		
		if (!agentCell.getNeighbours().contains(boxCell))
			subgoals.add((Goal) new GoToBoxGoal(agentCell, boxCell));
		
		// MoveBoxToGoal
		
		// FIXME: MoveToGoal ????
		
		return subgoals;
	}
	
	private List<Goal> splitDeliverBoxGoal(DeliverBoxGoal dbg, Agent agent) {
		List<Goal> subgoals = new ArrayList<Goal>();
		
		Cell goalCell = dbg.getTo();
		Cell agentCell = LevelMap.getInstance().getCellForAgent(agent);
		Box box = dbg.getWhat();
		Cell boxCell = LevelMap.getInstance().getCurrentState().getCellForBox(box);
		
		// if the agent and the box are not neighbours, it is a GoToBoxGoal
		if (!agentCell.getNeighbours().contains(boxCell))
			// check if the path to the box is clean, and otherwise, clean it or ask for help
			
			
			subgoals.add((Goal) new GoToBoxGoal(agentCell, boxCell));
		
		// check if the path from the box to the goal is clean, and clean it or ask for help
		
		
		subgoals.add((Goal) new MoveBoxGoal(box, goalCell));
		Logger.getLogger(GoalSplitter.class.getSimpleName()).info("Split " + dbg + " in: " + subgoals);

		return subgoals;
	}
}
