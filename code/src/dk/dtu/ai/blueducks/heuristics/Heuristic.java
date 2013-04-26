package dk.dtu.ai.blueducks.heuristics;

import dk.dtu.ai.blueducks.goals.Goal;
import dk.dtu.ai.blueducks.planner.AStarNode;

public abstract interface Heuristic<StateType extends AStarNode, GoalType extends Goal> {

	public double getHeuristicValue(StateType state, GoalType goal);
}
