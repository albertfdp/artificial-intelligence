package dk.dtu.ai.blueducks.goals;

import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.actions.Action;
import dk.dtu.ai.blueducks.map.Cell;

public abstract class Goal {
		
	public abstract Action getAction (Cell currentCell, Cell nextCell, Agent agent);

}
