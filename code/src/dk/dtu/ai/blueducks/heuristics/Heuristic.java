package dk.dtu.ai.blueducks.heuristics;


public interface Heuristic<NodeType, GoalType> {
	
	final static int PENALTY_UNDO_GOAL = 500;
	final static int PENALTY_LOCK_GOAL = 100;
	final static int PENALTY_OTHER_GOALS_CELLS = 500;
	
	final static int PENALTY_DELIVER_BOX_GOAL = 200;

	public float getHeuristicValue(NodeType state, GoalType goal, NodeType previousState);
}
