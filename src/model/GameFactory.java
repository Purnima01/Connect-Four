package model;
import utilities.Mode;
/**
 * Factory class to get the appropriate type of object for mode.
 */

public class GameFactory {
  
  public static IGameMode getGameModel(Mode human) {
    IGameMode model = null;
    if (human == Mode.HUMAN) {
      model = GameModeHumanvsHuman.getModelInstance();
    } else if (human == Mode.COMPUTER){
      model = GameModeHumanvsComputer.getModelInstance();
    }
    return model;
  }
}
