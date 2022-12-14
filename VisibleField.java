package PA3;


// Name: Ruitao Jiang
// USC NetID: ruitaoji
// CS 455 PA3
// Fall 2021


/**
  VisibleField class
  This is the data that's being displayed at any one point in the game (i.e., visible field, because it's what the
  user can see about the minefield). Client can call getStatus(row, col) for any square.
  It actually has data about the whole current state of the game, including  
  the underlying mine field (getMineField()).  Other accessors related to game status: numMinesLeft(), isGameOver().
  It also has mutators related to actions the player could do (resetGameDisplay(), cycleGuess(), uncover()),
  and changes the game state accordingly.
  
  It, along with the MineField (accessible in mineField instance variable), forms
  the Model for the game application, whereas GameBoardPanel is the View and Controller, in the MVC design pattern.
  It contains the MineField that it's partially displaying.  That MineField can be accessed (or modified) from 
  outside this class via the getMineField accessor.  
 */
public class VisibleField {
   // ----------------------------------------------------------   
   // The following public constants (plus numbers mentioned in comments below) are the possible states of one
   // location (a "square") in the visible field (all are values that can be returned by public method 
   // getStatus(row, col)).
   
   // The following are the covered states (all negative values):
   public static final int COVERED = -1;   // initial value of all squares
   public static final int MINE_GUESS = -2;
   public static final int QUESTION = -3;

   // The following are the uncovered states (all non-negative values):
   
   // values in the range [0,8] corresponds to number of mines adjacent to this square
   
   public static final int MINE = 9;      // this loc is a mine that hasn't been guessed already (end of losing game)
   public static final int INCORRECT_GUESS = 10;  // is displayed a specific way at the end of losing game
   public static final int EXPLODED_MINE = 11;   // the one you uncovered by mistake (that caused you to lose)
   // ----------------------------------------------------------   
  
   // <put instance variables here>
   // Store information of mine location
   private MineField mineField;
   // Store different status of all the squares
   private int[][] visibleField;
   // Indicate whether the game is over
   private boolean isGameOver;
   

   /**
      Create a visible field that has the given underlying mineField.
      The initial state will have all the mines covered up, no mines guessed, and the game
      not over.
      @param mineField  the minefield to use for for this VisibleField
    */
	public VisibleField(MineField mineField) {
		this.mineField = mineField;
		visibleField = new int[mineField.numRows()][mineField.numCols()];
		for (int i = 0; i < mineField.numRows(); i++) {
			for (int j = 0; j < mineField.numCols(); j++) {
				visibleField[i][j] = -1;
			}
		}
	}
   
   
   /**
      Reset the object to its initial state (see constructor comments), using the same underlying
      MineField. 
   */     
	public void resetGameDisplay() {
		visibleField = new int[mineField.numRows()][mineField.numCols()];
		for (int i = 0; i < mineField.numRows(); i++) {
			for (int j = 0; j < mineField.numCols(); j++) {
				visibleField[i][j] = -1;
			}
		}
		isGameOver = false;
	}
  
   
   /**
      Returns a reference to the mineField that this VisibleField "covers"
      @return the minefield
    */
	public MineField getMineField() {
		return mineField;
	}
   
