/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.planner;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.goals.ClearPathGoal;
import dk.dtu.ai.blueducks.goals.DeliverBoxGoal;
import dk.dtu.ai.blueducks.goals.Goal;
import dk.dtu.ai.blueducks.goals.TopLevelClearAgentGoal;
import dk.dtu.ai.blueducks.goals.WaitGoal;
import dk.dtu.ai.blueducks.heuristics.GoalPlannerHeuristic;
import dk.dtu.ai.blueducks.heuristics.GoalPlannerHeuristicFactory;

public class GoalPlanner {

	private Agent agent;

	public GoalPlanner(Agent agent) {
		super();
		this.agent = agent;

	}

	/**
	 * Generates a list with all the possible goals.
	 * 
	 * @param topLevelGoals the top level goals
	 * @return the list
	 */
	public List<Goal> generateAgentGoals(List<Goal> topLevelGoals) {
		List<Goal> agentGoals = new ArrayList<Goal>();

		for (Goal g : topLevelGoals) {
			if (g instanceof DeliverBoxGoal) {
				DeliverBoxGoal deliverBoxg = (DeliverBoxGoal) g;
				if (deliverBoxg.getWhat().getColor().equals(agent.getColor()))
					agentGoals.add(deliverBoxg);
			} else if (g instanceof TopLevelClearAgentGoal) {
				TopLevelClearAgentGoal tlcag = (TopLevelClearAgentGoal) g;
				if (tlcag.getAgentToBeCleared() == agent)
					agentGoals.add(tlcag);
			} else if (g instanceof ClearPathGoal) {
				ClearPathGoal cpg = (ClearPathGoal) g;
				if (cpg.getBox().getColor().equals(agent.getColor()))
					agentGoals.add(cpg);
			} else if (g instanceof WaitGoal) {
				WaitGoal wg = (WaitGoal) g;
				if (wg.getTargetAgent() == this.agent)
					agentGoals.add(wg);
			}

		}

		return agentGoals;
	}

	/**
	 * Compute goal costs.
	 * 
	 * @param topLevelGoals the top level goals
	 * @return the hash map
	 */
	public PriorityQueue<GoalCost> computeGoalCosts(List<Goal> topLevelGoals) {
		List<Goal> possibleGoals = generateAgentGoals(topLevelGoals);
		PriorityQueue<GoalCost> queue = new PriorityQueue<>(possibleGoals.size() + 1);

		for (Goal goal : possibleGoals) {
			float cost = GoalPlannerHeuristicFactory.getHeuristic().getHeuristicValue(agent, goal);
			queue.add(new GoalCost(cost, goal));
		}

		return queue;
	}

	public class GoalCost implements Comparable<GoalCost> {
		public float cost;

		public Goal goal;

		public GoalCost(float cost, Goal goal) {
			super();
			this.cost = cost;
			this.goal = goal;
		}

		@Override
		public int compareTo(GoalCost o) {
			if (this.cost < o.cost)
				return -1;
			else if (this.cost == o.cost)
				return 0;
			else
				return 1;
		}

		@Override
		public String toString() {
			return "GoalCost [" + goal + " = " + cost + "]";
		}
	}
}
