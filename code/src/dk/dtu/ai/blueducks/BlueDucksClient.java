/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Logger;

import dk.dtu.ai.blueducks.map.MapLoader;
import dtu.dk.ai.blueducks.actions.Action;

/**
 * The BlueDucksClient that handles the communication with the Environment Server.
 */
public class BlueDucksClient {

	private static final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	/** The Constant log. */
	private static final Logger log = Logger.getLogger("Client");

	/**
	 * Reads the map.
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static void readMap() throws IOException {
		MapLoader.loadMap(in);
	}

	/**
	 * Update the beliefs.
	 *
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<Boolean> updateBeliefs() throws IOException {
		// Read the percepts from the server
		String percepts = in.readLine();
		log.fine("Received percept from server: " + percepts);
		if (percepts == null)
			return null;

		return null;
	}

	/**
	 * Send joint action.
	 *
	 * @param actions the actions
	 */
	public static void sendJointAction(List<Action> actions) {

		// Build the joint action representation
		String jointAction = "[";
		for (Action action : actions) {
			jointAction += action.toCommandString() + ",";
		}

		// Remove the last comma
		if (jointAction.length() > 1)
			jointAction = jointAction.substring(0, jointAction.length() - 1);
		jointAction += "]";

		// Sent the joint action to the server
		log.fine("Sending joint action: " + jointAction);
		System.out.println(jointAction);
		System.out.flush();
	}

	public static void main(String[] args) {

		Logger.getAnonymousLogger().info("BlueDucksClient started. Initializing...");
		BlueDucksClient client = null;
		try {
			// Read the map and init the client
			BlueDucksClient.readMap();
			MotherOdin.init(client);
		} catch (IOException e) {
			Logger.getAnonymousLogger().severe("Error initializing map:" + e);
			return;
		}
		MotherOdin.getInstance().run();
	}

}
