package dk.dtu.ai.blueducks.merge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import dk.dtu.ai.blueducks.actions.Action;
import dk.dtu.ai.blueducks.map.LevelMap;
import dk.dtu.ai.blueducks.map.MultiAgentState;

public class PlanMerger {

	private static final Logger log = Logger.getLogger(PlanMerger.class.getSimpleName());
	private static final boolean[][] mergeOptions;
	private static final int AGENTS_COUNT;
	private static final int NO_MORE_OPTIONS = -2;
	static {
		AGENTS_COUNT = LevelMap.getInstance().getAgentsList().size();
		mergeOptions = PlanMergeOptions.OPTIONS[AGENTS_COUNT-1];
		log.config("Loaded options for " + AGENTS_COUNT + " agents.");
	}

	private static int getNextOptionIndex(int currentOptionsPos) {
		if (currentOptionsPos >= mergeOptions.length)
			return NO_MORE_OPTIONS;
		return currentOptionsPos + 1;
	}

	/**
	 * @param actions - line i contains the actions from the agent i's plan
	 * @param current - the node we are trying to expand
	 * @param step - the current depth of the plan (e.g. step 3 - we're trying to see what actions
	 *            from step 3 can be done)
	 * @return true if is successful in merging the plans or false otherwise
	 */
	// TODO - limit the go back
	// TODO - keep track of the most advanced point where it failed
	public static boolean mergePlans(Action[][] actions, PlanMergeNode current, int step) {
		if (log.isLoggable(Level.INFO))
			log.info("Merging plans step " + step);

		// we will keep a list of pairs of actions which are conflicting in the state
		List<Conflict> conflictingAgents = new ArrayList<Conflict>();
		// keeps track if there is a conflict found when trying to execute the actions for this step
		boolean conflictFound;
		int currentOptionsIndex = 0;

		// we try to get all option of generated activeAgents
		boolean[] activeAgents;

		while (currentOptionsIndex != NO_MORE_OPTIONS) {
			activeAgents = mergeOptions[currentOptionsIndex];
			if (log.isLoggable(Level.FINEST))
				log.finest("Checking options: " + Arrays.toString(activeAgents));

			// at the beginning no conflict exists
			conflictFound = false;

			// we duplicate state
			MultiAgentState duplicatedState = new MultiAgentState(current.getState().getAgents(), current
					.getState().getBoxes());

			// we try to add actions of the active agents and see if we have conflicts
			for (short i = 0; i < activeAgents.length; i++) {
				if (activeAgents[i] == true) {

					// checking if action can be performed on the state
					if (actions[i][step].isApplicable(duplicatedState)) {
						// if yes -> execute it
						actions[i][step].execute(duplicatedState);
					} else {
						// if no -> check with which action is in conflict
						for (short j = 0; j < i; j++) {
							if (actions[i][step].isInConflict(duplicatedState, actions[j][step])) {
								// added the conflict between already executed action j
								conflictingAgents.add(new Conflict(j, i));
								conflictFound = true;
								if (log.isLoggable(Level.FINE))
									log.fine("Conflict found: " + j + "x" + i);
							}
						}
					}
				}
			}

			// if we can move on to next step (no conflict was found at this one)
			if (conflictFound == false) {
				if (step < actions[0].length - 1) {
					PlanMergeNode next = new PlanMergeNode(duplicatedState, activeAgents, current);
					return mergePlans(actions, next, step + 1);
				} else {
					log.info("Plan merge found: " + current);
					return true;
				}
			} else {
				// get the next option
				currentOptionsIndex = getNextOptionIndex(currentOptionsIndex);
			}
		}
		log.info("Plan merge not possible.");
		return false;
	}

	private static class Conflict {
		public short agent1;
		public short agent2;

		public Conflict(short agent1, short agent2) {
			super();
			this.agent1 = agent1;
			this.agent2 = agent2;
		}
	}
}
