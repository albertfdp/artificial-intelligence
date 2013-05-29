/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.merge;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.actions.Action;
import dk.dtu.ai.blueducks.actions.MoveAction;
import dk.dtu.ai.blueducks.actions.PullAction;
import dk.dtu.ai.blueducks.actions.PushAction;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.State;

public class PlanAnalyzer {

	/**
	 * Detect unmergeable plans.
	 *
	 * @param plan1 the plan1
	 * @param plan2 the plan2
	 * @param affResources1 the aff resources1
	 * @param affResources2 the aff resources2
	 * @return true, if the plans are unmeargeable
	 */
	public boolean detectUnmergeablePlans(List<State> plan1, List<State> plan2,
			PlanAffectedResources affResources1, PlanAffectedResources affResources2) {
		
		//si no se puede hacer ninguna accion
		//blub la bla ble bli
		return false;
	}
	
	/**
	 * Identify the conflict area in the plans of 2 agents.
	 *
	 * @param plan1 the plan1
	 * @param plan2 the plan2
	 * @param affResources1 the aff resources1
	 * @param affResources2 the aff resources2
	 * @return the conflict area
	 */
	public ConflictArea identifyConflictArea(List<State> plan1, List<State> plan2,
			PlanAffectedResources affResources1, PlanAffectedResources affResources2) {
		int i = 0, j = 0;
		ConflictArea conflictArea = new ConflictArea();
		Set<Cell> affectedCells2 = affResources2.getAffectedCells();
		
		//find in the two plans the beginning of the common area
		//while the agent cell of the state in plan 1 is not affected by plan 2 or the cells are different
		while((i < plan1.size()) && (j < plan2.size()) && 
				((!affectedCells2.contains(plan1.get(i).getAgentCell())) || 
						(plan2.get(j).getAgentCell() != plan1.get(i).getAgentCell()))){
			
			Cell agentCell1 = plan1.get(i).getAgentCell(); 
			
			//next state of plan 1
			if (!affectedCells2.contains(plan1.get(i).getAgentCell()))
				i++;
			//next state of plan 2
			if (plan2.get(j).getAgentCell() != agentCell1)
				j++;	
		}

		//there is no common path
		if((i == plan1.size()) || (j == plan2.size()))
			return null;
		
		conflictArea.agent1ConflictStart = i;
		conflictArea.agent2ConflictStart = j;
		
		//find the rest of the path
		while(plan2.get(j).getAgentCell() == plan1.get(i).getAgentCell()){
			i++;
			j++;
		}
		 
		conflictArea.agent1ConflictEnd = i;
		conflictArea.agent2ConflictEnd = j;
		
		return conflictArea;
	}
	
	public static class ConflictArea{
		/** The agent1 conflict start index. */
		int agent1ConflictStart;
		/** The agent2 conflict start index. */
		int agent2ConflictStart;
		/** The agent1 conflict end index. */
		int agent1ConflictEnd;
		/** The agent2 conflict end index. */
		int agent2ConflictEnd;
	}
	

	public static <T> int getIntersection(Set<T> set1, Set<T> set2) {
		boolean set1IsLarger = set1.size() > set2.size();
		Set<T> cloneSet = new HashSet<T>(set1IsLarger ? set2 : set1);
		cloneSet.retainAll(set1IsLarger ? set1 : set2);
		return cloneSet.size();
	}

	public static List<CommonResources> findPossibleConflictingAgents(
			List<PlanAffectedResources> affectedResources) {
		List<CommonResources> commonResources = new LinkedList<CommonResources>();

		// for each agent we test to see if he has something in common with other CommonResources
		// from other agents
		for (short agent = 0; agent < affectedResources.size(); agent++) {

			// making a CommonResouces object for each agent at the beginning
			CommonResources agentCommonResources = new CommonResources(agent, affectedResources.get(agent));

			Iterator<CommonResources> it = commonResources.iterator();

			// we iterate through all the CommonResources
			while (it.hasNext()) {
				// extract the current CommonResources
				CommonResources c = it.next();

				// figuring out if the resources of the agent conflict with resources from other
				// groups or agents
				if (agentCommonResources.intersectWith(c)) {
					// if yes- we merge them
					agentCommonResources.merge(c);
					// and remove the previous ones cause now we have them in the new agent's group
					it.remove();
				}
			}

			// adding to the list of CommonResources the newly created CommonResources which is made
			// out of all the groups which conflict with the current agent
			commonResources.add(agentCommonResources);
		}
		return commonResources;
	}

	public static class CommonResources {
		public LinkedHashSet<Short> agents;
		public PlanAffectedResources resources;

		public CommonResources(short a, PlanAffectedResources res) {
			this.agents = new LinkedHashSet<Short>();
			this.agents.add(a);

			this.resources = res;
		}

		public void merge(CommonResources other) {
			agents.addAll(other.agents);
			resources.affectedCells.addAll(other.resources.affectedCells);
			resources.affectedBoxes.addAll(other.resources.affectedBoxes);
		}

		/**
		 * @param other - CommonResources
		 * @return true if the 2 have Cells or Boxes in common in the resources field and false
		 *         otherwise
		 */
		public boolean intersectWith(CommonResources other) {
			// if there are common cells it will return true
			if (getIntersection(this.resources.affectedCells, other.resources.affectedCells) != 0)
				return true;
			// if there are common boxes it will return true
			if (getIntersection(this.resources.affectedBoxes, other.resources.affectedBoxes) != 0)
				return true;

			// there are no common resources so it returns false
			return false;
		}

		@Override
		public String toString() {
			return "CommonResources [agents=" + agents + ", resources=" + resources + "]";
		}
	}
}
