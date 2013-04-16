package dtu.dk.ai.goals;

import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.map.Cell;
import dtu.dk.ai.actions.Action;

public class GoToBoxGoal extends Goal {
	
	private Cell from;
	private Box to;
	
	public GoToBoxGoal (Cell from, Box to) {
		this.from = from;
		this.to = to;
	}

	public Cell getFrom() {
		return from;
	}

	public void setFrom(Cell from) {
		this.from = from;
	}

	public Box getTo() {
		return to;
	}

	public void setTo(Box to) {
		this.to = to;
	}
	
	@Override
	public Action getAction(Cell currentCell, Cell nextCell) {
		return null;
	}

}
