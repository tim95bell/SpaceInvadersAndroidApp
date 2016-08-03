package com.timbell.spaceinvaders.Entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.timbell.spaceinvaders.SpaceInvaders;

import sun.jvm.hotspot.memory.Space;

/**
 * Created by timbell on 3/08/16.
 */
public class Swarm {

    public static final int ROWS = 5;
    public static final int COLS = 10;

    public static final int START_HEIGHT = SpaceInvaders.UNIT*4;

    public double sideWidth;
    Enemy[][] members;

    public Swarm(){
        members = new Enemy[ ROWS ][ COLS ];

        int swarmWidth = (COLS*3*SpaceInvaders.UNIT) + (COLS-1)*SpaceInvaders.UNIT;
        sideWidth = (SpaceInvaders.WIDTH-swarmWidth)/2.0;
    }

    public void update(){

    }

    public void draw(SpriteBatch batch){
        for(int row = 0; row < ROWS; ++row){
            for(int col = 0; col < COLS; ++col){
                members[row][col].draw(batch);
            }
        }
    }

    public boolean move(){
        boolean gonePastEdge = false;
        for(int row = 0; row < ROWS; ++row){
            for(int col = 0; col < COLS; ++col){
                // move this member, and capture it if any of them go past edge of screen
                gonePastEdge = members[row][col].move() || gonePastEdge;
            }
        }
        // if any went past edge of screen, then flip speed, move down, and move back twice
        // also capture if enemies have gone to low, then the game would be over
        boolean gameOver = false;
        Enemy.flipDirection();
        for(int row = 0; row < ROWS; ++row){
            for(int col = 0; col < COLS; ++col){
                // handle edge collision, and capture gameOver if it occurs
                gameOver = members[row][col].handleEdgeCollision() || gameOver;
            }
        }
        // returns true if the game is over
        return gameOver;
    }




    public void levelArrayToSwarm(int[][] level){

        for(int row = 0; row < ROWS; ++row){
            for(int col = 0; col < COLS; ++col){
                if(level[row][col] == 1){
                    int x = (int)sideWidth + (col*SpaceInvaders.UNIT*4) + ((col)*SpaceInvaders.UNIT);
                    int y = START_HEIGHT + (row*SpaceInvaders.UNIT*4);
                    members[row][col] = new EnemyOne( x, y );
                }
                else if(level[row][col] == 2){
                    int x = (int)sideWidth + (col*SpaceInvaders.UNIT*4) + ((col)*SpaceInvaders.UNIT);
                    int y = START_HEIGHT + (row*SpaceInvaders.UNIT*4);
                    members[row][col] = new EnemyTwo( x, y );
                }
                else if(level[row][col] == 3){
                    int x = (int)sideWidth + (col*SpaceInvaders.UNIT*4) + ((col)*SpaceInvaders.UNIT) + (SpaceInvaders.UNIT/2);
                    int y = START_HEIGHT + (row*SpaceInvaders.UNIT*4);
                    members[row][col] = new EnemyThree( x, y );
                }
            }
        }

    }


}
