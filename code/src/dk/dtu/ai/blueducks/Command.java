package dk.dtu.ai.blueducks;

import java.util.ArrayList;

import dk.dtu.ai.blueducks.domain.Direction;

public class Command {
	
	private final static String COMMAND_MOVE = "Move";
	private final static String COMMAND_PUSH = "Push";
	private final static String COMMAND_PULL = "Pull";
	private final static String COMMAND_NO_OPERATION = "NoOp";
	
	public static String Move (Direction dir) {
		return COMMAND_MOVE + "(" + dir + ")";
	}
	
	public static String Push (Direction from, Direction to) {
		return COMMAND_PUSH + "(" + from + "," + to + ")";
	}
	
	public static String Pull (Direction from, Direction to) {
		return COMMAND_PULL + "(" + from + "," + to + ")";
	}
	
	public static String NoOperation() {
		return COMMAND_NO_OPERATION;
	}
	
	public static boolean isOpposite (Direction d1, Direction d2) {
		return d1.ordinal() + d2.ordinal() == 3;
	}
	
	public static String sendAction(ArrayList<String> actions) {
		
		String jointAction = "[";
		
		for (String action : actions) {
			jointAction += action + ",";
		}
		
		// remove the last comma
		if (jointAction.length() > 1)
			jointAction = jointAction.substring(0, jointAction.length() - 1);
		
		jointAction += "]";
		
		return jointAction;
	}

}
