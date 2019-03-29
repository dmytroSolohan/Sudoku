package it.project.sudoku;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.RecursiveTask;

/**
  *	The class represent the sudoku as matrix of pieces
  * My implementation of parallel algorithm
  * @author Dima
  */
@SuppressWarnings("serial")
public class ParallelSudokuSolver extends RecursiveTask<Integer>
{

	/*
	 *The sudoku matrix 
	 */
	private Piece[][] matrix;
	
	/*
	 * Counter of possible solutions 
	 */
	private int counter;
	
	/*
	 * Variables used to maintain the recursion
	 * Position of the cell in a matrix
	 */
	private int x, y;
	
	/*
	 * Constructor for SudokuPar
	 */
	public ParallelSudokuSolver(Piece[][] matrix)
	{
		this.matrix = matrix;
		this.x = 0;
		this.y = 0;
		setMatrix();
	}
	
	/*
	 * Constructor for SudokuPar
	 * need to create a copy of the matrix 
	 */
	private ParallelSudokuSolver(Piece[][] matrix, int x, int y, int value)
	{
		this.matrix = deepCopy(matrix);
		setValue(x, y, value);
		this.x = x;
		this.y = y;
		delElements();
	}
	
	/*
	 * Set the specific value to a position in the matrix
	 */
	private void setValue(int x, int y, int value) { matrix[x][y].setValue(value); }
	
	/*
	 * Get the specific value from the position in the matrix
	 */
	private int getValue(int x, int y) { return matrix[x][y].getValue(); }
	
	/*
	 * Add a value to a cell
	 */
	private void addValue(int x, int y, int n) { matrix[x][y].addNumList(n);}
	
	/*
	 * Get the list of possible number in the cell
	 */
	private HashSet<Integer> getList(int x, int y) { return new HashSet<>(matrix[x][y].getList()); }
	
	/*
	 * Check if the number is in the row or col
	 */
	private boolean checkRowCol(int x, int y, int n)
	{
		for(int i = 0; i < matrix.length; i++)
			if(getValue(x, i) == n || getValue(i, y) == n)
				return true;
		return false;
	}	
	
	/*
	 * Check if the number is in the 3x3 box
	 */
	private boolean checkBox(int x, int y, int n)
	{
		int r = x - x % 3;
		int c = y - y % 3;
		
		for (int i = r; i < r + 3; i++)
			for (int j = c; j < c + 3; j++)
				if (getValue(i, j) == n)
					return true;
		return false;
	}
	
	/*
	 * Check if checkRow, checkRowCol are true all together
	 * If the number is present in col, row and box
	 */
	private boolean isValid(int x, int y, int n)
	{
		return !checkRowCol(x, y, n) && !checkBox(x, y, n);
	}
	
	/*
	 * For all the cells set the possible number list
	 */
	private void setMatrix()
	{
		for(int i = 0; i < matrix.length; i++)
			for(int j = 0; j < matrix[i].length; j++)
			{
				matrix[i][j].delElements();
				for(int k = 1; k < 10; k++)
					if(getValue(i, j) != 0)
						break;
					else if(isValid(i, j, k))
						addValue(i, j, k);
			}
	}
	
	@Override
	protected Integer compute() 
	{
		ArrayList<ParallelSudokuSolver> threads = new ArrayList<>();
    	
    	if(!findNext())
			return ++counter;
		        
    	// cutOff 
    	if(this.x == 0 && this.y > 3)
    	{
    		SerialSudokuSolver s = new SerialSudokuSolver(matrix);
    		s.solve();
    		return s.getCounter() + counter;
    	}
    	
    	//list of candidate numbers
    	ArrayList<Integer> list = new ArrayList<>(getList(this.x, this.y));
		//createList
    	for(int i = 0; i < list.size(); i++)
    		threads.add(new ParallelSudokuSolver(matrix, this.x, this.y, list.get(i)));
    	
    	ParallelSudokuSolver p = threads.get(threads.size() - 1);
    	threads.remove(threads.size() - 1);
    	
    	for(ParallelSudokuSolver thread : threads)
    		thread.fork();
    	
    	counter += p.compute();
    	
        //join
        for(ParallelSudokuSolver thread: threads)
        	counter += thread.join();
		
		return counter;
	}
	
	/*
	 * Delete elements from Row, col and 3x3
	 */
	private void delElements()
	{
		int value = getValue(this.x, this.y);
		
		//del from row and col
		for(int i = 0; i < matrix.length; i++)
		{
			if(getValue(this.x, i) != 0 || getValue(i, this.y) != 0)
				continue;
			matrix[this.x][i].delElement(value);
			matrix[i][this.y].delElement(value);
			// if only one number in list, set it 
			if(getList(this.x, i).size() == 1)
			{
				setValue(this.x, i, getList(this.x, i).iterator().next());
				matrix[this.x][i].delElements();
			}
			if(getList(i, this.y).size() == 1)
			{
				setValue(i, this.y, getList(i, this.y).iterator().next());
				matrix[i][this.y].delElements();
			}
		}
		
		//del from 3x3 submatrix
		int r = this.x - this.x % 3;
		int c = this.y - this.y % 3;
		
		for (int i = r; i < r + 3; i++)
			for (int j = c; j < c + 3; j++)
			{
				if(getValue(i, j) != 0)
					continue;
				matrix[i][j].delElement(value);
				// if only one number in list, set it
				if(getList(i, j).size() == 1)
				{
					setValue(i, j, getList(i, j).iterator().next());
					matrix[i][j].delElements();
				}
			}
	}
		
	/*
	 * find if there is a next empty cell
	 */
	private boolean findNext()
	{
		for(int i = 0; i < matrix.length; i++)
			for(int j = 0; j < matrix[i].length; j++)
				if(getValue(i, j) == 0)
				{
					this.x = i;
					this.y = j;
					return true;
				}
		return false;
	}
	
	/*
	 * Used to copy a matrix deeply
	 */
	private Piece[][] deepCopy(Piece[][] matrix)
	{
		Piece[][] s = new Piece[9][9];
		for(int i = 0; i < s.length; i++)
			for(int j = 0; j < s[i].length; j++)
			{
				s[i][j] = new Piece(matrix[i][j].getValue());
				if(matrix[i][j].getValue() == 0)
					s[i][j].setList(matrix[i][j].getList());
			}
		return s;
	}

	/*
	 * The toString version of sudoku
	 */
	@Override
	public String toString()
	{		
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < matrix.length; i++)
		{ 
			if(i%3 == 0)
				sb.append(" -----------\n");
			for(int j = 0; j < matrix[i].length; j++)
			{
				if(j%3 == 0)
					sb.append("|");
				sb.append(matrix[i][j].toString());
			}
			sb.append("|\n");
		}
		sb.append(" -----------\n");	
		return sb.toString();
	}

}
