package dk.dtu.ai.blueducks.heuristics;

import dk.dtu.ai.blueducks.goals.ClearAgentGoal;
import dk.dtu.ai.blueducks.goals.ClearBoxGoal;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.LevelMap;
import dk.dtu.ai.blueducks.map.State;

public class ClearBoxHeuristic implements Heuristic<State, ClearBoxGoal> {
	
	
	@Override
	public float getHeuristicValue(State state, ClearBoxGoal goal, State prevState) {
		
		float h = 1000;
		float dist = 0;
		for(Cell cell : goal.getCells()) {
			dist += LevelMap.getInstance().getDistance(cell, LevelMap.getInstance().getCurrentState().getCellForBox(goal.getBox()));
		}
		h = h / dist;
		
		return h;
	}
}
