package connectfourapp;

import model.GameMode;
import model.GameModel;
import utilities.Mode;
import views.Player;

/**
 * @author purnima
 * 
 * Enter a column (zero-based indexing) to append disk to.
 * 
 * Game auto-begins once required number of players have registered.
 * Game auto-resets after it is over. 
 * 
 * First player - Yellow. Second player/Computer - Red.
 * 
 * Please enter your column number in the text box. Do not click
 * on any cell in the table.
 */
public class Connect4app {
  private void startGame() {
    GameMode model = GameModel.getGameModel(Mode.COMPUTER);
    new Player(model);
    //new Player(model);
  }
  
  public static void main(String[] args) {
    new Connect4app().startGame();
  }
}