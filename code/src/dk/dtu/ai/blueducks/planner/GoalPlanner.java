package dk.dtu.ai.blueducks.planner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.goals.DeliverBoxGoal;
import dk.dtu.ai.blueducks.goals.Goal;
import dk.dtu.ai.blueducks.heuristics.Heuristic;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.LevelMap;

public class GoalPlanner {

	//private ArrayList<Goal> goalList;
	private LevelMap map;
	private Heuristic<Cell, Cell> heuristic;

	
	
	public GoalPlanner(LevelMap map, Heuristic<Cell, Cell> heuristic) {
		super();
		this.map = map;
		this.heuristic = heuristic;
		generateGoals();
	}
	
		
	/**
	 * Generates a list with all the possible goals
	 * 
	 */
	public List<Goal> generateGoals(){
		//TODO: 
		/*
		goalList = new ArrayList<Goal>();
		for(Box b: map.getCurrentState().getBoxes().values()){
			if(map.getGoals().containsKey(b.getId())){
					goalList.add(new DeliverBoxGoal(b,map.getGoals().get(b)));
			}
		}
		return this.goalList;
		*/
		return null;
		
	}
	/*
	/**
	 * Get the next Goal, now choose one randomly
	 * 
	 * @return Goal
	 */
	
	public Goal getNextGoal() {
		/*int i = 0;
		Random rand = new Random();
		
		i = rand.nextInt(goalList.size());
		return goalList.get(i);*/
		// TODO:
		return null;
	}
}
