/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.planner;

import java.util.ArrayList;
import java.util.List;

import dk.dtu.ai.blueducks.interfaces.HeuristicInterface;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.LevelMap;

public class PathPlanner{
	
	private LevelMap map;
	private HeuristicInterface<Cell> heuristic;
	private List<Cell> path;
	private List<Cell> closedSet;
	private List<Cell> openSet;
	
	/**
	 * Instantiates a new planner.
	 *
	 * @param map the map
	 * @param heuristic the heuristic
	 */
	public PathPlanner(LevelMap map, HeuristicInterface<Cell> heuristic) {
		super();
		this.map = map;
		this.heuristic = heuristic;
	}
	

	public List<Cell> getBestPath(Cell beginCell, Cell endCell) {
		// empty set for already explored cells
		closedSet = new ArrayList<>();
		
		// in the "to explore" set there is in the beginning just the cell with which we begin
		openSet = new ArrayList<>();
		openSet.add(beginCell);
		
		return path;
	}
}
