package dk.dtu.ai.blueducks.heuristics;


public interface Heuristic<NodeType, GoalType> {
	
	final static int PENALTY_UNDO_GOAL = 100;

	public float getHeuristicValue(NodeType state, GoalType goal, NodeType previousState);
}
