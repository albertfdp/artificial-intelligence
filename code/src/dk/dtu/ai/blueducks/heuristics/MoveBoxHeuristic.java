package dk.dtu.ai.blueducks.heuristics;

import dk.dtu.ai.blueducks.goals.MoveBoxGoal;
import dk.dtu.ai.blueducks.map.LevelMap;
import dk.dtu.ai.blueducks.map.State;

public class MoveBoxHeuristic implements Heuristic<State, MoveBoxGoal> {

//	@Override
//	public float getHeuristicValue(State state, MoveBoxGoal goal, State prevState) {
//		float distance = Math.abs(goal.getTo().x - state.getCellForBox(goal.getWhat()).x)
//				+ Math.abs(goal.getTo().y - state.getCellForBox(goal.getWhat()).y);
//
//		return distance;
//	}
	
	@Override
	public float getHeuristicValue(State state, MoveBoxGoal goal, State prevState) {
		float h;
		int penaltyForUndoingGoals = 100000;
			
		//betweenness in [0,1]
		float betweenness = LevelMap.getInstance().getBetweenessCentrality().get(state.getAgentCell()).floatValue();
		//actual distance on map
		float distance = LevelMap.getInstance().getDijkstraDistance(state.getAgentCell(), goal.getTo());
		//betweenness in [0,1]
		float goalCellBetweenness = LevelMap.getInstance().getBetweenessCentrality().get(goal.getTo()).floatValue();
		
		
		h = betweenness + distance + goalCellBetweenness;
		
		if (prevState != null) {
			if (state.getBoxesInGoalCells().size() < prevState.getBoxesInGoalCells().size()) {
				h += penaltyForUndoingGoals;
			}
		}
		return h;
		
	}

}
