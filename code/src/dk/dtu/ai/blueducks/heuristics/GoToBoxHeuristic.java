package dk.dtu.ai.blueducks.heuristics;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import dk.dtu.ai.blueducks.actions.PullAction;
import dk.dtu.ai.blueducks.actions.PushAction;
import dk.dtu.ai.blueducks.goals.GoToBoxGoal;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.LevelMap;
import dk.dtu.ai.blueducks.map.MapAnalyzer;
import dk.dtu.ai.blueducks.map.State;

public class GoToBoxHeuristic implements Heuristic<State, GoToBoxGoal> {
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(LevelMap.class.getSimpleName());
	
	@Override
	public float getHeuristicValue(State state, GoToBoxGoal goal, State prevState) {
		return getBestHeuristic(state, goal, prevState);
	}
	
	public float getHanoiHeuristicValue(State state, GoToBoxGoal goal, State prevState) {
		float h = 0;
		
		float a0 = 1; // distance
		float a1 = 2; // penalize actions other than move
		float a2 = 50; // penalize leaving boxes with high betweenness
		float a3 = 0; // penalize undoing goals
		float a4 = 1; // do not block goals
		
		float isMoveAction = 1;
		float betweennessBox = 0;
		
		Cell cellAgent = state.getAgentCell();
		Cell cellGoal = goal.getTo();
		float distance = LevelMap.getInstance().getDistance(cellAgent, cellGoal);
		
		if (prevState != null && state.getEdgeFromPrevNode() instanceof PullAction) {
			PullAction pullAction = (PullAction) state.getEdgeFromPrevNode();
			Cell cellBox = state.getCellForBox(pullAction.getBox());
			Cell cellBoxPrevious = prevState.getCellForBox(pullAction.getBox());
			
			// penalize undoing goals
			if (LevelMap.getInstance().getLockedCells().contains(cellBoxPrevious)) {
				a3 = 1;
			}
			
			if (MapAnalyzer.getNormalizedBetweennessCentrality().get(cellBox).floatValue()
					>= MapAnalyzer.getNormalizedBetweennessCentrality().get(cellBoxPrevious).floatValue()) {
				h += 100;
			} else {
				h -= 10;
			}
			
			// check if by resolving this goal, we block other goals
			Set<List<Cell>> groups = MapAnalyzer.getNeighbourGoals();
			for (List<Cell> group : groups) {
				
			}
			
			betweennessBox = MapAnalyzer.getNormalizedBetweennessCentrality().get(cellBox).floatValue();
			isMoveAction = 0;
			
		} else if (prevState != null && state.getEdgeFromPrevNode() instanceof PushAction) {
			PushAction pushAction = (PushAction) state.getEdgeFromPrevNode();
			Cell cellBox = state.getCellForBox(pushAction.getBox());
			Cell cellBoxPrevious = prevState.getCellForBox(pushAction.getBox());
			
			// penalize undoing goals
			if (LevelMap.getInstance().getLockedCells().contains(cellBoxPrevious)) {
				a3 = 1;
			}
			
			if (MapAnalyzer.getNormalizedBetweennessCentrality().get(cellBox).floatValue()
					>= MapAnalyzer.getNormalizedBetweennessCentrality().get(cellBoxPrevious).floatValue()) {
				h += 100;
			} else {
				h -= 10;
			}
			
			betweennessBox = MapAnalyzer.getNormalizedBetweennessCentrality().get(cellBox).floatValue();
			isMoveAction = 0;
		}
		
		h = a0 * distance + a1 * isMoveAction + a2 * betweennessBox + a3 * Heuristic.PENALTY_UNDO_GOAL;
		
		return h;
	}
	
