/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import dk.dtu.ai.blueducks.actions.Action;
import dk.dtu.ai.blueducks.actions.NoOpAction;
import dk.dtu.ai.blueducks.goals.ClearAgentGoal;
import dk.dtu.ai.blueducks.goals.ClearBoxGoal;
import dk.dtu.ai.blueducks.goals.ClearPathGoal;
import dk.dtu.ai.blueducks.goals.DeliverBoxGoal;
import dk.dtu.ai.blueducks.goals.GoToBoxGoal;
import dk.dtu.ai.blueducks.goals.Goal;
import dk.dtu.ai.blueducks.goals.MoveBoxGoal;
import dk.dtu.ai.blueducks.goals.WaitGoal;
import dk.dtu.ai.blueducks.heuristics.ClearAgentHeuristic;
import dk.dtu.ai.blueducks.heuristics.ClearBoxHeuristic;
import dk.dtu.ai.blueducks.heuristics.GoToBoxHeuristic;
import dk.dtu.ai.blueducks.heuristics.GoToBoxHeuristicFactory;
import dk.dtu.ai.blueducks.heuristics.MoveBoxHeuristic;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.LevelMap;
import dk.dtu.ai.blueducks.map.State;
import dk.dtu.ai.blueducks.map.State.CellVisibility;
import dk.dtu.ai.blueducks.merge.PlanAffectedResources;
import dk.dtu.ai.blueducks.planner.AStarSearch;
import dk.dtu.ai.blueducks.planner.GoalPlanner;
import dk.dtu.ai.blueducks.planner.GoalPlanner.GoalCost;
import dk.dtu.ai.blueducks.planner.GoalSplitter;

/**
 * The Class Agent.
 */
public class Agent {

	/** The id. */
	private int id;

	public int getId() {
		return id;
	}

	/** The color. */
	private String color;

	/** The goal planner. */
	private GoalPlanner goalPlanner;

	/** The goal splitter. */
	private GoalSplitter goalSplitter;

	/** The subgoals. */
	List<Goal> subgoals;

	/** The current goal. */
	Goal currentGoal;

	/** The current subgoal index. */
	int currentSubgoalIndex;

	/** The current position in path. */
	int currentPositionInPath;

	/** The Constant logger. */
	private final Logger log;

	public static int noOfAgents = 0;

	public int uniqueId;

	public int powerHashValue;

	public Cell forbidenCell;

