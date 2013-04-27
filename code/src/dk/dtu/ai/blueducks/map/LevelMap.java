/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.Box;

/**
 * The Map of a Level.
 */
public class LevelMap {

	private static LevelMap map;
	private Cell[][] matrix;
	private Map<Cell, Agent> agents;
	
	private static final Logger logger = Logger.getLogger(LevelMap.class.getSimpleName());
	private Map<Character, Cell> goals;
	private int width;
	private int height;
	private State currentState;
	
	private LevelMap() {
		agents = new HashMap<Cell, Agent>(10);
		goals = new HashMap<Character, Cell>();
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
			//TODO: instantiate currentStaet
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
	 * @param cell
	 * @param x
	 * @param y
	 * @param id
	 */
	public void addGoalCell(Cell cell, int x, int y, char id) {
		this.addCell(cell, x, y);
		goals.put(id, cell);
	}

	
	public void attachAgent(Cell cell, Agent agent) {
		agents.put(cell, agent);
	}
	
	public void attachBox(Cell cell, Box box) {
		this.currentState.addBox(cell, box);
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
	public Map<Character, Cell> getGoals() {
		// TODO Auto-generated method stub
		return goals;
	}

	public Cell[][] getMatrix() {
		return matrix;
	}

	public Map<Cell, Agent> getAgents() {
		return agents;
	}

	public int getWidth() {
		return width;
	}


	public int getHeight() {
		return height;
	}

	public Cell getCellForAgent(Agent agent){
		for(Entry<Cell, Agent> e : agents.entrySet()){
			if(e.getValue() == agent){
				return e.getKey();
			}
		}
		return null;
	}

	public State getCurrentState() {
		return currentState;
	}

}
