/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks;


public class Box {
	
	private char id;
	private String color;
	
	@Override
	public String toString() {
		return "Box [id=" + id + ", color=" + color + "]";
	}

	public Box(char id, String color) 
	{
		this.id = id;
		this.color = color;
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

}
