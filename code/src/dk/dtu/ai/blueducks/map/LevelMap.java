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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.Box;

/**
 * The Map of a Level.
 */
public class LevelMap {

	private ArrayList<Agent> agents;

	/** The map. */
	private static LevelMap map;

	/** The matrix. */
	private Cell[][] matrix;

	/** The agents. */
	private Map<Cell, Agent> agentCells;

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(LevelMap.class.getSimpleName());

	/** The goals. */
	private Map<Character, List<Cell>> goals;
	
	/** All the goals. */
	private List<Cell> allGoals;

	/** The width. */
	private int width;

	/** The height. */
	private int height;

	/** The current state. */
	private State currentState;

	/** The boxes list put in he order of the uniqueID. */
	private List<Box> boxesList;

	private Set<Cell> lockedCells;

	private List<Cell> verifiedCells;

	/**
	 * Instantiates a new level map.
	 */
	private LevelMap() {
		agentCells = new HashMap<Cell, Agent>(10);
		boxesList = new LinkedList<>();
		goals = new HashMap<Character, List<Cell>>();
		allGoals = new ArrayList<Cell>();
		verifiedCells = new ArrayList<Cell>();
		agents = new ArrayList<Agent>(10);
		for (int i = 0; i < 10; i++)
			agents.add(null);
		lockedCells = new HashSet<Cell>();
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
			map.currentState = new State(null, null, null, null, null, null);
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
		logger.info("Added Cell: " + x + " " + y);
		if (matrix == null || x >= matrix.length || matrix[0] == null || y >= matrix[0].length) {
			logger.warning("Could not add cell on position " + x + " " + y
					+ " as they exceed the declared size of the matrix");
			return;
		}
		matrix[x][y] = cell;
	}

	public void markAsNotWall(Cell cell) {
		if (!verifiedCells.contains(cell))
			this.verifiedCells.add(cell);
	}

	public boolean isVerified(Cell cell) {
		if (verifiedCells.contains(cell))
			return true;
		return false;
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
		allGoals.add(cell);
		logger.info("Added Cell FOR GOAL: " + x + " " + y + " GOAL " + id);
		List<Cell> list;
		if (!goals.containsKey(id)) {
			list = new ArrayList<Cell>();
			list.add(cell);
			goals.put(id, list);
		} else {
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
		logger.info("Attached agent" + agent.getId() + " to Cell: " + cell.x + " " + cell.y);
		agentCells.put(cell, agent);
		agents.add(agent.getId(), agent);
	}

	/**
	 * Attach box.
	 * 
	 * @param cell the cell
	 * @param box the box
	 */
	public void attachBoxes(Map<Cell, Box> boxes) {
		this.boxesList = new ArrayList<Box>(boxes.size());
		logger.info("BOX SIZE: " + boxes.size());
		for(int i = 0; i < boxes.size(); i++)
			this.boxesList.add(null);
		
		for(Entry<Cell, Box> entry : boxes.entrySet()) {
			logger.info("LOOP BOX ATTACH");
			this.boxesList.set(entry.getValue().uniqueId, entry.getValue());
			entry.getValue().computePowerHashValue();
		}
		for(Agent agent : agents) {
			agent.computePowerHashValue();
		}
		this.currentState.setInitalBoxesInLevelMap(boxes);
	}

	public List<Cell> getCells() {
		List<Cell> cells = new ArrayList<Cell>();
		for (int x = 0; x < matrix.length; x++) {
			for (int y = 0; y < matrix[0].length; y++) {
				if (matrix[x][y] != null)
					cells.add(matrix[x][y]);
			}
		}
		return cells;
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
		if (x < this.height && x >= 0 && y < this.width && y >= 0) {
			return matrix[x][y];
		}
		return null;
	}

	public void setAsWall(int x, int y) {
		matrix[x][y] = null;
	}

	/**
	 * Gets the goals.
	 * 
	 * @return the goals
	 */
	public Map<Character, List<Cell>> getGoals() {
		return goals;
	}

	public boolean isGoal(Cell cell) {
		return allGoals.contains(cell);
	}

	/**
	 * Gets the agents.
	 * 
	 * @return the agents
	 */
	public Map<Cell, Agent> getAgents() {
		return agentCells;
	}

	public ArrayList<Agent> getAgentsList() {
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
		for (Entry<Cell, Agent> e : agentCells.entrySet()) {
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

	public int getDistance(Cell cellA, Cell cellB) {
		return MapAnalyzer.getDistances().get(cellA).get(cellB).intValue();
	}

	public Set<Cell> getLockedCells() {
		return this.lockedCells;
	}

	public void lockCell(Cell cell) {
		this.lockedCells.add(cell);
	}

	public void unlockCell(Cell cell) {
		this.lockedCells.remove(cell);
	}
	
	public List<Cell> getAllGoals() {
		return allGoals;
	}

	public void finishLoading() {

		Iterator<Agent> agentsIterator = agents.iterator();
		while (agentsIterator.hasNext())
			if (agentsIterator.next() == null)
				agentsIterator.remove();
		logger.config("Cleaned agents: " + agents);
	}

	/**
	 * Execute a pre-analysis of the map.
	 */
	public void executeMapPreAnalysis() {

		MapAnalyzer.getInstance().doAnalysis();
		
		for (int i = 0; i < this.matrix.length; i++) {
			for (int j = 0; j < this.matrix[i].length; j++) {
				if (matrix[i][j] != null) {
					double nbc = MapAnalyzer.getNormalizedBetweennessCentrality().get(matrix[i][j]).doubleValue();
					logger.info(i + " " + j + " : " + nbc);
				}
			}
		}
		
	}
}
