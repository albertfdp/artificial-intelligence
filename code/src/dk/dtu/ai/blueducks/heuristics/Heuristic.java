package dk.dtu.ai.blueducks.heuristics;


public interface Heuristic<NodeType, GoalType> {
	
	final static int PENALTY_UNDO_GOAL = 1000;
	final static int PENALTY_LOCK_GOAL = 100;
	final static int PENALTY_OTHER_GOALS_CELLS = 1000;
	
	final static int PENALTY_DELIVER_BOX_GOAL = 200;

	public float getHeuristicValue(NodeType state, GoalType goal, NodeType previousState);
}
