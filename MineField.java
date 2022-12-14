package PA3;

import java.util.Random;
// Name: Ruitao Jiang
// USC NetID: ruitaoji
// CS 455 PA3
// Fall 2021


/** 
   MineField
      class with locations of mines for a game.
      This class is mutable, because we sometimes need to change it once it's created.
      mutators: populateMineField, resetEmpty
      includes convenience method to tell the number of mines adjacent to a location.
 */
public class MineField {
   
   // <put instance variables here>
   private boolean[][] mineData;
   // Number of mines
   private int numOfMines;
   // To generate the location of mines if the mine field is random
   private Random mineGenerator;
   
   
   /**
      Create a minefield with same dimensions as the given array, and populate it with the mines in the array
      such that if mineData[row][col] is true, then hasMine(row,col) will be true and vice versa.  numMines() for
      this minefield will corresponds to the number of 'true' values in mineData.
      @param mineData  the data for the mines; must have at least one row and one col,
                       and must be rectangular (i.e., every row is the same length)
    */
	public MineField(boolean[][] mineData) {
		this.mineData = new boolean[mineData.length][mineData[0].length];
		for (int i = 0; i < mineData.length; i++) {
			for (int j = 0; j < mineData[0].length; j++) {
				this.mineData[i][j] = mineData[i][j];
			}
		}
		for (boolean[] col : mineData) {
			for (boolean data : col) {
				if (data == true) {
					numOfMines++;
				}
			}
		}
	}
   
   
   /**
      Create an empty minefield (i.e. no mines anywhere), that may later have numMines mines (once 
      populateMineField is called on this object).  Until populateMineField is called on such a MineField, 
      numMines() will not correspond to the number of mines currently in the MineField.
      @param numRows  number of rows this minefield will have, must be positive
      @param numCols  number of columns this minefield will have, must be positive
      @param numMines   number of mines this minefield will have,  once we populate it.
      PRE: numRows > 0 and numCols > 0 and 0 <= numMines < (1/3 of total number of field locations). 
    */
	public MineField(int numRows, int numCols, int numMines) {
		mineData = new boolean[numRows][numCols];
		numOfMines = numMines;
	}

   /**
      Removes any current mines on the minefield, and puts numMines() mines in random locations on the minefield,
      ensuring that no mine is placed at (row, col).
      @param row the row of the location to avoid placing a mine
      @param col the column of the location to avoid placing a mine
      PRE: inRange(row, col) and numMines() < (1/3 * numRows() * numCols())
    */
	public void populateMineField(int row, int col) {
		resetEmpty();
		mineGenerator = new Random();
		int rowOfMine = 0;
		int colOfMine = 0;
		for (int i = 0; i < numOfMines; i++) {
			do {
				rowOfMine = mineGenerator.nextInt(mineData.length);
				colOfMine = mineGenerator.nextInt(mineData[0].length);
			}
			// If the chosen position is (row, col) or already has a mine, then choose again
			while ((rowOfMine == row && colOfMine == col) || mineData[rowOfMine][colOfMine] == true);
			mineData[rowOfMine][colOfMine] = true;
		}
	}
   
   
   /**
      Reset the minefield to all empty squares.  This does not affect numMines(), numRows() or numCols()
      Thus, after this call, the actual number of mines in the minefield does not match numMines().  
      Note: This is the state a minefield created with the three-arg constructor is in 
         at the beginning of a game.
    */
	public void resetEmpty() {
		// Reset the mine field to empty only when it is a random mine field
			mineData = new boolean[mineData.length][mineData[0].length];
		
	}
   
  /**
     Returns the number of mines adjacent to the specified mine location (not counting a possible 
     mine at (row, col) itself).
     Diagonals are also considered adjacent, so the return value will be in the range [0,8]
     @param row  row of the location to check
     @param col  column of the location to check
     @return  the number of mines adjacent to the square at (row, col)
     PRE: inRange(row, col)
   */
	public int numAdjacentMines(int row, int col) {
		// Number of mines adjacent to (row, col), including itself
		int count = 0;
		for (int i = row - 1; i <= row + 1; i++) {
			for (int j = col - 1; j <= col + 1; j++) {
				if (findMine(i, j)) {
					count++;
				}
			}
		}
		// Subtract one if there is a mine at (row, col)
		if (mineData[row][col]) {
			count -= 1;
		}
		return count;
	}
   
   
   /**
      Returns true iff (row,col) is a valid field location.  Row numbers and column numbers
      start from 0.
      @param row  row of the location to consider
      @param col  column of the location to consider
      @return whether (row, col) is a valid field location
   */
	public boolean inRange(int row, int col) {
		boolean isRowValid = (row >= 0 && row < mineData.length);
		boolean isColValid = (col >= 0 && col < mineData[0].length);
		return isRowValid && isColValid;
	}
   
   /**
      Returns the number of rows in the field.
      @return number of rows in the field
   */  
	public int numRows() {
		return mineData.length;
	}
      
   /**
      Returns the number of columns in the field.
      @return number of columns in the field
   */    
	public int numCols() {
		return mineData[0].length;
	}

   /**
      Returns whether there is a mine in this square
      @param row  row of the location to check
      @param col  column of the location to check
      @return whether there is a mine in this square
      PRE: inRange(row, col)   
   */    
	public boolean hasMine(int row, int col) {
		return mineData[row][col];
	}
   
   /**
      Returns the number of mines you can have in this minefield.  For mines created with the 3-arg constructor,
      some of the time this value does not match the actual number of mines currently on the field.  See doc for that
      constructor, resetEmpty, and populateMineField for more details.
    * @return
    */
	public int numMines() {
		return numOfMines;
	}

   
   // <put private methods here>
   /**
    * Helper method to find whether there is a mine in the loacation(row, col)
    * @param row  row of the location to check
    * @param col  column of the location to check
    * @return  whether there is a mine in this square, and will 
    *          return false if the row or column is out of range
    */
	private boolean findMine(int row, int col) {
		if (row < 0 || row >= mineData.length) {
			return false;
		}
		if (col < 0 || col >= mineData[0].length) {
			return false;
		}
		return mineData[row][col];
	}
         
}

