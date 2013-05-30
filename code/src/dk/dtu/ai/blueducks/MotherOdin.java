/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import dk.dtu.ai.blueducks.actions.Action;
import dk.dtu.ai.blueducks.actions.NoOpAction;
import dk.dtu.ai.blueducks.goals.ClearPathGoal;
import dk.dtu.ai.blueducks.goals.DeliverBoxGoal;
import dk.dtu.ai.blueducks.goals.Goal;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.LevelMap;
import dk.dtu.ai.blueducks.map.State;
import dk.dtu.ai.blueducks.merge.PlanAffectedResources;
import dk.dtu.ai.blueducks.merge.PlanAnalyzer;
import dk.dtu.ai.blueducks.merge.PlanAnalyzer.CommonResources;
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
	private HashMap<Agent, PriorityQueue<GoalCost>> goalCostsProposals = new HashMap<>();

	/** The agents' goals. */
	private HashMap<Agent, Goal> agentsAssignedGoals = new HashMap<>();

	private List<PlanAffectedResources> affectedResources;

	private List<LinkedList<State>> unmergedPlans;

	private List<LinkedList<Action>> mergedPlans;

	private List<List<State>> conflictSolvingPlans;

	private boolean needMerging[];
	private boolean pendingMerge;

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
		unmergedPlans = new ArrayList<LinkedList<State>>(agents.size());
		for (int i = 0; i < agents.size(); i++)
			unmergedPlans.add(new LinkedList<State>());
		conflictSolvingPlans = new ArrayList<List<State>>(agents.size());
		for (int i = 0; i < agents.size(); i++)
			conflictSolvingPlans.add(null);
		mergedPlans = new ArrayList<LinkedList<Action>>(agents.size());
		for (int i = 0; i < agents.size(); i++)
			mergedPlans.add(new LinkedList<Action>());
		affectedResources = new ArrayList<PlanAffectedResources>(agents.size());
		for (int i = 0; i < agents.size(); i++)
			affectedResources.add(null);
		this.needMerging = new boolean[agents.size()];
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
				int boxIndex = map.getCurrentState().getCellsForBoxes().indexOf(goalCell);
				Box boxOnGoal = null;
				if (boxIndex != -1) {
					boxOnGoal = boxes.get(boxIndex);
				}
				if (boxOnGoal != null && boxOnGoal.getId() == goalCells.getKey())
					continue;
				// Create goals for each of the boxes that could fulfill this goal
				for (Box b : boxes)
					if (b.getId() == goalCells.getKey()) {
						topLevelGoals.add(new DeliverBoxGoal(b, goalCell));
					}

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
				if (mergedPlans.get(agent).peek() != null) {
					Action nextAction = mergedPlans.get(agent).removeFirst();
					actions.add(nextAction);
					if (unmergedPlans.get(agent).size() > 1
							&& nextAction == unmergedPlans.get(agent).get(1).getEdgeFromPrevNode())
						unmergedPlans.get(agent).removeFirst();
				} else
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
					a.updateBeliefsActionFailed();
				}

			// Notify the agents that something has changed
			if (!jointActionSuccessful) {
				log.info("Triggering agent replanning!");
				generateTopLevelGoals();
				for (int a = 0; a < agents.size(); a++) { // By clearing the plan, a new plan will
															// be requested at the beginning of the
															// next loop
					mergedPlans.get(a).clear();
					agents.get(a).decreaseCurrentSubgoal();
				}
			}
		}
	}

	private void mergePlans() {
		// If there's no need for merging the plans, skip merging
		if (!pendingMerge)
			return;
		// If there is only one agent, skip merging
		if (agents.size() == 1) {
			mergedPlans.set(0, getActionsFromPlan(unmergedPlans.get(0)));
		}

		log.info("Starting plan merging...");

		// Check which agents are using same resources and merge only their plans together
		List<CommonResources> possibleConflicts = PlanAnalyzer
				.findPossibleConflictingAgents(affectedResources);
		if (log.isLoggable(Level.FINEST))
			log.finest("Possible conflicts: " + possibleConflicts);

		// Start the merging of the plans for agents who use common resources
		for (CommonResources c : possibleConflicts) {
			// If it's only one agent, there is no need for merging
			if (c.agents.size() == 1) {
				short agent = c.agents.iterator().next();
				log.fine("Not need for merging for agent " + agent);
				if (needMerging[agent]) {
					mergedPlans.set(agent, getActionsFromPlan(unmergedPlans.get(agent)));
				}
			} else {
				// If we have multiple agents and at least one of them needs merging
				boolean mergeNeeded = false;
				for (short agent : c.agents)
					if (needMerging[agent]) {
						mergeNeeded = true;
						break;
					}
				if (!mergeNeeded) {
					if (log.isLoggable(Level.FINE))
						log.fine("No need for merging for agents: " + c.agents);
					continue;
				}

				if (log.isLoggable(Level.FINE))
					log.fine("Merging plans for agents: " + c.agents);

				// Check if plans are mergeable
				// TODO:

				// Prepare actions
				Action[][] actions = new Action[c.agents.size()][];
				int index = 0;
				for (short agent : c.agents) {
					List<Action> agentPlan = getActionsFromPlan(unmergedPlans.get(agent));
					actions[index++] = agentPlan.toArray(new Action[agentPlan.size()]);
				}

				// Do the merging
				PlanMerger merger = new PlanMerger(c.agents.size(), actions);
				List<LinkedList<Action>> mergePlansResult = merger.run();
				if (mergePlansResult != null) {
					index = 0;
					for (short agent : c.agents) {
						// Trim final NoOps at the end of merge plans
						LinkedList<Action> agentPlan = mergePlansResult.get(index++);
						Iterator<Action> it = agentPlan.descendingIterator();
						while (it.hasNext())
							if (it.next() instanceof NoOpAction)
								it.remove();
							else
								break;
						// Add the merged plan
						this.mergedPlans.set(agent, agentPlan);
					}
				} else {
					log.info("Could not find way to merge plans...");
					if (c.agents.size() == 2) {
						solveConflict(c);
					} else {
						log.severe("FAILED merge of multiple agents...");
						// TODO: Do something or this as well
					}
					// Ask agents to come up with a solution

				}
			}
		}
		Arrays.fill(needMerging, false);
		pendingMerge = false;
	}

	private void solveConflict(CommonResources c) {
		Iterator<Short> it = c.agents.iterator();
		short agent1 = it.next();
		short agent2 = it.next();
		List<State> plan1 = unmergedPlans.get(agent1);
		List<State> plan2 = unmergedPlans.get(agent2);

		// Request conflict solving solution to first agent
		ClearPathGoal cpg1 = new ClearPathGoal(plan2, 0, plan2.size() - 1);
		agents.get(agent1).requestPlanForConflictSolving(cpg1, plan1.get(0), plan2.get(0));

		ClearPathGoal cpg2 = new ClearPathGoal(plan1, 0, plan1.size() - 1);
		agents.get(agent2).requestPlanForConflictSolving(cpg2, plan2.get(0), plan1.get(0));
		// TODO: Wait for synchronization when using multi-threading

		// Now we should have the conflict solving plans
		short fixingAgent, waitingAgent;
		if (conflictSolvingPlans.get(agent1) == null) {
			// If neither of the agents has a plan
			if (conflictSolvingPlans.get(agent2) == null) {
				log.severe("No agent found conflict solutions.");
				return;

			} // If only agent 2 has a plan
			else {
				fixingAgent = agent2;
				waitingAgent = agent1;
			}

		} // If only agent 1 has a plan
		else if (conflictSolvingPlans.get(agent2) == null) {
			fixingAgent = agent1;
			waitingAgent = agent2;

		} // If both agents have a plan
		else if (conflictSolvingPlans.get(agent1).size() <= conflictSolvingPlans.get(agent2).size()) {
			fixingAgent = agent1;
			waitingAgent = agent2;
		} else {
			fixingAgent = agent2;
			waitingAgent = agent1;
		}

		// Add stuff to merged plans
		mergedPlans.get(agent1).clear();
		mergedPlans.get(agent2).clear();
		// Add the fixing plan
		mergedPlans.get(fixingAgent).addAll(getActionsFromPlan(conflictSolvingPlans.get(fixingAgent)));
		mergedPlans.get(waitingAgent).addAll(
				getActionsForEmptyPlan(conflictSolvingPlans.get(fixingAgent).size()));
		// Add the other plan
		mergedPlans.get(waitingAgent).addAll(getActionsFromPlan(unmergedPlans.get(waitingAgent)));
		mergedPlans.get(fixingAgent).addAll(getActionsForEmptyPlan(unmergedPlans.get(waitingAgent).size()));

		// Make the fixing agent replan when needed
		agents.get(fixingAgent).resetCurrentSubgoal();

		if (log.isLoggable(Level.FINE)) {
			log.fine("Merged conflicted plans for fixing agent " + fixingAgent + ": "
					+ mergedPlans.get(fixingAgent));
			log.fine("Merged conflicted plans for waiting agent " + waitingAgent + ": "
					+ mergedPlans.get(waitingAgent));
		}
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
		if (goal instanceof DeliverBoxGoal)
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
	 * @param affectedResources the resources affected by the plan
	 */
	public synchronized void appendPlan(Agent agent, List<State> plan, PlanAffectedResources affectedResources) {
		this.unmergedPlans.get(agent.getId()).clear();
		this.unmergedPlans.get(agent.getId()).addAll(plan);
		this.affectedResources.set(agent.getId(), affectedResources);
		this.needMerging[agent.getId()] = true;
		this.pendingMerge = true;
	}

	/**
	 * Adds the agent's goals proposal.
	 * 
	 * @param agent the agent
	 * @param costs the costs
	 */
	public synchronized void addAgentGoalsProposal(Agent agent, PriorityQueue<GoalCost> costs) {
		goalCostsProposals.put(agent, costs);
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
				GoalCost bestGoal = goalCostsProposals.get(agent).peek();
				// If this agent has no more proposals for goals, just leave it like this
				if (bestGoal == null) {
					agentsAssignedGoals.put(agent, null);
					break;
				}
				// If any other agent has the same goal as the current agent and a better score,
				// try a new goal for the current agent
				for (Agent other : LevelMap.getInstance().getAgentsList())
					if (other != agent) {
						GoalCost gc = goalCostsProposals.get(other).peek();
						if (gc != null && bestGoal.goal == gc.goal && gc.cost < bestGoal.cost) {
							goalCostsProposals.get(agent).remove();
							done = false;
							break;
						}
					}
				// If this is the best goal the agent can do, mark it like this
				if (done)
					agentsAssignedGoals.put(agent, goalCostsProposals.get(agent).peek().goal);
			}
		}
		if (log.isLoggable(Level.FINE))
			log.fine("Assigned goals to agents: " + agentsAssignedGoals);

		for (Entry<Agent, Goal> e : agentsAssignedGoals.entrySet()) {
			// If the goals for any of the agent has been changed, request a new plan from him.
			if (e.getValue() != null && !e.getValue().equals(e.getKey().getCurrentGoal())) {
				log.info("Assigned new goal to " + e.getKey());
				e.getKey().requestPlan();
			}
		}
	}

	public Goal getGoalForAgent(Agent agent) {
		return agentsAssignedGoals.get(agent);
	}

	/**
	 * Append conflict plan.
	 * 
	 * @param agent the agent
	 * @param plan the plan, if one has been found, or null if no plan was identified
	 */
	public synchronized void appendConflictPlan(Agent agent, List<State> plan) {
		conflictSolvingPlans.set(agent.getId(), plan);
	}

	public static LinkedList<Action> getActionsFromPlan(List<State> plan) {
		// Prepare the actions, ignoring the first state in the plan (the current state)
		LinkedList<Action> actions = new LinkedList<Action>();
		Iterator<State> it = plan.iterator();
		it.next();
		while (it.hasNext())
			actions.add((Action) it.next().getEdgeFromPrevNode());
		return actions;
	}

	public static LinkedList<Action> getActionsForEmptyPlan(int planSize) {
		LinkedList<Action> actions = new LinkedList<>();
		for (int i = 0; i < planSize; i++)
			actions.add(new NoOpAction());
		return actions;
	}

}
