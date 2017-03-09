public class ImpossiblePlayer extends difficult_AIPlayer_ss1826
{
    private Coordinate[] shipPositions = new Coordinate[5];
    private char[] shipOrientations = new char[5];

    public ImpossiblePlayer(String playerName)
    {
        super(playerName);
        type = 'I';
    }

    public ImpossiblePlayer(String playerName, int indentOutput)
    {
        super(playerName, indentOutput);
        type = 'I';
    }

    public boolean moveShip(Coordinate cell)
    {
        boolean moved = false;
        int x = cell.getX();
        int y = cell.getY();

        int i;
        int shipHit = -1;
        String newName = "";

        if (Debug.enabled())
            System.out.println("Checking for " + cell.asString());

        for (i = 0; i < ships.length; i++)
        {
            int shipStartX = shipPositions[i].getX();
            int shipStartY = shipPositions[i].getY();
            int shipEndX = shipStartX;
            int shipEndY = shipStartY;
            if (shipOrientations[i] == 'V')
                shipEndX = shipEndX + ships[i].length() - 1;
            else
                shipEndY = shipEndY + ships[i].length() - 1;

            if (Debug.enabled())
               System.out.println("   from " + shipStartX + "," + shipStartY +
                                  " to " + shipEndX + ", " + shipEndY);
            newName = ships[i];
            int positionHit = 0;
            for (int row=shipStartX; row <= shipEndX; row++)
            {
               if (shipHit != -1)
                  break;
               for (int col=shipStartY; col <= shipEndY; col++)
               {
                   if (Debug.enabled())
                       System.out.println("       against (" + row + "," + col + ")");
                   if ((row == x) && (col == y))
                   {
                       if (Debug.enabled())
                          System.out.println("           matched ... at " + i);
                       shipHit = i;
                       newName = newName.substring(0, positionHit) + "H" + 
                                 newName.substring(positionHit+1);
                       break;
                   }
                   positionHit++;
               }
            }
            if (shipHit != -1)
               break;
        }

        boardA[x-1][y-1] = 'H';
        boolean positioned = false;
        ships[shipHit] = newName;

        for (i = 0; i < 100; i++)      // Try to move it 100 times
        {
            Coordinate newCell = Coordinate.getRandomCoordinate(1, 1, 10, 10);
            char orientation = getRandomOrientation();
            positioned = positionShip(boardA, shipnames[shipHit], newName,
                                      orientation, newCell);
            if (positioned)
            {
               // Remove the old ship
               int shipStartX = shipPositions[shipHit].getX();
               int shipStartY = shipPositions[shipHit].getY();
               int shipEndX = shipStartX;
               int shipEndY = shipStartY;
               if (shipOrientations[shipHit] == 'V')
                   shipEndX = shipEndX + ships[shipHit].length() - 1;
               else
                   shipEndY = shipEndY + ships[shipHit].length() - 1;
    
               for (int row=shipStartX; row <= shipEndX; row++)
               {
                  for (int col=shipStartY; col <= shipEndY; col++)
                  {
                     if (boardA[row-1][col-1] != 'H')
                         boardA[row-1][col-1] = '~';
                  }
               }
               
               shipPositions[shipHit] = newCell;
               shipOrientations[shipHit] = orientation;
               moved = true;
               if (Debug.enabled())
               {
                   System.out.println("!!Auto Move!!");
                   printSpecificBoard(boardA);
               }
               break;
            }
        }

        return moved;
    }
	
    public void placeShips()
    {        
        for (int i = 0; i < shipnames.length; i++)
        {
          boolean positioned = false;
          do
          {
            Coordinate cell = Coordinate.getRandomCoordinate(1, 1, 10, 10);
            char orientation = getRandomOrientation();
            positioned = positionShip(boardA, shipnames[i], ships[i], orientation, cell);
            if (positioned)
            {
                shipPositions[i] = cell;
                shipOrientations[i] = orientation;
                if (Debug.enabled())
                    printSpecificBoard(boardA);
            }
          }while (positioned == false);
        }
    }

    public char fireUpon(Coordinate x)
    {
        int row = x.getX()-1;
        int col = x.getY()-1;
        char result;

        if ((boardA[row][col] != '~') &&
            (boardA[row][col] != 'H') &&
            (boardA[row][col] != 'M'))
        {
            opponentBoardB[row][col] = 'H';
            boardA[row][col] = 'H';
            result = boardA[row][col];
            moveShip(x);
        }
        else
        {
           result = 'M';
           boardA[row][col] = 'M';
        }
        printResult(result);
        return result;
    }

}
