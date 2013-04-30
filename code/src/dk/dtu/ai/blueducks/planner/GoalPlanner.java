package dk.dtu.ai.blueducks.planner;


import java.util.ArrayList;
import java.util.List;

import dk.dtu.ai.blueducks.goals.DeliverBoxGoal;
import dk.dtu.ai.blueducks.goals.GoToBoxGoal;
import dk.dtu.ai.blueducks.goals.Goal;
import dk.dtu.ai.blueducks.heuristics.Heuristic;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.LevelMap;
import dk.dtu.ai.blueducks.map.State;


public class GoalPlanner {

	//private ArrayList<Goal> goalList;
	private LevelMap map;
	private Heuristic<Cell, Cell> heuristic;

	
	
	public GoalPlanner(Heuristic<Cell, Cell> heuristic) {
		super();
		this.map = LevelMap.getInstance();
		this.heuristic = heuristic;

	}
	
		
	/**
	 * Generates a list with all the possible goals
	 * 
	 */
	public List<Goal> generateAgentGoals(String color, List<Goal> topLevelGoals){
		//TODO: 
		List<Goal> agentGoals = new ArrayList<Goal>();
		
		for(Goal g: topLevelGoals){
			DeliverBoxGoal deliverBoxg = (DeliverBoxGoal) g;
			if(deliverBoxg.getWhat().getColor().equals(color))
				agentGoals.add(deliverBoxg);
		}
		
		return agentGoals;
		
	}
	/*
	/**
	 * Get the next Goal, now choose one randomly
	 * 
	 * @return Goal
	 */
	
	public Goal getNextGoal(List<Goal> goalList) {
		
		Goal nextGoal = null;
		
		for(Goal goal : goalList){
			if(nextGoal == null)
				nextGoal = goal;
			else
				nextGoal = bestGoal(goal, nextGoal);
		}
		return nextGoal;
	}
	
	private Goal bestGoal(Goal g1, Goal g2) {
		
		DeliverBoxGoal deliverBoxg1 = (DeliverBoxGoal) g1;
		DeliverBoxGoal deliverBoxg2 = (DeliverBoxGoal) g2;
		
		float heuristicG1 = this.heuristic.getHeuristicValue(map.getCurrentState().getAgentCell(),
				map.getCurrentState().getCellForBox(deliverBoxg1.getWhat()));
		
		float heuristicG2 = this.heuristic.getHeuristicValue(map.getCurrentState().getAgentCell(),
				map.getCurrentState().getCellForBox(deliverBoxg2.getWhat()));
		
		if( heuristicG1 > heuristicG2)
				return g1;
		
		return g2;
	}

}
