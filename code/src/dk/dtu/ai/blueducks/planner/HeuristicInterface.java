package dk.dtu.ai.blueducks.planner;

public interface HeuristicInterface<E>{

	public double getHeuristicValue(E state);
}
