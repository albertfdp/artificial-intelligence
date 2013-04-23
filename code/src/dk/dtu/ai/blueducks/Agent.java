/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dk.dtu.ai.blueducks.actions.Action;
import dk.dtu.ai.blueducks.goals.GoToBoxGoal;
import dk.dtu.ai.blueducks.goals.Goal;
import dk.dtu.ai.blueducks.goals.MoveBoxGoal;
import dk.dtu.ai.blueducks.heuristics.ManhattanHeuristic;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.CellContent;
import dk.dtu.ai.blueducks.map.Direction;
import dk.dtu.ai.blueducks.map.LevelMap;
import dk.dtu.ai.blueducks.planner.GoalPlanner;
import dk.dtu.ai.blueducks.planner.GoalSplitter;
import dk.dtu.ai.blueducks.planner.PathPlanner;

public class Agent extends CellContent {

	private char id;
	private String color;
	private GoalPlanner goalPlanner;
	private GoalSplitter goalSplitter;
	private PathPlanner pathPlanner;
	List<Cell> path;
	List<Goal> subgoals;
	Goal currentGoal;
	int currentSubgoal;
	int currentPositionInPath;
	/**
	 * Instantiates a new agent.
	 * 
	 * @param initialCell the initial cell
	 * @param id the id
	 * @param color the color
	 */
	public Agent(Cell initialCell, char id, String color) {
		super(initialCell);
		this.id = id;
		this.color = color;
		goalPlanner = new GoalPlanner(LevelMap.getInstance(), new ManhattanHeuristic());
		goalSplitter = new GoalSplitter();
		pathPlanner = new PathPlanner(LevelMap.getInstance(), new ManhattanHeuristic());
	}

	/**
	 * Move.
	 * 
	 * @param direction the direction
	 * @return the string
	 */
	public String move(Direction direction) {
		// return MoveAction(this, direction).toCommandString();
		return null;
	}

	/* ??????????????????? */
	public String nextAction() {
		return move(Direction.N);
	}

	public ArrayList<Cell> computeDesires() {
		//return LevelMap.getInstance().getGoals();
		return null;
	}

	public HashMap<Cell, Integer> computeScore() {
		HashMap<Cell, Integer> goalsScore = new HashMap<Cell, Integer>();
		ArrayList<Cell> goals = computeDesires();
		for (Cell cell : goals) {
			goalsScore.put(cell, computeScore(cell));
		}

		return goalsScore;
	}

	private Integer computeScore(Cell cell) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public Action getNextAction() {
		if (currentGoal == null) triggerReplanning();
		if (isSubgoalFinished()) {
			currentSubgoal++;
			if (currentSubgoal == subgoals.size()) triggerReplanning();
			if (subgoals.get(currentSubgoal) instanceof GoToBoxGoal) {
				GoToBoxGoal gtbGoal = (GoToBoxGoal) subgoals.get(currentSubgoal);
				path = pathPlanner.getBestPath(gtbGoal.getFrom(), gtbGoal.getTo().getCell());
				currentPositionInPath = 0;
			}
			else if (subgoals.get(currentSubgoal) instanceof MoveBoxGoal) {
				MoveBoxGoal mbGoal = (MoveBoxGoal) subgoals.get(currentSubgoal);
				path = pathPlanner.getBestPath(mbGoal.getWhat().getCell(), mbGoal.getTo());
				currentPositionInPath = 0;
			}
		}
		
		Cell currentCell = path.get(currentPositionInPath);
		currentPositionInPath++;
		Cell nextCell = path.get(currentPositionInPath);
		return subgoals.get(currentSubgoal).getAction(currentCell, nextCell, this);
		
	}
	/*
	public Action getNextAction() {
		if (currentGoal == null) triggerReplanning();
		if (isSubgoalFinished()) {
			currentSubgoal++;
			if (currentSubgoal == subgoals.size()) triggerReplanning();
			if (subgoals.get(currentSubgoal) instanceof GoToBoxGoal) {
				GoToBoxGoal gtbGoal = (GoToBoxGoal) subgoals.get(currentSubgoal);
				path = pathPlanner.getBestPath(gtbGoal.getFrom(), gtbGoal.getTo().getCell());
				currentPositionInPath = 0;
			}
			
			else if (subgoals.get(currentSubgoal) instanceof MoveBoxGoal) {
				MoveBoxGoal mbGoal = (MoveBoxGoal) subgoals.get(currentSubgoal);
				path = pathPlanner.getBestPath(mbGoal.getWhat().getCell(), mbGoal.getTo());
				currentPositionInPath = 0;
			}
		}
		
		Cell currentCell = path.get(currentPositionInPath);
		currentPositionInPath++;
		Cell nextCell = path.get(currentPositionInPath);
		if (subgoals.get(currentSubgoal) instanceof GoToBoxGoal) {
			return new MoveAction(currentCell.getDirection(nextCell), this);
		}
		if (subgoals.get(currentSubgoal) instanceof MoveBoxGoal) {
			// pull
			//if (nextCell == this.getCell()) {
				//FIXME FIXME FIXME how do I know where the agent moves 
				// if the pathplanner gives me the next position of the box?
				//return new PullAction(dirAgent, dirBox, agent, box)
				
			//}
			//else {
			MoveBoxGoal mbg = (MoveBoxGoal) subgoals.get(currentSubgoal);
			return new PushAction(this.getCell().getDirection(currentCell), 
									currentCell.getDirection(nextCell), this, mbg.getWhat());
			
			// }
		}
		
		return null;
	}*/
	
	public void triggerReplanning() {
		currentGoal = goalPlanner.getNextGoal();
		subgoals = goalSplitter.getSubgoal(currentGoal, this);
		currentSubgoal = 0;
	}
	
	private boolean isSubgoalFinished(){
		if (subgoals.get(currentSubgoal) instanceof GoToBoxGoal) {
			GoToBoxGoal gtbGoal = (GoToBoxGoal) subgoals.get(currentSubgoal);
			if (gtbGoal.getTo().getCell().getNeighbours().contains(this.getCell())) 
				return true;
		}
		else if (subgoals.get(currentSubgoal) instanceof MoveBoxGoal) {
			MoveBoxGoal mbGoal = (MoveBoxGoal) subgoals.get(currentSubgoal);
			if (mbGoal.getWhat().getCell() == mbGoal.getTo()) 
				return true;
		}
		return false;
	}

	public char getId() {
		return id;
	}

	public void setId(char id) {
		this.id = id;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

}
