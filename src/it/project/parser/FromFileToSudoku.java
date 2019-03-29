package it.project.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import it.project.sudoku.Piece;

/**
 * The parser class which helps to set a 
 * Sudoku matrix 9x9, from .txt file 
 * @author Dima
 */
public class FromFileToSudoku
{
	
	/**
	 * Static method which read a file and parse it
	 * @param path The path of the .txt file
	 * @return return the matrix in Sudoku object
	 */
	public static Piece[][] fromFile(Path path) 
	{
		Piece[][] matrix = new Piece[9][9];

		//read file
		try (BufferedReader br = Files.newBufferedReader(path)) 
		{
			String[] line;
			int i = 0;
			while(br.ready() && i < 9)
			{
				line = br.readLine().split("");
				//set matrix
				for(int j = 0; j < 9; j++)
					matrix[i][j] = line[j].equals(".") ? new Piece(0) : new Piece(Integer.parseInt(line[j]));
				i++;
			}
		} catch (IOException e) 
			{
				e.printStackTrace();
			}
		return matrix;
	}
}
