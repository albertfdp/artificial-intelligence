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
import dk.dtu.ai.blueducks.actions.NoOpAction;
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

/**
 * The Class Agent.
 */
public class Agent {

	/** The id. */
	private char id;

	/** The color. */
	private String color;

	/** The goal planner. */
	private GoalPlanner goalPlanner;

	/** The goal splitter. */
	private GoalSplitter goalSplitter;

	/** The path. */
	List<State> path;

	/** The subgoals. */
	List<Goal> subgoals;

	/** The current goal. */
	Goal currentGoal;

	/** The current subgoal index. */
	int currentSubgoalIndex;

	/** The current position in path. */
	int currentPositionInPath;

	/** The Constant logger. */
	private static final Logger log = Logger.getLogger(LevelMap.class.getSimpleName());

	/**
	 * Instantiates a new agent.
	 * 
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
	 * Gets the id.
	 * 
	 * @return the id
	 * @returns the id (the letter of the agent)
	 */
	public char getId() {
		return id;
	}

	/**
	 * Gets the color.
	 * 
	 * @return the color
	 * @returns the color of the agent
	 */
	public String getColor() {
		return color;
	}

	/**
	 * Gets the current goal.
	 * 
	 * @return the current goal
	 */
	public Goal getCurrentGoal() {
		return currentGoal;
	}

	/**
	 * Gets the next action.
	 * 
	 * @return the next action
	 * @returns the next action the agent will have to perform
	 */
	private Action getNextAction() {

		LevelMap map = LevelMap.getInstance();
		State agentState = new State(map.getCellForAgent(this), null, null, this);
		agentState.setBoxes(map.getCurrentState().getBoxes());

		log.info("Top Level Goal:" + this.currentGoal);
		if (currentGoal == null) {
			buildSubgoals();
			replan(agentState);
		}
		log.info("Current subgoal: " + subgoals.get(currentSubgoalIndex));
		if (subgoals.get(currentSubgoalIndex).isSatisfied(agentState)) {
			log.info("Subgoal is satisfied.");
			currentSubgoalIndex++;
			if (currentSubgoalIndex == subgoals.size()) {
				log.info("All subgoals are satisfied so top level goal is satisfied.");
				triggerGoalPlanning();
			}
			replan(agentState);
		}
		log.info("Path: " + path);
		State currentState = path.get(currentPositionInPath);
		currentPositionInPath++;
		return (Action) currentState.getEdgeFromPrevNode();

	}

	/**
	 * Trigger replanning. This method should be called when something has happened and the agent
	 * can't follow his plan and needs to recompute its goals.
	 */

	private void buildSubgoals() {
		log.info("Triggering replanning");

		currentGoal = MotherOdin.getInstance().getGoalForAgent(this);
		subgoals = goalSplitter.getSubgoal(currentGoal, this);
		currentSubgoalIndex = 0;
	}

	/**
	 * Replan.
	 * 
	 * @param agentState the agent state
	 */
	private void replan(State agentState) {
		if (subgoals.get(currentSubgoalIndex) instanceof GoToBoxGoal) {
			GoToBoxGoal gtbGoal = (GoToBoxGoal) subgoals.get(currentSubgoalIndex);
			path = AStarSearch.<State, GoToBoxGoal> getBestPath(agentState, gtbGoal, new GoToBoxHeuristic());
			currentPositionInPath = 0;
			path.remove(0);
		} else if (subgoals.get(currentSubgoalIndex) instanceof MoveBoxGoal) {
			MoveBoxGoal mbGoal = (MoveBoxGoal) subgoals.get(currentSubgoalIndex);
			path = AStarSearch.<State, MoveBoxGoal> getBestPath(agentState, mbGoal, new MoveBoxHeuristic());
			currentPositionInPath = 0;
			path.remove(0);
		}
	}

	/**
	 * Builds the plan.
	 */
	private void buildPlan() {
		buildSubgoals();
		State agentState = new State(LevelMap.getInstance().getCellForAgent(this), null, null, this);
		replan(agentState);
	}

	/**
	 * Request goals proposals. As a response to this method, the agent should respond with a
	 * proposal using {@link MotherOdin#addAgentGoalsProposal(Agent, java.util.PriorityQueue)}.
	 */
	public void requestGoalsProposals() {
		MotherOdin.getInstance().addAgentGoalsProposal(this,
				goalPlanner.computeGoalCosts(MotherOdin.getInstance().getTopLevelGoals()));
	}

	/**
	 * Request the plan of the agent. As a response to this method, the agent should respond with a
	 * proposal using {@link MotherOdin#appendPlan(Agent, List)}.<br/>
	 * <br/>
	 * If the agent has no plan, he must append a plan with at least one {@link NoOpAction}.
	 */
	public void requestPlan() {

	}
}
