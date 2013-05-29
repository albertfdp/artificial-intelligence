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
		return getModifiedPreviousHeuristicValue(state, goal, prevState);
	}
	
	public float getNewValue(State state, MoveBoxGoal goal, State prevState) {
		float h = 0;
		
		float a0 = 100; // distance
		float a1 = 0; // penalize unlock goals
		
		Cell cellAgent = state.getAgentCell();
		Cell cellBox = state.getCellForBox(goal.getWhat());
		Cell cellGoal = goal.getTo();
		
		float distance = LevelMap.getInstance().getDistance(cellBox, cellGoal);
		
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
		// do not unlock goals
		
		h += a0 * distance + a1 * Heuristic.PENALTY_UNDO_GOAL;
		
		return h;
	}
	
	public float getModifiedPreviousHeuristicValue(State state, MoveBoxGoal goal, State prevState) {	
		float h = 0;
		
		float a0 = 10;
		float a1 = 1;
		float a2 = 1;
		float a3 = 1;
		
		// cell where the agent is
		Cell cellAgent = state.getAgentCell();
		
		// cell of the box
		Cell cellBox = state.getCellForBox(goal.getWhat());
		
		// cell of the goal
		Cell cellGoal = goal.getTo();
		
		float distance = LevelMap.getInstance().getDistance(cellAgent, cellGoal);
		
		h = a3 * distance;
				
		// penalize undoing goals
		if (prevState != null && state.getEdgeFromPrevNode() instanceof PullAction) {
			PullAction pullAction = (PullAction) state.getEdgeFromPrevNode();
			if (pullAction.getBox() != goal.getWhat()) {
				Cell boxCell = prevState.getCellForBox(pullAction.getBox());
				
				// penalize undoing goals
				if (LevelMap.getInstance().getLockedCells().contains(boxCell)) {
					h += Heuristic.PENALTY_UNDO_GOAL;
				}
				
				// don't put the box in a high betweenness cell
				float betweennessCellBox = MapAnalyzer.getNormalizedBetweennessCentrality().get(boxCell).floatValue();
				h += 1000 * betweennessCellBox;
				
			}
			
		} else if (prevState != null && state.getEdgeFromPrevNode() instanceof PushAction) {
			PushAction pushAction = (PushAction) state.getEdgeFromPrevNode();
			if (pushAction.getBox() != goal.getWhat()) {
				Cell boxCell = prevState.getCellForBox(pushAction.getBox());
				if (LevelMap.getInstance().getLockedCells().contains(boxCell)) {
					h += Heuristic.PENALTY_UNDO_GOAL;
				}
				// don't put the box in a high betweenness cell
				float betweennessCellBox = MapAnalyzer.getNormalizedBetweennessCentrality().get(boxCell).floatValue();
				h += 1000 * betweennessCellBox;
			}
		}
				
		return h;
	}
		
	public float getPreviousHeuristicValue(State state, MoveBoxGoal goal, State prevState) {	
		float h = 0;
		
		float a0 = 10;
		float a1 = 1;
		float a2 = 1;
		float a3 = 1;
		
		// cell where the agent is
		Cell cellAgent = state.getAgentCell();
		
		// cell of the box
		Cell cellBox = state.getCellForBox(goal.getWhat());
		
		// cell of the goal
		Cell cellGoal = goal.getTo();
		
		float betweennessAgent = MapAnalyzer.getNormalizedBetweennessCentrality().get(cellAgent).floatValue();
		float betweennessBox = MapAnalyzer.getNormalizedBetweennessCentrality().get(cellBox).floatValue();
		float betweennessGoal = MapAnalyzer.getNormalizedBetweennessCentrality().get(cellGoal).floatValue();
		
		float distance = LevelMap.getInstance().getDistance(cellAgent, cellGoal);
		
		h = a0 * betweennessAgent + a1 * betweennessBox + a2 * betweennessGoal + a3 * distance;
				
		// penalize undoing goals
		if (prevState != null && state.getEdgeFromPrevNode() instanceof PullAction) {
			PullAction pullAction = (PullAction) state.getEdgeFromPrevNode();
			if (pullAction.getBox() != goal.getWhat()) {
				Cell boxCell = prevState.getCellForBox(pullAction.getBox());
				
				// penalize undoing goals
				if (LevelMap.getInstance().getLockedCells().contains(boxCell)) {
					h += Heuristic.PENALTY_UNDO_GOAL;
				}
				
				// don't put the box in a high betweenness cell
				float betweennessCellBox = MapAnalyzer.getNormalizedBetweennessCentrality().get(boxCell).floatValue();
				h += 1000 * betweennessCellBox;
				
			}
			
		} else if (prevState != null && state.getEdgeFromPrevNode() instanceof PushAction) {
			PushAction pushAction = (PushAction) state.getEdgeFromPrevNode();
			if (pushAction.getBox() != goal.getWhat()) {
				Cell boxCell = prevState.getCellForBox(pushAction.getBox());
				if (LevelMap.getInstance().getLockedCells().contains(boxCell)) {
					h += Heuristic.PENALTY_UNDO_GOAL;
				}
				// don't put the box in a high betweenness cell
				float betweennessCellBox = MapAnalyzer.getNormalizedBetweennessCentrality().get(boxCell).floatValue();
				h += 1000 * betweennessCellBox;
			}
		}
				
		return h;
	}
	
//	@Override
//	public float getHeuristicValue(State state, MoveBoxGoal goal, State prevState) {
//		float h;
//		int penaltyForUndoingGoals = 100000;
//			
//		//betweenness in [0,1]
//		float betweenness = LevelMap.getInstance().getBetweenessCentrality().get(state.getAgentCell()).floatValue();
//		//actual distance on map
//		float distance = LevelMap.getInstance().getDistance(state.getAgentCell(), goal.getTo());
//		//betweenness in [0,1]
//		float goalCellBetweenness = LevelMap.getInstance().getBetweenessCentrality().get(goal.getTo()).floatValue();
//		
//		
//		h = betweenness + distance + goalCellBetweenness;
//		
//		
//		if (prevState != null && state.getEdgeFromPrevNode() instanceof PullAction) {
//			PullAction pullAction = (PullAction) state.getEdgeFromPrevNode();
//			Cell cell = prevState.getCellForBox(pullAction.getBox());
//			//TODO: Why is this different from the one one GoToBox?
//			if (LevelMap.getInstance().getLockedCells().contains(cell)) {
//				h += penaltyForUndoingGoals;
//			}
//		}
		
//		if (prevState != null) {
//			if (state.getBoxesInGoalCells().size() < prevState.getBoxesInGoalCells().size()) {
//				h += penaltyForUndoingGoals;
//			}
//		}
//		return h;
//		
//	}

}
