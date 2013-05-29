package dk.dtu.ai.blueducks.merge;

import java.util.Arrays;

import dk.dtu.ai.blueducks.actions.Action;
import dk.dtu.ai.blueducks.map.MultiAgentState;

public class PlanMergeNode {
	private MultiAgentState state;
	private Action[] prevActions;
	private PlanMergeNode prev;
	private short[] agentCurrentActionIndex; 

	public PlanMergeNode(MultiAgentState state, Action[] prevActions, PlanMergeNode prev, short[] agentCurrentActionIndex) {
		this.state = state;
		this.prev = prev;
		this.prevActions=prevActions;
		this.agentCurrentActionIndex = agentCurrentActionIndex;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(agentCurrentActionIndex);
		result = prime * result + ((state == null) ? 0 : state.hashCode());
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
		PlanMergeNode other = (PlanMergeNode) obj;
		if (!Arrays.equals(agentCurrentActionIndex, other.agentCurrentActionIndex))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}

	public short[] getAgentCurrentActionIndex() {
		return agentCurrentActionIndex;
	}

	public MultiAgentState getState() {
		return state;
	}

	public Action[] getPrevActions() {
		return prevActions;
	}

	public PlanMergeNode getPrevNode() {
		return prev;
	}

	@Override
	public String toString() {
		return "PlanMergeNode [prevActions=" + Arrays.toString(prevActions) + "]";
	}
}
