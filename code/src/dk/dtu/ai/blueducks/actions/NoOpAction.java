/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.actions;

import dk.dtu.ai.blueducks.map.MultiAgentState;
import dk.dtu.ai.blueducks.map.State;


/**
 * The Class NoOpAction.
 */
public class NoOpAction extends Action {
	
	@Override
	public String toCommandString() {
		return "NoOp";
	}

	@Override
	public void updateBeliefs() {
		
	}

	@Override
	public State getNextState(State state) {
		return state;
	}
	public void invalidateAction(){
	}

	@Override
	public boolean isInConflict(MultiAgentState state, Action otherAction) {
		return false;
	}

	@Override
	public boolean isApplicable(MultiAgentState state) {
		return true;
	}

	@Override
	public void execute(MultiAgentState state) {
		// TODO Auto-generated method stub
		
	}
}
