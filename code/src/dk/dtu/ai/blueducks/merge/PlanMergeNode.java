package dk.dtu.ai.blueducks.merge;

import dk.dtu.ai.blueducks.map.MultiAgentState;

public class PlanMergeNode {
	private MultiAgentState state;
	private boolean[] activeAgents;
	private PlanMergeNode prev;

	public PlanMergeNode(MultiAgentState state, boolean[] activeAgents, PlanMergeNode prev) {
		this.state = state;
		this.prev = prev;
		this.activeAgents = activeAgents;
	}

	public MultiAgentState getState() {
		return state;
	}
	
	public boolean[] getActiveAgents() {
		return activeAgents;
	}
	
	public PlanMergeNode getPrevNode() {
		return prev;
	}
}
