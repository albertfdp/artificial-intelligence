package dk.dtu.ai.blueducks.merge;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.actions.Action;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.State;

public class PlanAffectedResources {

	public Set<Cell> affectedCells;
	public Set<Box> affectedBoxes;

	public PlanAffectedResources(List<State> states, List<Action> actions) {
		affectedBoxes=new LinkedHashSet<Box>();
		affectedCells=new LinkedHashSet<Cell>();
	}
}
