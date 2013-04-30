/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.actions;

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
	
}
