package dk.dtu.ai.blueducks.heuristics;

import java.util.Map;
import java.util.Set;

import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.goals.DeliverBoxGoal;
import dk.dtu.ai.blueducks.goals.Goal;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.HeuristicChooser;
import dk.dtu.ai.blueducks.map.LevelMap;
import dk.dtu.ai.blueducks.map.MapAnalyzer;

public class GoalPlannerHeuristicFactory {
	
	public static GoalPlannerHeuristic getHeuristic() {
		switch(MapAnalyzer.getMapHashCode()) {
			case (HeuristicChooser.FOSACrazyMonkeys):
				return new GoalPlannerHeuristicMultiAgent();
			case (HeuristicChooser.FOSABlueDucks):
				return new GoalPlannerHeuristicChooseNotBlocking();
			default:
				return new GoalPlannerHeuristicFOSAMASters();
		}
		
	}
	
	static class GoalPlannerHeuristicFOSAMASters extends GoalPlannerHeuristic {

		public float getDeliverBoxHeuristicValue(Agent agent, Goal goal) {

			float h = 0;

			float a0 = 1; // distance agent box goal
			float a1 = 0; // betweenness box
			float a3 = 0; // betweenness goal cell
			float a4 = 0; // locking goal
			float a5 = 0; // undoing a goal

			if (agent.getCurrentGoal() != null && agent.getCurrentGoal().equals(goal))
				return Heuristic.PENALTY_DELIVER_BOX_GOAL;

			DeliverBoxGoal deliverBoxGoal = (DeliverBoxGoal) goal;

			// cell where the agent is located now
			Cell agentCell = LevelMap.getInstance().getCellForAgent(agent);

			// cell of the box to be delivered
			Cell boxCell = LevelMap.getInstance().getCurrentState().getCellForBox(deliverBoxGoal.getWhat());

			// cell of the goal
			Cell goalCell = deliverBoxGoal.getTo();

			float distanceAgentBox = LevelMap.getInstance().getDistance(agentCell, boxCell);
			float distanceBoxGoal = LevelMap.getInstance().getDistance(boxCell, goalCell);

			Map<Cell, Double> nbc = MapAnalyzer.getNormalizedBetweennessCentrality();

			float betweennessBox = nbc.get(boxCell).floatValue();
			float betweennessGoal = nbc.get(goalCell).floatValue();

			// check if resolving this goal, we lock other goals
			Set<Set<Cell>> groupsOfGoals = MapAnalyzer.getNeighbourGoals();
			for (Set<Cell> group : groupsOfGoals) {
				if (group.contains(goalCell)) {
					// check if there are other goals with less betweennes unresolved
					for (Cell groupGoal : group) {
						if ((nbc.get(groupGoal).floatValue() < betweennessGoal)
								&& !LevelMap.getInstance().getLockedCells().contains(groupGoal)) {
							a4 = 1;
							break;
						}
					}
				}
			}

			if (deliverBoxGoal.getWhat().getId() == 'A') {
				a0 = 1;
			}

			// check that we are not undoing one solved goal
			if (LevelMap.getInstance().getLockedCells().contains(boxCell)) {
				a5 = 1;
			}

			if (distanceAgentBox == Integer.MAX_VALUE)
				return Integer.MAX_VALUE;

			h = a0 * (distanceAgentBox + distanceBoxGoal) + a1 * betweennessBox + a3 * betweennessGoal + a4
					* Heuristic.PENALTY_LOCK_GOAL + a5 * Heuristic.PENALTY_UNDO_GOAL;

			return h + Heuristic.PENALTY_DELIVER_BOX_GOAL;
		}

	}
	
	static class GoalPlannerHeuristicMultiAgent extends GoalPlannerHeuristic {

