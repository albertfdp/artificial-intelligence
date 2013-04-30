/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.Box;

/**
 * The Map of a Level.
 */
public class LevelMap {

	/** The map. */
	private static LevelMap map;

	/** The matrix. */
	private Cell[][] matrix;

	/** The agents. */
	private Map<Cell, Agent> agents;

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(LevelMap.class.getSimpleName());

	/** The goals. */
	private Map<Character, List<Cell>> goals;
	
	/** The width. */
	private int width;

	/** The height. */
	private int height;

	/** The current state. */
	private State currentState;

	/** The boxes list. */
	private List<Box> boxesList;

	/**
	 * Instantiates a new level map.
	 */
	private LevelMap() {
		agents = new HashMap<Cell, Agent>(10);
		boxesList = new LinkedList<>();
		goals = new HashMap<Character, List<Cell>>();
	}

	/**
	 * Gets the single instance of Map. {@link LevelMap#init(int)} must be called before using this
	 * method.
	 * 
	 * @return single instance of Map
	 */
	public static LevelMap getInstance() {
		if (LevelMap.map == null) {
			LevelMap.map = new LevelMap();
			map.currentState = new State(null, null, null, null);
		}
		return LevelMap.map;
	}

	/**
	 * Inits the map for a given size.
	 * 
	 * @param size the size
	 */
	public void init(int width, int height) {
		this.matrix = new Cell[height][];
		this.width = width;
		this.height = height;
		int i;
		for (i = 0; i < height; i++) {
			this.matrix[i] = new Cell[width];
		}
		Cell.map = this;

	}

	/**
	 * Adds a new cell- to WHERE
	 * 
	 * @param cell the cell
	 */
	public void addCell(Cell cell, int x, int y) {
		if (matrix == null || x >= matrix.length || matrix[0] == null || y >= matrix[0].length) {
			logger.warning("Could not add cell on position " + x + " " + y
					+ " as they exceed the declared size of the matrix");
			return;
		}
		matrix[x][y] = cell;
	}

	/**
	 * Adds a cell that is also a goal
	 * 
	 * @param cell
	 * @param x
	 * @param y
	 * @param id
	 */
	public void addGoalCell(Cell cell, int x, int y, char id) {
		this.addCell(cell, x, y);
		logger.info("Added Cell: " + x + " "+ y);
		List<Cell> list;
		if(!goals.containsKey(id)) {
			list = new ArrayList<Cell>();
			list.add(cell);
			goals.put(id, list);
		}else{
			list = goals.get(id);
			list.add(cell);
		}
	}

	/**
	 * Attach agent.
	 * 
	 * @param cell the cell
	 * @param agent the agent
	 */
	public void attachAgent(Cell cell, Agent agent) {
		logger.info("Attached agent" + agent.getId() +" to Cell: " + cell.x + " "+ cell.y);
		agents.put(cell, agent);
	}

	/**
	 * Attach box.
	 * 
	 * @param cell the cell
	 * @param box the box
	 */
	public void attachBox(Cell cell, Box box) {
		logger.info("Attached box" + box.getId() +" to Cell: " + cell.x + " "+ cell.y);
		this.currentState.addBox(cell, box);
		this.boxesList.add(box);
	}

	public List<Box> getBoxesList() {
		return boxesList;
	}

	/**
	 * Gets the cell at a given position.
	 * 
	 * @return the cell at
	 */
	public Cell getCellAt(int x, int y) {
		if (x < this.height && y < this.width) {
			return matrix[x][y];
		}
		return null;
	}

	/**
	 * Gets the goals.
	 * 
	 * @return the goals
	 */
	public Map<Character, List<Cell>> getGoals() {
		return goals;
	}

	/**
	 * Gets the agents.
	 * 
	 * @return the agents
	 */
	public Map<Cell, Agent> getAgents() {
		return agents;
	}

	/**
	 * Gets the width.
	 * 
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Gets the height.
	 * 
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Gets the cell for a given agent.
	 * 
	 * @param agent
	 * @return the cell for the agent
	 */
	public Cell getCellForAgent(Agent agent) {
		for (Entry<Cell, Agent> e : agents.entrySet()) {
			if (e.getValue() == agent) {
				return e.getKey();
			}
		}
		return null;
	}

	/**
	 * Gets the current state.
	 * 
	 * @return the current state
	 */
	public State getCurrentState() {
		return currentState;
	}

}
