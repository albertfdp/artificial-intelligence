/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.map;


/**
 * The Map of a Level.
 */
public class Map {

	private Map() {
	}

	/**
	 * Gets the single instance of Map. {@link Map#init(int)} must be called before using this
	 * method.
	 * 
	 * @return single instance of Map
	 */
	public static Map getInstance() {
		return null;
	}

	/**
	 * Inits the Map for a given size. Must be called before any use of {@link Map#getInstance()}.
	 * 
	 * @param size the size
	 */
	public static void init(int size) {

		//TODO: Attach the map instance to Cell.map;
	}

	/**
	 * Adds a new cell.
	 * 
	 * @param cell the cell
	 */
	public void addCell(Cell cell) {

	}

	/**
	 * Gets the cell at a given position.
	 * 
	 * @return the cell at
	 */
	public Cell getCellAt() {
		return null;
	}
}
