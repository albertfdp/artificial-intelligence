/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import dk.dtu.ai.blueducks.actions.Action;
import dk.dtu.ai.blueducks.map.LevelMap;
import dk.dtu.ai.blueducks.map.MapLoader;

/**
 * The BlueDucksClient that handles the communication with the Environment Server.
 */
public class BlueDucksClient {

	private static final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	/** The Constant log. */
	private static final Logger log = Logger.getLogger(BlueDucksClient.class.getSimpleName());

	/**
	 * Reads the map.
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static void readMap() throws IOException {
		MapLoader.loadMap(in);

		// Read empty new line
		in.readLine();

		LevelMap.getInstance().executeMapPreAnalysis();
	}

	/**
	 * Read percepts from the environment.
	 * 
	 * @return an array of boolean values stating which of the actions succeeded.
	 */
	public static boolean[] readPercepts() {
		// Read the percepts from the server
		String percepts = null;
		try {
			percepts = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (log.isLoggable(Level.FINEST))
			log.finest("Received percept from server: " + percepts);
		if (percepts == null) {
			log.warning("No percepts read from the server.");
			return null;
		}

		// Parse the percepts;
		percepts = percepts.replaceAll(" ", "");
		percepts = percepts.substring(1);
		String[] splitPercepts = percepts.split("[,\\]\\[]");
		boolean[] resp = new boolean[splitPercepts.length];
		for (int i = 0; i < splitPercepts.length; i++)
			resp[i] = Boolean.valueOf(splitPercepts[i]);
		if (log.isLoggable(Level.FINER))
			log.finer("Environment action success response: " + Arrays.toString(resp));

		return resp;
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

		// Init the logger
		try {
			LogManager.getLogManager().readConfiguration(new FileInputStream("logging.properties"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Logger.getAnonymousLogger().info("BlueDucksClient started. Initializing...");

		// Init the system
		try {
			// Read the map
			BlueDucksClient.readMap();
			log.info("Map read and intialized.");
		} catch (IOException e) {
			log.severe("Error initializing map:" + e);
			return;
		}

		// Run the main loop of the supervisor Mother Odin
		MotherOdin.getInstance().run();
	}

}
