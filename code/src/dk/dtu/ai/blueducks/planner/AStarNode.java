/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.planner;

import java.util.List;

import dk.dtu.ai.blueducks.actions.Action;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.Direction;
import dk.dtu.ai.blueducks.map.State;

/**
 * The AStarNode interface must be implemented by classes that can be used, as nodes, in the A Star
 * Search algorithm.
 */
public abstract class AStarNode implements Comparable<AStarNode> {

	/**
	 * Cost from start along best known path
	 */
	public float g;

	/**
	 * Estimated total cost from start to goal through this node
	 */
	public float f;

	/*
	 * Returns a negative integer, zero, or a positive integer as this object is less than, equal
	 * to, or greater than the specified object.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(AStarNode o) {
		int value = (int) this.f - (int) o.f;
		return value;
	}

	/**
	 * Gets the neighbours of this node.<br/>
	 * <br/>
	 * In the case of {@link State States}, the neighbours are the states which are accesible from
	 * this node.<br/>
	 * In the case of {@link Cell Cells}, the neighbours are the actual cell neighbours.
	 * 
	 * @return the neighbours
	 */
	public abstract List<AStarNode> getNeighbours();

	/**
	 * Gets the previous node in the search graph, if any.
	 * 
	 * @return the previous node, or null if this is the first node in the search
	 */
	public abstract AStarNode getPreviousNode();

	/**
	 * Gets the action required in order to reach this node from the previous node.<br/>
	 * <br/>
	 * In the case of {@link State States}, it should be an {@link Action}. In the case of
	 * {@link Cell Cells}, it should be a {@link Direction}.
	 * 
	 * @return the action from prev node
	 */
	public abstract Object getEdgeFromPrevNode();

}
