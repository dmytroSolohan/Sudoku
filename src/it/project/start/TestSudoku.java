package it.project.start;

import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.concurrent.ForkJoinPool;

import it.project.parser.FromFileToSudoku;
import it.project.sudoku.ParallelSudokuSolver;
import it.project.sudoku.Piece;
import it.project.sudoku.SerialSudokuSolver;

/**
 * Main class to Test all Sudoku 
 * @author Dima
 */
public class TestSudoku
{
	public static final int N = 9;
	
	public static void main(String[] args) 
	{
		if (args.length < 1 || args.length > 1)
			{
				System.err.println("Usage: java -jar sudoku.jar path");
				System.exit(1);
			}
		exec(args[0]);	        
	}
	
	private static void exec(String str) 
	{
		Piece[][] matrix = FromFileToSudoku.fromFile(Paths.get(str));
		SerialSudokuSolver s = new SerialSudokuSolver(matrix);
		
		System.out.println("/---------> " + str);
		System.out.println("|Empty cells    " + s.getEmptyCells());
		System.out.println("|Fill ratio     " + s.getRatio() + "%");
		
		System.out.println("|Space solution " + s.getSpace() + "\n|");
		System.out.println("|Solving in SEQUENTIAL ...");
		long start = System.currentTimeMillis();
			
	    s.solve();
	    long end = System.currentTimeMillis();
	        
	    long seqTime = end - start;
	    
	    
	    //sequential execution time
	    System.out.println("|Solutions:     " + s.getCounter());
	    System.out.println("|Seq Exec Time: " + seqTime + "ms");
	    System.out.print  ("|Seq Exec Time: " + format(seqTime) + "\n");
	    
	    System.out.println("|\n|Solving in PARALLEL ...");
	    
	    start = System.currentTimeMillis();
	        
	    ForkJoinPool fp = new ForkJoinPool();
	    int counter = fp.invoke(new ParallelSudokuSolver(matrix));
	        
	    end = System.currentTimeMillis();
	        
	    long parTime = end - start;
	    
	    // parallel execution time    
	    System.out.println("|Solutions:     " + counter);
	    System.out.println("|Par Exec Time: " + parTime + "ms");
	    System.out.println("|Par Exec Time: " + format(parTime));
	    
	    //speedup
	    DecimalFormat f = new DecimalFormat("#0.00");
	    System.out.println("|SpeedUp:       " + f.format((double) seqTime/parTime) + "\n --------->\n");
	}
	
	/*
	 * Transform milliseconds in minutes, seconds and milliseconds
	 */
	private static String format(long m) 
	{
		String s = "";
		long min = (m / 1000) / 60;
		long sec = (m / 1000) % 60;
		long ms = m % 1000;

		s += min + "m ";
		s += sec + "s ";
		s += ms + "ms";
		return s;
	}
	
}
