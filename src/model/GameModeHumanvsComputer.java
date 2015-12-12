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
public class GameModeHumanvsComputer implements IGameMode {
  private static GameModeHumanvsComputer singleInstance = null;
  private static GameBoard board;
  private Set<IListener> listeners;
  private IListener player1;
  private int playerCount;
  private int allowedNumberOfPlayers;
  private boolean gameInProgress;
  //Unique ID for every non-player listener
  private int listenerID;
  private char player1color = 'Y';
  private char player2color = 'R';
  private char computerPiece = player2color;
  private final char noWinner = 'N';
  
  private void initializeGame() {
    playerCount = 0;
    allowedNumberOfPlayers = 1;
    gameInProgress = false;
    listeners = new HashSet<IListener>();
    player1 = null;
    listenerID = 0;
    board = new GameBoard();
  }
  
  private GameModeHumanvsComputer() {
    initializeGame();
  }
  
  /**
   * Singleton class - use getModelInstance() to get instance.
   * @return - instance of the model.
   */
  public static GameModeHumanvsComputer getModelInstance() {
    if (singleInstance != null) {
      return singleInstance;
    }
    singleInstance = new GameModeHumanvsComputer();
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
    char playerPieceColor = player1color;
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
    char humanPieceColor = player1color;
    boolean result = board.updateBoardForMove(column, humanPieceColor);
    if (!result) {
      player.invalidMoveNotify();
      return false;
    } else {
      fireMoveMadeEvent();
    }
    
    //Is there a winner or is game over after human's move?
    int row = board.getMostRecentRowFilled(column);
    char winner = board.findWinner(row, column, player1color, player2color, noWinner);
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
    winner = board.findWinner(rowFilled, computerSelectedColumn, 
        player1color, player2color, noWinner);
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
    if (winner != noWinner) {
      fireGameWonEvent(playerPiece);
      gameInProgress = false;
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
      view.gameTied();
    }
  }
  
  public void startGameNotify(IListener firstPlayerToGo) {
    for (IListener view : listeners) {
      view.gameStartSignal(firstPlayerToGo);
    }
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

  @Override
  public int getNumberOfCols() {
    return board.getNumberOfCols();
  }

  @Override
  public int getNumberOfRows() {
    return board.getNumberOfRows();
  }
  
}