		public float getDeliverBoxHeuristicValue(Agent agent, Goal goal) {
			float h = 0;

			float a0 = 1; // distance agent box goal
			float a1 = 1; // betweenness box
			// float a2 = 0.01f; // distance other boxes
			float a3 = 200; // betweenness goal cell
			float a4 = 0; // locking goal
			float a5 = 0; // undoing a goal
			boolean moreAgentsColor = false;

			if (agent.getCurrentGoal() != null && agent.getCurrentGoal().equals(goal))
				return Heuristic.PENALTY_DELIVER_BOX_GOAL;

			DeliverBoxGoal deliverBoxGoal = (DeliverBoxGoal) goal;

			// cell where the agent is located now
			Cell agentCell = LevelMap.getInstance().getCellForAgent(agent);

			// cell of the box to be delivered
			Cell boxCell = LevelMap.getInstance().getCurrentState().getCellForBox(deliverBoxGoal.getWhat());

			// cell of the goal
			Cell goalCell = deliverBoxGoal.getTo();

			float distanceAgentBox = LevelMap.getInstance().getDistance(agentCell, boxCell);
			float distanceBoxGoal = LevelMap.getInstance().getDistance(boxCell, goalCell);

			for (Agent otherAgent : LevelMap.getInstance().getAgentsList()) {
				if (agent.getColor().equals(otherAgent.getColor()) && !otherAgent.equals(agent)) {
					moreAgentsColor = true;
					break;
				}
			}
			if (moreAgentsColor) {
				a0 = 20;
			}

			Map<Cell, Double> nbc = MapAnalyzer.getNormalizedBetweennessCentrality();

			float betweennessBox = nbc.get(boxCell).floatValue();
			float betweennessGoal = nbc.get(goalCell).floatValue();

			// check if resolving this goal, we lock other goals
			Set<Set<Cell>> groupsOfGoals = MapAnalyzer.getNeighbourGoals();
			for (Set<Cell> group : groupsOfGoals) {
				if (group.contains(goalCell)) {
					// check if there are other goals with less betweennes unresolved
					for (Cell groupGoal : group) {
						if ((nbc.get(groupGoal).floatValue() < betweennessGoal)
								&& !LevelMap.getInstance().getLockedCells().contains(groupGoal)) {
							a4 = 1;
							break;
						}
					}
				}
			}

			// check that we are not undoing one solved goal
			if (LevelMap.getInstance().getLockedCells().contains(boxCell)) {
				a5 = 1;
			}

			h = a0 * (distanceAgentBox + distanceBoxGoal) + a1 * betweennessBox + a3 * betweennessGoal + a4
					* Heuristic.PENALTY_LOCK_GOAL + a5 * Heuristic.PENALTY_UNDO_GOAL;

			return h + Heuristic.PENALTY_DELIVER_BOX_GOAL;
		}
	}
	
	static class GoalPlannerHeuristicChooseNotBlocking extends GoalPlannerHeuristic {

		public float getDeliverBoxHeuristicValue(Agent agent, Goal goal) {

			float h = 0;

			float a0 = 1; // distance agent box goal
			float a1 = 1; // betweenness box
			float a2 = 0.01f; // distance other boxes
			float a3 = 200; // betweenness goal cell
			float a4 = 0; // locking goal
			float a5 = 0; // undoing a goal

			if (agent.getCurrentGoal() != null && agent.getCurrentGoal().equals(goal))
				return Heuristic.PENALTY_DELIVER_BOX_GOAL;

			DeliverBoxGoal deliverBoxGoal = (DeliverBoxGoal) goal;

			// cell where the agent is located now
			Cell agentCell = LevelMap.getInstance().getCellForAgent(agent);

			// cell of the box to be delivered
			Cell boxCell = LevelMap.getInstance().getCurrentState().getCellForBox(deliverBoxGoal.getWhat());

			// cell of the goal
			Cell goalCell = deliverBoxGoal.getTo();

			float distanceAgentBox = LevelMap.getInstance().getDistance(agentCell, boxCell);
			float distanceBoxGoal = LevelMap.getInstance().getDistance(boxCell, goalCell);

			Map<Cell, Double> nbc = MapAnalyzer.getNormalizedBetweennessCentrality();

			float betweennessBox = nbc.get(boxCell).floatValue();
			float betweennessGoal = nbc.get(goalCell).floatValue();

			// check if resolving this goal, we lock other goals
			Set<Set<Cell>> groupsOfGoals = MapAnalyzer.getNeighbourGoals();
			for (Set<Cell> group : groupsOfGoals) {
				if (group.contains(goalCell)) {
					// check if there are other goals with less betweennes unresolved
					for (Cell groupGoal : group) {
						if ((nbc.get(groupGoal).floatValue() < betweennessGoal)
								&& !LevelMap.getInstance().getLockedCells().contains(groupGoal)) {
							a4 = 1;
							break;
						}
					}
				}
			}

			float distanceOtherBoxes = 0;
			for (Box box : LevelMap.getInstance().getBoxesList()) {
				if (box != deliverBoxGoal.getWhat()) {
					Cell otherBoxCell = LevelMap.getInstance().getCurrentState().getCellForBox(box);
					if (!LevelMap.getInstance().getLockedCells().contains(otherBoxCell))
						distanceOtherBoxes += LevelMap.getInstance().getDistance(otherBoxCell, goalCell);
				}
			}

			// check that we are not undoing one solved goal
			if (LevelMap.getInstance().getLockedCells().contains(boxCell)) {
				a5 = 1;
			}

			h = a0 * (distanceAgentBox + distanceBoxGoal) + a1 * betweennessBox + a2 * distanceOtherBoxes
					+ a3 * betweennessGoal + a4 * Heuristic.PENALTY_LOCK_GOAL + a5
					* Heuristic.PENALTY_UNDO_GOAL;

			return h + Heuristic.PENALTY_DELIVER_BOX_GOAL;
		}
	}

}
