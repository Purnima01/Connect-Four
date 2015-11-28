package views;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import model.GameMode;
import utilities.ListenerInformation;


public class Player implements IListener {

  private GameMode model;
  private char piece;
  private String id;
  
  private ImageIcon emptyIcon;
  private ImageIcon opponentColorIcon;
  private ImageIcon myColorIcon;
  private ImageIcon blackIcon;
  private JFrame frame = new JFrame("CONNECT FOUR");
  private JTextField textField = new JTextField();
  private JTable table;
  private JButton reset = new JButton("Reset Game");
  private JButton submit = new JButton("Submit Column");
  
  public Player(GameMode model) {
    this.model = model;
    ListenerInformation playerInfo = model.registerListener(this, true);
    
    /*If more than allowed number of players register or player re-registers,
      do not allow - throw exception to indicate this*/
    if (playerInfo == null) {
      throw new UnsupportedOperationException();
    }
    
    piece = playerInfo.getPieceColor();
    id = playerInfo.getId();

    int numRows = 6;
    int numCols = model.getNumberOfColumnsOnBoard();
    
    
    emptyIcon = new ImageIcon(this.getClass().getResource("/images/unoccupied.png"));
    blackIcon = new ImageIcon(this.getClass().getResource("/images/black.png"));
    if (getPiece() == 'Y') {
      myColorIcon = new ImageIcon(this.getClass().getResource("/images/yellow.png"));
      opponentColorIcon = new ImageIcon(this.getClass().getResource("/images/red.png"));
    } else if (getPiece() == 'R') {
      myColorIcon = new ImageIcon(this.getClass().getResource("/images/red.png"));
      opponentColorIcon = new ImageIcon(this.getClass().getResource("/images/yellow.png"));
    } else {
      myColorIcon = emptyIcon;
      opponentColorIcon = emptyIcon;
    }
    
    DefaultTableModel tableModel = new DefaultTableModel(numRows, numCols) {
      @Override
      public Class<?> getColumnClass(int column) {
          return ImageIcon.class;
      }
    };
   
    table = new JTable(tableModel);
    table.setRowHeight(70);
    for (int i = 0; i < numRows; i ++) {
      for (int j = 0; j < numCols; j ++) {
        table.setValueAt(emptyIcon, i, j);
      }
    }
    
    JPanel panel = new JPanel(new BorderLayout());
   
    JPanel bottomPanel = new JPanel(new BorderLayout());

    bottomPanel.add(textField, BorderLayout.CENTER);

    //bottomPanel.add(reset, BorderLayout.WEST);
    bottomPanel.add(textField, BorderLayout.CENTER);
    bottomPanel.add(submit, BorderLayout.EAST);
    
    panel.add(bottomPanel, BorderLayout.SOUTH);
    
    panel.add(table, BorderLayout.CENTER);
    
    frame.getContentPane().add(panel);

    frame.setSize(510,510);

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    
    reset.addActionListener(new ActionListener() {

      @Override

      public void actionPerformed(ActionEvent arg0) {

        resetButtonPressed();

      }

    });
    
    submit.addActionListener(new ActionListener() {

      @Override

      public void actionPerformed(ActionEvent arg0) {
        
        buttonPressed(numCols);

      }

    });
    
    frame.setVisible(true);
  }
  
  private void resetButtonPressed() {
    model.resetGame(true);
  }
  
  public void shutFrame() {
    frame.setVisible(false);
    frame.dispose();
  }
  
  private void buttonPressed(int numCols) {
    
    String getTextEntered = textField.getText();
    if (getTextEntered.equals("")) {
      JOptionPane.showMessageDialog(frame, "Please enter a valid column.\n");
      textField.setText("");
      return;
    }

    if (!getTextEntered.matches("\\d+$")) {
      JOptionPane.showMessageDialog(frame, "Please enter a valid column.\n");
      textField.setText("");
      return;
    }
    
    int num = Integer.parseInt(getTextEntered);
     
    if (num < 0 || num >= numCols) {
      JOptionPane.showMessageDialog(frame, "Please enter a valid column.\n");
      textField.setText("");
      return;
    }
    textField.setText("");
    model.selectColumnForMove(this, num);

  }
  
  
  @Override
  public void gameStartSignal(IListener firstPlayerToGo) {
    if (this == firstPlayerToGo) {
      JOptionPane.showMessageDialog(frame, "Begin playing - your turn\n");
    } else {
      JOptionPane.showMessageDialog(frame, 
          "Waiting for " + firstPlayerToGo.getId() + " to start...\n");
    }
    //prevent game from being reset at this stage
    reset.setEnabled(false);
  }


  @Override
  public void invalidTurnNotify() {
    JOptionPane.showMessageDialog(frame, "Not your turn!\n");
  }

  @Override
  public void invalidMoveNotify() {
    JOptionPane.showMessageDialog(frame, 
        "That move was invalid! Please try again.\n");
  }

  @Override
  public void gameNotInProgressNotify() {
    JOptionPane.showMessageDialog(frame, "Game is not in progress!\n");
  }

  @Override
  public void correctMoveNotify() {
    //Render board
    char[][] board = model.getCopyOfGameBoard();
    char myColor = getPiece();
    char opponentColor = ((myColor == 'Y') ? 'R' : 'Y');
    for (int i = 0; i < board.length; i ++) {
      for (int j = 0; j < board[0].length; j ++) {
        if (board[i][j] == myColor) {
          table.setValueAt(myColorIcon, i, j);
        } else if (board[i][j] == opponentColor) {
          table.setValueAt(opponentColorIcon, i, j);
        }
      }
    }
  }

  @Override
  public void gameWinNotify(char winnerPiece) {

    char[][] board = model.getCopyOfGameBoard();
    for (int i = 0; i < board.length; i ++) {
      for (int j = 0; j < board[0].length; j ++) {
        if (board[i][j] == ' ') {
          table.setValueAt(blackIcon, i, j);
        }
      }
    }
    if (winnerPiece == getPiece()) {
      JOptionPane.showMessageDialog(frame, "Congratulations, you won!\n");
    } else {
      JOptionPane.showMessageDialog(frame, "Sorry, you lost!\n");
    }
    
    reset.setEnabled(false);
    submit.setEnabled(false);
  }

  @Override
  public void gameOverNotify() {
    JOptionPane.showMessageDialog(frame, "Game Over - No Winner!\n");
    char[][] board = model.getCopyOfGameBoard();
    for (int i = 0; i < board.length; i ++) {
      for (int j = 0; j < board[0].length; j ++) {
        if (board[i][j] == ' ') {
          table.setValueAt(blackIcon, i, j);
        }
      }
    }
    
    reset.setEnabled(false);
    submit.setEnabled(false);
  }

  @Override
  public char getPiece() {
    return piece;
  }
  
  public String getId() {
    return id;
  }
  
}
