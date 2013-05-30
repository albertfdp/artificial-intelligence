package dk.dtu.ai.blueducks.heuristics;

import java.util.logging.Logger;

import dk.dtu.ai.blueducks.actions.PullAction;
import dk.dtu.ai.blueducks.actions.PushAction;
import dk.dtu.ai.blueducks.goals.MoveBoxGoal;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.LevelMap;
import dk.dtu.ai.blueducks.map.MapAnalyzer;
import dk.dtu.ai.blueducks.map.State;

public class MoveBoxHeuristic implements Heuristic<State, MoveBoxGoal> {
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(LevelMap.class.getSimpleName());
	
	@Override
	public float getHeuristicValue(State state, MoveBoxGoal goal, State prevState) {
		return getFOSAMASters(state, goal, prevState);
	}
	
	public float getHanoiValue(State state, MoveBoxGoal goal, State prevState) {
		float h = 0;
		
		float a0 = 1; // distance
		float a1 = 1; // penalize leaving boxes with high betweenness
		float a2 = 1; // penalize undoing goals
		float a3 = 1; // do not block goals
		
		Cell agentCell = state.getAgentCell();
		Cell boxCell = state.getCellForBox(goal.getWhat());
		Cell goalCell = goal.getTo();
		
		float distance = LevelMap.getInstance().getDistance(agentCell, goalCell);
		
		if (prevState != null && state.getEdgeFromPrevNode() instanceof PullAction) {
			PullAction pullAction = (PullAction) state.getEdgeFromPrevNode();
		} else if (prevState != null && state.getEdgeFromPrevNode() instanceof PushAction) {
			PushAction pushAction = (PushAction) state.getEdgeFromPrevNode();
		}
		
		h = a0 * distance;
		
		return h;
	}
	
	public float getFOSAFavasHeuristic(State state, MoveBoxGoal goal, State prevState) {
		return getBlueDucksHeuristic(state, goal, prevState);
	}
	
	public float getFOSAMASters(State state, MoveBoxGoal goal, State prevState) {
		float h = 0;

		float a0 = 2; // distance
		float a1 = 0; // penalize undoing goals
		float a2 = 1; // award pulling

		Cell cellAgent = state.getAgentCell();
		Cell cellBox = state.getCellForBox(goal.getWhat());
		Cell cellGoal = goal.getTo();
		
		if (prevState != null && state.getEdgeFromPrevNode() instanceof PullAction) {
			PullAction pullAction = (PullAction) state.getEdgeFromPrevNode();
			Cell cellBoxPrevious = prevState.getCellForBox(pullAction.getBox());

			if (LevelMap.getInstance().getLockedCells().contains(cellBoxPrevious)) {
				a1 = 1;
			}
			Cell cellAgentPrevious = prevState.getAgentCell();
			
		} else if (prevState != null && state.getEdgeFromPrevNode() instanceof PushAction) {
			PushAction pushAction = (PushAction) state.getEdgeFromPrevNode();
			Cell cellBoxPrevious = prevState.getCellForBox(pushAction.getBox());
			
			if (LevelMap.getInstance().getLockedCells().contains(cellBoxPrevious)) {
				a1 = 1;
			}
		}

		float distance = LevelMap.getInstance().getDistance(cellBox, cellGoal);

		h = a0 * distance + a1 * Heuristic.PENALTY_UNDO_GOAL + a2;

		return h;
	}
	
	public float getBlueDucksHeuristic(State state, MoveBoxGoal goal, State prevState) {
		float h = 0;

		float a0 = 1; // distance
		float a1 = 0; // penalize undoing goals

		Cell cellAgent = state.getAgentCell();
		Cell cellBox = state.getCellForBox(goal.getWhat());
		Cell cellGoal = goal.getTo();

		if (prevState != null && state.getEdgeFromPrevNode() instanceof PullAction) {
			PullAction pullAction = (PullAction) state.getEdgeFromPrevNode();
			Cell cellBoxPrevious = prevState.getCellForBox(pullAction.getBox());

			if (LevelMap.getInstance().getLockedCells().contains(cellBoxPrevious)) {
				a1 = 1;
			}

		} else if (prevState != null && state.getEdgeFromPrevNode() instanceof PushAction) {
			PushAction pushAction = (PushAction) state.getEdgeFromPrevNode();
			Cell cellBoxPrevious = prevState.getCellForBox(pushAction.getBox());

			if (LevelMap.getInstance().getLockedCells().contains(cellBoxPrevious)) {
				a1 = 1;
			}
		}

		float distance = LevelMap.getInstance().getDistance(cellBox, cellGoal);

		h = a0 * distance + a1 * Heuristic.PENALTY_UNDO_GOAL;

		return h;
	}
}