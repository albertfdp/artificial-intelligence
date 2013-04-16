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

	/** The content. */
	private CellContent content;

	/** The Constant map. */
	protected static LevelMap map;
	
	public int x;
	public int y;

	
	public Cell(int x, int y){
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
		if(dir == Direction.E)
			neighborY--;
		else if(dir == Direction.W)
			neighborY++;
		else if(dir == Direction.N)
			neighborX--;
		else if(dir == Direction.S)
			neighborX++;
		Cell neighbor = map.getCellAt(neighborX, neighborY);
		return neighbor;
	}

	public List<Cell> getNeighbours() {
		List<Cell> neighbors = new ArrayList<Cell>();
		neighbors.add(this.getNeighbour(Direction.N));
		neighbors.add(this.getNeighbour(Direction.S));
		neighbors.add(this.getNeighbour(Direction.E));
		neighbors.add(this.getNeighbour(Direction.W));
		return neighbors;
	}
	
	/**
	 * Attaches a cell content.
	 * 
	 * @param content the content
	 */
	
	public void attachCellContent(CellContent content) {
		this.content = content;
		content.setCell(this);
	}

	/**
	 * Gets the content.
	 * 
	 * @return the content
	 */
	public CellContent getContent() {
		return content;
	}
}
