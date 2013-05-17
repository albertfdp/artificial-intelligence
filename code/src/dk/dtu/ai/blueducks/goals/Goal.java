/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.goals;

import dk.dtu.ai.blueducks.planner.AStarNode;

public abstract class Goal {

	/**
	 * Checks whether the goal is satisfied in the given state.
	 * 
	 * @param state the state
	 * @return true, if is satisfied
	 */
	public abstract boolean isSatisfied(AStarNode node);
}
