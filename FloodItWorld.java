import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Deque;
import java.util.LinkedList;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

// We have extra credit utilities to our game:
//      -- A time counter on the bottom
//      -- Keeping time, lowest time creates a high score on a leaderboard on the right side

interface INode {
  // is this node flooded
  boolean isFlooded();

  // are two cells the same color
  boolean sameColor(Color other);

  // make the next cell flooded
  void makeFlooded();

  // adds this cell to the given array
  void addTo(ArrayList<Cell> arr);

  // adds the given to the hashmap
  void addToHash(Hashtable<Integer, Cell> ht);

  // adds the cell to the deque
  void addToDeque(Deque<Cell> d);
}

class Leaf implements INode {
  // the lack of a cell is not flooded
  public boolean isFlooded() {
    return false;
  }

  //the lack of a cell has no color
  public boolean sameColor(Color other) {
    return false;
  }

  // cannot make a missing cell flooded
  public void makeFlooded() {
    // does nothing because an empty cell will never be flooded
    // is needed so that double dispatch can be used to call this
    // function on actual cells
  }

  // does nothing because leaves are not desired in the arraylist
  public void addTo(ArrayList<Cell> arr) {
    // this function does nothing because
    // the leaf should not be added to the
    // arraylist
  }

  // does nothing because leaves are not desired in the hashtable
  public void addToHash(Hashtable<Integer, Cell> ht) {
    // this function does nothing because
    // the leaf should not be added to the
    // hashtable
  }

  // this function does nothing but is needed for double dispatch
  public void addToDeque(Deque<Cell> d) {
    // this function does nothing because
    // the leaf should not be added to the
    // deque
  }
}

//Represents a single square of the game area
class Cell implements INode {
  // In logical coordinates, with the origin at the top-left corner of the screen
  int x;
  int y;
  Color color;
  boolean flooded;
  // the four adjacent cells to this one
  INode left;
  INode top;
  INode right;
  INode bottom;

  // constructor for a cell
  Cell(int x, int y, Color color, boolean flooded) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded = flooded;
    this.left = null;
    this.top = null;
    this.right = null;
    this.bottom = null;
  }

  // returns the image representing this cell
  RectangleImage draw() {
    return new RectangleImage(30, 30, OutlineMode.SOLID, this.color);
  }

  // a cell is flooded if it is flooded
  public boolean isFlooded() {
    return this.flooded;
  }

  // true if both cells have the same color
  public boolean sameColor(Color other) {
    return this.color.equals(other);
  }

  // makes this cell flooded
  // EFFECT: Makes this cell flooded
  public void makeFlooded() {
    this.flooded = true;
  }

  // adds this cell to the given arraylist at back
  public void addTo(ArrayList<Cell> arr) {
    arr.add(this);
  }

  // adds this cell to the deque
  public void addToDeque(Deque<Cell> d) {
    d.addLast(this);
  }

  // adds this cell to the hashtable
  public void addToHash(Hashtable<Integer, Cell> ht) {
    ht.put(this.hashCode(), this);
  }

  // unique hashcode for each cell during a game
  // hashmap used for extra speed/credit
  public int hashCode() {
    return this.x + this.y * FloodItWorld.BOARD_SIZE;
  }

  // override equals to use hash
  // instanceof is acceptable because equals is being overridden
  public boolean equals(Object other) {
    if (other instanceof Cell) {
      Cell temp = (Cell) other;
      return this.x == temp.x && this.y == temp.y;
    }
    return false;
  }

}

class ExamplesINode {
  INode leaf = new Leaf();
  INode testCell1 = new Cell(0, 0, Color.red, false);
  INode testCell2 = new Cell(8, 4, Color.blue, true);

  ArrayList<Cell> testArray = new ArrayList<Cell>();
  Hashtable<Integer, Cell> testHashTable = new Hashtable<Integer, Cell>();
  Deque<Cell> testDeque = new LinkedList<Cell>();

