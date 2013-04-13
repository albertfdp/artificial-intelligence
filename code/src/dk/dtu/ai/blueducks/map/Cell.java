/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.map;


/**
 * The Class Cell.
 */
public class Cell {

	/** The content. */
	private CellContent content;

	/** The Constant map. */
	protected static Map map;

	/**
	 * Gets the neighbour in the given direction.
	 * 
	 * @param dir the direction
	 * @return the neighbour
	 */
	public Cell getNeighbour(Direction dir) {
		// TODO:
		return null;
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
