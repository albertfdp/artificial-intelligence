/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks;

import java.util.logging.Logger;

import dk.dtu.ai.blueducks.map.Cell;

public class Box {

	// TODO: should we add a unique id?

	private char id;
	private String color;

	public static int noOfBoxes = 0;
	public int uniqueId;
	public int powerHashValue;
	private static final Logger log = Logger.getLogger(Box.class.getSimpleName());
	@Override
	public String toString() {
		return "Box[" + id + "]";
	}

	public Box(char id, String color) {
		this.id = id;
		this.color = color;
		this.uniqueId = Box.noOfBoxes;
		Box.noOfBoxes ++;
		
		
	}

	public char getId() {
		return id;
	}

	public void setId(char id) {
		this.id = id;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public void computePowerHashValue() {
		log.info("NUMBER OF CELLS: " + Cell.noOfCells);
		this.powerHashValue = (int) Math.pow(Cell.noOfCells, this.uniqueId);

		log.info("Box " + this.uniqueId + " Power hash value: " + this.powerHashValue);
		// TODO Auto-generated method stub
		
	}
}
