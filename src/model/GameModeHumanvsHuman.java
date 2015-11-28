package model;
import java.util.HashSet;
import java.util.Set;

import utilities.ListenerInformation;
import views.IListener;

public class GameModeHumanvsHuman implements GameMode {
  private static GameModeHumanvsHuman singleInstance = null;
  private static GameBoard board;
  private Set<IListener> listeners;
  private IListener player1;
  private IListener player2;
  private int playerCount;
  private int allowedNumberOfPlayers;
  private boolean gameInProgress;
  private IListener turnOfPlayer;
  //Unique ID for every non-player listener
  private int listenerID;
  
  //Does not initialize the board. Must be done separately on reset.
  private void initializeGame() {
    playerCount = 0;
    allowedNumberOfPlayers = 2;
    gameInProgress = false;
    listeners = new HashSet<IListener>();
    player1 = null;
    player2 = null;
    turnOfPlayer = null;
    listenerID = 0;
  }
  
  /**For testing purposes only*/
  void setSingleInstanceToNull() {
    singleInstance = null;
  }
  
  /**
   * Returns number of players - for testing 
   */
  @Override
  public int getNumberOfPlayers() {
    return allowedNumberOfPlayers;
  }
  
  /*For tests only*/
  public int getNumberOfRegisteredPlayers() {
    return playerCount;
  }
  
  /**
   * Returns number of columns on the board
   */
  public int getNumberOfColumnsOnBoard() {
    return board.getNumberOfCols();
  }
  
  private GameModeHumanvsHuman(int numberOfColumns) {
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
  public static GameModeHumanvsHuman getModelInstance(int numberOfColumns) {
    if (singleInstance != null) {
      return singleInstance;
    }
    singleInstance = new GameModeHumanvsHuman(numberOfColumns);
    return singleInstance;
  }

  /**
   * Non-players assigned G (gray) to distinguish them from players,
   * who are assigned Y -for first player- and R -for the second.
   */
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
    char playerPieceColor;
    String playerID;
    if (player1 == null) {
      playerPieceColor = 'Y';
      playerID = "Player1";
      player1 = newListener;
      turnOfPlayer = player1;
    } else {
      playerPieceColor = 'R';
      playerID = "player2";
      player2 = newListener;
      //Set gameInProgress once we have both players
      gameInProgress = true;
    }
    listeners.add(newListener);
    playerCount ++;
    
    if (playerCount == allowedNumberOfPlayers) {
      startGameNotify(player1);
    }
    
    ListenerInformation newInformation = new ListenerInformation(playerPieceColor, playerID);
    return newInformation;
  }
  
  @Override
  public boolean selectColumnForMove(IListener player, int column) {
    if (!gameInProgress) {
      player.gameNotInProgressNotify();
      return false;
    }
    if (turnOfPlayer != player) {
      player.invalidTurnNotify();
      return false;
    }
    char pieceColor = ((player == player1) ? 'Y' : 'R');
    boolean result = board.updateBoardForMove(column, pieceColor);
    if (!result) {
      player.invalidMoveNotify();
      return false;
    } else {
      fireMoveMadeEvent();
      toggleTurnOfPlayer();
    }
    int row = board.getMostRecentRowFilled(column);
    char winner = board.findWinner(row, column);
    if (winner != 'N') {
      fireGameWonEvent(player.getPiece());
      gameInProgress = false;
      resetGame(false);
      return true;
    }
    //Tied/No winner but board is full
    if (board.isGameOver()) {
      fireGameOverEvent();
      gameInProgress = false;
      resetGame(false);
      return true;
    }
    return true;
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

  void toggleTurnOfPlayer() {
    if (turnOfPlayer == player1) {
      turnOfPlayer = player2;
    } else {
      turnOfPlayer = player1;
    }
  }
  
  /*Only for testing toggleTurnOfPlayer()*/
  IListener getCurrentPlayer() {
    return turnOfPlayer;
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
  
}
