/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.map;

public enum Direction {
	N, W, E, S;

	/**
	 * Checks if the given direction is opposite this one.
	 * 
	 * @param direction the direction
	 * @return true, if is opposite
	 */
	public boolean isOpposite(Direction direction) {
		return this.ordinal() + direction.ordinal() == 3;
	}
}
