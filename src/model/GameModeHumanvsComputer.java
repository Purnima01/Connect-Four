package model;
import utilities.ListenerInformation;
import views.IListener;

/**
 * Computer plays as the second player - R.
 */
public class GameModeHumanvsComputer extends GameModeBase {
  private static GameModeHumanvsComputer singleInstance = null;
  private char computerPiece = player2color;
  
  private GameModeHumanvsComputer() {
    initializeGame(1);
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
    if (!isPlayer) {
      return nonPlayerListener(newListener);
    }
    if (playerCount >= allowedNumberOfPlayers) {
      return null;
    }
    if (listeners.contains(newListener)) {
      return null;
    }
    ListenerInformation newInformation = new ListenerInformation(player1color, "Player1");
    player1 = newListener;
    //Set gameInProgress once we have the player
    gameInProgress = true;
    
    listeners.add(newListener);
    startGameNotify(player1);
    playerCount ++;
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
  
}
