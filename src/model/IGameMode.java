package model;
import utilities.ListenerInformation;
import views.IListener;
/**
 * Implementation of the observer pattern to allow
 * listeners (view objects) to communicate with the 
 * model (GameMode) object. Model object is a singleton.
 */

public interface IGameMode {
  /**
   * Used to register listeners with the model.
   * Assumes game begins once expected number of players
   * according to mode have registered.
   */
  ListenerInformation registerListener(IListener listener, boolean isPlayer);
  
  /**
   * Select the intended column to place piece at
   * @return true if move was successful, false otherwise
   */
  boolean selectColumnForMove(IListener player, int col);

  /**
   * Returns a deep copy of the game board as of current state
   */
  char[][] getCopyOfGameBoard();
  
  /**
   * Get number of columns on board
   */
  int getNumberOfCols();
  
  /**
   * Get number of rows on board
   */
  int getNumberOfRows();

  /**
   * Get number of players 
   */
  int getNumberOfPlayers();

}
