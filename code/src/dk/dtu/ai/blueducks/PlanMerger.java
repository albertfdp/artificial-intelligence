package dk.dtu.ai.blueducks;

import java.util.ArrayList;
import java.util.List;

import dk.dtu.ai.blueducks.actions.Action;
import dk.dtu.ai.blueducks.map.MultiAgentState;

public class PlanMerger {

	static boolean[] getNextOption() {
		return null;
	}
	
	/**
	 * @param actions - line i contains the actions from the agent i's plan
	 * @param current - the node we are trying to expand
	 * @param step - the current depth of the plan (e.g. step 3 - we're trying to see what actions from step 3 can be done) 
	 * @return true if is successful in merging the plans or false otherwise
	 */
	static boolean mergePlans(Action[][] actions, PlanNode current, int step) {
		// we will keep a list of pairs of actions which are conflicting in the state
		List<Conflict> conflictingAgents = new ArrayList<Conflict>();
		// keeps track if there is a conflict found when trying to execute the actions for this step
		boolean conflictFound;

		// we try to get all option of generated activeAgents
		boolean[] activeAgents;
		activeAgents = getNextOption();
		
		while(activeAgents != null) {
			// at the beginning no conflict exists
			conflictFound = false;
			
			// we duplicate state
			MultiAgentState duplicatedState = current.getState();
			
			// we try to add actions of the active agents and see if we have conflicts
			for(short i=0;i<activeAgents.length;i++) {
				if(activeAgents[i] == true) {
					
					// checking if action can be performed on the state
					if(actions[i][step].isApplicable(duplicatedState)) {
						// if yes -> execute it
						actions[i][step].execute(duplicatedState);
					}
					else {
						// if no -> check with which action is in conflict
						for(short j=0; j<i; j++) {
							if(actions[i][step].isInConflict(duplicatedState, actions[j][step])) {
								// added the conflict between already executed action j
								conflictingAgents.add(new Conflict(j, i));
								conflictFound = true;
							}
						}
					}
				}
			}
			
			// if we can move on to next step (no conflict was found at this one)
			if(conflictFound == false) {
				PlanNode next = new PlanNode(duplicatedState, activeAgents, current);
				mergePlans(actions, next, step+1);
			}
			else {
				// get the next option
				activeAgents = getNextOption();
			}
		}
		return false;
	}
	
	private static class Conflict {
		private short agent1;
		private short agent2;
		
		public Conflict(short agent1, short agent2) {
			super();
			this.agent1 = agent1;
			this.agent2 = agent2;
		}

		public short getAgent1() {
			return agent1;
		}

		public short getAgent2() {
			return agent2;
		}
	}
}
