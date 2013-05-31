package dk.dtu.ai.blueducks.heuristics;

import dk.dtu.ai.blueducks.actions.PullAction;
import dk.dtu.ai.blueducks.actions.PushAction;
import dk.dtu.ai.blueducks.goals.GoToBoxGoal;
import dk.dtu.ai.blueducks.goals.MoveBoxGoal;
import dk.dtu.ai.blueducks.heuristics.GoToBoxHeuristicFactory.GoToBoxBlueDucksHeuristic;
import dk.dtu.ai.blueducks.heuristics.GoToBoxHeuristicFactory.GoToBoxCrazyMonkeysHeuristics;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.HeuristicChooser;
import dk.dtu.ai.blueducks.map.LevelMap;
import dk.dtu.ai.blueducks.map.MapAnalyzer;
import dk.dtu.ai.blueducks.map.State;

public class MoveBoxHeuristicFactory {
	
	public Heuristic<State, MoveBoxGoal> getHeuristic() {
		switch(MapAnalyzer.getMapHashCode()) {
			case (HeuristicChooser.FOSACrazyMonkeys):
				return new HanoiHeuristic();
			case (HeuristicChooser.FOMACrazyMonkeys):
				return new HanoiHeuristic();
			case (HeuristicChooser.FOSABlueDucks):
				return new BlueDucksMoveBoxHeuristic();
			case (HeuristicChooser.FOSAAIGroup01):
				return new FOSAMAStersHeuristic();
			case (HeuristicChooser.FOSAteam42):
				return new BlueDucksMoveBoxHeuristic();
			case (HeuristicChooser.FOSAPurpleJam):
				return new FOSAMAStersHeuristic();
			case (HeuristicChooser.FOMAchenapans):
				return new FOSAMAStersHeuristic();
			case (HeuristicChooser.FOMABlueDucks):
				return new BlueDucksMoveBoxHeuristic();
			case (HeuristicChooser.POMABlueDucks):
				return new BlueDucksMoveBoxHeuristic();
			case (HeuristicChooser.POMAchenapans):
				return new FOSAMAStersHeuristic();
			default:
				return new BlueDucksMoveBoxHeuristic();
		}
		
	}
	
	class FOSACrazyMonkeys implements Heuristic<State, MoveBoxGoal> {

		@Override
		public float getHeuristicValue(State state, MoveBoxGoal goal, State previousState) {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}
	
	class HanoiHeuristic implements Heuristic<State, MoveBoxGoal> {

		@Override
		public float getHeuristicValue(State state, MoveBoxGoal goal, State previousState) {
			float h = 0;
			
			float a0 = 1; // distance
			
			Cell agentCell = state.getAgentCell();
			Cell goalCell = goal.getTo();
			
			float distance = LevelMap.getInstance().getDistance(agentCell, goalCell);
			
			h = a0 * distance;
			
			return h;
		}
		
	}
	
	class FOSAMAStersHeuristic implements Heuristic<State, MoveBoxGoal> {

		@Override
		public float getHeuristicValue(State state, MoveBoxGoal goal, State prevState) {
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
		
	}
	
	class BlueDucksMoveBoxHeuristic implements Heuristic<State, MoveBoxGoal> {

		@Override
		public float getHeuristicValue(State state, MoveBoxGoal goal, State prevState) {
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

}
