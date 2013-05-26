/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.merge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import dk.dtu.ai.blueducks.MotherOdin;
import dk.dtu.ai.blueducks.actions.Action;
import dk.dtu.ai.blueducks.actions.NoOpAction;
import dk.dtu.ai.blueducks.map.LevelMap;
import dk.dtu.ai.blueducks.map.MultiAgentState;

public class PlanMerger {

	private static final Logger log = Logger.getLogger(PlanMerger.class.getSimpleName());
	private static final int NO_MORE_OPTIONS = -2;

	private final boolean[][] mergeOptions;
	private final int agentsCount;
	private final Action[][] actions;
	
	private List<LinkedList<Action>> mergedPlan;

	public PlanMerger(int agentsCount, Action[][] actionsToMerge) {
		this.mergeOptions = PlanMergeOptions.OPTIONS[agentsCount - 1];
		this.agentsCount = agentsCount;
		this.actions = actionsToMerge;
	}

	/**
	 * Runs the merger.
	 */
	public List<LinkedList<Action>> run() {
		// Prepare multiagent state
		MultiAgentState startState = new MultiAgentState(LevelMap.getInstance().getAgents(), LevelMap
				.getInstance().getCurrentState().getOccupiedCells(),LevelMap
				.getInstance().getCurrentState().getCellsForBoxes());
		// Prepare start indexes
		short[] agentsCurrentActionsIndex = new short[agentsCount];
		boolean result=this.mergePlans(agentsCurrentActionsIndex, new PlanMergeNode(startState, null, null), 0);
		if(result)
		{
			log.info("Plan merging successful.");
			if (log.isLoggable(Level.FINER))
				log.finer("Merged plan: " + mergedPlan);
			return this.mergedPlan;
		}
		else {
			log.info("Plan merging failed.");
			return null;
		}
			
	}

	private int getNextOptionIndex(int currentOptionsPos, Set<Conflict> conflictingAgents,
			boolean[] applicableActions, Action[] agentsActions) {
		if (currentOptionsPos >= mergeOptions.length)
			return NO_MORE_OPTIONS;

		boolean[] activeAgents;
		boolean conflict = true;

		// we consider the next position to start the testing
		currentOptionsPos++;

		// we don't consider possibilities which we already know would be conflicting
		while (conflict == true) {
			if (currentOptionsPos >= mergeOptions.length)
				return NO_MORE_OPTIONS;
			conflict = false;
			activeAgents = mergeOptions[currentOptionsPos];
			// Check if the actions are applicable
			for (int agent = 0; agent < this.agentsCount; agent++)
				if (activeAgents[agent]
						&& (!applicableActions[agent] || agentsActions[agent] instanceof NoOpAction)) {
					currentOptionsPos++;
					conflict = true;
					if (log.isLoggable(Level.FINEST))
						log.finest("Option not valid due to lack of applicability: "
								+ Arrays.toString(activeAgents));
					break;
				}

			if (conflict)
				continue;

			// Check if the conflicts are respected
			for (Conflict c : conflictingAgents)
				if (activeAgents[c.agent1] == true && activeAgents[c.agent2] == true) {
					currentOptionsPos++;
					conflict = true;
					if(log.isLoggable(Level.FINEST))
						log.finest("Option not valid due to conflict: " + Arrays.toString(activeAgents));
					break;
				}
		}
		return currentOptionsPos;
	}

	private boolean[] getApplicableActions(Action[] agentsActions, MultiAgentState state) {
		boolean[] applicable = new boolean[this.agentsCount];
		for (short agent = 0; agent < this.agentsCount; agent++)
			if (agentsActions[agent].isApplicable(state))
				applicable[agent] = true;
		return applicable;
	}

