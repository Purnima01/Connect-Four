package model;
import utilities.Mode;
/**
 * Factory class to get the appropriate type of class for mode.
 * Number of columns can only be set the first time as model is singleton.
 */

public class GameModel {
  public static GameMode getGameModel(Mode mode, int numCols) {
    GameMode model = null;
    if (mode == Mode.HUMAN) {
      model = GameModeHumanvsHuman.getModelInstance(numCols);
    } else if (mode == Mode.COMPUTER){
      model = GameModeHumanvsComputer.getModelInstance(numCols);
    }
    return model;
  }
  
  public static GameMode getGameModel(Mode human) {
    GameMode model = null;
    if (human == Mode.HUMAN) {
      model = GameModeHumanvsHuman.getModelInstance(7);
    } else if (human == Mode.COMPUTER){
      model = GameModeHumanvsComputer.getModelInstance(7);
    }
    return model;
  }
}