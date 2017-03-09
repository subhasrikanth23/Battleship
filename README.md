# Battleship
A command prompt-based Java program replicating the popular Battleship board game.

<b>Overview</b>

This project implements the familiar Battleship game in Java, where there are two players each owning a fleet of vessels with different lengths positioned in the ocean (horizontally and vertically on a grid). Each player, without knowing the positions occupied by the opponent's vessels, takes turns to take a shot at different positions. If it hits a part of any of the opponent's vessels, it is “Hit” and the player gets another chance to guess; else, the opponent gets the chance. Whoever successfully destroys all the vessels of the opponent completely is declared to be the winner.

The project has been implemented with various options:
  - Human vs. Human
  - Human vs. Computer (Choices of difficulty: Simple, Smart, Very Smart, or Impossible)

This document provides the design layout for the algorithms and implementation used for these options.

<b>Basic Data Structures</b>

Each player needs to maintain a set of 2 boards (public and private). The first board is where they place their own ships (which we will
call A), and the second board shows their hits and misses (which we will call B). Each of these boards is represented by a 10x10
character array.

Each vessel has a type (e.g. “Aircraft Carrier”, “Battleship”, etc.) and a length indicating the number cells it occupies in the grid.

<b>Basic Classes</b>

<b>Coordinate:</b>
A simple cell position that represents a valid position (x, y) in the grid. It also provides handy methods to generate:
  - A random position
  - A position next to the current one in up/down/left/right directions
  - Any of the eight possible positions reachable by a knight in a chess game
The last option is an interesting choice that gets used by the “Smart Computer” player when it has to make a random guess.

<b>Player:</b>

“Player” is an interface provides the methods such as: placeShips(), fireUpon(), fire(), fireResult(), lost() etc. Each player (“Human”, “Computer”, “Smart Computer”, “Very Smart Computer” and “Impossible-to-beat Computer”) implements these methods as required.

<b>Human Player</b>

The “Human Player” class implements all the methods in the Player Interface. Some of these methods are:
  - placeShips() to collect the vessel positions from the standard input and place the vessels
  - fire() method to let the player get the target position (x, y) from the standard input
  - fireResult() to check if the target passed-in by the opponent has hit a vessel and update the internal data structures
  - fireUpon() to let the opponent know whether the target passed-in is a hit of a specific vessel type or a miss
  - lost() to check if the opponent has sunk all the vessels.  
There are other handy methods for all other miscellaneous house-keeping and checks.

<b>Simple Computer Player</b>

The “Simple Computer Player” class implements all the methods in the Player Interface similar to the “Human Player” (with some
differences). The “fire()” method does not take any input, but makes random selection of positions in the battlefield grid. The
“placeShips()” method uses a mix of random and smart selections for positioning the ships.

<b>Smart Computer Player</b>

The “Smart Computer Player” class is derived from the “Simple Computer Player,” with the key difference being in the “fire()”
method.

| # | Condition | Next Guess |
| --- | --- | --- |
| `1` | Initial Guess | Random (valid) Guess |
| `2` | Last Guess resulted in sinking an opponent's ship, with no other partially sunk ship found in our Board B | Random (valid) Guess |
| `3` | Last Guess resulted in sinking an opponent's ship, with another partially sunk ship found in our Board B | Reset the last guess to the partially sunk ship position and retry the “smart guess” (see below) |
| `4` | Last Guess was a “hit” (without sinking a ship)  | Make a "smart guess" looking both horizontally as well as vertically for making the next guess just around the Last Guess |
| `5` | Last Guess was a “miss” | Make a "smart guess" around the Last Guess, skipping its immediate next positions |
Smart Guess:
  1. When the last guess was a “miss”:
    - Check for a partially hit ship (from the previous hits); if found, return one of its neighboring positions as the next guess.
    - Get the coordinates of the possible knight-like positions (following the chess game rules) and rate them to see how good these positions are (using a smart algorithm counting the “empty positions” around them; more emptiness implies better choice).
    - Choose the “Knight Position” that gets the “best”/“good”/ “average” rating (in that order). If all of the Knight positions are found to be “bad,” make a random guess (“bad” is determined if all of the neighboring cells have been either missed already or invalid; “best”/“good”/ “average” rating is determined by the unexplored positions available around a “Knight Position”).
    - Using a “Knight Position” gets the advantage of not guessing in the same row or same column as the missed cell.
  2. When the last guess was a “hit”:
    - Check whether the next part of the same ship has been hit in the horizontal direction or vertical direction, by looking at the
     neighboring cells. If the same ship has been found as being hit, make the next guess along the appropriate direction
     (horizontal/vertical).
    - If this hit happens to be on the first part of the ship (i.e. none of the neighboring cells indicate the same ship as being hit), choose the next left/up/right/down cell if said cell has not been attempted yet.
    - If none of the neighboring cells are clear, make a random guess; however, this condition should never occur

<b>Very Smart Computer Player</b>
Some of the differences in the “Very Smart Computer Player” class (derived from “Smart Computer Player”) are:
  - “Random Guess” is made along the diagonal lines of the grid
  - “Smart Guess” uses a diagonal position (instead of the “Knight Position”) to get the “best”/“good”/ “average” rating

<b>Impossible Computer Player</b>

“Impossible Computer Player” class is actually a cheater. Whenever one of its vessels is hit by the “Human Player”, “Impossible
Computer Player” silently moves the broken ship to another strategically chosen location, making it very difficult for the “Human
Player” to make another successful “hit” around the previous guess. The purpose of this class is to add a bit of fun to the game. This
player admits at the end that it cheated.

<b>Conclusion</b>

There are many variations and strategies that can be explored, designed and implemented in the Battleship game, making it an ideal
project for students learning programming, data structures, algorithms and their efficiencies (Big O). It provides an opportunity to
encourage out-of-the-box and innovative strategical thinking as well as enjoy fun-filled programming.

<b>Reference</b>
  1. Battleship game: https://en.wikipedia.org/wiki/Battleship_(game)
