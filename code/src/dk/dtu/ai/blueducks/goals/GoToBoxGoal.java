package dk.dtu.ai.blueducks.goals;

import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.actions.Action;
import dk.dtu.ai.blueducks.actions.MoveAction;
import dk.dtu.ai.blueducks.map.Cell;

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
	public Action getAction(Cell currentCell, Cell nextCell, Agent agent) {
		return new MoveAction(currentCell.getDirection(nextCell), agent);
	}

}
