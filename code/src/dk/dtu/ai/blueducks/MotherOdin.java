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
import java.util.logging.Logger;

import dk.dtu.ai.blueducks.actions.Action;
import dk.dtu.ai.blueducks.map.LevelMap;

/**
 * The Class MotherOdin.
 */
public class MotherOdin {

	/** The Constant log. */
	private static final Logger log = Logger.getLogger(MotherOdin.class.getSimpleName());

	/** The instance. */
	private static MotherOdin mInstance = new MotherOdin();

	/** The map. */
	private LevelMap map = LevelMap.getInstance();

	/**
	 * Gets the single instance of MotherOdin.
	 * 
	 * @return single instance of MotherOdin
	 */
	public static MotherOdin getInstance() {
		return mInstance;
	}

	/**
	 * The main Running cycle of the app.
	 */
	public void run() {
		List<Action> actions = new LinkedList<Action>();
		int currentLoop = 0;

		while (currentLoop < 5) {
			log.info("Starting loop " + (++currentLoop) + "...");
			actions.clear();
			// Get the actions from each agent
			for (Agent agent : map.getAgents())
				actions.add(agent.getNextAction());
			// Send the joint actions to the server
			BlueDucksClient.sendJointAction(actions);
			// Read the percepts from the environment
			boolean[] percepts = BlueDucksClient.readPercepts();

			// Update the map
			int i = 0;
			boolean jointActionSuccessful = true;
			for (Action a : actions)
				if (percepts[i++])
					a.updateBeliefs();
				else {
					log.fine("Action not successful: " + a);
					jointActionSuccessful = false;
				}

			// Notify the agents that something has changed
			if (!jointActionSuccessful) {
				log.info("Triggering agent replanning!");
				for (Agent agent : map.getAgents())
					agent.triggerReplanning();
			}
		}
	}
}
