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
 * or a tie. Default - HumanVsComputer. For HumanVsHuman, change the mode
 * to Mode.HUMAN in startGame() and create another player GUI
 * (new PlayerGUI(model);). In HumanVsHuman, two windows are displayed -
 * one for each player. Do not forget to move the second window, as it
 * is displayed on top of the first.
 * 
 * First PlayerGUI - Yellow. Second PlayerGUI/Computer - Red.
 * 
 * Please enter your column number (0-6) in the text box; do not click
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
