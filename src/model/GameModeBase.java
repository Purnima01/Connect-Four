package model;

import java.util.HashSet;
import java.util.Set;

import utilities.ListenerInformation;
import views.IListener;

/**
 * Parent class of GameModeHumanvsHuman mode and 
 * GameModeHumanvsComputer mode. 
 * 
 * Declared as abstract because some methods 
 * only make sense in the context of either 
 * of the two modes - these methods are 
 * left to be implemented in the subclasses. 
 * 
 * The rest of the methods (ie., almost duplicate code)
 * are implemented here, in the parent.
 */
public abstract class GameModeBase implements IGameMode {
  protected static GameBoard board;
  protected Set<IListener> listeners;
  protected IListener player1;
  protected IListener player2;
  protected int playerCount;
  protected int allowedNumberOfPlayers;
  protected boolean gameInProgress;
  //Unique ID for every non-player listener
  protected int listenerID;
  protected final char noWinner = 'N';
  protected final char player1color = 'Y';
  protected final char player2color = 'R';
  
  protected void initializeGame(int maxNumberOfPlayers) {
    playerCount = 0;
    allowedNumberOfPlayers = maxNumberOfPlayers;
    gameInProgress = false;
    listeners = new HashSet<IListener>();
    player1 = null;
    player2 = null;
    listenerID = 0;
    board = new GameBoard();
  }
 
  protected ListenerInformation nonPlayerListener(IListener newListener) {
    listeners.add(newListener);
    String listenerNumber = String.valueOf(listenerID);
    String id = "Listener" + listenerNumber;
    ListenerInformation newListenerInformation = new ListenerInformation('G', id); 
    listenerID ++;
    return newListenerInformation;
  }
  
  public abstract ListenerInformation registerListener(IListener newListener, boolean isPlayer);
  
  public abstract boolean selectColumnForMove(IListener player, int column);
  
  protected ListenerInformation getPlayerInformation(char playerPieceColor, String playerID) {
    return new ListenerInformation(playerPieceColor, playerID);
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
  
  public void fireGameTied() {
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
  public int getNumberOfCols() {
    return board.getNumberOfCols();
  }

  @Override
  public int getNumberOfRows() {
    return board.getNumberOfRows();
  }
  
  protected boolean gameOverCheckRoutine() {
    if (board.isGameOver()) {
      fireGameTied();
      gameInProgress = false;
      return true;
    }
    return false;
  }
  
  protected boolean winnerCheckRoutine(char winner, char playerPiece) {
    if (winner != noWinner) {
      fireGameWonEvent(playerPiece);
      gameInProgress = false;
      return true;
    }
    return false;
  }

}
