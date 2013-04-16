/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.map;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.Box;


/**
 * The Map of a Level.
 */
public class Map {

	private static Map map;
	private Cell[][] matrix;
	private List<Agent> agents;
	private List<Box> boxes;
	private static Logger logger = Logger.getAnonymousLogger();
	private Map() {
		agents = new ArrayList<Agent>();
		boxes = new ArrayList<Box>();
	}

	/**
	 * Gets the single instance of Map. {@link Map#init(int)} must be called before using this
	 * method.
	 * 
	 * @return single instance of Map
	 */
	public static Map getInstance() {
		if(Map.map == null) {
			Map.map = new Map();
		}
		return Map.map;
	}

	/**
	 * Inits the map for a given size.
	 * 
	 * @param size the size
	 */
	public void init(int width, int height) {
		this.matrix = new Cell[height][];
		int i;
		for(i = 0; i < height; i++){
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
		if(matrix == null || x >= matrix.length || matrix[0] == null || y >= matrix[0].length){
			logger.severe("Class Map: could not add cell on position " + x + " " + y + " as they execeed the declared size of the matrix");
			return;
		}
		matrix[x][y] = cell;
		if(cell.getContent() != null){
			if(cell.getContent() instanceof Box){
				boxes.add((Box)cell.getContent());
			}
			if(cell.getContent() instanceof Agent){
				agents.add((Agent)cell.getContent());
			}
		}
		
	}

	/**
	 * Gets the cell at a given position.
	 * 
	 * @return the cell at
	 */
	public Cell getCellAt(int x, int y) {
		if(matrix != null && x < matrix.length && matrix[0] != null && y < matrix[0].length){
			return matrix[x][y];
		}
		return null;
	}
}
