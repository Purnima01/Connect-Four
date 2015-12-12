package model;

import java.util.Arrays;
import java.util.Random;

/**
 * Intentionally not made public so
 * that no malicious moves can be made
 * on the board outside of the model,
 * as player-turn validation occurs
 * in the model.
 */

class GameBoard {
  private char[][] board;
  private final int MAXROWNUM = 6;
  private final int MAXCOLNUM = 7;
  private Random rand = new Random();
  
  public GameBoard() {
    board = new char[MAXROWNUM][MAXCOLNUM];
    initializeBoard();
  }
  
  //Initialize cells to space
  private void initializeBoard() {
    for (int row = 0; row <= (MAXROWNUM - 1); row ++) {
      for (int col = 0; col <= (MAXCOLNUM - 1); col ++) {
        board[row][col] = ' '; 
      }
    }
  }
 
  /**
   * Returns a deep copy of the current state of the board: O(Rows * Columns) operation.
   */
  public char[][] returnBoardCurrentState() {
    char[][] copyOfBoard = new char[MAXROWNUM][MAXCOLNUM];
    for (int i = 0; i < MAXROWNUM; i ++) {
      copyOfBoard[i] = Arrays.copyOf(board[i], MAXCOLNUM);
    }
    return copyOfBoard;
  }
  
  /** 
   * Validates the move. Then determines the 
   * first empty row from bottom 
   * for column and places char there.
   * Returns false if move was invalid.
   */
  public boolean updateBoardForMove(int column, char playerChar) {
    if (!validateMoveForColumn(column)) {
      return false;
    }
    
    int placeInRow = getFirstEmptyRowFromBottom(column);
    board[placeInRow][column] = playerChar;
    return true;
    
  }
  
  /**
   * Determine if the next move leads to a win for computer
   * Followed by updateBoardForMove(...) to make the move.
   * @return column number if win exists
   * @return -1 if no win moves for computer exist
   */
  public int computerFindNextWinMove(char playerColor) {
    for (int col = 0; col <= (MAXCOLNUM - 1); col ++) {
      //If the column is already full, try next column
      if (board[0][col] != ' ') {
        continue;
      }
      
       /*
        Landed on a candidate column. Scan rows bottom-top to find
        the first available row for column. If this row leads to a
        win for the computer, place disk there. Else, go on to next
        column and repeat process.
       */
      for (int row = (MAXROWNUM - 1); row >= 0; row --) {
        if (board[row][col] != ' ') {
          continue;
        }
        if (winPlayer(row, col, playerColor)) {
          return col;
        }
      }
    }
    return -1;
  }
  
  /**
   * Find a non-empty column for move
   */
  public int computerFindNextRegularMove() {
    int colGeneratedAtRandom = rand.nextInt(MAXCOLNUM);
    while (board[0][colGeneratedAtRandom] != ' ') {
      colGeneratedAtRandom = rand.nextInt(MAXCOLNUM);
    }
    return colGeneratedAtRandom;
  }
  
  private boolean validateMoveForColumn(int col) {
    if ((col < 0) || (col > (MAXCOLNUM - 1))) {
      return false;
    }
    if (getFirstEmptyRowFromBottom(col) == -1) {
      return false;
    }
    return true;
  }
  
  /**
   * First empty row for a column
   * @return -1 if column is full
   */
  public int getFirstEmptyRowFromBottom(int col) {
    for (int i = (MAXROWNUM - 1); i >= 0; i --) {
      if (board[i][col] == ' ') {
        return i;
      }
    }
    return -1;
  }
  
  /**
   * Determines if there is a winner after player
   * plays the piece at the new position/column.
   * @return 'n' if no winner found
   * @return corresponding char of player
   * if there is a winner
   */
  public char findWinner(int newPieceRow, int newPieceCol,
      char player1PieceColor, char player2PieceColor, char noWinner) {
    
    if (winPlayer(newPieceRow, newPieceCol, player1PieceColor) && 
        (board[newPieceRow][newPieceCol] == player1PieceColor)) {
      return player1PieceColor;
    } else if (winPlayer(newPieceRow, newPieceCol, player2PieceColor) && 
        (board[newPieceRow][newPieceCol] == player2PieceColor)) {
      return player2PieceColor;
    }
    return noWinner;
  }
  
  private boolean winPlayer(int row, int col, char playerPieceColor) {
    if (verticalWin(row, col, playerPieceColor) || 
        horizontalWin(row, col, playerPieceColor)) {
      return true;
    }
    if (leftDiagonalWin(row, col, playerPieceColor) || 
        rightDiagonalWin(row, col, playerPieceColor)) {
      return true;
    }
    return false;
  }
  
