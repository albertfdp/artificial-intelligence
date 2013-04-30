/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import dk.dtu.ai.blueducks.actions.Action;
import dk.dtu.ai.blueducks.goals.DeliverBoxGoal;
import dk.dtu.ai.blueducks.goals.Goal;
import dk.dtu.ai.blueducks.map.Cell;
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

	/** The top level goals. */
	private List<Goal> topLevelGoals = new ArrayList<>();

	/**
	 * Gets the single instance of MotherOdin.
	 * 
	 * @return single instance of MotherOdin
	 */
	public static MotherOdin getInstance() {
		return mInstance;
	}

	public void generateTopLevelGoals() {
		topLevelGoals.clear();
		List<Box> boxes = map.getBoxesList();
		for (Map.Entry<Character, List<Cell>> goalCells : map.getGoals().entrySet()) {
			for (Cell cell : goalCells.getValue())
				for (Box b : boxes)
					if (b.getId() == goalCells.getKey())
						topLevelGoals.add(new DeliverBoxGoal(b, cell));
		}
	}

	/**
	 * The main Running cycle of the app.
	 */
	public void run() {
		List<Action> actions = new LinkedList<Action>();
		int currentLoop = 0;
		generateTopLevelGoals();
		
		while (true) {
			log.info("Starting loop " + (++currentLoop) + "...");
			actions.clear();
			// Get the actions from each agent
			for (Agent agent : map.getAgents().values())
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
				for (Agent agent : map.getAgents().values())
					agent.triggerReplanning();
			}
		}
	}

	public List<Goal> getTopLevelGoals() {
		return topLevelGoals;
	}
}
