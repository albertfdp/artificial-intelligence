package dk.dtu.ai.blueducks.goals;

import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.actions.Action;
import dk.dtu.ai.blueducks.actions.MoveAction;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.State;

public class GoToBoxGoal extends Goal {

	private Cell from;
	private Cell to;

	public GoToBoxGoal(Cell from, Cell to) {
		this.from = from;
		this.to = to;
	}

	public Cell getFrom() {
		return from;
	}

	public Cell getTo() {
		return to;
	}

	@Override
	public Action getAction(Cell currentCell, Cell nextCell, Agent agent) {
		return new MoveAction(currentCell.getDirection(nextCell), agent);
	}

	@Override
	public boolean isSatisfied(State state) {
		if(state.getAgentCell().getNeighbours().contains(to))
			return true;
		return false;
	}

}
