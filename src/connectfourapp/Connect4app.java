package connectfourapp;

import model.GameFactory;
import model.IGameMode;
import utilities.Mode;
import views.PlayerGUI;

/**
 * 
 * Enter a column (zero-based indexing) to append disk to.
 * 
 * Game auto-begins once required number of players have registered.
 * Game auto-resets after it is over, which happens if there is a winner
 * or a tie. 
 * 
 * First PlayerGUI - Yellow. Second PlayerGUI/Computer - Red.
 * 
 * Please enter your column number in the text box; do not click
 * on any cell in the table.
 * 
 */
public class Connect4app {
  private void startGame() {
    IGameMode model = GameFactory.getGameModel(Mode.COMPUTER);
    new PlayerGUI(model);
  }
  
  public static void main(String[] args) {
    new Connect4app().startGame();
  }
}
