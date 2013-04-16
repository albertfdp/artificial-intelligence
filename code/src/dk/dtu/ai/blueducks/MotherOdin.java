/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks;

import java.util.LinkedList;
import java.util.List;

import dk.dtu.ai.blueducks.actions.Action;
import dk.dtu.ai.blueducks.actions.MoveAction;
import dk.dtu.ai.blueducks.map.Direction;
import dk.dtu.ai.blueducks.map.LevelMap;

/**
 * The Class MotherOdin.
 */
public class MotherOdin {

	/** The instance. */
	private static MotherOdin mInstance = new MotherOdin();

	/**
	 * Gets the single instance of MotherOdin.
	 * 
	 * @return single instance of MotherOdin
	 */
	public static MotherOdin getInstance() {
		return mInstance;
	}

	public void run() {
		List<Action> actions = new LinkedList<Action>();
		actions.add(new MoveAction(Direction.E, LevelMap.getInstance().getAgents().get(0)));
		BlueDucksClient.sendJointAction(actions);
	}
}
