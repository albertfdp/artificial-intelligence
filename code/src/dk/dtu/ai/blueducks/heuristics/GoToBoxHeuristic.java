package dk.dtu.ai.blueducks.heuristics;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import dk.dtu.ai.blueducks.actions.PullAction;
import dk.dtu.ai.blueducks.actions.PushAction;
import dk.dtu.ai.blueducks.goals.GoToBoxGoal;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.Direction;
import dk.dtu.ai.blueducks.map.LevelMap;
import dk.dtu.ai.blueducks.map.MapAnalyzer;
import dk.dtu.ai.blueducks.map.State;

public class GoToBoxHeuristic implements Heuristic<State, GoToBoxGoal> {
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(LevelMap.class.getSimpleName());
	
	@Override
	public float getHeuristicValue(State state, GoToBoxGoal goal, State prevState) {
		return getFOSAMASters(state, goal, prevState);
	}
	
	/*
	 * PinkPantherHeuristic
	 */
	public float getPinkPantherHeuristic(State state, GoToBoxGoal goal, State prevState) {
		
		float h = 1;
		
		float a0 = 1; // distance
		float a1 = 1; // penalize being sorrounded by boxes
		
		Cell agentCell = state.getAgentCell();
		Cell goalCell = goal.getTo();
		
		float distance = LevelMap.getInstance().getDistance(agentCell, goalCell);
		
		List<Cell> neighbours = agentCell.getCellNeighbours();
		int numNeighbourBoxes = 0;
		for (Cell neighbour : neighbours) {
			if (neighbour != null && state.getOccupiedCells().get(neighbour.uniqueId))
				numNeighbourBoxes++;
		}
		if (numNeighbourBoxes == 4)
				a1 = 1000;
		
		if (prevState != null && state.getEdgeFromPrevNode() instanceof PullAction) {
			PullAction pullAction = (PullAction) state.getEdgeFromPrevNode();
		} else if (prevState != null && state.getEdgeFromPrevNode() instanceof PushAction) {
			PushAction pushAction = (PushAction) state.getEdgeFromPrevNode();
		}
		
		h = a0 * distance + a1;
		
		return h;
	}
	
