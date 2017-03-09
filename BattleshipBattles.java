public class BattleshipBattles
{
    static String player1Name = "KnightMover";
    static String player2Name = "DiagonalDriver";
    static int maxTimes = 100;
    public static void main(String[] args)
    {
        if (args.length > 0)
        {
          int count = 0;
          if (args[count].equals("debug"))
          {
              Debug.enable();
              count++;
          }
          if (args.length > count)
            maxTimes = Integer.parseInt(args[count]);
        }

        int[] winCount = new int[2];
        for (int i=0; i<maxTimes; i++)
        {
            System.out.println("---------------------- Game #" + i + " ----------------------");
            int winner = playGame();
            winCount[winner-1]++;   
        }

        System.out.println("After " + maxTimes + " games");
        System.out.println("    " + player1Name + " won " + winCount[0] + " times.");
        System.out.println("    " + player2Name + " won " + winCount[1] + " times.");

        if (winCount[0] > winCount[1])
           System.out.println("**** " + player1Name + " is the champion!");
        else if (winCount[1] > winCount[0])
           System.out.println("**** " + player2Name + " is the champion!");
        else
           System.out.println("**** Both " + player1Name + " and " + player2Name + " are champions!");
    }

    public static int playGame()
    {
        Player player1 = new difficult_AIPlayer_ss1826(player1Name);
        Player player2 = new diagonal_AIPlayer_ss1826(player2Name, 44);
        
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
        } while(!gameOver);
        
        activePlayer.printBoard();
        System.out.println(activePlayerName + " WINS!");
        if (activePlayer == player1)
           return 1;
        else
           return 2;
    }
}
