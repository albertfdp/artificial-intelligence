package dk.dtu.ai.blueducks.heuristics;

import dk.dtu.ai.blueducks.goals.Goal;
import dk.dtu.ai.blueducks.planner.AStarNode;

public interface Heuristic<NodeType extends AStarNode, GoalType extends Goal> {

	public float getHeuristicValue(NodeType state, GoalType goal);
}
