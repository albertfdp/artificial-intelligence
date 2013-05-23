package dk.dtu.ai.blueducks.heuristics;

import dk.dtu.ai.blueducks.actions.PullAction;
import dk.dtu.ai.blueducks.actions.PushAction;
import dk.dtu.ai.blueducks.goals.GoToBoxGoal;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.LevelMap;
import dk.dtu.ai.blueducks.map.State;

public class GoToBoxHeuristic implements Heuristic<State, GoToBoxGoal> {

	@Override
	public float getHeuristicValue(State state, GoToBoxGoal goal, State prevState) {
		float h;
		
		// betweenness in [0, 1]
		float agentCellBetweenness = LevelMap.getInstance().getBetweenessCentrality().get(state.getAgentCell()).floatValue();
		float goalCellBetweenness = LevelMap.getInstance().getBetweenessCentrality().get(goal.getTo()).floatValue();
		
		// distance on a map
		float distance = LevelMap.getInstance().getDistance(state.getAgentCell(), goal.getTo());
		
		
		h = agentCellBetweenness + goalCellBetweenness + distance;
		
		// penalize undoing goals
		if (prevState != null && state.getEdgeFromPrevNode() instanceof PullAction) {
			PullAction pullAction = (PullAction) state.getEdgeFromPrevNode();
			Cell boxCell = prevState.getCellForBox(pullAction.getBox());
			if (LevelMap.getInstance().getLockedCells().contains(boxCell)) {
				h += Heuristic.PENALTY_UNDO_GOAL;
			}
		} else if (prevState != null && state.getEdgeFromPrevNode() instanceof PushAction) {
			PushAction pushAction = (PushAction) state.getEdgeFromPrevNode();
			Cell boxCell = prevState.getCellForBox(pushAction.getBox());
			if (LevelMap.getInstance().getLockedCells().contains(boxCell)) {
				h += Heuristic.PENALTY_UNDO_GOAL;
			}
		}
				
		return h;
		
	}
}
