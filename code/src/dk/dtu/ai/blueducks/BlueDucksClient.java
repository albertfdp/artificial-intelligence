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
import java.util.ArrayList;

import dk.dtu.ai.blueducks.map.Map;
import dk.dtu.ai.blueducks.map.MapLoader;

public class BlueDucksClient {
	
	private BufferedReader in = new BufferedReader( new InputStreamReader( System.in ) );
	private ArrayList<Agent> agents = new ArrayList<Agent>();
			
	public BlueDucksClient() throws IOException {
		readMap(); 
	}
	
	/*
	 * 
	 */
	private void readMap() throws IOException {
		Map map = MapLoader.loadMap(in);
	}
	
	public boolean update() throws IOException {
		
		ArrayList<String> actions = new ArrayList<String>();
		
		for (Agent agent : agents) {
			actions.add(agent.nextAction());
		}
		
		System.out.println(BlueDucksClient.sendAction(actions));
		System.out.flush();
		
		// Disregard these for now, but read or the server stalls when outputbuffer gets filled!
		String percepts = in.readLine();
		if (percepts == null)
			return false;
		
		return true;
		
	}
	
	public static void main(String [] args) {
		
		// use stderr to print to console
		System.err.println("Hello from custom client!");
		
		try {
			BlueDucksClient client = new BlueDucksClient();
			while (client.update()) {
				// send next decision
			}
		} catch (IOException e) {
			// got nowhere to write
		}
		
	}
	
	public static String sendAction(ArrayList<String> actions) {
		
		String jointAction = "[";
		
		for (String action : actions) {
			jointAction += action + ",";
		}
		
		// remove the last comma
		if (jointAction.length() > 1)
			jointAction = jointAction.substring(0, jointAction.length() - 1);
		
		jointAction += "]";
		
		return jointAction;
	}


}
