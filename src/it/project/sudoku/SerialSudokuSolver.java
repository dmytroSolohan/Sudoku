package it.project.sudoku;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Locale;

/**
 * The class represent the sudoku as matrix of pieces
 * the classic sequential algorithm
 * @author Dima
 */
public class SerialSudokuSolver 
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
	 * Create a new sudoku, using a specific Piece matrix
	 */
	public SerialSudokuSolver(Piece[][] matrix)
	{
		this.matrix = matrix;
		setMatrix();
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
	 * Return the counter
	 */
	public int getCounter() { return counter; }
	
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
				
				// if there's only one element in list, set it	
				if(getList(i, j).size() == 1)
					setValue(i, j, getList(i, j).iterator().next());
			}	
	}
	
	/*
	 * Linear algorithm
	 * for test simulating
	 */
	public void solve()
	{
		solve(0,0);
	}
	
	/*
	 * The recursive solution algorithm 
	 */
    private int solve(int i, int j) 
    {
    	if (i == 9)
    	{
    		i = 0;
    		if (++j == 9)
    			return ++counter;
    	}
    	
    	if (getValue(i, j) != 0)
    		return solve(i + 1, j);
    	
    	for (int v : getList(i, j))
    		if(isValid(i, j, v))
    		{
    			setValue(i, j, v);
    			solve(i + 1, j);
    			setValue(i, j, 0);
    		}
    	
    	addValue(i, j, 0);
    	return 0;
    }

    /**
     * @return the number of cells set before 
     */
    public int getSettedCells()
    {
    	int k = 0;
    	for(int i = 0; i < matrix.length; i++)
    		for(int j = 0; j < matrix[i].length; j++)
    			if(getValue(i, j) != 0)
    				k++;
    	return k;
    }
    
    /**
     * @return the % of empty cells
     */
    public int getRatio()
    {
    	return Math.round((( (float) getSettedCells() / (matrix.length * matrix.length)) * 100));
    }
    
    /** 
	 * @return the approximate solution space as a String
	 */
	public String getSpace() 
	{
		BigInteger sol = BigInteger.valueOf(1);
		NumberFormat nf = new DecimalFormat("0.######E0", DecimalFormatSymbols.getInstance(Locale.ROOT));
		for (int i = 0; i < matrix.length; i++) 
			for (int j = 0; j < matrix[i].length; j++)
				if(getValue(i, j) == 0)
					sol = sol.multiply(BigInteger.valueOf(getList(i, j).size()));
		return nf.format(sol);
	}
    
    /**
     * @return the number of empty cells  
     */
    public int getEmptyCells()
    {
    	int k = 0;
    	for(int i = 0; i < matrix.length; i++)
    		for(int j = 0; j < matrix[i].length; j++)
    			if(getValue(i, j) == 0)
    				k++;
    	return k;
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
