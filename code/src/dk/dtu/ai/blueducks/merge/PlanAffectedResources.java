package dk.dtu.ai.blueducks.merge;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.actions.Action;
import dk.dtu.ai.blueducks.actions.PullAction;
import dk.dtu.ai.blueducks.actions.PushAction;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.State;

public class PlanAffectedResources {

	public Set<Cell> affectedCells;
	public Set<Box> affectedBoxes;

	public PlanAffectedResources(List<State> states) {
		affectedBoxes = new LinkedHashSet<Box>();
		affectedCells = new LinkedHashSet<Cell>();
		Action prevAction;
		for (State state : states) {
			affectedCells.add(state.getAgentCell());
			prevAction = (Action) state.getEdgeFromPrevNode();

			if (prevAction != null) {
				if (prevAction instanceof PullAction) {
					affectedCells.add(state.getCellForBox(((PullAction) prevAction).getBox()));
					affectedBoxes.add(((PullAction) prevAction).getBox());
				} else if (prevAction instanceof PushAction) {
					affectedCells.add(state.getCellForBox(((PushAction) prevAction).getBox()));
					affectedBoxes.add(((PushAction) prevAction).getBox());
				}
			}
		}
	}

	public PlanAffectedResources() {
		affectedBoxes = new LinkedHashSet<Box>();
		affectedCells = new LinkedHashSet<Cell>();
	}

	/**
	 * @return the affectedCells
	 */
	public Set<Cell> getAffectedCells() {
		return affectedCells;
	}

	/**
	 * @return the affectedBoxes
	 */
	public Set<Box> getAffectedBoxes() {
		return affectedBoxes;
	}

	@Override
	public String toString() {
		return "PlanAffectedResources [affectedCells=" + affectedCells + ", affectedBoxes=" + affectedBoxes
				+ "]";
	}

}