	/**
	 * Instantiates a new agent.
	 * 
	 * @param id the id
	 * @param color the color
	 */
	public Agent(char id, String color) {
		this.id = id - '0';
		this.color = color;
		this.goalPlanner = new GoalPlanner(this);
		this.goalSplitter = new GoalSplitter();
		this.log = Logger.getLogger("Agent " + id);
		this.uniqueId = Agent.noOfAgents;
		Agent.noOfAgents++;
		this.forbidenCell = null;
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

	public void resetCurrentSubgoal() {
		this.currentSubgoalIndex = 0;
	}

	public void decreaseCurrentSubgoal() {
		this.currentSubgoalIndex--;
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
	 * Compute the plan states and actions for a given goal starting from a given state.
	 * 
	 * @param agentState the agent state
	 */
	private List<State> computePlanStates(Goal goal, State agentState) {
		List<State> path = null;
		if (goal instanceof GoToBoxGoal) {
			GoToBoxGoal gtbGoal = (GoToBoxGoal) goal;
			GoToBoxHeuristicFactory ghf = new GoToBoxHeuristicFactory();
			path = AStarSearch.<State, GoToBoxGoal> getBestPath(agentState, gtbGoal, ghf.getHeuristic());
		} else if (goal instanceof MoveBoxGoal) {
			MoveBoxGoal mbGoal = (MoveBoxGoal) goal;
			path = AStarSearch.<State, MoveBoxGoal> getBestPath(agentState, mbGoal, new MoveBoxHeuristic());
		} else if (goal instanceof ClearAgentGoal) {
			ClearAgentGoal caGoal = (ClearAgentGoal) goal;
			path = AStarSearch.<State, ClearAgentGoal> getBestPath(agentState, caGoal,
					new ClearAgentHeuristic());
		} else if (goal instanceof ClearBoxGoal) {
			ClearBoxGoal cbGoal = (ClearBoxGoal) goal;
			path = AStarSearch.<State, ClearBoxGoal> getBestPath(agentState, cbGoal, new ClearBoxHeuristic());
		} else if (goal instanceof WaitGoal) {
			WaitGoal wGoal = (WaitGoal) goal;
			wGoal.complete();
			path = new LinkedList<State>();
			path.add(agentState);
			for (int i = 0; i < wGoal.getNumberOfTurnsToWait(); i++) {
				agentState = (new NoOpAction()).getNextState(agentState);
				path.add(agentState);
			}
		}
		return path;
	}

	/**
	 * Request the plan of the agent. As a response to this method, the agent should respond with a
	 * proposal using {@link MotherOdin#appendPlan(Agent, List)}.<br/>
	 * <br/>
	 * If the agent has no plan, he must append a plan with at least one {@link NoOpAction}.
	 */
	public void requestPlan() {

		// Build the subgoals
		Goal newGoal = MotherOdin.getInstance().getGoalForAgent(this);
		if (newGoal instanceof ClearPathGoal)
			this.forbidenCell = LevelMap.getInstance().getCellForAgent(
					((ClearPathGoal) newGoal).getRequestingAgent());
		else
			this.forbidenCell = null;
		// If he now doest not have a goal, just exit
		if (newGoal == null) {
			this.currentGoal = null;
			MotherOdin.getInstance().agentHasNoGoal(this);
			// TODO: For multithreading, take care
			return;
		}

		if (this.currentGoal == null || !this.currentGoal.equals(newGoal)) {

			this.currentGoal = newGoal;
			if (log.isLoggable(Level.INFO))
				log.info("Planning for new goal: " + currentGoal);
			this.subgoals = goalSplitter.getSubgoal(currentGoal, this);
			if (log.isLoggable(Level.FINEST))
				log.finest("Subgoals generated: " + subgoals);
			currentSubgoalIndex = 0;
		} else {
			if (log.isLoggable(Level.INFO))
				log.info("Planning for existing goal: " + currentGoal);
		}

		// If there are no more subgoals that need to be satisfied
		if (currentSubgoalIndex >= this.subgoals.size()) {
			log.info("Finished planning for all subgoals.");
			MotherOdin.getInstance().finishedTopLevelGoal(this, this.currentGoal);
			this.currentGoal = null;
			return;
		}

		// Replan
		State agentState = new State(LevelMap.getInstance().getCellForAgent(this), null, null, this, LevelMap
				.getInstance().getCurrentState().getOccupiedCells(), LevelMap.getInstance().getCurrentState()
				.getCellsForBoxes());

		Goal subgoal = subgoals.get(currentSubgoalIndex++);
		if (log.isLoggable(Level.FINER))
			log.finer("\tCurrent subgoal: " + subgoal);
		List<State> plan = computePlanStates(subgoal, agentState);
		boolean needBoxesCleaned = false;
		// TODO: Needs checking...
		if (plan == null) {
			log.finest("No plan found for goal using classic approach. Exploring while ignoring boes of other colors.");
			// Try to see if a path is found ignoring boxes of other colors
			agentState.clearBoxesOfOtherColor();
			plan = computePlanStates(subgoal, agentState);
			if (plan == null) {
				log.info("No possible plan found.");
				currentSubgoalIndex--; // Stick to the same subgoal as before
				List<State> emptyPlan = new LinkedList<State>();
				emptyPlan.add(agentState);
				MotherOdin.getInstance().appendPlan(this, emptyPlan, new PlanAffectedResources());
				return;
			}
			needBoxesCleaned = true;
		}

		// Prepare the affected resources
		PlanAffectedResources affectedResources = new PlanAffectedResources(plan);

		// Logging
		if (log.isLoggable(Level.FINEST)) {
			List<Action> actions = MotherOdin.getActionsFromPlan(plan);
			log.finest("Generated plan actions: " + actions);
		}
		if (log.isLoggable(Level.FINEST))
			log.finest("Affected resources: " + affectedResources);

		if (needBoxesCleaned) {
			List<Box> boxesToClean = getBoxesToClean(affectedResources);
			if (log.isLoggable(Level.FINEST))
				log.finest("Cells that need to be cleaned: " + boxesToClean);
			for (Box b : boxesToClean)
				MotherOdin.getInstance().addRequestedGoal(
						new ClearPathGoal(b, affectedResources.affectedCells, this));
		}

		MotherOdin.getInstance().appendPlan(this, plan, affectedResources);
	}

	private List<Box> getBoxesToClean(PlanAffectedResources affectedResources) {
		List<Box> boxesToClean = new LinkedList<>();
		for (Cell c : affectedResources.affectedCells) {
			if (LevelMap.getInstance().getCurrentState().isFree(c) == CellVisibility.NOT_FREE) {
				int boxIndex = LevelMap.getInstance().getCurrentState().getCellsForBoxes().indexOf(c);
				boxesToClean.add(LevelMap.getInstance().getBoxesList().get(boxIndex));
			}
		}

		return boxesToClean;
	}

	/**
	 * Request goals proposals. As a response to this method, the agent should respond with a
	 * proposal using {@link MotherOdin#addAgentGoalsProposal(Agent, java.util.PriorityQueue)}.
	 */
	public void requestGoalsProposals() {
		log.fine("Request for Goals Proposals received.");
		PriorityQueue<GoalCost> proposals = goalPlanner.computeGoalCosts(MotherOdin.getInstance()
				.getTopLevelGoals());
		if (log.isLoggable(Level.FINEST))
			log.finest("Goals proposals: " + proposals);
		MotherOdin.getInstance().addAgentGoalsProposal(this, proposals);
	}

	@Override
	public String toString() {
		return "Agent" + id;
	}

	public void computePowerHashValue() {
		// TODO Auto-generated method stub
		powerHashValue = (int) Math.pow(Cell.noOfCells, (this.uniqueId + Box.noOfBoxes));
		log.info("Power hash value: " + this.powerHashValue);
	}

	/**
	 * Request the plan of the agent to solve a conflict. As a response to this method, the agent
	 * should respond with a proposal using {@link MotherOdin#appendConflictPlan(Agent, List)}.<br/>
	 * <br/>
	 * If the agent has no plan, he must call the callback method with a null as a plan.
	 */
	public void requestPlanForConflictSolving(ClearPathGoal goal, State agentState, State otheAgentState) {
		this.forbidenCell = otheAgentState.getAgentCell();

		log.finest("Looking for a plan for the clearPathGoal " + goal);

		if (goal.isSatisfied(agentState)) {
			log.finest("Plan found. No action taken");
			List<State> emptyPlan = new LinkedList<State>();
			emptyPlan.add(agentState);
			MotherOdin.getInstance().appendConflictPlan(this, emptyPlan);
			return;
		}

		List<Goal> clearPathSubgoals = goalSplitter.getSubgoal(goal, this);

		if (log.isLoggable(Level.FINEST)) {
			log.finest("CLEAR PATH SUBGOAL: " + clearPathSubgoals.get(0));
		}
		List<State> completePlan = new ArrayList<State>();

		for (int i = 0; i < clearPathSubgoals.size(); i++) {
			List<State> plan = computePlanStates(clearPathSubgoals.get(i), agentState);
			if (plan != null)
				completePlan.addAll(plan);
			log.finest("THE PLAN " + plan);
		}

		// If a plan was not found
		if (completePlan.size() == 0) {
			log.finest("No plan found for goal.");
			MotherOdin.getInstance().appendConflictPlan(this, null);
			return;
		}

		// // Prepare the affected resources
		// PlanAffectedResources affectedResources = new PlanAffectedResources(completePlan);

		// Logging
		if (log.isLoggable(Level.FINEST)) {
			List<Action> actions = MotherOdin.getActionsFromPlan(completePlan);
			log.finest("Generated plan actions: " + actions);
		}
		// if (log.isLoggable(Level.FINEST))
		// log.finest("Affected resources: " + affectedResources);

		MotherOdin.getInstance().appendConflictPlan(this, completePlan);
		this.forbidenCell = null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Agent other = (Agent) obj;
		if (color == null) {
			if (other.color != null)
				return false;
		} else if (!color.equals(other.color))
			return false;
		if (id != other.id)
			return false;
		return true;
	}
	
}
