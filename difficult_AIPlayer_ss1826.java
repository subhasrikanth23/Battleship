public class difficult_AIPlayer_ss1826 extends AIPlayer_ss1826
{
    //Put your fields here:
    protected String[] opponentUnsunkShips = {"AAAAA", "BBBB", "SSS", "DDD", "PP"};

    public difficult_AIPlayer_ss1826(String playerName)
    {
        super(playerName);
        type = 'D';
    }

    public difficult_AIPlayer_ss1826(String playerName, int indentOutput)
    {
        super(playerName, indentOutput);
        type = 'D';
    }
	
    //Implement your methods here:

    public boolean isClearCell(Coordinate cell)
    {
       if (cell.valid() && (boardB[cell.getX()-1][cell.getY()-1] == '~'))
          return true;
       else
          return false;
    }

    public boolean isMissedOrInvalidCell(Coordinate cell)
    {
       if (!cell.valid() || (boardB[cell.getX()-1][cell.getY()-1] == 'M'))
          return true;
       else
          return false;
    }

    public String validateGuess(Coordinate cell)
    {
        if (!isClearCell(cell))
            return "Bad";

        // It is a bad guess if all of the neighboring cells have been 
        // either missed already or invalid

        if (isMissedOrInvalidCell(cell.left()) &&
            isMissedOrInvalidCell(cell.right()) &&
            isMissedOrInvalidCell(cell.up()) &&
            isMissedOrInvalidCell(cell.down()))
              return "Bad";

        // Get the smallest & the largest un-sunken ships
        int minimumLength= 9999;
        int maximumLength = 0;

        for (int i = 0; i < opponentUnsunkShips.length; i++)
        {
            if (opponentUnsunkShips[i].length() > maximumLength)
               maximumLength = opponentUnsunkShips[i].length();
            if (opponentUnsunkShips[i].length() < minimumLength)
               minimumLength = opponentUnsunkShips[i].length();
        }

        int hClear = 1;                        // Current cell is clear

        // Get the number of clear cells in the horizontal direction
        Coordinate rightCell = cell.right();
        while (rightCell.valid() && isClearCell(rightCell))
        {
           hClear++;
           rightCell = rightCell.right();
        }
        Coordinate leftCell = cell.left();
        while (leftCell.valid() && isClearCell(leftCell))
        {
           hClear++;
           leftCell = leftCell.left();
        }

        int vClear = 1;                        // Current cell is clear

        // Get the number of clear cells in the vertical direction
        Coordinate upCell = cell.up();
        while (upCell.valid() && isClearCell(upCell))
        {
           vClear++;
           upCell = upCell.up();
        }
        Coordinate downCell = cell.down();
        while (downCell.valid() && isClearCell(downCell))
        {
           vClear++;
           downCell = downCell.down();
        }

        if ((hClear < minimumLength) && (vClear < minimumLength))
            return "Bad";
        else if ((hClear >= maximumLength) && (vClear >= maximumLength))
            return "Best";
        else if ((hClear >= maximumLength) || (vClear >= maximumLength))
            return "Good";
        else
            return "Average";
    }

    public void fireResult(char result)
    {
        /* There are two possibilities of how to implement this method.

          The first possibility updates the active player's board B with
          'H' for each hit.
          The second possibility updates the active player's board B with
          the character representation of the ship that was hit.

          In order to assist our "smart guess", the second possibility
          has been implemented.
       */
       
       boardB[lastGuess.getX() - 1][lastGuess.getY() - 1] = result;


       // Eat up a character in the corresponding ship in the 
       // "opponentUnsunkShips" to make it easy to determine whether
       // a ship has been partially hit or sunken

       for (int i = 0; i < opponentUnsunkShips.length; i++)
       {
           if (opponentUnsunkShips[i].length() == 0)
              continue;

           if (result == opponentUnsunkShips[i].charAt(0))
           {
              // Found the name of the ship that has been hit
              // Reduce the name by one character (e.g.: "AAAAA" becomes "AAAA")

              int remainingLen = opponentUnsunkShips[i].length();

              opponentUnsunkShips[i] = 
                              opponentUnsunkShips[i].substring(0, remainingLen-1);

              // If the ship has been sunken, reset the lastGuess to either 
              //     - the position of another partially sunken ship (if found)
              //     - null, to trigger a random guess (if there is no partially
              //       sunken ship)

              if (opponentUnsunkShips[i].length() == 0)
              {
                 if (Debug.enabled())
                    System.out.println(leadingSpaces + "Hey ! I have sunk a ship !");
                 Coordinate c = checkForPartiallyHitShip();
                 if (c.valid())
                    lastGuess = c;
                 else
                 {
                     // We just sunk a ship, and there is no other partially hit ship..
                     // Let us restart with a random guess.
                     lastGuess = null;
                 }
              }
              break;
           }
       }
    }

    /*
    The AI firing strategy is as follows:
    Condition                           Guess
    ======================================================================
    1) Initial Guess                   ==> Random (valid) Guess

    2) Last Guess resulted in sinking  ==> Random (valid) Guess
       an opponent's ship, with no
       other partially sunk ship found
       in our boardB

    3) Last Guess resulted in sinking  ==> Reset the last guess to the 
       an opponent's ship, with            partially sunk ship position
       another partially sunk ship         and retry the smart-search
       found in our boardB

    4) Last Guess was a Hit            ==> Make a "smart guess" looking both
       (without sinking a ship)            horizontally as well as vertically
                                           for making the next guess just
                                           around the Last Guess

    5) Last Guess was a Miss           ==> Make a "smart guess" around the
                                           Last Guess, skipping its immediate
                                           next positions (see the comments in
                                           makeSmartGuess method)

    The "smart guess" logic has been implemented in the method
    "makeSmartGuess()".

    */

    public Coordinate fire()
    {
        numberOfAttempts++;
        System.out.println(leadingSpaces + "It is now " + name + "'s turn #" +
                           numberOfAttempts + ".");
        printBoard();

        do
        {
           if (lastGuess == null)
           {
               lastGuess = Coordinate.getRandomCoordinate(1, 1, 10, 10);
           }
           else
           {
               char lastResult = boardB[lastGuess.getX() - 1][lastGuess.getY() - 1];
               lastGuess = makeSmartGuess(lastGuess, lastResult);
           }
           if (validate(boardB,lastGuess))
                   break;
        } while(true);
        System.out.println(leadingSpaces + name + " guessed: " + lastGuess.asString());
        return lastGuess;
    }
    
    /*
         opponentUnsunkShips[] contains the names of the ships not yet fully
         sunk.

         To start with, they have the same original ship names, but, after each
         hit to the opponent's ship, the corresponding ship name in the 
         opponentUnsunkShips will start shrinking (shrinking is done by the
         "fireResult").

         The following method checks into the opponentUnsunkShips for any ship
         that is not yet sunk, but partially hit (i.e. the shrunk name of the
         ship will not be empty and will not match the original name).

         If such a partially hit ship is found, the method returns the
         Coordinate of the ship from our boardB.
    */

    public Coordinate checkForPartiallyHitShip()
    {
        for (int i = 0; i < ships.length; i++)
        {
            if ((opponentUnsunkShips[i].length() > 0) &&
                (ships[i].length() != opponentUnsunkShips[i].length()))
            {
                // Look for the coordinate where the partially sunken ship is
                // and make it as the last guess.
                for (int row = 0; row < boardB.length; row++)
                {
                    for (int col = 0; col < boardB[i].length; col++)
                    {
                       if (boardB[row][col] == opponentUnsunkShips[i].charAt(0))
                       {
                           Coordinate guess = new Coordinate(1, 1, 10, 10, row+1, col+1);
                           return guess;
                       }
                    }
                }
            }
        }
        return new Coordinate();   // Invalid coordinate
    }

    /*
         Smart Guess:

         When the last guess was a Miss
         ==============================
            - Check for a partially hit ship (from the previous hits);
              if found, return its position as the next guess

            - Get the coordinates of the possible Knight-like positions
              (following the chess game rules) and rate them to see
              how good these positions are (see the comments in the method
              "validateGuess").

              Choose the Knight position that gets the Best/Good/Average
              rating (in that order). If all of the Knight positions are
              found to be Bad, make a random guess.
               
              Using a Knight position gets the advantage of not guessing in
              the same row or same column as the missed cell. Another way
              (not implemented here) is to guess along the diagonal route.

         When the last guess was a Hit
         =============================
            - Check whether the next part of the same ship has been hit
              in the horizontal direction or vertical direction, by
              looking at the neighboring cells.

              If the same ship has been found as being hit, make the next
              guess along the appropriate direction (horizontal/vertical).

            - If this hit happens to be on the first part of the ship
              (i.e. none of the neighboring cells indicate the same ship
              as being hit), choose the left/up/right/down cell, whatever
              is "clear" (i.e. represented by '~').

            - If none of the neighboring cells are clear, make a random
              guess (this condition should never occur though !)
    */
    public Coordinate makeSmartGuess(Coordinate lastGuess, char lastResult)
    {
        if (lastResult == 'M')
        {
            Coordinate c = checkForPartiallyHitShip();
            if (c.valid())
                return c;
   
            Coordinate[] knightPositions = new Coordinate[8];
            String[] knightRanks = new String[8];

            Coordinate firstBestGuess = null;
            Coordinate firstGoodGuess = null;
            Coordinate firstAverageGuess = null;
            for (int i=0; i<8; i++)
            {
               knightPositions[i] = lastGuess.knightMove(i+1);
               knightRanks[i] = validateGuess(knightPositions[i]);
               if (Debug.enabled())
                  System.out.println(leadingSpaces + "Validated " +
                          knightPositions[i].asString() + ".... as " + knightRanks[i]);
               if ((firstBestGuess == null) && (knightRanks[i] == "Best"))
                   firstBestGuess = knightPositions[i];
               if ((firstGoodGuess == null) && (knightRanks[i] == "Good"))
                   firstGoodGuess = knightPositions[i];
               if ((firstAverageGuess == null) && (knightRanks[i] == "Average"))
                   firstAverageGuess = knightPositions[i];
            }
            if (firstBestGuess != null)
                return firstBestGuess;
            else if (firstGoodGuess != null)
                return firstGoodGuess;
            else if (firstAverageGuess != null)
                return firstAverageGuess;
            else
            {
                if (Debug.enabled())
                    System.out.println(leadingSpaces +
                                       "Re-starting with a random guess !!");
                return Coordinate.getRandomCoordinate(1, 1, 10, 10);
            }
        }
        else         
        {
            // Check if any of the neighboring cell has the same result
            // so that search in that direction makes sense

            boolean horizontalSearch = false;

            Coordinate leftCell = lastGuess.left();
            while (leftCell.valid() && 
                   (boardB[leftCell.getX()-1][leftCell.getY()-1] == lastResult))     
             {   
                horizontalSearch = true;
                leftCell = leftCell.left();
            }

            Coordinate rightCell = lastGuess.right();
            while (rightCell.valid() && 
                   (boardB[rightCell.getX()-1][rightCell.getY()-1] == lastResult))     
             {   
                horizontalSearch = true;
                rightCell = rightCell.right();
            }

            boolean verticalSearch = false;

            Coordinate upCell = lastGuess.up();
            while (upCell.valid() && 
                   (boardB[upCell.getX()-1][upCell.getY()-1] == lastResult))     
             {   
                verticalSearch = true;
                upCell = upCell.up();
            }

            Coordinate downCell = lastGuess.down();
            while (downCell.valid() && 
                   (boardB[downCell.getX()-1][downCell.getY()-1] == lastResult))     
             {   
                verticalSearch = true;
                downCell = downCell.down();
            }

            // Try guessing the left, up, right and then down cells as long as
            // guessing in that direction makes sense.

            if (horizontalSearch && leftCell.valid() && isClearCell(leftCell))
                   return leftCell;

            if (verticalSearch && upCell.valid() && isClearCell(upCell))
                   return upCell;

            if (horizontalSearch && rightCell.valid() && isClearCell(rightCell))
                   return rightCell;

            if (verticalSearch && downCell.valid() && isClearCell(downCell))
                   return downCell;

            if (leftCell.valid() && isClearCell(leftCell))
                return leftCell;
            if (upCell.valid() && isClearCell(upCell))
                return upCell;
            if (rightCell.valid() && isClearCell(rightCell))
                return rightCell;
            if (downCell.valid() && isClearCell(downCell))
                return downCell;

            Coordinate c = checkForPartiallyHitShip();
            if (c.valid())
                return c;

            if (Debug.enabled())
                System.out.println(leadingSpaces +
                                   "Once again..starting with a random guess !!");
            return Coordinate.getRandomCoordinate(1, 1, 10, 10);
        }
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
        int endRow = row;
        int endCol = column;
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

        if (orientation == 'H')
           endCol = column;
        else
           endRow = row;

        Coordinate endCell = new Coordinate(1,1,10,10,endRow+1,endCol+1);

        Coordinate c1 = cell.left();
        Coordinate c2 = cell.up();
        Coordinate c3 = cell.right();
        Coordinate c4 = cell.down();

        Coordinate c5 = endCell.left();
        Coordinate c6 = endCell.up();
        Coordinate c7 = endCell.right();
        Coordinate c8 = endCell.down();

        if (c1.valid() && (board[c1.getX()-1][c1.getY()-1] != '~'))
        {
            if (Debug.enabled())
               System.out.println("Skipping " + cell.asString() + " as c1 has a ship");  
            return false;
        }
        if (c2.valid() && (board[c2.getX()-1][c2.getY()-1] != '~'))
        {
            if (Debug.enabled())
               System.out.println("Skipping " + cell.asString() + " as c2 has a ship");
            return false;
        }
        if (c3.valid() && (board[c3.getX()-1][c3.getY()-1] != '~'))
        {
            if (Debug.enabled())
               System.out.println("Skipping " + cell.asString() + " as c3 has a ship");
            return false;
        }
        if (c4.valid() && (board[c4.getX()-1][c4.getY()-1] != '~'))
        {
            if (Debug.enabled())
               System.out.println("Skipping " + cell.asString() + " as c4 has a ship");
            return false;
        }
        if (c5.valid() && (board[c5.getX()-1][c5.getY()-1] != '~'))
        {
            if (Debug.enabled())
               System.out.println("Skipping " + cell.asString() + " as c5 has a ship");
            return false;
        }
        if (c6.valid() && (board[c6.getX()-1][c6.getY()-1] != '~'))
        {
            if (Debug.enabled())
               System.out.println("Skipping " + cell.asString() + " as c6 has a ship");
            return false;
        }
        if (c7.valid() && (board[c7.getX()-1][c7.getY()-1] != '~'))
        {
            if (Debug.enabled())
               System.out.println("Skipping " + cell.asString() + " as c7 has a ship");
            return false;
        }
        if (c8.valid() && (board[c8.getX()-1][c8.getY()-1] != '~'))
        {
            if (Debug.enabled())
               System.out.println("Skipping " + cell.asString() + " as c8 has a ship");
            return false;
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

    /*
    The difficult AI position strategy is as follows:
         1) Strategy #1: Do not position all the ships in the same
                         orientation
            This is done by keeping trackof the number of horizontally
            positioned ships and vertically positioned ships.
            Neither of them is allowed to become 5.

         2) Strategy #2: Do not let any two ships touch each other
            This is done inside the method "positionShip()" by
            checking the left/up/right/down positions of the selected
            Coordinate as well as the end Coordinate based on the
            ship's direction.
    */
    
    public void placeShips()
    {
        int horizontalShips = 0;
        int verticalShips = 0;

        for (int i = 0; i < shipnames.length; i++)
        {
          boolean positioned = false;
          do
          {
            Coordinate cell = Coordinate.getRandomCoordinate(1, 1, 10, 10);
            char orientation = getRandomOrientation();
            if ((orientation == 'H') && (horizontalShips == 4))
            {
               if (Debug.enabled())
                  System.out.println("Avoiding all 5 being horizontal");
               continue;     // All the ships can't be horizontal
            }
            else if ((orientation == 'V') && (verticalShips == 4))
            {
               if (Debug.enabled())
                 System.out.println("Skipping " + cell.asString() + " as c2 has a ship");
               continue;   // All the ships can't be vertical
            }

            positioned = positionShip(boardA, shipnames[i], ships[i], orientation, cell);
            if (positioned)
            {
               if (Debug.enabled())
               {
                   System.out.println("SRI:After positioning " + ships[i]);
                   printSpecificBoard(boardA);
               }
               if (orientation == 'H')
                  horizontalShips++;
               else
                  verticalShips++;
            }
          }while (positioned == false);
        }
    }

    public static void main(String[] args)
    {
        // Tester for investigation into specific issues

/*
        Player h = new difficult_AIPlayer_ss1826("Difficult1");
        difficult_AIPlayer_ss1826 p1 = new difficult_AIPlayer_ss1826("Difficult2");
        Player p = p1;
        Coordinate c = new Coordinate(1, 1, 10, 10, 1, 9);
        p1.lastGuess = c;
        do 
        {
          System.out.println("Now applying " + c.asString());
          char result = h.fireUpon(c);
          p.fireResult(result);
          c = p.fire();
          IO.readChar();
        } while (true);
*/
        Player h = new difficult_AIPlayer_ss1826("Difficult1", 0);
        h.placeShips();
    }
}
