package dk.dtu.ai.blueducks.heuristics;

import dk.dtu.ai.blueducks.map.Cell;

public class ManhattanHeuristic implements Heuristic<Cell, Cell> {

	private double distance;

	@Override
	public double getHeuristicValue(Cell state, Cell goal) {
		distance = Math.abs(goal.x - state.x) + Math.abs(goal.y - state.y);
		return distance;
	}

}