	public float getFOSAMASters(State state, GoToBoxGoal goal, State prevState) {
		float h = 1;

		float a0 = 100; // distance
		float a1 = 0; // penalize actions other than move
		float a2 = 1000; // penalize leaving boxes in cells with high betweenness
		float a3 = 0; // penalize leaving boxes in goal cells
		float a4 = 0; // penalize blocking goals
		float a5 = 0; // penalize undoing goals
		float a6 = 0; // reward pulling

		Cell cellAgent = state.getAgentCell();
		Cell cellGoal = goal.getTo();

		float distance = LevelMap.getInstance().getDistance(cellAgent, cellGoal);
		float isNotMoveAction = 0;
		float betweennessCellBox = 0;

		List<Cell> allGoals = LevelMap.getInstance().getAllGoals();
		Set<Set<Cell>> groupsOfGoals = MapAnalyzer.getNeighbourGoals();
		Map<Cell, Double> nbc = MapAnalyzer.getNormalizedBetweennessCentrality();

		if (prevState != null && state.getEdgeFromPrevNode() instanceof PullAction) {
			PullAction pullAction = (PullAction) state.getEdgeFromPrevNode();

			Cell cellBox = state.getCellForBox(pullAction.getBox());
			Cell cellBoxPrevious = prevState.getCellForBox(pullAction.getBox());

			Cell cellAgentPrevious = prevState.getAgentCell();
			isNotMoveAction = 100;
			
			betweennessCellBox = nbc.get(cellBox).floatValue();

			if (LevelMap.getInstance().getLockedCells().contains(cellBoxPrevious)) {
				a5 = 1;
			}

			for (Cell otherGoalCell : allGoals) {
				if (otherGoalCell != cellGoal && otherGoalCell == cellBox)
					a3 = 1;
			}

			// check if resolving this goal, locks other goals
			for (Set<Cell> groupOfGoals : groupsOfGoals) {
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
			for (Set<Cell> groupOfGoals : groupsOfGoals) {
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
				+ a4 * Heuristic.PENALTY_LOCK_GOAL + a5 * Heuristic.PENALTY_UNDO_GOAL * 10 + a6;

		return h;
	}
	
	
	public float getFOSAFAVASHeuristic(State state, GoToBoxGoal goal, State prevState) {
		float h = 1;

		float a0 = 100; // distance
		float a1 = 0; // penalize actions other than move
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
		Set<Set<Cell>> groupsOfGoals = MapAnalyzer.getNeighbourGoals();
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
			for (Set<Cell> groupOfGoals : groupsOfGoals) {
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
			for (Set<Cell> groupOfGoals : groupsOfGoals) {
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
	
	public float getBlueDucksHeuristic(State state, GoToBoxGoal goal, State prevState) {
		float h = 1;

		float a0 = 100; // distance
		float a1 = 0; // penalize actions other than move
		float a2 = 1000; // penalize leaving boxes in cells with high betweenness
		float a3 = 0; // penalize leaving boxes in goal cells
		float a4 = 0; // penalize blocking goals
		float a5 = 0; // penalize undoing goals
		float a6 = 0; // penalize being blocked by boxes

		Cell cellAgent = state.getAgentCell();
		Cell cellGoal = goal.getTo();

		float distance = LevelMap.getInstance().getDistance(cellAgent, cellGoal);
		float isNotMoveAction = 0;
		float betweennessCellBox = 0;

		List<Cell> allGoals = LevelMap.getInstance().getAllGoals();
		Set<Set<Cell>> groupsOfGoals = MapAnalyzer.getNeighbourGoals();
		Map<Cell, Double> nbc = MapAnalyzer.getNormalizedBetweennessCentrality();
		
		if (prevState != null && state.getEdgeFromPrevNode() instanceof PullAction) {
			PullAction pullAction = (PullAction) state.getEdgeFromPrevNode();

			Cell cellBox = state.getCellForBox(pullAction.getBox());
			Cell cellBoxPrevious = prevState.getCellForBox(pullAction.getBox());

			Cell cellAgentPrevious = prevState.getAgentCell();
			isNotMoveAction = 100;

			betweennessCellBox = nbc.get(cellBox).floatValue();

			if (LevelMap.getInstance().getLockedCells().contains(cellBoxPrevious)) {
				a5 = 1;
			}

			for (Cell otherGoalCell : allGoals) {
				if (otherGoalCell != cellGoal && otherGoalCell == cellBox)
					a3 = 1;
			}

			// check if resolving this goal, locks other goals
			for (Set<Cell> groupOfGoals : groupsOfGoals) {
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
			for (Set<Cell> groupOfGoals : groupsOfGoals) {
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
		
		List<Cell> neighbours = cellAgent.getCellNeighbours();
		int numNeighbourBoxes = 0;
		for (Cell neighbour : neighbours) {
			if (neighbour != null && state.getOccupiedCells().get(neighbour.uniqueId))
				numNeighbourBoxes++;
		}
		if (numNeighbourBoxes == 4)
				a6 = 1000;

		h = a0 * distance + a1 * isNotMoveAction + a2 * betweennessCellBox + a3 * Heuristic.PENALTY_OTHER_GOALS_CELLS 
				+ a4 * Heuristic.PENALTY_LOCK_GOAL + a5 * Heuristic.PENALTY_UNDO_GOAL + a6;

		return h;
	}
	
	public float getCrazyMonkeysHeuristic(State state, GoToBoxGoal goal, State prevState) {
		float h = 1;
		
		float a0 = 100; // distance
		float a1 = 0; // penalize actions other than move
		float a2 = 1000; // penalize leaving boxes in cells with high betweenness
		float a3 = 0; // penalize leaving boxes in goal cells
		float a4 = 0; // penalize blocking goals
		float a5 = 0; // penalize undoing goals
		float a6 = 0; // penalize pulling in dead ends
		float a7 = 0;
		
		Cell cellAgent = state.getAgentCell();
		Cell cellGoal = goal.getTo();
		
		float distance = LevelMap.getInstance().getDistance(cellAgent, cellGoal);
		float isNotMoveAction = 0;
		float betweennessCellBox = 0;
		
		List<Cell> allGoals = LevelMap.getInstance().getAllGoals();
		Set<Set<Cell>> groupsOfGoals = MapAnalyzer.getNeighbourGoals();
		Map<Cell, Double> nbc = MapAnalyzer.getNormalizedBetweennessCentrality();
		
		if (prevState != null && state.getEdgeFromPrevNode() instanceof PullAction) {
			PullAction pullAction = (PullAction) state.getEdgeFromPrevNode();
			
			Cell cellBox = state.getCellForBox(pullAction.getBox());
			Cell cellBoxPrevious = prevState.getCellForBox(pullAction.getBox());
			
			Cell cellAgentPrevious = prevState.getAgentCell();
			isNotMoveAction = 100;
			
			betweennessCellBox = nbc.get(cellBox).floatValue();
						
			if (LevelMap.getInstance().getLockedCells().contains(cellBoxPrevious)) {
				a5 = 1;
			}
			
			for (Cell otherGoalCell : allGoals) {
				if (otherGoalCell != cellGoal && otherGoalCell == cellBox)
					a3 = 1;
			}
			
			// check if resolving this goal, locks other goals
			for (Set<Cell> groupOfGoals : groupsOfGoals) {
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
			for (Set<Cell> groupOfGoals : groupsOfGoals) {
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
			
			/**************************************************************************/
			Direction dir = cellBox.getDirection(cellBoxPrevious);
			
			if(MapAnalyzer.getNormalizedBetweennessCentrality().get(cellBox.getNeighbour(dir)).floatValue()
					> MapAnalyzer.getNormalizedBetweennessCentrality().get(cellBox).floatValue()){
				a7 = MapAnalyzer.getNormalizedBetweennessCentrality().get(cellBox.getNeighbour(dir)).floatValue();
			}
			
			
				
			/**************************************************************************/
		}
		
		h = a0 * distance + a1 * isNotMoveAction + a2 * betweennessCellBox + a3 * Heuristic.PENALTY_OTHER_GOALS_CELLS 
				+ a4 * Heuristic.PENALTY_LOCK_GOAL + a5 * Heuristic.PENALTY_UNDO_GOAL + a6 + a7 * 750 ;
		return h;
	}
	
	public float getAIGroupHeuristic(State state, GoToBoxGoal goal, State prevState) {
		float h = 1;
		
		float a0 = 100; // distance
		float a1 = 0; // penalize actions other than move
		float a2 = 1000; // penalize leaving boxes in cells with high betweenness
		float a3 = 0; // penalize leaving boxes in goal cells
		float a4 = 0; // penalize blocking goals
		float a5 = 0; // penalize undoing goals
		float a6 = 0; // penalize pulling in dead ends
		float a7 = 0;
		
		Cell cellAgent = state.getAgentCell();
		Cell cellGoal = goal.getTo();
		
		float distance = LevelMap.getInstance().getDistance(cellAgent, cellGoal);
		float isNotMoveAction = 0;
		float betweennessCellBox = 0;
		
		List<Cell> allGoals = LevelMap.getInstance().getAllGoals();
		Set<Set<Cell>> groupsOfGoals = MapAnalyzer.getNeighbourGoals();
		Map<Cell, Double> nbc = MapAnalyzer.getNormalizedBetweennessCentrality();
		
		if (prevState != null && state.getEdgeFromPrevNode() instanceof PullAction) {
			PullAction pullAction = (PullAction) state.getEdgeFromPrevNode();
			
			Cell cellBox = state.getCellForBox(pullAction.getBox());
			Cell cellBoxPrevious = prevState.getCellForBox(pullAction.getBox());
			
			Cell cellAgentPrevious = prevState.getAgentCell();
			isNotMoveAction = 100;
			
			betweennessCellBox = nbc.get(cellBox).floatValue();
						
			if (LevelMap.getInstance().getLockedCells().contains(cellBoxPrevious)) {
				a5 = 10;
			}
			
			/*for (Cell otherGoalCell : allGoals) {
				if (otherGoalCell != cellGoal && otherGoalCell == cellBox)
					a3 = 1;
			}*/
			
			// check if resolving this goal, locks other goals
			/*for (Set<Cell> groupOfGoals : groupsOfGoals) {
				if (groupOfGoals.contains(cellBox)) {
					boolean hasLargestBetweenness = true;
					for (Cell cellGroup : groupOfGoals) {
						if (nbc.get(cellGroup) > betweennessCellBox)
							hasLargestBetweenness = false;
					}
					if (hasLargestBetweenness)
						a4 = 1;
				}
			}*/
			
			
		} else if (prevState != null && state.getEdgeFromPrevNode() instanceof PushAction) {
			PushAction pushAction = (PushAction) state.getEdgeFromPrevNode();
			
			Cell cellBox = state.getCellForBox(pushAction.getBox());
			Cell cellBoxPrevious = prevState.getCellForBox(pushAction.getBox());
			isNotMoveAction = 1;
			
			betweennessCellBox = nbc.get(cellBox).floatValue();
			
			if (LevelMap.getInstance().getLockedCells().contains(cellBoxPrevious)) {
				a5 = 10;
			}
			/*
			for (Cell otherGoalCell : allGoals) {
				if (otherGoalCell != cellGoal && otherGoalCell == cellBox)
					a3 = 1;
			}
			
			// check if resolving this goal, locks other goals
			for (Set<Cell> groupOfGoals : groupsOfGoals) {
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
			
			/**************************************************************************/
			/*Direction dir = cellBox.getDirection(cellBoxPrevious);
			
			if(MapAnalyzer.getNormalizedBetweennessCentrality().get(cellBox.getNeighbour(dir)).floatValue()
					> MapAnalyzer.getNormalizedBetweennessCentrality().get(cellBox).floatValue()){
				a7 = MapAnalyzer.getNormalizedBetweennessCentrality().get(cellBox.getNeighbour(dir)).floatValue();
			}
			
			
				
			/**************************************************************************/
		}
		
		h = a0 * distance + a5 * Heuristic.PENALTY_UNDO_GOAL;// + a1 * isNotMoveAction + a2 * betweennessCellBox + a3 * Heuristic.PENALTY_OTHER_GOALS_CELLS 
				//+ a4 * Heuristic.PENALTY_LOCK_GOAL + a5 * Heuristic.PENALTY_UNDO_GOAL + a6 + a7 * 750 ;
		return h;
	}
	
	

}
