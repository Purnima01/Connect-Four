package model;
import java.util.HashSet;
import java.util.Set;

import utilities.ListenerInformation;
import views.IListener;

/**
 * Similar to HumanvsHuman for the most part. However,
 * I decided to create a separate class so I can implement
 * the factory pattern according to the user's preference.
 * 
 * Computer plays as the second player - R.
 */
public class GameModeHumanvsComputer implements GameMode {
  private static GameModeHumanvsComputer singleInstance = null;
  private static GameBoard board;
  private Set<IListener> listeners;
  private IListener player1;
  private int playerCount;
  private int allowedNumberOfPlayers;
  private boolean gameInProgress;
  //Unique ID for every non-player listener
  private int listenerID;
  private char computerPiece = 'R';
  
  //Does not initialize the board. Must be done separately on reset.
  private void initializeGame() {
    playerCount = 0;
    allowedNumberOfPlayers = 1;
    gameInProgress = false;
    listeners = new HashSet<IListener>();
    player1 = null;
    listenerID = 0;
  }
  
  /**
   * Returns number of columns on the board
   */
  public int getNumberOfColumnsOnBoard() {
    return board.getNumberOfCols();
  }
  
  private GameModeHumanvsComputer(int numberOfColumns) {
    initializeGame();
    if (numberOfColumns == 7) {
      board = new GameBoard.Builder().build();
    } else {
      board = new GameBoard.Builder().numberOfColumns(numberOfColumns).build();
    }
  }
  
  /**
   * Singleton class - use getModelInstance(...) to get instance.
   * @param numberOfColums - how many columns in game. Number of rows
   * is fixed at 6. If number of columns is less than one, it will
   * result in the default value of 7.
   * @return - instance of the model. To reset, call resetGame(...).
   */
  public static GameModeHumanvsComputer getModelInstance(int numberOfColumns) {
    if (singleInstance != null) {
      return singleInstance;
    }
    singleInstance = new GameModeHumanvsComputer(numberOfColumns);
    return singleInstance;
  }
  
  @Override
  public ListenerInformation registerListener(IListener newListener, boolean isPlayer) {
    if (newListener == null) {
      return null;
    }
    
    //Non-player
    if (!isPlayer) {
      listeners.add(newListener);
      String listenerNumber = String.valueOf(listenerID);
      String id = "Listener" + listenerNumber;
      ListenerInformation newListenerInformation = new ListenerInformation('G', id); 
      listenerID ++;
      return newListenerInformation;
    }
    //Already have two players - malicious addition.
    if (playerCount >= allowedNumberOfPlayers) {
      return null;
    }
    //Prevent duplicate registration of a listener
    if (listeners.contains(newListener)) {
      return null;
    }
    //Valid player addition
    char playerPieceColor = 'Y';
    String playerID;
    playerID = "Player1";
    player1 = newListener;
    //Set gameInProgress once we have the player
    gameInProgress = true;
    
    listeners.add(newListener);
    startGameNotify(player1);
    playerCount ++;
    ListenerInformation newInformation = new ListenerInformation(playerPieceColor, playerID);
    return newInformation;
  }
  
  @Override
  public boolean selectColumnForMove(IListener player, int column) {
    if (!gameInProgress) {
      player.gameNotInProgressNotify();
      return false;
    }
    char humanPieceColor = 'Y';
    boolean result = board.updateBoardForMove(column, humanPieceColor);
    if (!result) {
      player.invalidMoveNotify();
      return false;
    } else {
      fireMoveMadeEvent();
    }
    
    //Is there a winner or is game over after human's move?
    int row = board.getMostRecentRowFilled(column);
    char winner = board.findWinner(row, column);
    if (winnerCheckRoutine(winner, humanPieceColor)) {
      return true;
    }
    if (gameOverCheckRoutine()) {
      return true;
    }
    
    //Computer makes a move: check if win move exists. if not, make regular move.
    int computerSelectedColumn = computerMove(computerPiece);
    board.updateBoardForMove(computerSelectedColumn, computerPiece);
    fireMoveMadeEvent();
    
    //Is there a winner or is game over after computer move?
    int rowFilled = board.getMostRecentRowFilled(computerSelectedColumn);
    winner = board.findWinner(rowFilled, computerSelectedColumn);
    if (winnerCheckRoutine(winner, computerPiece)) {
      return true;
    }
    if (gameOverCheckRoutine()) {
      return true;
    }
    return true;
  }
  
  private int computerMove(char computerPiece) {
    int computerSelectedColumn = board.computerFindNextWinMove(computerPiece);
    //No win move exists for computer - choose regular move
    if (computerSelectedColumn == -1) {
      computerSelectedColumn = board.computerFindNextRegularMove();
    }
    return computerSelectedColumn;
  }
  
  private boolean winnerCheckRoutine(char winner, char playerPiece) {
    if (winner != 'N') {
      fireGameWonEvent(playerPiece);
      gameInProgress = false;
      resetGame(false);
      return true;
    }
    return false;
  }
  
  /*For testing purposes only*/
  public boolean getGameInProgress() {
    return gameInProgress;
  }
  
  private boolean gameOverCheckRoutine() {
    if (board.isGameOver()) {
      fireGameOverEvent();
      gameInProgress = false;
      resetGame(false);
      return true;
    }
    return false;
  }
  
  public void fireMoveMadeEvent() {
    for (IListener view : listeners) {
      view.correctMoveNotify();
    }
  }
  
  public void fireGameWonEvent(char winnerPiece) {
    for (IListener view : listeners) {
      view.gameWinNotify(winnerPiece);
    }
  }
  
  public void fireGameOverEvent() {
    for (IListener view : listeners) {
      view.gameOverNotify();
    }
  }
  
  public void startGameNotify(IListener firstPlayerToGo) {
    for (IListener view : listeners) {
      view.gameStartSignal(firstPlayerToGo);
    }
  }
  
  @Override
  public boolean resetGame(boolean shutWindow) {
    if (gameInProgress) {
      throw new UnsupportedOperationException();
    }
    if (shutWindow) {
      for (IListener listener : listeners) {
        listener.shutFrame();
      }
    }
    initializeGame();
    board.resetBoard();
    return true;
  }

  @Override
  public char[][] getCopyOfGameBoard() {
    return board.returnBoardCurrentState();
  }
  
  @Override
  public int getNumberOfPlayers() {
    return allowedNumberOfPlayers;
  }

  void setSingleInstanceToNull() {
    singleInstance = null;
  }
  
}
