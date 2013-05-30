package dk.dtu.ai.blueducks.heuristics;

import java.util.Random;

import dk.dtu.ai.blueducks.goals.ClearAgentGoal;

import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.LevelMap;

import dk.dtu.ai.blueducks.map.State;

public class ClearAgentHeuristic implements Heuristic<State, ClearAgentGoal> {
	
	
	@Override
	public float getHeuristicValue(State state, ClearAgentGoal goal, State prevState) {
//		Random randomGenerator = new Random();
//		return randomGenerator.nextFloat();
		float h = 1000;
		float dist = 0;
		for(Cell cell : goal.getCells()) {
			dist += LevelMap.getInstance().getDistance(cell, state.getAgentCell());
		}
		h = h / dist;
		
		return h;
	}
}
