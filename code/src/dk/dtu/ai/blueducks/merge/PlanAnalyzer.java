package dk.dtu.ai.blueducks.merge;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class PlanAnalyzer {

	public static <T> int getIntersection(Set<T> set1, Set<T> set2) {
		boolean set1IsLarger = set1.size() > set2.size();
		Set<T> cloneSet = new HashSet<T>(set1IsLarger ? set2 : set1);
		cloneSet.retainAll(set1IsLarger ? set1 : set2);
		return cloneSet.size();
	}

	public static List<CommonResources> findPossibleConflictingAgents(List<PlanAffectedResources> affectedResources) {
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
