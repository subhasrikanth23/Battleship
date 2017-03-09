public class Battleship
{
    public static void main(String[] args)
    {
        String player1Name = "PLAYER 1";
        String player2Name = "PLAYER 2";
        if (args.length > 0)
        {
          int count = 0;
          if (args[count].equals("debug"))
          {
              Debug.enable();
              count++;
          }
          if (args.length > count)
            player1Name = args[count++];
          if (args.length > count)
            player2Name = args[count];
        }
        Player player1 = new Player_ss1826(player1Name);
        Player player2 = null;
        
        do
        {
          System.out.print("Play against a human (H) or an AI (A)? ");
          char choice = IO.readChar();
          if (choice == 'H')
            player2 = new Player_ss1826(player2Name);
          else if (choice == 'A')
          {
            System.out.print("Play easy (E) or difficult (D)? ");
            choice = IO.readChar();
            if (choice == 'E')
              player2 = new AIPlayer_ss1826(player2Name, 44);
            else if (choice == 'D')
            {
              System.out.print("Play moderately difficult (M) or impossible (I)? ");
              choice = IO.readChar();
              if (choice == 'M')
                player2 = new difficult_AIPlayer_ss1826(player2Name, 44);
              else if (choice == 'I')
                player2 = player2 = new ImpossiblePlayer(player2Name, 44);
            }
          }
        } while(player2 == null);
          
        player1.placeShips();
        player2.placeShips();
        
        Player activePlayer = player1;
        Player opponent = player2;
        String activePlayerName = player1Name;
        
        boolean gameOver = false;
        
        do
        {
          Coordinate target = activePlayer.fire();
          char result = opponent.fireUpon(target);
          activePlayer.fireResult(result);
          if (result == 'M')
          {
            if (activePlayer == player1)
            {
              activePlayer = player2;
              opponent = player1;
              activePlayerName = player2Name;
            }
            else
            {
              activePlayer = player1;
              opponent = player2;
              activePlayerName = player1Name;
            }
          }
          else
          {
            gameOver = opponent.lost();
            if (!gameOver)
            {
              System.out.println(activePlayerName + " goes again.");
            }
          }
          //if (player2 instanceof ImpossiblePlayer)
          //  player2.placeShips();
        } while(!gameOver);
        
        activePlayer.printBoard();
        System.out.println(activePlayerName + " WINS!");
    }
}
