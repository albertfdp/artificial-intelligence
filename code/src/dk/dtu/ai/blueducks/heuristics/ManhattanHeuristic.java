package dk.dtu.ai.blueducks.heuristics;

import dk.dtu.ai.blueducks.map.Cell;

public class ManhattanHeuristic implements Heuristic<Cell, Cell> {

	private float distance;

	@Override
	public float getHeuristicValue(Cell state, Cell goal, Cell previousState) {
		distance = Math.abs(goal.x - state.x) + Math.abs(goal.y - state.y);
		return distance;
	}

}