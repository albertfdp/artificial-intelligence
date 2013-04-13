/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks;

import dk.dtu.ai.blueducks.domain.Command;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.CellContent;
import dk.dtu.ai.blueducks.map.Direction;

public class Agent extends CellContent {

	private char id;
	private String color;

	public Agent(Cell initialCell, char id, String color) {
		super(initialCell);
		this.id = id;
		this.color = color;
	}

	public String move(Direction dir) {
		return Command.Move(dir);
	}

	public String nextAction() {
		return move(Direction.N);
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
