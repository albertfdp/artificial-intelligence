/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.map;

public class CellEdge {
	
	/** The cell a. */
	private Cell cellA;
	
	/** The cell b. */
	private Cell cellB;
	
	/**
	 * Instantiates a new cell edge.
	 *
	 * @param cellA the cell a
	 * @param cellB the cell b
	 */
	public CellEdge(Cell cellA, Cell cellB) {
		this.cellA = cellA;
		this.cellB = cellB;
	}

	/**
	 * Gets the cell a.
	 *
	 * @return the cell a
	 */
	public Cell getCellA() {
		return cellA;
	}

	/**
	 * Sets the cell a.
	 *
	 * @param cellA the new cell a
	 */
	public void setCellA(Cell cellA) {
		this.cellA = cellA;
	}

	/**
	 * Gets the cell b.
	 *
	 * @return the cell b
	 */
	public Cell getCellB() {
		return cellB;
	}

	/**
	 * Sets the cell b.
	 *
	 * @param cellB the new cell b
	 */
	public void setCellB(Cell cellB) {
		this.cellB = cellB;
	}

}
