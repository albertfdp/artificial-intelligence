package dk.dtu.ai.blueducks;

import dk.dtu.ai.blueducks.map.MultiAgentState;

public class PlanNode {
	private MultiAgentState state;
	private boolean[] activeAgents;
	private PlanNode prev;

	public PlanNode(MultiAgentState state, boolean[] activeAgents, PlanNode prev) {
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
	
	public PlanNode getPrevNode() {
		return prev;
	}
}
