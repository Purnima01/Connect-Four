package model;
import java.util.HashSet;
import java.util.Set;

import utilities.ListenerInformation;
import views.IListener;

public class GameModeHumanvsHuman implements IGameMode {
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
  private final char noWinner = 'N';
  private final char player1color = 'Y';
  private final char player2color = 'R';
  
  private void initializeGame() {
    playerCount = 0;
    allowedNumberOfPlayers = 2;
    gameInProgress = false;
    listeners = new HashSet<IListener>();
    player1 = null;
    player2 = null;
    turnOfPlayer = null;
    listenerID = 0;
    board = new GameBoard();
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
  
  private GameModeHumanvsHuman() {
    initializeGame();
  }
  
  /**
   * Singleton class - use getModelInstance(...) to get instance.
   * @return - instance of the model.
   */
  public static GameModeHumanvsHuman getModelInstance() {
    if (singleInstance != null) {
      return singleInstance;
    }
    singleInstance = new GameModeHumanvsHuman();
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
      playerPieceColor = player1color;
      playerID = "Player1";
      player1 = newListener;
      turnOfPlayer = player1;
    } else {
      playerPieceColor = player2color;
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
    char pieceColor = ((player == player1) ? player1color: player2color);
    boolean result = board.updateBoardForMove(column, pieceColor);
    if (!result) {
      player.invalidMoveNotify();
      return false;
    } else {
      fireMoveMadeEvent();
      toggleTurnOfPlayer();
    }
    int row = board.getMostRecentRowFilled(column);
    char winner = board.findWinner(row, column, player1color, player2color, noWinner);
    if (winner != noWinner) {
      fireGameWonEvent(player.getPiece());
      gameInProgress = false;
      return true;
    }
    //Tied/No winner but board is full
    if (board.isGameOver()) {
      fireGameOverEvent();
      gameInProgress = false;
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
      view.gameTied();
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
  public char[][] getCopyOfGameBoard() {
    return board.returnBoardCurrentState();
  }
  
  @Override
  public int getNumberOfCols() {
    return board.getNumberOfCols();
  }

  @Override
  public int getNumberOfRows() {
    return board.getNumberOfRows();
  }
}
