package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import domain.Agent;

public class Client {
	
	private BufferedReader in = new BufferedReader( new InputStreamReader( System.in ) );
	private ArrayList<Agent> agents = new ArrayList<Agent>();
			
	public Client() throws IOException {
		readMap();
	}
	
	/*
	 * 
	 */
	private void readMap() throws IOException {
		// We will read it depending on the data structure decided
	}
	
	public boolean update() throws IOException {
		
		ArrayList<String> actions = new ArrayList<String>();
		
		for (Agent agent : agents) {
			actions.add(agent.nextAction());
		}
		
		System.out.println(Command.sendAction(actions));
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
			Client client = new Client();
			while (client.update()) {
				// send next decision
			}
		} catch (IOException e) {
			// got nowhere to write
		}
		
	}

}