  // resets the cells for testing purposes
  void reset() {
    leaf = new Leaf();
    testCell1 = new Cell(0, 0, Color.red, false);
    testCell2 = new Cell(8, 4, Color.blue, true);
    testArray = new ArrayList<Cell>();
    testHashTable = new Hashtable<Integer, Cell>();
    testDeque = new LinkedList<Cell>();
  }

  // tests the cell's method to draw itself
  boolean testDrawCell(Tester t) {
    Cell cell1 = new Cell(0, 0, Color.red, false);
    Cell cell2 = new Cell(8, 4, Color.blue, true);
    boolean test1 = t.checkExpect(cell1.draw(),
        new RectangleImage(30, 30, OutlineMode.SOLID, Color.red));
    boolean test2 = t.checkExpect(cell2.draw(),
        new RectangleImage(30, 30, OutlineMode.SOLID, Color.blue));
    return test1 && test2;
  }

  // tests checking for flooded status
  boolean testIsFlooded(Tester t) {
    reset();
    return t.checkExpect(leaf.isFlooded(), false)
        && t.checkExpect(testCell1.isFlooded(), false)
        && t.checkExpect(testCell2.isFlooded(), true);
  }

  // tests making a cell flooded
  boolean testMakeFlooded(Tester t) {
    reset();
    boolean test1 = t.checkExpect(leaf.isFlooded(), false);
    leaf.makeFlooded();
    boolean test2 = t.checkExpect(leaf.isFlooded(), false);
    boolean test3 = t.checkExpect(testCell1.isFlooded(), false);
    testCell1.makeFlooded();
    boolean test4 = t.checkExpect(testCell1.isFlooded(), true);
    boolean test5 = t.checkExpect(testCell2.isFlooded(), true);
    testCell2.makeFlooded();
    boolean test6 = t.checkExpect(testCell2.isFlooded(), true);
    reset();
    return test1 && test2 && test3 && test4 && test5 && test6;
  }

  // test if the INode is of the given color
  boolean testSameColor(Tester t) {
    return t.checkExpect(leaf.sameColor(Color.white), false)
        && t.checkExpect(testCell1.sameColor(Color.red), true)
        && t.checkExpect(testCell1.sameColor(Color.blue), false)
        && t.checkExpect(testCell2.sameColor(Color.red), false)
        && t.checkExpect(testCell2.sameColor(Color.blue), true);
  }

  // test the addTo function
  boolean testAddTo(Tester t) {
    reset();
    boolean test1 = t.checkExpect(testArray.size(), 0);
    leaf.addTo(testArray);
    boolean test2 = t.checkExpect(testArray.size(), 0);
    testCell1.addTo(testArray);
    boolean test3 = t.checkExpect(testArray.get(0), testCell1);
    testCell2.addTo(testArray);
    boolean test4 = t.checkExpect(testArray.get(1), testCell2);
    reset();
    return test1 && test2 && test3 && test4;
  }

  // tests the overridden hashcode function for cells
  boolean testHashCode(Tester t) {
    return t.checkExpect(testCell1.hashCode(), 0)
        && t.checkExpect(testCell2.hashCode(), 8 + 4 * FloodItWorld.BOARD_SIZE); 
  }

  boolean testAddToHash(Tester t) {
    reset();
    boolean test1 = t.checkExpect(testHashTable.size(), 0);
    this.testCell1.addToHash(this.testHashTable);
    boolean test2 = t.checkExpect(testHashTable.size(), 1);
    boolean test3 = t.checkExpect(testHashTable.get(testCell1.hashCode()), this.testCell1);
    reset();

    return test1 && test2 && test3;
  }

  boolean testAddToDeque(Tester t) {
    reset();
    boolean test1 = t.checkExpect(testDeque.size(), 0);
    this.testCell1.addToDeque(this.testDeque);
    boolean test2 = t.checkExpect(testDeque.size(), 1);
    boolean test3 = t.checkExpect(testDeque.getFirst(), this.testCell1);
    reset();

    return test1 && test2 && test3;
  }
}


