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

	/** The client. */
	private BlueDucksClient client;

	/**
	 * Instantiates a new Mother Odin.
	 * 
	 * @param client the client
	 */
	public MotherOdin(BlueDucksClient client) {
		super();
		this.client = client;
	}

	/** The instance. */
	private static MotherOdin mInstance;

	/**
	 * Gets the single instance of MotherOdin.
	 * 
	 * @return single instance of MotherOdin
	 */
	public static MotherOdin getInstance() {
		return mInstance;
	}

	public static void init(BlueDucksClient client) {
		if (mInstance == null) {
			mInstance = new MotherOdin(client);
		}
	}

	public void run() {
		List<Action> actions = new LinkedList<Action>();
		actions.add(new MoveAction(Direction.E, LevelMap.getInstance().getAgents().get(0)));
		BlueDucksClient.sendJointAction(actions);
	}
}
