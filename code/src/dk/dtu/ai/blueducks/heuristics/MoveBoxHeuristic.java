package dk.dtu.ai.blueducks.heuristics;

import dk.dtu.ai.blueducks.goals.MoveBoxGoal;
import dk.dtu.ai.blueducks.map.State;

public class MoveBoxHeuristic implements Heuristic<State, MoveBoxGoal> {

	@Override
	public float getHeuristicValue(State state, MoveBoxGoal goal, State prevState) {
		float distance = Math.abs(goal.getTo().x - state.getCellForBox(goal.getWhat()).x)
				+ Math.abs(goal.getTo().y - state.getCellForBox(goal.getWhat()).y);

		return distance;
	}

}