	/**
	 * Merge plans recursively.
	 *
	 * @param agentCurrentActionIndex the agent current action index
	 * @param current - the node we are trying to expand
	 * @param step - the current depth of the plan (e.g. step 3 - we're trying to see what actions
	 * from step 3 can be done)
	 * @return true if is successful in merging the plans or false otherwise
	 */
	// TODO - limit the go back
	// TODO - keep track of the most advanced point where it failed
	private boolean mergePlans(short[] agentCurrentActionIndex, PlanMergeNode current, int step) {
		// getting current action for each agent
		Action[] agentsActions = new Action[this.agentsCount];
		short agentsDone = 0;
		for (int agent = 0; agent < this.agentsCount; agent++)
			if (agentCurrentActionIndex[agent] >= actions[agent].length) {
				agentsActions[agent] = new NoOpAction();
				agentsDone++;
			} else
				agentsActions[agent] = actions[agent][agentCurrentActionIndex[agent]];
		if (log.isLoggable(Level.FINE)) {
			log.fine("Merging plans step " + step + ". Current actions to merge: "
					+ Arrays.toString(agentsActions));
		}

		if (agentsDone == this.agentsCount) {
			List<LinkedList<Action>> mergedPlan = prepareResponse(current);
			this.mergedPlan=mergedPlan;
			return true;
		}

		// we will keep a list of pairs of actions which are conflicting in the state
		Set<Conflict> conflictingAgents = new LinkedHashSet<>();

		// keeps track if there is a conflict found when trying to execute the actions for this step
		boolean conflictFound;

		// we try to get all option of generated activeAgents
		boolean[] activeAgents;
		int currentOptionsIndex = -1;

		// check applicable actions
		boolean[] applicableActions = getApplicableActions(agentsActions, current.getState());
		currentOptionsIndex = getNextOptionIndex(-1, Collections.<Conflict> emptySet(), applicableActions,
				agentsActions);

		while (currentOptionsIndex != NO_MORE_OPTIONS) {
			activeAgents = mergeOptions[currentOptionsIndex];

			if (log.isLoggable(Level.FINEST))
				log.finest("Checking options: " + Arrays.toString(activeAgents));

			// at the beginning no conflict exists
			conflictFound = false;

			// we duplicate state
			MultiAgentState duplicatedState = new MultiAgentState(current.getState().getAgents(), current
					.getState().getOccupiedCells(), current.getState().getCellForBoxes());

			// we try to add actions of the active agents and see if we have conflicts
			for (short i = 0; i < agentsActions.length; i++) {
				if (activeAgents[i] == true) {
					Action agentIAction = agentsActions[i];
					// checking if action can be performed on the state
					if (agentIAction.isApplicable(duplicatedState)) {
						// if yes -> execute it
						agentIAction.execute(duplicatedState);
					} else {
						// the action is not applicable so it conflicts with another action
						if (log.isLoggable(Level.FINER)) {
							log.finer("Action not applicable: " + agentIAction + " in state "
									+ duplicatedState);

						}
						conflictFound = true;

						// if no -> check with which action is in conflict
						for (short j = 0; j < i; j++) {
							if (agentIAction.isInConflict(duplicatedState, agentsActions[j])) {
								// added the conflict between already executed action j
								conflictingAgents.add(new Conflict(j, i));
								if (log.isLoggable(Level.FINE))
									log.fine("Conflict found between agents: " + j + "x" + i);
							}
						}
					}
				}
			}

			// if we can move on to next step (no conflict was found at this one)
			if (conflictFound == false) {
				short[] nextAgentCurrentActionIndex = new short[agentCurrentActionIndex.length];
				Action[] currentSelectedActions = new Action[this.agentsCount];
				for (int i = 0; i < this.agentsCount; i++)
					if (activeAgents[i]) {
						nextAgentCurrentActionIndex[i] = (short) (agentCurrentActionIndex[i] + 1);
						currentSelectedActions[i] = agentsActions[i];
					} else {
						nextAgentCurrentActionIndex[i] = agentCurrentActionIndex[i];
						currentSelectedActions[i] = new NoOpAction();
					}

				// log.info("Moving to next step after selecting: " +
				// Arrays.toString(currentSelectedActions));
				// log.info("Moving to next step with agent action indexes: "
				// + Arrays.toString(nextAgentCurrentActionIndex));
				PlanMergeNode next = new PlanMergeNode(duplicatedState, currentSelectedActions, current);
				boolean success = mergePlans(nextAgentCurrentActionIndex, next, step + 1);
				if (success)
					return true;
			}
			// get the next option
			currentOptionsIndex = getNextOptionIndex(currentOptionsIndex, conflictingAgents,
					applicableActions, agentsActions);
		}
		log.finest("Plan merging path failed. Going back...");
		return false;
	}

	private List<LinkedList<Action>> prepareResponse(PlanMergeNode finalNode) {
		// return the plan...
		PlanMergeNode crt = finalNode;
		List<LinkedList<Action>> mergedPlan = new ArrayList<LinkedList<Action>>();
		for (int i = 0; i < this.agentsCount; i++)
			mergedPlan.add(new LinkedList<Action>());

		// while there are nodes to go back to (and not the first node, because it doesn't have
		// stored actions which led to it)
		while (crt.getPrevNode() != null) {
			for (int i = 0; i < this.agentsCount; i++)
				// in the plan of i we put the action of i that brought us to this state
				mergedPlan.get(i).push(crt.getPrevActions()[i]);

			// go back one more node in the planning
			crt = crt.getPrevNode();
		}
		return mergedPlan;
	}

	private static class Conflict {
		public short agent1;
		public short agent2;

		public Conflict(short agent1, short agent2) {
			super();
			this.agent1 = agent1;
			this.agent2 = agent2;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + agent1;
			result = prime * result + agent2;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			Conflict other = (Conflict) obj;
			if (agent1 != other.agent1)
				return false;
			if (agent2 != other.agent2)
				return false;
			return true;
		}
	}
}
