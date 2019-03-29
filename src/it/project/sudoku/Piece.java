package it.project.sudoku;

import java.util.HashSet;

/**
 * The class that represent a single cell of matrix
 * @author Dima
 */
public class Piece 
{
	/*
	 * The value of the cell
	 */
	private int value;
	
	/*
	 * The list of candidates values
	 */
	private HashSet<Integer> s;
	
	/**
	 * @param value The value of the cell
	 */
	public Piece(int value)
	{
		this.value = value;
		s = new HashSet<>();
	}
	
	/*
	 * Set the cell value
	 */
	public void setValue(int value) { this.value = value; }
	
	/*
	 * Get the cell value
	 */
	public int getValue() { return value; }

	/**
	 * Method used to add a candidate to the list
	 * @param s The value to add to the list
	 */
	public void addNumList(int s) { this.s.add(s); }
	
	/**
	 * Method used to set a list to the candidate list
	 * @param list The list to add
	 */
	public void setList(HashSet<Integer> list) {this.s = list;}
	
	/*
	 * List of possible candidates
	 */
	public HashSet<Integer> getList() { return new HashSet<Integer>(s); }
	
	/*
	 * Delete all elements in list 
	 */
	public void delElements() { s = new HashSet<>(); }
	
	/*
	 * Delete specific element from list
	 */
	public void delElement(Integer i) { if(s.contains(i)) s.remove(i); }
	
	@Override
	public String toString() { return value == 0 ? "." : value + ""; }
	
}