class FloodItWorld extends World {
  // All the cells of the game
  ArrayList<Cell> board;


  // a deque used to queue the flooded elements
  // I assume using the inbuilt deque is acceptable
  // given that we already made one in assignment 8
  // Deque is an IDeque and LinkedList is an IList
  Deque<Cell> waterfall = new LinkedList<Cell>();

  // hashtable to store previously visited nodes 
  // with quicker lookup times than an arraylist
  Hashtable<Integer, Cell> visited = new Hashtable<Integer, Cell>();

  // hashtable to store nodes in the queue that have not yet been visited
  // makes it quicker
  Hashtable<Integer, Cell> queue = new Hashtable<Integer, Cell>();

  // game constants
  static final int BOARD_SIZE = 12;      // must be 4 < X < 50
  int numberOfColors;
  int moveLimit;
  Random rand;
  ArrayList<Color> colors = new ArrayList<Color>(Arrays.asList(Color.red, Color.green,
      Color.yellow, Color.blue, Color.cyan, Color.magenta));


  // game variables
  int movesMade;
  boolean flooding;
  int floodStage;
  Color currentColor;
  int tilesTouched;
  int timer;
  boolean wonGame = false;
  boolean lostGame = false;
  int bestTime = -1;

  // constructors
  FloodItWorld(Random rand) {
    this.rand = rand;
  }

  // constructor given random
  FloodItWorld(int numColors, Random rand) {
    this(rand);
    this.movesMade = 0;
    this.floodStage = 0;
    this.timer = 0;
    this.tilesTouched = 0;
    this.numberOfColors = numColors;
    this.moveLimit = (int) (Math.floor((50 * BOARD_SIZE * numColors / (28 * 6)))) + 3;
    this.board = initializeBoard(numColors);
    this.currentColor = this.board.get(0).color;
    this.waterfall.add(this.board.get(0));
    this.flooding = true;
  }

  // constructor given just board restraints
  FloodItWorld(int numColors) {
    this(new Random());
    this.movesMade = 0;
    this.floodStage = 0;
    this.timer = 0;
    this.tilesTouched = 0;
    this.moveLimit = (int) (Math.floor((50 * BOARD_SIZE * numColors / (28 * 6)))) + 3;
    this.numberOfColors = numColors;
    this.board = initializeBoard(numColors);
    this.currentColor = this.board.get(0).color;
    this.waterfall.add(this.board.get(0));
    this.flooding = true;
  }

  // creates the initial board
  ArrayList<Cell> initializeBoard(int numColors) {
    ArrayList<Cell> tempBoard = new ArrayList<Cell>();

    for (int i = 0; i < BOARD_SIZE; i++) {
      for (int j = 0; j < BOARD_SIZE; j++) {
        if (i == 0 && j == 0) {
          tempBoard.add(new Cell(0, 0, colors.get(rand.nextInt(numColors)), true));
        }
        else {
          tempBoard.add(new Cell(j, i, colors.get(rand.nextInt(numColors)), false));
        }
      }
    }

    // link the cells to each other in 2-D Grid
    for (Cell c:tempBoard) {
      if (c.x != 0) {
        c.left = tempBoard.get(c.y * BOARD_SIZE + c.x - 1);
      }
      else {
        c.left = new Leaf();
      }
      if (c.x != BOARD_SIZE - 1) {
        c.right = tempBoard.get(c.y * BOARD_SIZE + c.x + 1);
      }
      else {
        c.right = new Leaf();
      }
      if (c.y != 0) {
        c.top = tempBoard.get((c.y * BOARD_SIZE) - BOARD_SIZE + c.x);
      }
      else {
        c.top = new Leaf();
      }
      if (c.y != BOARD_SIZE - 1) {
        c.bottom = tempBoard.get((c.y * BOARD_SIZE) + BOARD_SIZE + c.x);
      }
      else {
        c.bottom = new Leaf();
      }
    }
    return tempBoard;
  }