  /*
   * @param row, col : Player places piece at row, col
   * @paran playerPieceColor : color of the player's piece
   * Checks if the next three pieces (below) 
   * current piece belong to the same player.
   * It does not also count the current piece
   * because the same function is used to
   * determine if the next move for computer 
   * in (row,col) will lead to a win. If so, the
   * computer makes the move/chooses (row,col) as 
   * next move. As the computer hasn't already 
   * made a move on row, col before determining this, 
   * we only check the next three pieces.
   * Same goes for the win functions in other directions, too.
   * 
   */
  
  private boolean verticalWin(int row, int col, char playerPieceColor) {
    // Less than 4 of player's pieces in this column
    if (row >= ((MAXROWNUM - 1) - 2)) {
      return false;
    }
    for (int i = (row + 1); i < (row + 4); i ++) {
      // Either the cell is empty or it is occupied by the opponent
      if (board[i][col] != playerPieceColor) {
        return false;
      }
    }
    return true;
  }
  
  private boolean horizontalWin(int row, int col, char playerPieceColor) {
    int leftPlayerPieceCount = 0;
    int rightPlayerPieceCount = 0;
    
    for (int i = col - 1; i >= 0; i --) {
      if (board[row][i] != playerPieceColor) {
        break;
      }
      leftPlayerPieceCount ++;
    }
    
    for (int i = col + 1; i <= (MAXCOLNUM - 1); i ++) {
      if (board[row][i] != playerPieceColor) {
        break;
      }
      rightPlayerPieceCount ++;
    }
    
    /* 
     * excluding the current piece (if present), there should be a total of
     * three or more (neighboring) pieces on the same row for a horizontal win
     */
    if ((leftPlayerPieceCount + rightPlayerPieceCount) >= 3) {
      return true;
    }
    return false;
  }
  
  /**
   * going right from bottom to top
   */
  private boolean rightDiagonalWin(int row, int col, char playerPieceColor) {
    int playerPiecesBottomLeft = countPiecesBottomLeft(row, col, playerPieceColor); 
    int playerPiecesTopRight = countPiecesTopRight(row, col, playerPieceColor);
    if ((playerPiecesBottomLeft + playerPiecesTopRight) >= 3) {
      return true;
    }
    return false;
  }
  
  /**
   * going left from bottom to top
   */
  private boolean leftDiagonalWin(int row, int col, char playerPieceColor) {
    int playerPiecesTopLeft = countPiecesTopLeft(row, col, playerPieceColor);
    int playerPiecesBottomRight = countPiecesBottomRight(row, col, playerPieceColor);
    if ((playerPiecesTopLeft + playerPiecesBottomRight) >= 3) {
      return true;
    }
    return false;
  }
  
  private int countPiecesBottomLeft(int row, int col, char piece) {
    int count = 0;
    row = row + 1;
    col = col - 1;
    while (row <= (MAXROWNUM - 1) && col >= 0) {
      if (board[row][col] != piece) {
        break;
      }
      count ++;
      row ++;
      col --;
    }
    return count;
  }
  
  private int countPiecesTopRight(int row, int col, char piece) {
    int count = 0;
    row = row - 1;
    col = col + 1;
    while (row >= 0 && col <= (MAXCOLNUM - 1)) {
      if (board[row][col] != piece) {
        break;
      }
      count ++;
      row --;
      col ++;
    }
    return count;
  }
  
  private int countPiecesTopLeft(int row, int col, char piece) {
    int count = 0;
    row = row - 1;
    col = col - 1;
    while (row >= 0 && col >= 0) {
      if (board[row][col] != piece) {
        break;
      }
      count ++;
      row --;
      col --;
    }
    return count;
  }
  
  private int countPiecesBottomRight(int row, int col, char piece) {
    int count = 0;
    row = row + 1;
    col = col + 1;
    while (row <= (MAXROWNUM - 1) && col <= (MAXCOLNUM - 1)) {
      if (board[row][col] != piece) {
        break;
      }
      count ++;
      row ++;
      col ++;
    }
    return count;
  }
  
  /**
   * game tied or no winner found but no empty slots left
   */
  
  public boolean isGameOver() {
    for (int i = 0; i <= (MAXCOLNUM - 1); i ++) {
      // If the top row is occupied (but no winner) game is over
      if (board[0][i] == ' ') {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns -1 if column is empty/was not touched even once
   */
  public int getMostRecentRowFilled(int column) {
    //If last row for column is empty, the column was never filled
    if (board[MAXROWNUM - 1][column] == ' ') {
      return -1;
    }
    for (int row = (MAXROWNUM - 1); row >= 0; row --) {
      if (board[row][column] == ' ') {
        return (row + 1);
      }
    }
    return 0;
  }
  
  public int getNumberOfRows() {
    return MAXROWNUM;
  }
  
  public int getNumberOfCols() {
    return MAXCOLNUM;
  }
  
}