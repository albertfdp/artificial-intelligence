package client;

import java.util.LinkedList;

public class Command {
	static {
		LinkedList< Command > cmds = new LinkedList< Command >();
		for ( dir d : dir.values() ) {
			cmds.add( new Command( d ) );
		}

		for ( dir d1 : dir.values() ) {
			for ( dir d2 : dir.values() ) {
				if ( !Command.isOpposite( d1, d2 ) ) {
					cmds.add( new Command( "Push", d1, d2 ) );
				}
			}
		}
		for ( dir d1 : dir.values() ) {
			for ( dir d2 : dir.values() ) {
				if ( d1 != d2 ) {
					cmds.add( new Command( "Pull", d1, d2 ) );
				}
			}
		}

		every = cmds.toArray( new Command[0] );
	}

	public final static Command[] every;

	private static boolean isOpposite( dir d1, dir d2 ) {
		return d1.ordinal() + d2.ordinal() == 3;
	}

	// Order of enum important for determining opposites
	protected static enum dir {
		N, W, E, S
	};

	protected String cmd;
	protected dir dir1;
	protected dir dir2;

	public Command( dir d ) {
		cmd = "Move";
		dir1 = d;
	}

	public Command( String s, dir d1, dir d2 ) {
		cmd = s;
		dir1 = d1;
		dir2 = d2;
	}

	public String toString() {
		if ( dir2 == null )
			return cmd + "(" + dir1 + ")";

		return cmd + "(" + dir1 + "," + dir2 + ")";
	}

}
