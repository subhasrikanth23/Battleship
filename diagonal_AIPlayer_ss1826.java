public class diagonal_AIPlayer_ss1826 extends difficult_AIPlayer_ss1826 
{
    //Put your fields here:
    protected String[] opponentUnsunkShips = {"AAAAA", "BBBB", "SSS", "DDD", "PP"};
    boolean veryFirstGuess = true;

    public diagonal_AIPlayer_ss1826(String playerName)
    {
        super(playerName);
        type = 'd';
    }

    public diagonal_AIPlayer_ss1826(String playerName, int indentOutput)
    {
        super(playerName, indentOutput);
        type = 'd';
    }

    /*
    The AI firing strategy is as follows:
    Condition                           Guess
    ======================================================================
    1) Initial Guess                   ==> Random (valid) Guess along the
                                           diagonal line of the entire board

    2) Last Guess resulted in sinking  ==> Random (valid) Guess along the
       an opponent's ship, with no         diagonal line of the last guess
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
               if (veryFirstGuess && (lastGuess.getX() != lastGuess.getY()))
               {
                  lastGuess = null;
                  continue;
               }
           }
           else
           {
               char lastResult = boardB[lastGuess.getX() - 1][lastGuess.getY() - 1];
               lastGuess = makeSmartGuess(lastGuess, lastResult);
           }
           if (validate(boardB,lastGuess))
                   break;
        } while(true);
        veryFirstGuess = false;
        System.out.println(leadingSpaces + name + " guessed: " + lastGuess.asString());
        return lastGuess;
    }
    
    /*
         Smart Guess:

         When the last guess was a Miss
         ==============================
            - Check for a partially hit ship (from the previous hits);
              if found, return its position as the next guess

            - Get the coordinates of the possible diagonal positions
              and rate them to see how good these positions are 
              (see the comments in the method "validateGuess").

              Choose the diagonal position that gets the Best/Good/Average
              rating (in that order). If all of the diagonal positions are
              found to be Bad, make a random guess.
               
              Using a diagonal position gets the advantage of not guessing in
              the same row or same column as the missed cell. 

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
   
            Coordinate[] diagonalPositions = new Coordinate[38];
            String[] diagonalRanks = new String[38];

            Coordinate firstBestGuess = null;
            Coordinate firstGoodGuess = null;
            Coordinate firstAverageGuess = null;
            // Look for diagonal cells from top left to bottom right
            for (int i=0; i<19; i++)
            {
               int x = lastGuess.getX() + i - 9;
               int y = lastGuess.getY() + i - 9;
               diagonalPositions[i] = new Coordinate(1,1,10,10,x,y);
               diagonalRanks[i] = validateGuess(diagonalPositions[i]);

               if (Debug.enabled())
                  System.out.println(leadingSpaces + "Validated "
                      + diagonalPositions[i].asString() + ".... as " + diagonalRanks[i]);

               if ((firstBestGuess == null) && (diagonalRanks[i] == "Best"))
                   firstBestGuess = diagonalPositions[i];
               if ((firstGoodGuess == null) && (diagonalRanks[i] == "Good"))
                   firstGoodGuess = diagonalPositions[i];
               if ((firstAverageGuess == null) && (diagonalRanks[i] == "Average"))
                   firstAverageGuess = diagonalPositions[i];
            }

            // Look for diagonal cells from bottom left to top right
            for (int i=0; i<19; i++)
            {
               int x = lastGuess.getX() - i + 9;
               int y = lastGuess.getX() + lastGuess.getY() - x;
               diagonalPositions[i+19] = new Coordinate(1,1,10,10,x,y);
               diagonalRanks[i+19] = validateGuess(diagonalPositions[i]);

               if (Debug.enabled())
                  System.out.println(leadingSpaces + "Validated " +
                                     diagonalPositions[i+19].asString() + ".... as " +
                                     diagonalRanks[i+19]);

               if ((firstBestGuess == null) && (diagonalRanks[i+19] == "Best"))
                   firstBestGuess = diagonalPositions[i+19];
               if ((firstGoodGuess == null) && (diagonalRanks[i+19] == "Good"))
                   firstGoodGuess = diagonalPositions[i+19];
               if ((firstAverageGuess == null) && (diagonalRanks[i+19] == "Average"))
                   firstAverageGuess = diagonalPositions[i+19];
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

            // Try guessing the left, up, right and then down cells, as long as
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
}
