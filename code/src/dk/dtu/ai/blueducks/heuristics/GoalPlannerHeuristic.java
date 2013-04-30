package dk.dtu.ai.blueducks.heuristics;

import dk.dtu.ai.blueducks.map.Cell;

public class GoalPlannerHeuristic implements Heuristic<Cell, Cell> {

	private float distance;

	@Override
	public float getHeuristicValue(Cell state, Cell goal) {
		distance = Math.abs(goal.x - state.x) + Math.abs(goal.y - state.y);
		return distance;
	}

}