  // resets the game on button press of r
  // EFFECT: resets the game variables
  public void onKeyEvent(String k) {
    if (k.equalsIgnoreCase("r")) {
      this.movesMade = 0;
      this.timer = 0;
      this.tilesTouched = 0;
      this.wonGame = false;
      this.lostGame = false;
      this.board = initializeBoard(numberOfColors);
      this.currentColor = this.board.get(0).color;
      this.waterfall.add(this.board.get(0));
      this.flooding = true;
    }
  }

  // records a mouse click and changes the board accordingly
  // EFFECTS: may flood board if clicked accordingly
  public void onMouseClicked(Posn p) {
    // if the click is within the playable region (the cells)
    if (p.x > 35 && p.x < 30 * BOARD_SIZE + 35 
        && p.y > 35 && p.y < 30 * BOARD_SIZE + 35 && !this.flooding 
        && (!wonGame && !lostGame)) {
      int indexCellClicked = (p.x - 35) / 30 + (p.y - 35) / 30 * BOARD_SIZE;
      this.flood(this.board.get(indexCellClicked));
    }
  }

  // floods the board with the given color
  // EFFECT: Changes the flooded state of affected cells,
  // adds one to movesMade if valid move is made
  void flood(Cell c) {
    if (this.currentColor != c.color) {
      this.currentColor = c.color;
      this.board.get(0).color = c.color;
      this.flooding = true;
      this.waterfall.add(this.board.get(0));
      this.queue.put(this.waterfall.getFirst().hashCode(), this.waterfall.getFirst());
      movesMade++;
    }
  }



  //animate the waterfall motion of the color transition
  // uses breadth first search to simulate flowing water
  // EFFECT: Create the waterfall-like pattern for display
  void animateFlood() {
    // if the list is non-empty and the element is flooded
    if (this.waterfall.size() > 0 && !this.visited.contains(this.waterfall.getFirst())) {

      Cell currentCell = this.waterfall.getFirst();
      INode left = currentCell.left;
      INode top = currentCell.top;
      INode right = currentCell.right;
      INode bottom = currentCell.bottom;

      if (left.sameColor(this.currentColor)) {
        left.makeFlooded();
      }
      if (top.sameColor(this.currentColor)) {
        top.makeFlooded();
      }
      if (right.sameColor(this.currentColor)) {
        right.makeFlooded();
      }
      if (bottom.sameColor(this.currentColor)) {
        bottom.makeFlooded();
      }

      // makes this cell the new color
      currentCell.color = this.currentColor;

      // adds new cells to the queue
      if (left.isFlooded() 
          && !this.queue.contains(left) 
          && !this.visited.contains(left)) {
        left.addToDeque(this.waterfall);
        left.addToHash(this.queue);
      }
      if (top.isFlooded() 
          && !this.queue.contains(top) 
          && !this.visited.contains(top)) {
        top.addToDeque(this.waterfall);
        top.addToHash(this.queue);
      }
      if (right.isFlooded() 
          && !this.queue.contains(right) 
          && !this.visited.contains(right)) {
        right.addToDeque(this.waterfall);
        right.addToHash(this.queue);
      }
      if (bottom.isFlooded() 
          && !this.queue.contains(bottom)
          && !this.visited.contains(bottom)) {
        bottom.addToDeque(this.waterfall);
        bottom.addToHash(this.queue);
      }
    }

    if (this.waterfall.size() == 0) {
      this.flooding = false;
      this.tilesTouched = this.visited.size();
      this.visited.clear();
      this.queue.clear();
    }
    else {

      if (!this.visited.contains(this.waterfall.getFirst())) {
        this.visited.put(this.waterfall.getFirst().hashCode(), this.waterfall.getFirst());
      }
      this.waterfall.removeFirst();
    }
  }

