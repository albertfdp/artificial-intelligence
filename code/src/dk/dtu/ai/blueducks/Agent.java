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

	/**
	 * Instantiates a new agent.
	 *
	 * @param initialCell the initial cell
	 * @param id the id
	 * @param color the color
	 */
	public Agent(Cell initialCell, char id, String color) {
		super(initialCell);
		this.id = id;
		this.color = color;
	}

	/**
	 * Move.
	 *
	 * @param direction the direction
	 * @return the string
	 */
	public String move(Direction direction) {
		return Command.Move(this, direction);
	}

	/*???????????????????*/
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
