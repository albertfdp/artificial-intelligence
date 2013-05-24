package dk.dtu.ai.blueducks.heuristics;

import java.util.logging.Logger;

import dk.dtu.ai.blueducks.actions.PullAction;
import dk.dtu.ai.blueducks.actions.PushAction;
import dk.dtu.ai.blueducks.goals.GoToBoxGoal;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.LevelMap;
import dk.dtu.ai.blueducks.map.State;

public class GoToBoxHeuristic implements Heuristic<State, GoToBoxGoal> {
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(LevelMap.class.getSimpleName());
	
	@Override
	public float getHeuristicValue(State state, GoToBoxGoal goal, State prevState) {
		float h = 1;
		
		Cell cellAgent = state.getAgentCell();
		Cell cellGoal = goal.getTo();
		
		float distance = LevelMap.getInstance().getDistance(cellAgent, cellGoal);
		
		if (prevState != null && state.getEdgeFromPrevNode() instanceof PullAction) {
			PullAction pullAction = (PullAction) state.getEdgeFromPrevNode();
			Cell cellBox = state.getCellForBox(pullAction.getBox());
			Cell cellBoxPrevious = prevState.getCellForBox(pullAction.getBox());
			
			// don't put the box in a high betweenness cell
			float betweennessCellBox = LevelMap.getInstance().getBetweenessCentrality().get(cellBox).floatValue();
			h += 1000 * betweennessCellBox;
			
			// don't unlock goals
			if (LevelMap.getInstance().getLockedCells().contains(cellBoxPrevious)) {
				h += Heuristic.PENALTY_UNDO_GOAL;
			}
			
		} else if (prevState != null && state.getEdgeFromPrevNode() instanceof PushAction) {
			PushAction pushAction = (PushAction) state.getEdgeFromPrevNode();
			
			Cell cellBox = state.getCellForBox(pushAction.getBox());
			Cell cellBoxPrevious = prevState.getCellForBox(pushAction.getBox());
			
			// don't put the box in a high betweenness cell
			float betweennessCellBox = LevelMap.getInstance().getBetweenessCentrality().get(cellBox).floatValue();
			h += 1000 * betweennessCellBox;
			
			// don't unlock goals
			if (LevelMap.getInstance().getLockedCells().contains(cellBoxPrevious)) {
				h += Heuristic.PENALTY_UNDO_GOAL;
			}
			
		}
		
		h += h * distance;
		
		return h;
	}
	
//	@Override
//	public float getHeuristicValue(State state, GoToBoxGoal goal, State prevState) {
//		float h = 1;
//		
//		float a0 = 1;
//		float a1 = 1;
//		
//		// cell where the agent is
//		Cell cellAgent = state.getAgentCell();
//		
//		// cell of the goal
//		Cell cellGoal = goal.getTo();
//		
//		float betweennessAgent = LevelMap.getInstance().getBetweenessCentrality().get(cellAgent).floatValue();
//		
//		float distance = LevelMap.getInstance().getDistance(cellAgent, cellGoal);		
//		
//		h += a0 * distance + a1 * betweennessAgent;
//		
//		if (prevState != null) {
//			if (state.getEdgeFromPrevNode() instanceof PullAction) {
//				
//				PullAction pullAction = (PullAction) state.getEdgeFromPrevNode();
//				Cell cellBoxPrevious = prevState.getCellForBox(pullAction.getBox());
//				Cell cellBoxNow = state.getCellForBox(pullAction.getBox());
//				
//				if (LevelMap.getInstance().getLockedCells().contains(cellBoxPrevious)) {
//					h += Heuristic.PENALTY_UNDO_GOAL;
//				}
//				
//				float betweennessBoxPrevious = LevelMap.getInstance().getBetweenessCentrality().get(cellBoxPrevious).floatValue();
//				float betweennessBoxNow = LevelMap.getInstance().getBetweenessCentrality().get(cellBoxNow).floatValue();
//				if (betweennessBoxPrevious <= betweennessBoxNow) {
//					h += 1000;
//				} else {
//					h -= 10;
//				}
//				
//			} else if (state.getEdgeFromPrevNode() instanceof PushAction) {
//				PushAction pushAction = (PushAction) state.getEdgeFromPrevNode();
//				Cell cellBoxPrevious = prevState.getCellForBox(pushAction.getBox());
//				Cell cellBoxNow = state.getCellForBox(pushAction.getBox());
//				
//				if (LevelMap.getInstance().getLockedCells().contains(cellBoxPrevious)) {
//					h += Heuristic.PENALTY_UNDO_GOAL;
//				}
//				
//				float betweennessBoxPrevious = LevelMap.getInstance().getBetweenessCentrality().get(cellBoxPrevious).floatValue();
//				float betweennessBoxNow = LevelMap.getInstance().getBetweenessCentrality().get(cellBoxNow).floatValue();
//				if (betweennessBoxPrevious <= betweennessBoxNow) {
//					h += 1000;
//				} else {
//					h -= 10;
//				}
//				
//			}
//		}
//		return h;
//		
//	}
}
