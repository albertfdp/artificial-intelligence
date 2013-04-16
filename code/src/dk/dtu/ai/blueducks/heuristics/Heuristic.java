package dk.dtu.ai.blueducks.heuristics;

public interface Heuristic<StateType, GoalType> {

	public double getHeuristicValue(StateType state, GoalType goal);
}
