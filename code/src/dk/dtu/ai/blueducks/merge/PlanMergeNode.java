package dk.dtu.ai.blueducks.merge;

import java.util.Arrays;

import dk.dtu.ai.blueducks.actions.Action;
import dk.dtu.ai.blueducks.map.MultiAgentState;

public class PlanMergeNode {
	private MultiAgentState state;
	private Action[] prevActions;
	private PlanMergeNode prev;

	public PlanMergeNode(MultiAgentState state, Action[] prevActions, PlanMergeNode prev) {
		this.state = state;
		this.prev = prev;
		this.prevActions=prevActions;
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
