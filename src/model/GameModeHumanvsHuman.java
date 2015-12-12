package model;
import utilities.ListenerInformation;
import views.IListener;

public class GameModeHumanvsHuman extends GameModeBase {
  private static GameModeHumanvsHuman singleInstance = null;
  private IListener turnOfPlayer = null;
  
  private GameModeHumanvsHuman() {
    initializeGame(2);
  }
  
  /**
   * Singleton class - use getModelInstance() to get instance.
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
    if (!isPlayer) {
      return nonPlayerListener(newListener);
    }
    if (playerCount >= allowedNumberOfPlayers) {
      return null;
    }
    if (listeners.contains(newListener)) {
      return null;
    }
    ListenerInformation newInformation = null;
    if (player1 == null) {
      newInformation = getPlayerInformation(player1color, "Player1");
      player1 = newListener;
      turnOfPlayer = player1;
    } else {
      newInformation = getPlayerInformation(player2color, "Player2");
      player2 = newListener;
      //Set gameInProgress once we have both players
      gameInProgress = true;
    }
    listeners.add(newListener);
    playerCount ++;
    
    if (playerCount == allowedNumberOfPlayers) {
      startGameNotify(player1);
    }
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
    if (winnerCheckRoutine(winner, player.getPiece())) {
      return true;
    }
    if (gameOverCheckRoutine()) {
      return true;
    }
    return true;
  }

  void toggleTurnOfPlayer() {
    if (turnOfPlayer == player1) {
      turnOfPlayer = player2;
    } else {
      turnOfPlayer = player1;
    }
  }
}
