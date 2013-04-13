package dk.dtu.ai.blueducks.domain;

import dk.dtu.ai.blueducks.Command;

public class Agent {
	
	private char id;
	private String color;
	private int row;
	private int column;
	
	public Agent(char id, String color) {
		this.id = id;
		this.color = color;
	}
	
	public Agent(char id, String color, int row, int column) {
		this.id = id;
		this.color = color;
		this.row = row;
		this.column = column;
	}
	
	public String move(Direction dir) {
		return Command.Move(dir);
	}
	
	public String nextAction() {
		return move(Direction.N);
	}

	public char getId() {
		return id;
	}

	public void setId(char id) {
		this.id = id;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}
	
	

}
