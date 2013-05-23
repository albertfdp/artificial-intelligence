/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.heuristics;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.goals.DeliverBoxGoal;
import dk.dtu.ai.blueducks.goals.Goal;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.CellEdge;
import dk.dtu.ai.blueducks.map.LevelMap;
import dk.dtu.ai.blueducks.map.MapAnalyzer;

/**
 * The Heuristic used for the GoalPlanner to pick a goal.
 */
public class GoalPlannerHeuristic {
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(LevelMap.class.getSimpleName());

	public static float getHeuristicValue(Agent agent, Goal goal) {
		return chooseNotBlockingGoal(agent, goal);
		//return chooseClosestGoal(agent, goal);
//		DeliverBoxGoal dbg =(DeliverBoxGoal) goal;
//		Cell agentCell = LevelMap.getInstance().getCellForAgent(agent);
//		Cell boxCell = LevelMap.getInstance().getCurrentState().getCellForBox(dbg.getWhat());
//		Cell goalCell = dbg.getTo();
//		
//		// We init distance with a number large enough to avoid chosing unachievable cells
//		float distance = 100000;
//		
//		// If the box exists, the distance would be Manhattan/Dijkstra (depends on the size the map)
//		if (boxCell != null) { 
//			distance = LevelMap.getInstance().getDistance(agentCell, boxCell);
//			distance += LevelMap.getInstance().getDistance(boxCell, goalCell);
//		}
//		
//		// Avoid selecting locked cells, if possible
//		if (LevelMap.getInstance().getLockedCells().contains(boxCell)) {
//			distance += distance * 10;
//		}
//		/*
//		List<Cell> goalNeighbours = goalCell.getCellNeighbours();
//		for (Cell neighbour : goalNeighbours) {
//			Map<Cell, Double> nbc = LevelMap.getInstance().getBetweenessCentrality();
//				
//			if (neighbour != null && (MapAnalyzer.getInstance().getDegreeCentrality().get(goalCell) == 2)
//					&& LevelMap.getInstance().isGoal(neighbour) && (nbc.get(neighbour) <= nbc.get(goalCell))) {
//				distance += (100 * nbc.get(goalCell));
//			}
//		}*/
//		
//		// TODO: Add more stuff
//		logger.info(agent.toString() + " cost of " + goal.toString() + " => " + distance);
//		return distance;
	}
	
	private static float chooseClosestGoal(Agent agent, Goal goal) {
		float h = 0;
		
		float a0 = 1;
		float a1 = 1;
		
		DeliverBoxGoal deliverBoxGoal = (DeliverBoxGoal) goal;
		
		// cell where the agent is located now
		Cell agentCell = LevelMap.getInstance().getCellForAgent(agent);
		
		// cell of the box to be delivered
		Cell boxCell = LevelMap.getInstance().getCurrentState().getCellForBox(deliverBoxGoal.getWhat());
		
		// cell of the goal
		Cell goalCell = deliverBoxGoal.getTo();
		
		float distanceAgentBox = LevelMap.getInstance().getDistance(agentCell, boxCell);
		float distanceBoxGoal = LevelMap.getInstance().getDistance(boxCell, goalCell);
		
		h = a0 * distanceAgentBox + a1 * distanceBoxGoal;
		
		return h;
	}
	
	private static float chooseNotBlockingGoal(Agent agent, Goal goal) {
		
		float h = 0;
		
		float a0 = 1;
		float a1 = 1;
		float a2 = 1;
		float a3 = 100;
		float a4 = 1;
		
		DeliverBoxGoal deliverBoxGoal = (DeliverBoxGoal) goal;
		
		// cell where the agent is located now
		Cell agentCell = LevelMap.getInstance().getCellForAgent(agent);
		
		// cell of the box to be delivered
		Cell boxCell = LevelMap.getInstance().getCurrentState().getCellForBox(deliverBoxGoal.getWhat());
		
		// cell of the goal
		Cell goalCell = deliverBoxGoal.getTo();
		
		float distanceAgentBox = LevelMap.getInstance().getDistance(agentCell, boxCell);
		float distanceBoxGoal = LevelMap.getInstance().getDistance(boxCell, goalCell);
		
		float betweennessBox = (float) LevelMap.getInstance().getBetweenessCentrality().get(boxCell).floatValue();
		float betweennessGoal = (float) LevelMap.getInstance().getBetweenessCentrality().get(goalCell).floatValue();
		
		// check if resolving this goal, locks other goals
		float lockingGoal = 0;
		for (Cell neighbour : goalCell.getCellNeighbours()) {
			Map<Cell, Double> nbc = LevelMap.getInstance().getBetweenessCentrality();
			if (neighbour != null && (LevelMap.getInstance().isGoal(neighbour)) && (nbc.get(neighbour) <= nbc.get(goalCell))) {
				lockingGoal = nbc.get(goalCell).floatValue();
			}
		}
		
		h = a0 * distanceAgentBox + a1 * betweennessBox + a2 * distanceBoxGoal + a3 * betweennessGoal + a4 * lockingGoal;
		logger.info(deliverBoxGoal.toString() + " => " + h);
//		logger.info("h = " + " + " + a0 * distanceAgentBox + " + " 
//				+ a1 * betweennessBox + " + " + a2 * distanceBoxGoal + " + " 
//				+ a3 * betweennessGoal + " + " + a4 * lockingGoal * distanceBoxGoal + " = " + h);
		
		return h;
	}
	
}