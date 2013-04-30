/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.map;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class Cell.
 */
public class Cell {



	/** The Constant map. */
	protected static LevelMap map;

	public int x;
	public int y;

	public Cell(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	/**
	 * Gets the neighbour in the given direction.
	 * 
	 * @param dir the direction
	 * @return the neighbour
	 */
	public Cell getNeighbour(Direction dir) {
		int neighborX = this.x, neighborY = this.y;
		if (dir == Direction.E)
			neighborY++;
		else if (dir == Direction.W)
			neighborY--;
		else if (dir == Direction.N)
			neighborX--;
		else if (dir == Direction.S)
			neighborX++;
		Cell neighbor = map.getCellAt(neighborX, neighborY);
		return neighbor;
	}

	/**
	 * Gets the direction to the second cell respectively to the current instance.
	 * 
	 * @param cell the cell
	 * @return the direction
	 */
	public Direction getDirection(Cell cell) {

		int xdiff = this.x - cell.x;
		int ydiff = this.y - cell.y;

		if (ydiff > 0) {
			return Direction.W;
		} else if (ydiff < 0) {
			return Direction.E;
		} else if (xdiff > 0) {
			return Direction.N;
		} else {
			return Direction.S;
		}
	}

	/**
	 * Gets the neighbours.
	 * 
	 * @return the neighbours
	 */
	public List<Cell> getNeighbours() {
		List<Cell> neighbors = new ArrayList<Cell>();
		neighbors.add(this.getNeighbour(Direction.N));
		neighbors.add(this.getNeighbour(Direction.S));
		neighbors.add(this.getNeighbour(Direction.E));
		neighbors.add(this.getNeighbour(Direction.W));
		return neighbors;
	}

	@Override
	public String toString() {
		return "Cell[" + x + ", " + y + "]";
	}
}