	public float getBestHeuristic(State state, GoToBoxGoal goal, State prevState) {
		float h = 1;
		
		float a0 = 100; // distance
		float a1 = 1000; // penalize actions other than move
		float a2 = 1000; // penalize leaving boxes in cells with high betweenness
		float a3 = 0; // penalize leaving boxes in goal cells
		float a4 = 0; // penalize blocking goals
		float a5 = 0; // penalize undoing goals
		float a6 = 0; // penalize pulling in dead ends
		
		Cell cellAgent = state.getAgentCell();
		Cell cellGoal = goal.getTo();
		
		float distance = LevelMap.getInstance().getDistance(cellAgent, cellGoal);
		float isNotMoveAction = 0;
		float betweennessCellBox = 0;
		
		List<Cell> allGoals = LevelMap.getInstance().getAllGoals();
		Set<List<Cell>> groupsOfGoals = MapAnalyzer.getNeighbourGoals();
		Map<Cell, Double> nbc = MapAnalyzer.getNormalizedBetweennessCentrality();
		
		if (prevState != null && state.getEdgeFromPrevNode() instanceof PullAction) {
			PullAction pullAction = (PullAction) state.getEdgeFromPrevNode();
			
			Cell cellBox = state.getCellForBox(pullAction.getBox());
			Cell cellBoxPrevious = prevState.getCellForBox(pullAction.getBox());
			
			Cell cellAgentPrevious = prevState.getAgentCell();
			isNotMoveAction = 100;
			
			/*
			Set<List<Cell>> deadEnds = MapAnalyzer.getInstance().getDeadEnds();
			for (List<Cell> deadEnd : deadEnds) {
				
				double distanceAgentEnd = LevelMap.getInstance().getDistance(cellAgent, deadEnd.get(0));
				double distancePreviousAgentEnd = LevelMap.getInstance().getDistance(cellAgentPrevious, deadEnd.get(0));
				
				if (deadEnd.contains(cellAgent) && (distanceAgentEnd < distancePreviousAgentEnd)) {
					a6 += 10000;
				}
				
			}*/
			
			betweennessCellBox = nbc.get(cellBox).floatValue();
						
			if (LevelMap.getInstance().getLockedCells().contains(cellBoxPrevious)) {
				a5 = 1;
			}
			
			for (Cell otherGoalCell : allGoals) {
				if (otherGoalCell != cellGoal && otherGoalCell == cellBox)
					a3 = 1;
			}
			
			// check if resolving this goal, locks other goals
			for (List<Cell> groupOfGoals : groupsOfGoals) {
				if (groupOfGoals.contains(cellBox)) {
					boolean hasLargestBetweenness = true;
					for (Cell cellGroup : groupOfGoals) {
						if (nbc.get(cellGroup) > betweennessCellBox)
							hasLargestBetweenness = false;
					}
					if (hasLargestBetweenness)
						a4 = 1;
				}
			}
			
			
		} else if (prevState != null && state.getEdgeFromPrevNode() instanceof PushAction) {
			PushAction pushAction = (PushAction) state.getEdgeFromPrevNode();
			
			Cell cellBox = state.getCellForBox(pushAction.getBox());
			Cell cellBoxPrevious = prevState.getCellForBox(pushAction.getBox());
			isNotMoveAction = 1;
			
			betweennessCellBox = nbc.get(cellBox).floatValue();
			
			if (LevelMap.getInstance().getLockedCells().contains(cellBoxPrevious)) {
				a5 = 1;
			}
			
			for (Cell otherGoalCell : allGoals) {
				if (otherGoalCell != cellGoal && otherGoalCell == cellBox)
					a3 = 1;
			}
			
			// check if resolving this goal, locks other goals
			for (List<Cell> groupOfGoals : groupsOfGoals) {
				if (groupOfGoals.contains(cellBox)) {
					boolean hasLargestBetweenness = true;
					for (Cell cellGroup : groupOfGoals) {
						if (nbc.get(cellGroup) > betweennessCellBox)
							hasLargestBetweenness = false;
					}
					if (hasLargestBetweenness)
						a4 = 1;
				}
			}
		}
		
		h = a0 * distance + a1 * isNotMoveAction + a2 * betweennessCellBox + a3 * Heuristic.PENALTY_OTHER_GOALS_CELLS 
				+ a4 * Heuristic.PENALTY_LOCK_GOAL + a5 * Heuristic.PENALTY_UNDO_GOAL + a6;
		
		return h;
	}
		
	public float getPreviousHeuristic(State state, GoToBoxGoal goal, State prevState) {
		float h = 1;
		
		Cell cellAgent = state.getAgentCell();
		Cell cellGoal = goal.getTo();
		
		float distance = LevelMap.getInstance().getDistance(cellAgent, cellGoal);
		
		if (prevState != null && state.getEdgeFromPrevNode() instanceof PullAction) {
			PullAction pullAction = (PullAction) state.getEdgeFromPrevNode();
			Cell cellBox = state.getCellForBox(pullAction.getBox());
			Cell cellBoxPrevious = prevState.getCellForBox(pullAction.getBox());
			
			// don't put the box in a high betweenness cell
			float betweennessCellBox = MapAnalyzer.getNormalizedBetweennessCentrality().get(cellBox).floatValue();
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
			float betweennessCellBox = MapAnalyzer.getNormalizedBetweennessCentrality().get(cellBox).floatValue();
			h += 1000 * betweennessCellBox;
			
			// don't unlock goals
			if (LevelMap.getInstance().getLockedCells().contains(cellBoxPrevious)) {
				h += Heuristic.PENALTY_UNDO_GOAL;
			}
			
		}
		
		h += h * distance;
		
		return h;
	}

}
