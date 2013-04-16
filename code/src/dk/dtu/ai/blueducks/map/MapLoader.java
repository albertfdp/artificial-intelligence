/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.map;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import dk.dtu.ai.blueducks.Agent;

/**
 * The Map Loader.
 */
public class MapLoader {
	
	/** The Constant REGEX_WALL. */
	private final static String REGEX_WALL = "^\\+$";
	
	/** The Constant REGEX_UNKNOWN. */
	private final static String REGEX_UNKNOWN = "^\\*$";
	
	/** The Constant REGEX_AGENT. */
	private final static String REGEX_AGENT = "^\\d$";
	
	/** The Constant REGEX_BOX. */
	private final static String REGEX_BOX = "^[A-Z]$";
	
	/** The Constant REGEX_GOAL_CELL. */
	private final static String REGEX_GOAL_CELL = "^[a-z]$";
	
	private final static String REGEX_COLOR_DEFINITION = "^[a-z]+:\\s*[0-9A-Z](,\\s*[0-9A-Z])*\\s*$";
	
	private final static String DEFAULT_COLOR = "blue";
	
	/**
	 * Loads the map.
	 *
	 * @param br the br sent by the server
	 * @return the map
	 * @throws IOException 
	 */
	public static LevelMap loadMap(BufferedReader br) throws IOException {
		
		LevelMap map=LevelMap.getInstance();
		map.init(0,0);
		
		String line;
		String color = DEFAULT_COLOR;
		
		// read lines specifying colors
		while ((line = br.readLine()).matches(REGEX_COLOR_DEFINITION)) {
			
			line = line.replace("\\s", ""); // remove spaces
			color = line.split(":")[0];
			
			//for (String id : line.split(":")[1].split(",")) // assign color, id
				//map.addColor(id.charAt(0), color);
		}
		
		// read lines specifying layout
		int y = 0; // line counter
		while (!line.equals("")) {
			for (int x = 0; x < line.length(); x++) {
				
				String sCell = line.substring(x, x + 1);
				
				if (isWall(sCell) || isUnknown(sCell)) {
					
				} else if (isAgent(sCell)) {
					//Agent agent = new Agent(sCell.charAt(0), color);
					//Cell cell = new Cell();
					//cell.attachCellContent(agent);
				} else if (isBox(sCell)) {
					
				} else if (isGoalCell(sCell)) {
					
				}
			}
			line = br.readLine();
			y++;
		}
		
		return map;
	}
	
	
	/**
	 * Checks if is wall.
	 *
	 * @param s the s
	 * @return true, if is wall
	 */
	private static boolean isWall(String s) {
		return s.matches(REGEX_WALL);
	}
	
	/**
	 * Checks if is unknown.
	 *
	 * @param s the s
	 * @return true, if is unknown
	 */
	private static boolean isUnknown(String s) {
		return s.matches(REGEX_UNKNOWN);
	}
	
	/**
	 * Checks if is goal cell.
	 *
	 * @param s the s
	 * @return true, if is goal cell
	 */
	private static boolean isGoalCell(String s) {
		return s.matches(REGEX_GOAL_CELL);
	}
	
	/**
	 * Checks if is agent.
	 *
	 * @param s the s
	 * @return true, if is agent
	 */
	private static boolean isAgent(String s) {
		return s.matches(REGEX_AGENT);
	}
	
	/**
	 * Checks if is box.
	 *
	 * @param s the s
	 * @return true, if is box
	 */
	private static boolean isBox(String s) {
		return s.matches(REGEX_BOX);
	}
}
