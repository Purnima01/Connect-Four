package utilities;
/**
 * Class used by registerListener in GameMode to return key
 * details to the listeners. Particularly useful for players
 * of the game.
 */

public class ListenerInformation {
  private char colorOfPiece;
  private String listenerId;
  public ListenerInformation(char pieceColor, String id) {
    colorOfPiece = pieceColor;
    listenerId = id;
  }
  public char getPieceColor() {
    return colorOfPiece;
  }
  public String getId() {
    return listenerId;
  }
}
