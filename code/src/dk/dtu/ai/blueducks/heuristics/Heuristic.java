package dk.dtu.ai.blueducks.heuristics;

public abstract interface Heuristic<StateType, GoalType> {

	public double getHeuristicValue(StateType state, GoalType goal);
}
