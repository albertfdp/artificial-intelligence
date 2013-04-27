package dk.dtu.ai.blueducks.heuristics;


public interface Heuristic<NodeType, GoalType> {

	public float getHeuristicValue(NodeType state, GoalType goal);
}
