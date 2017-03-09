    public class Coordinate
    {
       public Coordinate()
       {
            x = -1;
            y = -1;
            validCoordinate = false;
       }

       public Coordinate(int x, int y)
       {
            this.x = x;
            this.y = y;
            validCoordinate = true;
       }

       public Coordinate(int minRow, int minCol, int maxRow, int maxCol, 
                         int row, int col)
       {
            min_x = minRow;
            min_y = minCol;
            max_x = maxRow;
            max_y = maxCol;
            x = row;
            y = col;
            validCoordinate = validate();
       }

       public Coordinate(Coordinate orig)
       {
              x = orig.x;
              y = orig.y;
              min_x = orig.min_x;
              min_y = orig.min_y;
              max_x = orig.max_x;
              max_y = orig.max_y;
              validCoordinate = orig.validCoordinate;
       }

       private boolean validate()
       {
            if ((min_x >= 0) && (x < min_x))
               return false;
            if ((min_y >= 0) && (y < min_y))
               return false;
            if ((max_x >= 0) && (x > max_x))
               return false;
            if ((max_y >= 0) && (y > max_y))
               return false;
            return true;
       }

       public static Coordinate getRandomCoordinate(int minX, int minY,
                                                    int maxX, int maxY)
       {
           int xRange = maxX - minX;
           int yRange = maxY - minY;
           
           int x = (int) Math.floor(xRange*Math.random());
           int y = (int) Math.floor(yRange*Math.random());
           Coordinate randomCoordinate = 
                    new Coordinate(minX, minY, maxX, maxY, x + minX, y + minY);
           return randomCoordinate;
       }

       public static Coordinate getRandomCoordinate()
       {
           int x = (int) Math.ceil(10*Math.random());
           int y = (int) Math.ceil(10*Math.random());
           Coordinate randomCoordinate = new Coordinate(x,y);
           return randomCoordinate;
       }

       public int getX() { return x; }
       public int getY() { return y; }
       public boolean valid() { return validCoordinate; }

       public Coordinate right()
       {
             return  new Coordinate(min_x, min_y, max_x, max_y, x, y+1);
       }

        public Coordinate left()
       {
             return  new Coordinate(min_x, min_y, max_x, max_y, x, y-1);
       }

       public Coordinate up()
       {
             return  new Coordinate(min_x, min_y, max_x, max_y, x-1, y);
       }

       public Coordinate down()
       {
             return  new Coordinate(min_x, min_y, max_x, max_y, x+1, y);
       }

       public Coordinate knightMove(int position)
       {
              int newX = x;
              int newY = y;
              switch (position)
              {
                      case 1: newX -= 1;
                              newY += 2;
                              break;
                      case 2: newX -= 2;
                              newY += 1;
                              break;
                      case 3: newX -= 2;
                              newY -= 1;
                              break;
                      case 4: newX -= 1;
                              newY -= 2;
                              break;
                      case 5: newX += 1;
                              newY -= 2;
                              break;
                      case 6: newX += 2;
                              newY -= 1;
                              break;
                      case 7: newX += 2;
                              newY += 1;
                              break;
                      case 8: newX += 1;
                              newY += 2;
                              break;
                      default: newX = -1;
                               newY = -1;
                               break;
              }
              return new Coordinate(min_x, min_y, max_x, max_y, newX, newY);
       }

       public String asString()
       {
           if (validCoordinate)
               return "(" + x + "," + y + ")";
           else
               return "Invalid " + "(" + x + "," + y + ")";
       }

       private int x;
       private int y;
       private int min_x = -1;
       private int min_y = -1;
       private int max_x = -1;
       private int max_y = -1;
       private boolean validCoordinate = false;

       // Testing the Coordinate methods
       public static void main(String[] args)
       {
           Coordinate cell = getRandomCoordinate();
           System.out.println("Random Coordinate: " + cell.asString());
           cell = getRandomCoordinate(1,1,8,8);
           System.out.println("Random Coordinate within 8x8: " + cell.asString());
           System.out.println("Knight's Position1: " + cell.knightMove(1).asString());
           System.out.println("Knight's Position2: " + cell.knightMove(2).asString());
           System.out.println("Knight's Position3: " + cell.knightMove(3).asString());
           System.out.println("Knight's Position4: " + cell.knightMove(4).asString());
           System.out.println("Knight's Position5: " + cell.knightMove(5).asString());
           System.out.println("Knight's Position6: " + cell.knightMove(6).asString());
           System.out.println("Knight's Position7: " + cell.knightMove(7).asString());
           System.out.println("Knight's Position8: " + cell.knightMove(8).asString());
           System.out.println("Left: " + cell.left().asString());
           System.out.println("Right: " + cell.right().asString());
           System.out.println("Up: " + cell.up().asString());
           System.out.println("Down: " + cell.down().asString());
       }
    };
