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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import dk.dtu.ai.blueducks.actions.Action;
import dk.dtu.ai.blueducks.goals.DeliverBoxGoal;
import dk.dtu.ai.blueducks.goals.Goal;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.LevelMap;
import dk.dtu.ai.blueducks.planner.GoalPlanner.GoalCost;

/**
 * The Class MotherOdin.
 */
public class MotherOdin {

	/** The Constant log. */
	private static final Logger log = Logger.getLogger(MotherOdin.class.getSimpleName());

	/** The instance. */
	private static MotherOdin mInstance = new MotherOdin();

	/** The map. */
	private LevelMap map = LevelMap.getInstance();

	/** The top level goals. */
	private List<Goal> topLevelGoals = new ArrayList<>();

	/** The goal costs. */
	private HashMap<Agent, PriorityQueue<GoalCost>> goalCosts;

	/** The agents' goals. */
	private HashMap<Agent, Goal> agentsGoals = new HashMap<>();

	private HashMap<Agent, LinkedList<Action>> plans = new HashMap<>();

	/**
	 * Gets the single instance of MotherOdin.
	 * 
	 * @return single instance of MotherOdin
	 */
	public static MotherOdin getInstance() {
		return mInstance;
	}

	public MotherOdin() {
		super();
		for (Agent a : LevelMap.getInstance().getAgentsList())
			plans.put(a, new LinkedList<Action>());
	}

	/**
	 * Generate top level goals.
	 */
	public void generateTopLevelGoals() {
		topLevelGoals.clear();
		List<Box> boxes = map.getBoxesList();
		for (Map.Entry<Character, List<Cell>> goalCells : map.getGoals().entrySet()) {
			for (Cell goalCell : goalCells.getValue()) {
				// If there's a box on the goal and it has the same color, do not add this goal
				// anymore
				Box boxOnGoal = map.getCurrentState().getBoxes().get(goalCell);
				if (boxOnGoal != null && boxOnGoal.getId() == goalCells.getKey())
					continue;
				// Create goals for each of the boxes that could fulfill this goal
				for (Box b : boxes)
					if (b.getId() == goalCells.getKey())
						topLevelGoals.add(new DeliverBoxGoal(b, goalCell));
			}
		}

		if (log.isLoggable(Level.FINE))
			log.fine("Generated top level goals: " + topLevelGoals);
	}

	/**
	 * The main Running cycle of the app.
	 */
	public void run() {
		int currentLoop = 0;
		generateTopLevelGoals();
		for (Agent a : LevelMap.getInstance().getAgentsList())
			a.requestGoalsProposals();
		// TODO: Wait for synchronization when using multi-threading
		assignAgentsGoals();
		// TODO: Wait for synchronization when using multi-threading

		while (true) {
			log.info("Starting loop " + (++currentLoop) + "...");

			// Check if any agent is out of actions
			for (Entry<Agent, LinkedList<Action>> entry : plans.entrySet())
				if (entry.getValue().isEmpty()) {
					entry.getKey().requestPlan();
				}
			// TODO: Wait for synchronization when using multi-threading

			// Build the joint action
			List<Action> actions = new LinkedList<>();
			for (Agent a : LevelMap.getInstance().getAgentsList())
				actions.add(plans.get(a).remove());

			// Send the joint actions to the server
			BlueDucksClient.sendJointAction(actions);
			// Read the percepts from the environment
			boolean[] percepts = BlueDucksClient.readPercepts();

			// Update the map
			int i = 0;
			boolean jointActionSuccessful = true;
			for (Action a : actions)
				if (percepts[i++])
					a.updateBeliefs();
				else {
					log.fine("Action not successful: " + a);
					jointActionSuccessful = false;
				}

			// Notify the agents that something has changed
			if (!jointActionSuccessful) {
				log.info("Triggering agent replanning!");
				for (Agent a : LevelMap.getInstance().getAgentsList())
					// By clearing the plan, a new plan will be requested at the beginning of the
					// next loop
					plans.get(a).clear();
			}
		}
	}

	/**
	 * Gets all the current top level goals in the system.
	 * 
	 * @return the top level goals
	 */
	public List<Goal> getTopLevelGoals() {
		return topLevelGoals;
	}

	/**
	 * Called when an agent finishes a top level goal.
	 * 
	 * @param agent the agent
	 * @param goal the goal
	 */
	public void finishedTopLevelGoal(Agent agent, Goal goal) {
		generateTopLevelGoals();
		assignAgentsGoals();
	}

	/**
	 * Appends the plan of a given agent.
	 * 
	 * @param agent the agent
	 * @param plan the plan
	 */
	public void appendPlan(Agent agent, List<Action> plan) {
		plans.get(agent).addAll(plan);
	}

	/**
	 * Adds the agent's goals proposal.
	 *
	 * @param agent the agent
	 * @param costs the costs
	 */
	public synchronized void addAgentGoalsProposal(Agent agent, PriorityQueue<GoalCost> costs) {
		goalCosts.put(agent, costs);
	}

	/**
	 * Assign the goals for each agent.
	 */
	private void assignAgentsGoals() {
		// For each agent
		for (Agent agent : LevelMap.getInstance().getAgentsList()) {
			boolean done = false;
			// Try to find a goal for this agent
			while (!done) {
				done = true;
				GoalCost bestGoal = goalCosts.get(agent).peek();
				// If this agent has no more proposals for goals, just leave it like this
				if (bestGoal == null) {
					agentsGoals.put(agent, null);
					break;
				}
				// If any other agent has the same goal as the current agent and a better score,
				// try a new goal for the current agent
				for (Agent other : LevelMap.getInstance().getAgentsList())
					if (other != agent) {
						GoalCost gc = goalCosts.get(other).peek();
						if (bestGoal.goal == gc.goal && gc.cost < bestGoal.cost) {
							goalCosts.get(agent).remove();
							done = false;
							break;
						}
					}
				// If this is the best goal the agent can do, mark it like this
				if (done)
					agentsGoals.put(agent, goalCosts.get(agent).peek().goal);
			}
		}

		for (Entry<Agent, Goal> e : agentsGoals.entrySet()) {
			// If the goals for any of the agent has been changed, request a new plan from him.
			if (!e.getKey().getCurrentGoal().equals(e.getValue())) {
				e.getKey().requestPlan();
			}
		}
	}

	public Goal getGoalForAgent(Agent agent) {
		return agentsGoals.get(agent);
	}

}
