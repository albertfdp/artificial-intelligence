package dk.dtu.ai.blueducks.goals;

import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.planner.AStarNode;

public class CellPathGoal extends Goal{
	private Cell from;
	private Cell to;

	public CellPathGoal(Cell from, Cell to) {
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
	public boolean isSatisfied(AStarNode node) {
		Cell cell = (Cell) node;
		return cell == this.to;
	}

}
