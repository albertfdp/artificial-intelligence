package dk.dtu.ai.blueducks.goals;

import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.planner.AStarNode;

public class WaitGoal extends Goal {
	public Agent getTargetAgent() {
		return targetAgent;
	}

	private int numberOfTurnsToWait;
	private boolean completed = false;
	private Agent targetAgent;

	@Override
	public boolean isSatisfied(AStarNode node) {
		return completed;
	}

	public void complete() {
		this.completed = true;
	}

	public WaitGoal(int numberOfTurnsToWait, Agent agent) {
		this.targetAgent = agent;
		this.numberOfTurnsToWait = numberOfTurnsToWait;
	}

	public int getNumberOfTurnsToWait() {
		return numberOfTurnsToWait;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + numberOfTurnsToWait;
		result = prime * result + ((targetAgent == null) ? 0 : targetAgent.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WaitGoal other = (WaitGoal) obj;
		if (numberOfTurnsToWait != other.numberOfTurnsToWait)
			return false;
		if (targetAgent == null) {
			if (other.targetAgent != null)
				return false;
		} else if (!targetAgent.equals(other.targetAgent))
			return false;
		return true;
	}

}
