package views;

public interface IListener {
  /**Notify beginning of game (all required players registered)*/
  void gameStartSignal(IListener firstPlayerToGo);
  
  /**Notify invalid turn - current player with turn can play again*/
  void invalidTurnNotify();
  
  /**Column number out of bounds or column is full*/
  void invalidMoveNotify();
  
  /**Waiting for required number of players to register*/
  void gameNotInProgressNotify();
  
  /**Send notification and board for views to update their UI*/
  void correctMoveNotify();
  
  /**Game has been won - display message accordingly*/
  void gameWinNotify(char winnerPiece);
  
  /**Game ended without a winner*/
  void gameTied();
  
  /**Get player's disk color*/
  char getPiece();
  
  /**Get view's ID*/
  String getId();
  
  /**Close frame on resetGame(..) before game has been won or ended*/
  void shutFrame();
}
