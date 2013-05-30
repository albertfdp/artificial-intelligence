package dk.dtu.ai.blueducks.heuristics;

import java.util.Random;
import java.util.logging.Logger;

import dk.dtu.ai.blueducks.goals.ClearAgentGoal;
import dk.dtu.ai.blueducks.map.LevelMap;
import dk.dtu.ai.blueducks.map.State;

public class ClearAgentHeuristic implements Heuristic<State, ClearAgentGoal> {
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(LevelMap.class.getSimpleName());
	
	@Override
	public float getHeuristicValue(State state, ClearAgentGoal goal, State prevState) {
		Random randomGenerator = new Random();
		return randomGenerator.nextFloat();
	}
}
