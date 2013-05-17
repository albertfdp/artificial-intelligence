/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks;

import java.util.List;
import java.util.logging.Logger;

import dk.dtu.ai.blueducks.actions.Action;
import dk.dtu.ai.blueducks.goals.GoToBoxGoal;
import dk.dtu.ai.blueducks.goals.Goal;
import dk.dtu.ai.blueducks.goals.MoveBoxGoal;
import dk.dtu.ai.blueducks.heuristics.GoToBoxHeuristic;
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
	int currentSubgoalIndex;
	int currentPositionInPath;

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(LevelMap.class.getSimpleName());

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
		goalPlanner = new GoalPlanner(this);
		goalSplitter = new GoalSplitter();
	}

	/**
	 * @returns the next action the agent will have to perform
	 */
	public Action getNextAction() {
		LevelMap map = LevelMap.getInstance();
		State agentState = new State(map.getCellForAgent(this), null, null, this);
		agentState.setBoxes(map.getCurrentState().getBoxes());
		logger.info("CURRENT GOAL" + this.currentGoal);
		if (currentGoal == null) {
			triggerReplanning();
			if (subgoals.get(currentSubgoalIndex) instanceof GoToBoxGoal) {
				GoToBoxGoal gtbGoal = (GoToBoxGoal) subgoals.get(currentSubgoalIndex);
				path = AStarSearch.<State, GoToBoxGoal> getBestPath(agentState, gtbGoal,
						new GoToBoxHeuristic());
				currentPositionInPath = 0;
				path.remove(0);
			} else if (subgoals.get(currentSubgoalIndex) instanceof MoveBoxGoal) {
				MoveBoxGoal mbGoal = (MoveBoxGoal) subgoals.get(currentSubgoalIndex);
				path = AStarSearch.<State, MoveBoxGoal> getBestPath(agentState, mbGoal,
						new MoveBoxHeuristic());
				currentPositionInPath = 0;
				path.remove(0);
			}
		}
		logger.info("Current subgoal: " + subgoals.get(currentSubgoalIndex));
		if (subgoals.get(currentSubgoalIndex).isSatisfied(agentState)) {
			logger.info("Subgoal is satisfied.");
			currentSubgoalIndex++;
			if (currentSubgoalIndex == subgoals.size()) {
				logger.info("All subgoals are satisfied so top level goal is satisfied.");
				triggerReplanning();
			}
			if (subgoals.get(currentSubgoalIndex) instanceof GoToBoxGoal) {
				GoToBoxGoal gtbGoal = (GoToBoxGoal) subgoals.get(currentSubgoalIndex);
				path = AStarSearch.<State, GoToBoxGoal> getBestPath(agentState, gtbGoal,
						new GoToBoxHeuristic());
				currentPositionInPath = 0;
				path.remove(0);
			} else if (subgoals.get(currentSubgoalIndex) instanceof MoveBoxGoal) {
				MoveBoxGoal mbGoal = (MoveBoxGoal) subgoals.get(currentSubgoalIndex);
				path = AStarSearch.<State, MoveBoxGoal> getBestPath(agentState, mbGoal,
						new MoveBoxHeuristic());
				currentPositionInPath = 0;
				path.remove(0);
			}
		}
		logger.info("Path: " + path);
		State currentState = path.get(currentPositionInPath);
		currentPositionInPath++;
		return (Action) currentState.getEdgeFromPrevNode();

	}

	/**
	 * Trigger replanning. This method should be called when something has happened and the agent
	 * can't follow his plan and needs to recompute its goals.
	 */
	public void triggerReplanning() {
		logger.info("Triggering replanning");
		// Regenerate top level goals
		MotherOdin.getInstance().generateTopLevelGoals();
		// Pick a new goal
		currentGoal = goalPlanner.computeGoalCosts(MotherOdin.getInstance().getTopLevelGoals()).peek().goal;
		//currentGoal = goalPlanner.getNextGoal(color, MotherOdin.getInstance().getTopLevelGoals());
		subgoals = goalSplitter.getSubgoal(currentGoal, this);
		currentSubgoalIndex = 0;

	}

	/**
	 * @returns the id (the letter of the agent)
	 */
	public char getId() {
		return id;
	}

	/**
	 * @returns the color of the agent
	 */
	public String getColor() {
		return color;
	}

}
