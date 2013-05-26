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
import dk.dtu.ai.blueducks.actions.NoOpAction;
import dk.dtu.ai.blueducks.goals.DeliverBoxGoal;
import dk.dtu.ai.blueducks.goals.Goal;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.LevelMap;
import dk.dtu.ai.blueducks.map.MultiAgentState;
import dk.dtu.ai.blueducks.merge.PlanMergeNode;
import dk.dtu.ai.blueducks.merge.PlanMerger;
import dk.dtu.ai.blueducks.planner.GoalPlanner.GoalCost;

/**
 * The Class MotherOdin.
 */
public class MotherOdin {

	/** The Constant log. */
	private static final Logger log = Logger.getLogger(MotherOdin.class.getSimpleName());

	ArrayList<Agent> agents;

	/** The instance. */
	private static MotherOdin mInstance = new MotherOdin();

	/** The map. */
	private LevelMap map = LevelMap.getInstance();

	/** The top level goals. */
	private List<Goal> topLevelGoals = new ArrayList<>();

	/** The goal costs. */
	private HashMap<Agent, PriorityQueue<GoalCost>> goalCosts = new HashMap<>();

	/** The agents' goals. */
	private HashMap<Agent, Goal> agentsGoals = new HashMap<>();

	private List<LinkedList<Action>> unmergedPlans;

	private List<LinkedList<Action>> mergedPlans;

	private boolean needMerging;

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
		agents = LevelMap.getInstance().getAgentsList();
		// Init the plans list
		unmergedPlans = new ArrayList<LinkedList<Action>>();
		for (int i = 0; i < agents.size(); i++)
			unmergedPlans.add(new LinkedList<Action>());
		mergedPlans = new ArrayList<LinkedList<Action>>();
		for (int i = 0; i < agents.size(); i++)
			mergedPlans.add(new LinkedList<Action>());

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
		mergePlans();

		while (true) {
			log.info("Starting loop " + (++currentLoop) + "...");

			// Check if any agent is out of actions
			for (int agent = 0; agent < mergedPlans.size(); agent++)
				if (mergedPlans.get(agent).isEmpty()) {
					agents.get(agent).requestPlan();
				}
			// TODO: Wait for synchronization when using multi-threading
			mergePlans();

			// Build the joint action
			List<Action> actions = new LinkedList<>();
			for (int agent = 0; agent < agents.size(); agent++)
				if (mergedPlans.get(agent).peek() != null)
				{
					Action nextAction=mergedPlans.get(agent).remove();
					actions.add(nextAction);
					if(nextAction==unmergedPlans.get(agent).peek())
						unmergedPlans.get(agent).remove(0);
				}
				else
					actions.add(new NoOpAction());

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
				// TODO: Optimize this...
				generateTopLevelGoals();
				assignAgentsGoals();
				for (int a = 0; a < agents.size(); a++)
					// By clearing the plan, a new plan will be requested at the beginning of the
					// next loop
					mergedPlans.get(a).clear();
			}
		}
	}

	private void mergePlans() {
		if(!needMerging)
			return;
		if(agents.size()==1){
			mergedPlans=unmergedPlans;
		}
		
		log.info("Starting plan merging...");
		// Prepare actions
		Action[][] actions = new Action[agents.size()][];
		int index = 0;
		for (List<Action> agentPlan : unmergedPlans)
			actions[index++] = (Action[]) agentPlan.toArray(new Action[agentPlan.size()]);

		// Start the merging
		PlanMerger merger=new PlanMerger(agents.size(), actions);
		merger.run();
		needMerging=false;
	}

	public void setMergedPlan(List<LinkedList<Action>> mergedPlan) {
		this.mergedPlans = mergedPlan;
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
	 */
	public void finishedTopLevelGoal(Agent agent, Goal goal) {
		log.info(agent + " completed goal: " + goal);
		LevelMap.getInstance().lockCell(((DeliverBoxGoal) goal).getTo());
		generateTopLevelGoals();
		if (topLevelGoals.size() > 0) {
			for (Agent a : LevelMap.getInstance().getAgentsList())
				a.requestGoalsProposals();
			// TODO: Wait for synchronization when using multi-threading
			assignAgentsGoals();
		} else
			log.info("No more top level goals.");
	}

	/**
	 * Appends the plan of a given agent.
	 * 
	 * @param agent the agent
	 * @param plan the plan
	 */
	public synchronized void appendPlan(Agent agent, List<Action> plan) {
		unmergedPlans.get(agent.getId()).addAll(plan);
		needMerging = true;
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
						if (bestGoal.goal == gc.goal && gc.cost <= bestGoal.cost) {
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
		if (log.isLoggable(Level.FINE))
			log.fine("Assigned goals to agents: " + agentsGoals);

		for (Entry<Agent, Goal> e : agentsGoals.entrySet()) {
			// If the goals for any of the agent has been changed, request a new plan from him.
			if (!e.getValue().equals(e.getKey().getCurrentGoal())) {
				log.info("Assigned new goal to " + e.getKey());
				e.getKey().requestPlan();
			}
		}
	}

	public Goal getGoalForAgent(Agent agent) {
		return agentsGoals.get(agent);
	}

}
