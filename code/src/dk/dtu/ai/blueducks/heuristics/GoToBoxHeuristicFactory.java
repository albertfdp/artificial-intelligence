package dk.dtu.ai.blueducks.heuristics;

import java.util.logging.Logger;

import dk.dtu.ai.blueducks.goals.GoToBoxGoal;
import dk.dtu.ai.blueducks.map.HeuristicChooser;
import dk.dtu.ai.blueducks.map.LevelMap;
import dk.dtu.ai.blueducks.map.State;

public class GoToBoxHeuristicFactory {
	
	private static final Logger logger = Logger.getLogger(LevelMap.class.getSimpleName());
	
	public Heuristic<State, GoToBoxGoal> getHeuristic() {
		switch(LevelMap.getInstance().hashCode()) {
			case (HeuristicChooser.FOSACrazyMonkeys):
				return new GoToBoxCrazyMonkeysHeuristics();
			case (HeuristicChooser.FOSABlueDucks):
				return new GoToBoxBlueDucksHeuristic();
			default:
				return new GoToBoxBlueDucksHeuristic();
		}
	}
	
	class GoToBoxCrazyMonkeysHeuristics implements Heuristic<State, GoToBoxGoal> {
		
		@Override
		public float getHeuristicValue(State state, GoToBoxGoal goal, State previousState) {
			logger.info("Running in GoToBoxCrazyMonkeys ...");
			return 0;
		}

	}
	
	class GoToBoxBlueDucksHeuristic implements Heuristic<State, GoToBoxGoal> {
			
		@Override
		public float getHeuristicValue(State state, GoToBoxGoal goal, State previousState) {
			logger.info("Running in GoToBoxBlueDucks ...");
			return 0;
		}
	
	}

}
