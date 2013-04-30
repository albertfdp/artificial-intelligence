package dk.dtu.ai.blueducks.planner;

import java.util.ArrayList;
import java.util.List;

import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.goals.DeliverBoxGoal;
import dk.dtu.ai.blueducks.goals.Goal;
import dk.dtu.ai.blueducks.heuristics.Heuristic;
import dk.dtu.ai.blueducks.heuristics.ManhattanHeuristic;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.LevelMap;

public class GoalPlanner {

	// private ArrayList<Goal> goalList;
	private LevelMap map;
	private Heuristic<Cell, Cell> heuristic;
	private Agent agent;

	public GoalPlanner(Agent agent) {
		super();
		this.map = LevelMap.getInstance();
		this.heuristic = new ManhattanHeuristic();
		this.agent = agent;

	}

	/**
	 * Generates a list with all the possible goals
	 * 
	 */
	public List<Goal> generateAgentGoals(String color, List<Goal> topLevelGoals) {
		// TODO:
		List<Goal> agentGoals = new ArrayList<Goal>();

		for (Goal g : topLevelGoals) {
			DeliverBoxGoal deliverBoxg = (DeliverBoxGoal) g;
			if (deliverBoxg.getWhat().getColor().equals(color))
				agentGoals.add(deliverBoxg);
		}

		return agentGoals;

	}

	/*
	 * /** Get the next Goal, now choose one randomly
	 * 
	 * @return Goal
	 */

	public Goal getNextGoal(String color, List<Goal> topLevelGoals) {

		Goal nextGoal = null;

		for (Goal goal : generateAgentGoals(color, topLevelGoals)) {
			if (nextGoal == null)
				nextGoal = goal;
			else
				nextGoal = bestGoal(goal, nextGoal);
		}
		return nextGoal;
	}

	private Goal bestGoal(Goal g1, Goal g2) {

		DeliverBoxGoal deliverBoxg1 = (DeliverBoxGoal) g1;
		DeliverBoxGoal deliverBoxg2 = (DeliverBoxGoal) g2;

		float heuristicG1 = this.heuristic.getHeuristicValue(map.getCellForAgent(agent), map
				.getCurrentState().getCellForBox(deliverBoxg1.getWhat()));

		float heuristicG2 = this.heuristic.getHeuristicValue(map.getCellForAgent(agent), map
				.getCurrentState().getCellForBox(deliverBoxg2.getWhat()));

		if (heuristicG1 > heuristicG2)
			return g1;

		return g2;
	}

}