  // changes the flood over the tick rate
  public void onTick() {

    if (this.flooding) {
      this.animateFlood();
    }
    else if (this.tilesTouched == BOARD_SIZE * BOARD_SIZE
        && this.movesMade <= this.moveLimit) {
      this.wonGame = true;
      if (this.bestTime > this.timer / 1000 || this.bestTime == -1) {
        this.bestTime = this.timer / 1000;
      }
    }
    else if (this.movesMade >= this.moveLimit) {
      this.lostGame = true;
    }

    if (!wonGame && !lostGame) {
      timer += 1;
    }
  }


  // draws the game
  public WorldScene makeScene() {
    WorldScene scene = new WorldScene(2000, 2000);
    for (Cell c:this.board) {
      scene.placeImageXY(c.draw(), c.x * 30 + 50, c.y * 30 + 50);
    }
    scene.placeImageXY(new TextImage("Moves made: " + movesMade + "/" + this.moveLimit,
        22, Color.black),
        (BOARD_SIZE * 30 + 100) / 2, BOARD_SIZE * 30 + 80);
    scene.placeImageXY(new TextImage(timer / 1000 + "  Seconds Passed", 22, Color.black), 
        (BOARD_SIZE * 30 + 100) / 2, BOARD_SIZE * 30 + 120);
    if (wonGame) {
      scene.placeImageXY(new TextImage("You win :)", 20, Color.green),
          (BOARD_SIZE * 30 + 100) / 2, 20);
    }
    else if (lostGame) {
      scene.placeImageXY(new TextImage("You lose :(", 20, Color.red),
          (BOARD_SIZE * 30 + 100) / 2, 20);
    }
    if (bestTime > -1) {
      scene.placeImageXY(new TextImage("Best Time(sec): " + this.bestTime, 19, Color.black),
          (BOARD_SIZE * 30) + 115, (BOARD_SIZE * 30 + 100) / 2);
    }
    return scene;
  }
}


class ExamplesFloodItWorld {

  // tests for the INodes and Cells can be found
  // directly under those classes @ line 119

  FloodItWorld World1 = new FloodItWorld(3, new Random(42));
  FloodItWorld World2 = new FloodItWorld(3, new Random(42));


  // resets the worlds for testing purposes
  void reset() {
    World1 = new FloodItWorld(3, new Random(42));
    World2 = new FloodItWorld(3, new Random(42));
  }

  // test random board generation
  boolean testColorRandomization(Tester t) {
    reset();
    boolean test1 = t.checkExpect(World1.rand.nextInt(), World2.rand.nextInt());
    boolean test2 = t.checkExpect(World1.board.get(0), World2.board.get(0));
    boolean test3 = t.checkExpect(World1.board.get(1), World2.board.get(1));
    boolean test4 = t.checkExpect(World1.board.get(2), World2.board.get(2));
    boolean test5 = t.checkExpect(World1.board.get(3), World2.board.get(3));
    reset();
    return test1 && test2 && test3 && test4 && test5;
  }

  // tests that the key handler correctly processes keyPresses
  boolean testKeyEvents(Tester t) {
    reset();
    boolean test1 = t.checkExpect(World1, World2);
    World1.onKeyEvent("o");
    boolean test2 = t.checkExpect(World1, World2);
    World1.onKeyEvent("r");
    boolean test3 = t.checkFail(World1, World2);
    World2.onKeyEvent("r");
    boolean test4 = t.checkExpect(World1, World2);
    reset();
    boolean test5 = t.checkExpect(World1, World2);
    World1.onKeyEvent("R");// uppercase resets as well
    World2.onKeyEvent("r");
    boolean test6 = t.checkExpect(World1, World2);
    return test1 && test2 && test3 && test4 && test5 && test6;
  }

  // tests that the click handler correctly processes mouse clicks
  boolean testMouseEvents(Tester t) {
    reset();
    boolean test1 = t.checkExpect(World1, World2);
    World1.onMouseClicked(new Posn(0, 0));
    boolean test2 = t.checkExpect(World1, World2);
    World1.onMouseClicked(new Posn(74, 38));
    World2.onMouseClicked(new Posn(74, 38));
    boolean test3 = t.checkExpect(World1, World2);
    reset();
    return test1 && test2 && test3;
  }

