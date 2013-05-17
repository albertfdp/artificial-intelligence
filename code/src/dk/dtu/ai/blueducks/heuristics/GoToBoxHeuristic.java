package dk.dtu.ai.blueducks.heuristics;

import dk.dtu.ai.blueducks.actions.PullAction;
import dk.dtu.ai.blueducks.actions.PushAction;
import dk.dtu.ai.blueducks.goals.GoToBoxGoal;
import dk.dtu.ai.blueducks.map.LevelMap;
import dk.dtu.ai.blueducks.map.State;

public class GoToBoxHeuristic implements Heuristic<State, GoToBoxGoal> {

//	@Override
//	public float getHeuristicValue(State state, GoToBoxGoal goal) {
//		float distance = Math.abs(goal.getTo().x - state.getAgentCell().x)
//				+ Math.abs(goal.getTo().y - state.getAgentCell().y);
//		if (state.getEdgeFromPrevNode() instanceof PullAction)
//			distance += 2;
//		if (state.getEdgeFromPrevNode() instanceof PushAction)
//			distance += 1;
//
//		return distance;
//	}
	
	@Override
	public float getHeuristicValue(State state, GoToBoxGoal goal, State prevState) {
		float h;
		int penaltyForUndoingGoals = 100000;
		
		float betweenness = LevelMap.getInstance().getBetweenessCentrality().get(state.getAgentCell()).floatValue();
		float distance = LevelMap.getInstance().getDijkstraDistance(state.getAgentCell(), goal.getTo());
		
		
		h = betweenness + distance;
		
		if (state.getEdgeFromPrevNode() instanceof PullAction || state.getEdgeFromPrevNode() instanceof PushAction) {
			h += 1000; // TODO: to set correctly
		}
				
		if (prevState != null) {
			if (state.getBoxesInGoalCells().size() < prevState.getBoxesInGoalCells().size()) {
				h += penaltyForUndoingGoals;
			}
		}
		return h;
		
	}
}
