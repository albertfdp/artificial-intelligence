/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.map;

/**
 * The Cell Content.
 */
public class CellContent {

	/** The cell. */
	protected Cell cell;

	/**
	 * Instantiates a new cell content.
	 * 
	 * @param cell the cell
	 */
	public CellContent(Cell cell) {
		super();
		this.cell = cell;
	}

	/**
	 * Gets the cell.
	 * 
	 * @return the cell
	 */
	public Cell getCell() {
		return cell;
	}

	/**
	 * Sets the cell.
	 * 
	 * @param cell the new cell
	 */
	public void setCell(Cell cell) {
		this.cell = cell;
	}

}