  // tests that everything is checked on tick
  // Extra Credit: Tests for the timer/time record in this method
  boolean testOnTick(Tester t) {
    reset();
    World1.movesMade = World1.moveLimit - 1;
    World1.onTick();
    boolean test1 = t.checkExpect(World1.lostGame, false); // no loss one under limit
    World1.movesMade = World1.moveLimit;
    World1.flooding = false;
    World1.onTick();
    boolean test2 = t.checkExpect(World1.lostGame, true); // loss when limit reached
    reset();
    World1.movesMade = World1.moveLimit;
    World1.tilesTouched = FloodItWorld.BOARD_SIZE * FloodItWorld.BOARD_SIZE;
    World1.flooding = false;
    World1.onTick();
    boolean test3 = t.checkExpect(World1.wonGame, true); // win is found at the limit
    reset();
    boolean test4 = t.checkExpect(World1.timer, 0);
    World1.onTick();
    boolean test5 = t.checkExpect(World1.timer, 1); // timer advances
    World1.onTick();
    boolean test6 = t.checkExpect(World1.timer, 2); // timer advances more
    World1.wonGame = true;
    World1.onTick();
    boolean test7 = t.checkExpect(World1.timer, 2); // timer stops ticking after game ends
    reset();
    World1.timer = 2007;
    World1.tilesTouched = FloodItWorld.BOARD_SIZE * FloodItWorld.BOARD_SIZE;
    World1.flooding = false;
    World1.onTick();
    boolean test8 = t.checkExpect(World1.bestTime, 2); // stores the score after a win
    World1.onKeyEvent("r");
    World1.timer = 1504;
    World1.tilesTouched = FloodItWorld.BOARD_SIZE * FloodItWorld.BOARD_SIZE;
    World1.flooding = false;
    World1.onTick();
    boolean test9 = t.checkExpect(World1.bestTime, 1); // faster speeds overwrite slower ones
    reset();

    return test1 && test2 && test3 && test4 
        && test5 && test6 && test7 && test8 && test9;
  }


  // tests that flood initiates the flooding process
  boolean testFloodAndAnimate(Tester t) {
    reset();
    // waits for the board to initialize
    while (World1.flooding) {
      World1.onTick();
    }
    boolean test1 = t.checkExpect(World1.currentColor, World1.board.get(0).color);
    boolean test2 = t.checkExpect(World1.flooding, false);
    boolean test3 = t.checkExpect(World1.waterfall.size(), 0);
    World1.flood(new Cell(1, 0, Color.red, false));
    boolean test4 = t.checkExpect(World1.currentColor, Color.red);
    boolean test5 = t.checkExpect(World1.flooding, true);
    boolean test6 = t.checkExpect(World1.board.get(0).color, Color.red);
    boolean test7 = t.checkExpect(World1.waterfall.size(), 1);
    boolean test8 = t.checkExpect(World1.waterfall.getFirst().color, World1.currentColor);
    World1.onTick();
    boolean test9 = t.checkExpect(World1.waterfall.getFirst().color, World1.currentColor);
    while (World1.flooding) {
      World1.onTick();
    }
    // all cells that should be flooded are the correct color
    boolean test10 = true;
    for (Cell c:World1.board) {
      if (c.flooded) {
        test10 = test10 && c.sameColor(World1.currentColor);
      }
    }
    test10 = t.checkExpect(test10, true);
    reset();

    return test1 && test2 && test3 && test4 
        && test5 && test6 && test7 && test8 
        && test9 && test10;
  }

  // tests the bigBang function/the game itself
  void testBigBang(Tester t) {
    FloodItWorld testGuy = new FloodItWorld(4);
    int gameSize = (FloodItWorld.BOARD_SIZE * 30) + 100;
    testGuy.bigBang(gameSize + 105, gameSize + 60, 0.0001);
  }
}