/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks;

import java.util.List;

import dk.dtu.ai.blueducks.actions.Action;
import dk.dtu.ai.blueducks.goals.GoToBoxGoal;
import dk.dtu.ai.blueducks.goals.Goal;
import dk.dtu.ai.blueducks.goals.MoveBoxGoal;
import dk.dtu.ai.blueducks.heuristics.GoToBoxHeuristic;
import dk.dtu.ai.blueducks.heuristics.ManhattanHeuristic;
import dk.dtu.ai.blueducks.heuristics.MoveBoxHeuristic;
import dk.dtu.ai.blueducks.map.LevelMap;
import dk.dtu.ai.blueducks.map.State;
import dk.dtu.ai.blueducks.planner.AStarSearch;
import dk.dtu.ai.blueducks.planner.GoalPlanner;
import dk.dtu.ai.blueducks.planner.GoalSplitter;

public class Agent {

	private char id;
	private String color;
	private GoalPlanner goalPlanner;
	private GoalSplitter goalSplitter;
	List<State> path;
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
	public Agent(char id, String color) {
		this.id = id;
		this.color = color;
		goalPlanner = new GoalPlanner(LevelMap.getInstance(), new ManhattanHeuristic());
		goalSplitter = new GoalSplitter();
	}
	
	public Action getNextAction() {
		LevelMap map = LevelMap.getInstance();
		State agentState = map.getCurrentState().duplicate();
		if (currentGoal == null)
			triggerReplanning();
		if (currentGoal.isSatisfied(map.getCurrentState())) {
			currentSubgoal++;
			if (currentSubgoal == subgoals.size())
				triggerReplanning();
			if (subgoals.get(currentSubgoal) instanceof GoToBoxGoal) {
				GoToBoxGoal gtbGoal = (GoToBoxGoal) subgoals.get(currentSubgoal);
				path = AStarSearch.<State, GoToBoxGoal> getBestPath(agentState,
						gtbGoal, new GoToBoxHeuristic());
				currentPositionInPath = 0;
				path.remove(0);
			} else if (subgoals.get(currentSubgoal) instanceof MoveBoxGoal) {
				MoveBoxGoal mbGoal = (MoveBoxGoal) subgoals.get(currentSubgoal);
				path = AStarSearch.<State, MoveBoxGoal> getBestPath(agentState,
						mbGoal, new MoveBoxHeuristic());
				currentPositionInPath = 0;
				path.remove(0);
			}
		}
		State currentState = path.get(currentPositionInPath);
		currentPositionInPath++;
		return (Action) currentState.getEdgeFromPrevNode();

	}

	public void triggerReplanning() {
		currentGoal = goalPlanner.getNextGoal();
		subgoals = goalSplitter.getSubgoal(currentGoal, this);
		currentSubgoal = 0;
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
