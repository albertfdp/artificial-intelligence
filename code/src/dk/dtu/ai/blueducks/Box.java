/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks;

import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.CellContent;

public class Box extends CellContent {
	
	private char id;
	private String color;
	
	public Box(Cell cell, char id, String color) {
		super(cell);
		// TODO Auto-generated constructor stub
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

}
