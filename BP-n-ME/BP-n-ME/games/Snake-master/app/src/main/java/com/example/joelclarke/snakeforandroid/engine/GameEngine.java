package com.example.joelclarke.snakeforandroid.engine;

import com.example.joelclarke.snakeforandroid.classes.Coordinates;
import com.example.joelclarke.snakeforandroid.enums.Difficulty;
import com.example.joelclarke.snakeforandroid.enums.Direction;
import com.example.joelclarke.snakeforandroid.enums.GameState;
import com.example.joelclarke.snakeforandroid.enums.TileType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by joelclarke on 21/08/2017.
 */

public class GameEngine {
    public static final int GameWidth = 24;
    public static final int GameHeight = 32;

    public void UpdateDifficulty(Difficulty newDiff)
    {
        currentDiff = newDiff;
    }

    private Difficulty currentDiff = Difficulty.Medium;

    //objects within the game area
    private List<Coordinates> walls = new ArrayList<>();
    private List<Coordinates> snake = new ArrayList<>();
    private List<Coordinates> food = new ArrayList<>();

    private int Score = 0;

    //random individual coordinates for food
    private Random random = new Random();

    //increase length after eating
    private boolean increaseTail = false;

    private Direction currentDirection = Direction.East;

    private GameState currentGameState = GameState.Running;



    //first point in the ArrayList is the snake head
    private Coordinates getSnakeHead() {
        return snake.get(0);
    }

        public GameEngine(){

    }

    public void initGame(){

        AddSnake();
        AddWalls();
        AddFood();
    }

    public void UpdateDirection(Direction newDirection) {
            if ( Math.abs(newDirection.ordinal() - currentDirection.ordinal()) % 2 == 1 ) {
                //can turn at 90 degrees but no other turn available
                currentDirection = newDirection;
            }
    }

    public void Update() {
        switch (currentDirection)
        {
            //update position of snake dependant on direction
            case North:
                UpdateSnake(0,-1);
                break;
            case East:
                UpdateSnake(1,0);
                break;
            case South:
                UpdateSnake(0,1);
                break;
            case West:
                UpdateSnake(-1,0);
                break;
        }

        //has it hit the wall?
        for (Coordinates w : walls) {

            if ( snake.get(0).equals(w)) {
                currentGameState = GameState.Lost;
            }
        }

        //collided with self
        for (int i = 1; i < snake.size(); i++) {
            if (getSnakeHead().equals(snake.get(i))) {
                currentGameState = GameState.Lost;
                return;
            }
        }

        int x = 0;
        //eaten food?
        Coordinates foodRemoval = null;
        for (Coordinates f : food) {
            if (getSnakeHead().equals(f)) {
                foodRemoval = f;
                increaseTail = true;
                //update score
                switch (currentDiff)
                {
                    case Easy:
                        Score = Score + 50;
                        break;
                    case Medium:
                        Score = Score + 100;
                        break;
                    case Hard:
                        Score = Score + 200;
                        break;
                }

            }
        }
        if (foodRemoval != null){
            food.remove(foodRemoval);
            AddFood();
            x = x + 1;
        }
    }

    //determine the game area and the positioning of objects
    public TileType[][] getMap() {
        TileType[][] map = new TileType[GameWidth][GameHeight];

        for (int x = 0; x < GameWidth; x++) {
            for(int y = 0; y < GameHeight; y++) {
                map[x][y] = TileType.Nothing;
            }
        }

        for ( Coordinates s : snake) {
            map[s.getX()][s.getY()] = TileType.SnakeTail;
        }

        for ( Coordinates f : food ) {
            map[f.getX()][f.getY()] = TileType.Food;
        }
        map[snake.get(0).getX()][snake.get(0).getY()] = TileType.SnakeHead;

        for (Coordinates wall: walls) {
            map[wall.getX()][wall.getY()] = TileType.Wall;
        }

        return map;
    }

    private void UpdateSnake( int x, int y) {
        int newX = snake.get(snake.size() - 1).getX();
        int newY = snake.get(snake.size() - 1).getY();

        for (int i = snake.size() -1 ; i > 0; i--) {
            snake.get(i).setX( snake.get(i-1).getX() );
            snake.get(i).setY( snake.get(i-1).getY() );
        }

        if (increaseTail) {
            snake.add(new Coordinates(newX, newY));
            increaseTail = false;
        }

        snake.get(0).setX( snake.get(0).getX() + x);
        snake.get(0).setY( snake.get(0).getY() + y);
    }

    //coordinates for the snake on the game square to start with
    private void AddSnake() {
        snake.clear();

        //amount of snake.add determines starting size of the snake
        snake.add(new Coordinates(9,7));
        snake.add(new Coordinates(8,7));
        snake.add(new Coordinates(7,7));
        snake.add(new Coordinates(6,7));
        snake.add(new Coordinates(5,7));
        snake.add(new Coordinates(4,7));
        snake.add(new Coordinates(3,7));
        snake.add(new Coordinates(2,7));
        snake.add(new Coordinates(1,7));
    }

    //coordinates for the walls on the game square
    private void AddWalls() {
        //roof and floor of square
        for (int i = 0; i < GameWidth; i++) {
            walls.add(new Coordinates(i,0));
            walls.add(new Coordinates(i,GameHeight-1));
        }

        //left and right wall
        for (int x = 0; x < GameHeight; x++) {
            walls.add(new Coordinates(0,x));
            walls.add(new Coordinates(GameWidth - 1, x));
        }
    }

    //coordinates for the food that appears randomly
    private void AddFood() {
        Coordinates coordinates = null;

        boolean added = false;
        while (!added) {
            int x = 1 + random.nextInt( GameWidth - 2);
            int y = 1 + random.nextInt( GameHeight - 2 );

            coordinates = new Coordinates( x , y );
            boolean collision = false;
            for (Coordinates s : snake)
            {
                if (s.equals(coordinates)) {
                    collision = true;
                    break;
                }
            }

            for (Coordinates a : food) {
                if (a.equals(coordinates)) {
                    collision = true;
                    //break;
                }
            }

            added = !collision;
        }

        food.add(coordinates);
    }

    public GameState getCurrentGameState() {
        return currentGameState;
    }

    public Integer getScore()
    {
        return Score;
    }



}

