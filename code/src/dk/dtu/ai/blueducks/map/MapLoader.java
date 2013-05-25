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
import java.util.Map;
import java.util.Scanner;

import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.Box;

/**
 * The Map Loader.
 */
public class MapLoader {
		
	/** The Constant REGEX_AGENT. */
	private final static String REGEX_AGENT = "\\d";
	
	/** The Constant REGEX_BOX. */
	private final static String REGEX_BOX = "[A-Z]";
	
	/** The Constant REGEX_GOAL_CELL. */
	private final static String REGEX_GOAL_CELL = "^[a-z]$";
	
	private final static String REGEX_COLOR_DEFINITION = "^[a-z]+:\\s*[0-9A-Z](,\\s*[0-9A-Z])*\\s*$";
	
	private final static String DEFAULT_COLOR = "blue";

	private static final String REGEX_FREE_CELL = "\\s";
	
	/**
	 * Loads the map.
	 *
	 * @param br the br sent by the server
	 * @return the map
	 * @throws IOException 
	 */
	public static LevelMap loadMap(BufferedReader br) throws IOException {
		
		StringBuilder colorsSb = new StringBuilder();
		StringBuilder mapSb = new StringBuilder();
		String line;
		
		int width = 0;
		int height = 0;
		
		while ((line = br.readLine()) != null) {
			if (!line.equals("")) {
				if (!line.matches(REGEX_COLOR_DEFINITION)) {
					height++;
					width = (line.length() > width) ? line.length() : width;
					mapSb.append(line + "\n");
				} else {
					colorsSb.append(line + "\n");
				}
			}
			else {
				break;
			}
		}
		
		LevelMap map = LevelMap.getInstance();
		map.init(width, height);
		
		Map<Character, String> colors = readColorDefinition(colorsSb.toString());
		Scanner scan = new Scanner(mapSb.toString());
		int x = 0;
		Map<Cell, Box> boxes = new HashMap<Cell, Box>();
		while (scan.hasNextLine()) {
			String l = scan.nextLine();
			
			for (int y = 0; y < l.length(); y++) {
				char c = l.charAt(y);
				String color = (colors.get(c) == null) ? DEFAULT_COLOR : colors.get(c);
				
				if (isAgent(c)) {
					Cell cell = new Cell(x, y);
					map.addCell(cell, x, y);
					map.attachAgent(cell, new Agent(c, color));
				} else if (isBox(c)) {
					Cell cell = new Cell(x, y);
					map.addCell(cell, x, y);
					boxes.put(cell, new Box(c, color));
					//map.attachBox(cell, new Box(c, color));
				} else if (isGoalCell(c)) {
					Cell cell = new Cell(x, y);
					map.addGoalCell(cell, x, y, Character.toUpperCase(c));
				} else if (isFreeCell(c)) {
					Cell cell = new Cell(x, y);
					map.addCell(cell, x, y);
				}
			}
			
			x++;
		}
		scan.close();
		map.attachBoxes(boxes);
//		//TODO: change the logging
//		for (Entry<Cell, Agent> e : map.getAgents().entrySet()) {
//			Agent a = e.getValue();
//			LevelMap map
//			log.info("agent-" + a.getId() + " [" + a.getColor() + "] at [" + a.getCell().x + "," + a.getCell().y + "]");
//		}
//		
//		for (Map.Entry<Character, List<Box>> entry : map.getBoxes().entrySet()) {
//			for (Box b : entry.getValue()) {
//				log.info("box-" + b.getId() + " [" + b.getColor() + "] at [" + b.getCell().x + "," + b.getCell().y + "]");
//			}
//		}
//		
//		for (Map.Entry<Character, Cell> entry : map.getGoals().entrySet()) {
//			log.info("goal-" + entry.getKey() + " at [" + entry.getValue().x + "," + entry.getValue().y + "]");
//		}
//		
		return map;
	}
	
	/**
	 * Read color definition.
	 *
	 * @param string the string
	 * @return the map of <id, color>
	 */
	private static Map<Character, String> readColorDefinition(String string) {
		Map<Character, String> colors = new HashMap<Character, String>();
		Scanner scan = new Scanner(string);
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			if (line.matches(REGEX_COLOR_DEFINITION)) {
				line = line.replaceAll("\\s", "");
				for (String id : line.split(":")[1].split(",")) {
					colors.put(id.charAt(0), line.split(":")[0]);
				}
			}
		}
		scan.close();
		return colors;
	}
		
	private static boolean isFreeCell(char c) {
		return Character.toString(c).matches(REGEX_FREE_CELL);
	}
	
	private static boolean isGoalCell(char c) {
		return Character.toString(c).matches(REGEX_GOAL_CELL);
	}
	
	private static boolean isAgent(char c) {
		return Character.toString(c).matches(REGEX_AGENT);
	}
	
	private static boolean isBox(char c) {
		return Character.toString(c).matches(REGEX_BOX);
	}
}
