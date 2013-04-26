package dk.dtu.ai.blueducks.planner;

import java.util.ArrayList;
import java.util.List;

import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.goals.DeliverBoxGoal;
import dk.dtu.ai.blueducks.goals.GoToBoxGoal;
import dk.dtu.ai.blueducks.goals.Goal;
import dk.dtu.ai.blueducks.goals.MoveBoxGoal;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.LevelMap;

public class GoalSplitter {

	/**
	 * Split the goal into subgoals
	 * 
	 * @param goal initial goal
	 * @param agent agent to achive the goal
	 * @return List<Goal> of subgoals
	 */
	public List<Goal> getSubgoal(Goal goal, Agent agent) {
		List<Goal> subgoals = new ArrayList<Goal>();
		Cell goalCell = ((DeliverBoxGoal)goal).getTo();
		Cell agentCell = LevelMap.getInstance().getCellForAgent(agent);
		Box b = ((DeliverBoxGoal)goal).getWhat();
		
		if(!agentCell.getNeighbours().contains(goalCell))
			subgoals.add((Goal) new GoToBoxGoal(agentCell,b));
		
		subgoals.add((Goal) new MoveBoxGoal(b, goalCell));
			
		return subgoals;
	}
	
}
