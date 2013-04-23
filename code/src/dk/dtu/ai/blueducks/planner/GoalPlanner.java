package dk.dtu.ai.blueducks.planner;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.goals.DeliverBoxGoal;
import dk.dtu.ai.blueducks.goals.Goal;
import dk.dtu.ai.blueducks.heuristics.Heuristic;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.LevelMap;

public class GoalPlanner {

	private ArrayList<Goal> goalList;
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
	private void generateGoals(){
		
		this.goalList = new ArrayList<Goal>();
		for(Character c: map.getBoxes().keySet()){
			if(map.getGoals().containsKey(c)){
				for(Box b: map.getBoxes().get(c)){
					goalList.add(new DeliverBoxGoal(b,map.getGoals().get(b)));
				}
			}
		}
		
	}
	
	/**
	 * Get the next Goal, now choose one randomly
	 * 
	 * @return Goal
	 */
	
	public Goal getNextGoal() {
		int i = 0;
		Random rand = new Random();
		
		i = rand.nextInt(goalList.size());
		return goalList.get(i);
	}
	
}
