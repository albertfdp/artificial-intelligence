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

import dk.dtu.ai.blueducks.goals.Goal;
import dk.dtu.ai.blueducks.planner.AStarNode;

/**
 * The Class Cell.
 */
public class Cell extends AStarNode{

	public static int noOfCells = 0;
	public int uniqueId;

	/** The Constant map. */
	protected static LevelMap map;

	public int x;
	public int y;
	public Cell previousCell;
	public Direction dirFromPrev;

	public Cell(int x, int y) {
		super();
		this.x = x;
		this.y = y;
		// not set yet (or maybe non-existent)
		this.previousCell = null;
		// not set yet (or maybe non-existent)
		this.dirFromPrev = null;
		this.uniqueId = Cell.noOfCells;
		Cell.noOfCells ++;
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
	 * Gets the neighbours (Cells).
	 * 
	 * @return the neighbours
	 */
	public List<Cell> getCellNeighbours() {
		List<Cell> neighbors = new ArrayList<Cell>();

		neighbors.add(this.getNeighbour(Direction.N));
		neighbors.add(this.getNeighbour(Direction.S));
		neighbors.add(this.getNeighbour(Direction.E));
		neighbors.add(this.getNeighbour(Direction.W));
		return neighbors;
	}
	
	/**
	 * Gets the neighbours (AStarNodes).
	 * 
	 * @return the neighbours
	 */
	public List<AStarNode> getNeighbours() {
		List<AStarNode> neighbors = new ArrayList<AStarNode>();
		
		Cell possibleN = this.getNeighbour(Direction.N);
		// in case at that cell there is no wall
		if(possibleN!=null) {
			possibleN.previousCell = this;
			possibleN.dirFromPrev = Direction.N;
			neighbors.add(possibleN);
		}
		
		Cell possibleS = this.getNeighbour(Direction.S);
		// in case at that cell there is no wall
		if(possibleS!=null) {
			possibleS.previousCell = this;
			possibleS.dirFromPrev = Direction.S;
			neighbors.add(possibleS);
		}
		
		Cell possibleE = this.getNeighbour(Direction.E);
		// in case at that cell there is no wall
		if(possibleE!=null) {
			possibleE.previousCell = this;
			possibleE.dirFromPrev = Direction.E;
			neighbors.add(possibleE);
		}
		
		Cell possibleW = this.getNeighbour(Direction.W);
		// in case at that cell there is no wall
		if(possibleW!=null) {
			possibleW.previousCell = this;
			possibleW.dirFromPrev = Direction.W;
			neighbors.add(possibleW);
		}
		return neighbors;
	}

	@Override
	public String toString() {
		return "Cell[" + x + ", " + y + "]";
	}

	@Override
	public AStarNode getPreviousNode() {
		return this.previousCell;
	}

	@Override
	public Object getEdgeFromPrevNode() {
		return this.dirFromPrev;
	}

	@Override
	public boolean satisfiesGoal(Goal goal) {
		return goal.isSatisfied(this);
	}
}
