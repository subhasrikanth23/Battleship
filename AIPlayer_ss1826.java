public class AIPlayer_ss1826 implements Player
{
    //Put your fields here:
    protected char[][] boardA;
    protected char[][] boardB;
    protected char[][] opponentBoardB;
    protected String name;
    protected char type;
    protected Coordinate lastGuess = null;
    protected int numberOfAttempts = 0;
    protected String[] shipnames = {"Aircraft Carrier", "Battleship", "Submarine",
                                    "Destroyer", "Patrol Boat"};
    protected String[] ships = {"AAAAA", "BBBB", "SSS", "DDD", "PP"};
    protected String leadingSpaces = "";

    public AIPlayer_ss1826(String playerName)
    {
        name = playerName;
        type = 'E';
        boardA = new char[10][10];
        boardB = new char[10][10];
        opponentBoardB = new char[10][10];
        initBoard(boardA);
        initBoard(boardB);
        initBoard(opponentBoardB);
    }
	
    public AIPlayer_ss1826(String playerName, int n)
    {
        name = playerName;
        type = 'E';
        boardA = new char[10][10];
        boardB = new char[10][10];
        opponentBoardB = new char[10][10];
        initBoard(boardA);
        initBoard(boardB);
        initBoard(opponentBoardB);
        for (int i = 0; i < n; i++)
           leadingSpaces += " ";
    }

    //Implement your methods here:
    public String getName()
    {
        return name;
    }
    
    public char getType()
    {
        return type;
    }
    
    public static void initBoard(char[][] board)
    {
        for (int i = 0; i < board.length; i++)
        {
          for (int j = 0; j < board[i].length; j++)
          {
            board[i][j] = '~';
          }
        }
    }
    
    public char fireUpon(Coordinate x)
    {
        int row = x.getX()-1;
        int col = x.getY()-1;
        char result;
        if (boardA[row][col] != '~')
        {
           opponentBoardB[row][col] = 'H';
           result = boardA[row][col];
        }
        else
        {
           result = 'M';
        }
        printResult(result);
        return result;
    }
    
    public void printResult(char result)
    {
        if (result == 'M')
          System.out.println(leadingSpaces + "Missed!");
        else if (result == 'A')
        {
          int count = 0;
          for (int i = 0; i < boardA.length; i++)
          {
            for (int j = 0; j < boardA[0].length; j++)
            {
              if (opponentBoardB[i][j] == 'H' && boardA[i][j] == 'A')
                count++;
            }
          }  
          if (count == 5)
            System.out.println(leadingSpaces + "An aircraft carrier has been sunken!");
          else
            System.out.println(leadingSpaces + "An aircraft carrier has been hit!");
        }
        else if (result == 'B')
        {
          int count = 0;
          for (int i = 0; i < boardA.length; i++)
          {
            for (int j = 0; j < boardA[0].length; j++)
            {
              if (opponentBoardB[i][j] == 'H' && boardA[i][j] == 'B')
                count++;
            }
          }
          if (count == 4)
            System.out.println(leadingSpaces + "A battleship has been sunken!");
          else
            System.out.println(leadingSpaces + "A battleship has been hit!");
        }
        else if (result == 'D')
        {
          int count = 0;
          for (int i = 0; i < boardA.length; i++)
          {
            for (int j = 0; j < boardA[0].length; j++)
            {
              if (opponentBoardB[i][j] == 'H' && boardA[i][j] == 'D')
                count++;
            }
          }
          if (count == 3)
            System.out.println(leadingSpaces + "A destroyer has been sunken!");
          else
            System.out.println(leadingSpaces + "A destroyer has been hit!");
        }
        else if (result == 'S')
        {
          int count = 0;
          for (int i = 0; i < boardA.length; i++)
          {
            for (int j = 0; j < boardA[0].length; j++)
            {
              if (opponentBoardB[i][j] == 'H' && boardA[i][j] == 'S')
                count++;
            }
          }
          if (count == 3)
            System.out.println(leadingSpaces + "A submarine has been sunken!");
          else
            System.out.println(leadingSpaces + "A submarine has been hit!");
        }
        else if (result == 'P')
        {
          int count = 0;
          for (int i = 0; i < boardA.length; i++)
          {
            for (int j = 0; j < boardA[0].length; j++)
            {
              if (opponentBoardB[i][j] == 'H' && boardA[i][j] == 'P')
                count++;
            }
          }
          if (count == 2)
            System.out.println(leadingSpaces + "A patrol boat has been sunken!");
          else
            System.out.println(leadingSpaces + "A patrol boat has been hit!");
        }
    }
    
    public static boolean verify(char[][] boardA, char[][] boardB)
    {
        for (int i = 0; i < boardA.length; i++)
        {
          for (int j = 0; j < boardA[0].length; j++)
          {
            if (boardA[i][j] != '~' && boardB[i][j] != 'H')
              return false;
          }
        }
        return true;
    }
    
    public Coordinate fire()
    {
        numberOfAttempts++;
        System.out.println(leadingSpaces + " It is now " + name + "'s turn #" +
                           numberOfAttempts + ".");
        printBoard();
        do
        {
          lastGuess = Coordinate.getRandomCoordinate(1,1,10,10);
          if (validate(boardB,lastGuess))
            break;
        } while(true);
        System.out.println(leadingSpaces + name + " guessed: " + lastGuess.asString());
        return lastGuess;
    }
    
    public void fireResult(char result)
    {
        /* There are two possibilities of how to implement this method.
          The first possibility updates the active player's board B with 'H'
          for each hit.
          The second possibility updates the active player's board B with the character
          representation of the ship that was hit.
          In the interest of keeping the program as close as possible to the original
          game, the first possibility has been implemented rather than the second.
       */
       
        if (result != 'M')
          boardB[lastGuess.getX() - 1][lastGuess.getY() - 1] = 'H';
        else
          boardB[lastGuess.getX()- 1][lastGuess.getY() - 1] = 'M';
          
    }
    
    public static boolean validate(char[][] board, Coordinate cell)
    {
        if (cell.getX() < 1 || cell.getX() > board.length ||
            cell.getY() < 1 || cell.getY() > board[0].length)
          return false;
        int x = cell.getX()-1;
        int y = cell.getY()-1;
        if (board[x][y] != '~')
          return false;
        return true;
    }
    
    public static boolean positionShip(char[][] board, String shiptype,
                                       String ship, char orientation, Coordinate cell)
    {
        orientation = Character.toUpperCase(orientation);
        if (validate(board, cell) == false)
          return false;
        if (orientation == 'H' && (cell.getY()+ship.length() - 1) > board[0].length)
          return false;
        if (orientation == 'V' && (cell.getX()+ship.length() - 1) > board.length)
          return false;
        int row = cell.getX()-1;
        int column = cell.getY()-1;
        if (orientation != 'H' && orientation != 'V')
          return false;
        for (int i = 0; i < ship.length(); i++)
        {
          if (board[row][column] != '~')
            return false;
          else if (orientation == 'H')
            column++;
          else
            row++;
        }
        row = cell.getX()-1;
        column = cell.getY()-1;
        for (int i = 0; i < ship.length(); i++)
        {
          if (orientation == 'H')
          {
            board[row][column] = ship.charAt(i);
            column++;
          }
          else
          {
            board[row][column] = ship.charAt(i);
            row++;
          }
        }
        return true;
    }
    
    public void placeShips()
    {        
        for (int i = 0; i < shipnames.length; i++)
        {
          boolean positioned = false;
          do
          {
            Coordinate cell = Coordinate.getRandomCoordinate(1,1,10,10);
            char orientation = getRandomOrientation();
            positioned = positionShip(boardA, shipnames[i], ships[i], orientation, cell);
          } while (positioned == false);
        }
    }
    
    public boolean lost()
    {
        for (int i = 0; i < boardA.length; i++)
        {
          for (int j = 0; j < boardA[0].length; j++)
          {
            if (boardA[i][j] != '~' && opponentBoardB[i][j] != 'H')
              return false;
          }
        }
        return true;
    
    }
    
    protected void printSpecificBoard(char[][] board)
    {
        int rowindex;
        int colindex;
        
        System.out.print(leadingSpaces);
        System.out.print("   ");
        for (colindex = 0; colindex < board[0].length; colindex++)
        {
          System.out.print("   " + (colindex+1));
        }
        System.out.println();
        System.out.println(leadingSpaces +
                           "     _______________________________________");
        System.out.println();
        
        for(rowindex = 0; rowindex < board.length; rowindex++)
        {
            System.out.print(leadingSpaces);
            if (rowindex < 9)
              System.out.print(" "+(rowindex+1)+"|");
            else
              System.out.print((rowindex+1)+"|");
            for(colindex = 0; colindex < board[0].length; colindex++)
            {
                String spaces = "   ";
                if ((rowindex == 9) && (colindex == 0))
                   spaces = "   ";
                System.out.print(spaces + board[rowindex][colindex]);
            }
            System.out.println();
        }
    }
    
    public void printBoard()
    {
        printSpecificBoard(boardB);
    }
    
    protected static char getRandomOrientation()
    {
        int i = (int) Math.ceil(2*Math.random());
        if (i == 1)
          return 'H';
        else
          return 'V';
    }
}
