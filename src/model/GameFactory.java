package model;
import utilities.Mode;
/**
 * Factory class to get the appropriate type of object for mode.
 */

public class GameFactory {
  
  public static IGameMode getGameModel(Mode mode) {
    IGameMode model = null;
    if (mode == Mode.HUMAN) {
      model = GameModeHumanvsHuman.getModelInstance();
    } else if (mode == Mode.COMPUTER){
      model = GameModeHumanvsComputer.getModelInstance();
    } else {
      throw new NullPointerException();
    }
    return model;
  }
}