   /**
      Returns the visible status of the square indicated.
      @param row  row of the square
      @param col  col of the square
      @return the status of the square at location (row, col).  See the public constants at the beginning of the class
      for the possible values that may be returned, and their meanings.
      PRE: getMineField().inRange(row, col)
    */
	public int getStatus(int row, int col) {
		return visibleField[row][col];
	}

   
   /**
      Returns the the number of mines left to guess.  This has nothing to do with whether the mines guessed are correct
      or not.  Just gives the user an indication of how many more mines the user might want to guess.  This value can
      be negative, if they have guessed more than the number of mines in the minefield.     
      @return the number of mines left to guess.
    */
	public int numMinesLeft() {
		int numMinesGussed = 0;
		for (int[] row : visibleField) {
			for (int data : row) {
				if (data == MINE_GUESS) {
					numMinesGussed++;
				}
			}
		}
		return mineField.numMines() - numMinesGussed;
	}
 
   
   /**
      Cycles through covered states for a square, updating number of guesses as necessary.  Call on a COVERED square
      changes its status to MINE_GUESS; call on a MINE_GUESS square changes it to QUESTION;  call on a QUESTION square
      changes it to COVERED again; call on an uncovered square has no effect.  
      @param row  row of the square
      @param col  col of the square
      PRE: getMineField().inRange(row, col)
    */
	public void cycleGuess(int row, int col) {
		if (visibleField[row][col] == QUESTION) {
			visibleField[row][col] = COVERED;
			return;
		}
		if (visibleField[row][col] == COVERED || visibleField[row][col] == MINE_GUESS) {
			visibleField[row][col] -= 1;
		}
	}

   
   /**
      Uncovers this square and returns false iff you uncover a mine here.
      If the square wasn't a mine or adjacent to a mine it also uncovers all the squares in 
      the neighboring area that are also not next to any mines, possibly uncovering a large region.
      Any mine-adjacent squares you reach will also be uncovered, and form 
      (possibly along with parts of the edge of the whole field) the boundary of this region.
      Does not uncover, or keep searching through, squares that have the status MINE_GUESS. 
      Note: this action may cause the game to end: either in a win (opened all the non-mine squares)
      or a loss (opened a mine).
      @param row  of the square
      @param col  of the square
      @return false   iff you uncover a mine at (row, col)
      PRE: getMineField().inRange(row, col)
    */
	public boolean uncover(int row, int col) {
		// Can not uncover a square that has status MINE_GUESS
		if (visibleField[row][col] == MINE_GUESS) {
			return false;
		}
		// If the square has a mine and is not a status of MINE_GUESS
		// Lose the game
		if (mineField.hasMine(row, col)) {
			visibleField[row][col] = EXPLODED_MINE;
			isGameOver = true;
			updateAfterLoss();
			return false;
		}
		// If the square has status covered or questioned and has none adjacent mines
		// Uncovers all the squares in the neighboring area that are also not next to any mines
		if ((visibleField[row][col] == COVERED || visibleField[row][col] == QUESTION)
				&& mineField.numAdjacentMines(row, col) == 0) {
			uncoverAll(row, col);
		}
		// If the square has status covered or questioned and has at least one adjacent mines
		// Just uncover itself
		if ((visibleField[row][col] == COVERED || visibleField[row][col] == QUESTION)
				&& mineField.numAdjacentMines(row, col) > 0) {
			visibleField[row][col] = mineField.numAdjacentMines(row, col);
		}
		if (isWinning()) {
			isGameOver = true;
			updateAfterWin();
		}
		return true;

	}
    
   /**
      Returns whether the game is over.
      (Note: This is not a mutator.)
      @return whether game over
    */
	public boolean isGameOver() {
		return isGameOver;
	}
   
   /**
      Returns whether this square has been uncovered.  (i.e., is in any one of the uncovered states, 
      vs. any one of the covered states).
      @param row of the square
      @param col of the square
      @return whether the square is uncovered
      PRE: getMineField().inRange(row, col)
    */
	public boolean isUncovered(int row, int col) {
		return visibleField[row][col] >= 0;
	}
   
 
   // <put private methods here>
   /**
    * Uncovers all the squares in the neighboring area of (row, col) that are also not next to any mines
    * @param row  row of the square
    * @param col  col of the square
    */
	private void uncoverAll(int row, int col) {
		for (int i = row - 1; i <= row + 1; i++) {
			for (int j = col - 1; j <= col + 1; j++) {
				// If square(i, j) is in range
				if (mineField.inRange(i, j)) {
					// If square(i, j) is covered or question and has none adjacent mines, recursive call
					if (mineField.numAdjacentMines(i, j) == 0
							&& (visibleField[i][j] == COVERED || visibleField[i][j] == QUESTION)) {
						visibleField[i][j] = 0;
						uncoverAll(i, j);
						continue;
					}
					// If square(i, j) is covered or question but has at least one adjacent mine
					if (mineField.numAdjacentMines(i, j) > 0
							&& (visibleField[i][j] == COVERED || visibleField[i][j] == QUESTION)) {
						visibleField[i][j] = mineField.numAdjacentMines(i, j);
					}
				}
			}
		}
	}
   // Check whether the user is winning the game
	private boolean isWinning() {
		for (int i = 0; i < visibleField.length; i++) {
			for (int j = 0; j < visibleField[0].length; j++) {
				// All the squares that has no mines should be uncovered
				if (!mineField.hasMine(i, j) && visibleField[i][j] < 0) {
					return false;
				}
			}
		}
		return true;
	}
   
   // Update the visibleField when winning the game
   // Turn all the squares that have mine to yellow
	private void updateAfterWin() {
		for (int i = 0; i < visibleField.length; i++) {
			for (int j = 0; j < visibleField[0].length; j++) {
				if (mineField.hasMine(i, j)) {
					visibleField[i][j] = MINE_GUESS;
				}
			}
		}
	}
   
   // Update the visibleField when losing the game
	private void updateAfterLoss() {
		for (int i = 0; i < visibleField.length; i++) {
			for (int j = 0; j < visibleField[0].length; j++) {
				// Wrong guess
				if (visibleField[i][j] == MINE_GUESS && !mineField.hasMine(i, j)) {
					visibleField[i][j] = INCORRECT_GUESS;
					continue;
				}
				// Mines haven't been guessed
				if ((visibleField[i][j] == COVERED || visibleField[i][j] == QUESTION) && mineField.hasMine(i, j)) {
					visibleField[i][j] = MINE;
				}
			}
		}
	}
      
}
