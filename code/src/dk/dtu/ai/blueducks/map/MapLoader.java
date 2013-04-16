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
		while (scan.hasNextLine()) {
			String l = scan.nextLine();
			for (int y = 0; y < l.length(); y++) {
				String sCell = l.substring(x, x + 1);
				Cell cell = new Cell(x, y);
				if (isAgent(sCell)) {
					String agentColor = colors.get(sCell.charAt(0));
					agentColor = (agentColor == null) ? DEFAULT_COLOR : agentColor;
					cell.attachCellContent(new Agent(cell, sCell.charAt(0), agentColor));
					map.addCell(cell, x, y);
				} else if (isBox(sCell)) {
					String boxColor = colors.get(sCell.charAt(0));
					boxColor = (boxColor == null) ? DEFAULT_COLOR : boxColor;
					cell.attachCellContent(new Box(cell, sCell.charAt(0), boxColor));
					map.addCell(cell, x, y);
				} else if (isGoalCell(sCell)) {
					map.addGoalCell(cell, x, y, Character.toUpperCase(sCell.charAt(0)));
				}
			}
			x++;
		}
		scan.close();
		
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
				line = line.replace("\\s", "");
				for (String id : line.split(":")[1].split(","))
					colors.put(id.charAt(0), line.split(":")[0]);
			}
		}
		scan.close();
		return colors;
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
