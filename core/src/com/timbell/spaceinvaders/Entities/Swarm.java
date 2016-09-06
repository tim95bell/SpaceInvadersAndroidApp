package com.timbell.spaceinvaders.Entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.timbell.spaceinvaders.SpaceInvaders;

/**
 * Created by timbell on 3/08/16.
 */
public class Swarm {

    public static final int ROWS = 5;
    public static final int COLS = 10;

//    private float enterPos;
    private boolean entering;

    private int direction = 1;

    public static final int START_HEIGHT = SpaceInvaders.HEIGHT - SpaceInvaders.UNIT*4;

    public double sideWidth;
//    Enemy[][] members;
    // TODO: change members to an array, and make only the bottom one shoot. also instead of deleting them, simply make them dead
    public Array<Enemy> members;
    public Bullet[] bullets;
    int numBullets;


    private float movePeriod;
    private float timeSinceMove = 0;
    private float shootChance = 0.01f;


    public Swarm(int[][] level){
        entering = true;
//        enterPos = -SpaceInvaders.WIDTH;

//        members = new Enemy[ ROWS ][ COLS ];
        members = new Array(false, ROWS*COLS);
        bullets = new Bullet[20];
        for(int i = bullets.length-1; i >= 0; --i)
            bullets[i] = new Bullet();
        numBullets = 0;

        levelArrayToSwarm(level);

        int swarmWidth = (COLS*3*SpaceInvaders.UNIT) + (COLS-1)*SpaceInvaders.UNIT;
        sideWidth = (SpaceInvaders.WIDTH-swarmWidth)/2.0;

        updateSpeedAndShootChance();
    }

    // TODO: complete reset
    public void reset(){

    }

    public void update(float delta){

        timeSinceMove += delta;
        while(timeSinceMove > movePeriod) {
            Enemy.changeImage();

            boolean gameOver = move();

            for(int i = 0; i < members.size; ++i){
                if(numBullets < bullets.length  &&  Math.random() < shootChance){
                    members.get(i).shoot(bullets[numBullets]);
                    ++numBullets;
                }
            }

            timeSinceMove -= movePeriod;
        }

        for(int i = 0; i < numBullets; ++i){
            bullets[i].update(delta);
        }
    }

    public void draw(SpriteBatch batch){
        for(int i = 0; i < members.size; ++i){
            members.get(i).draw(batch);
        }
    }

    public void draw(SpriteBatch batch, float xRel, float yRel){
        for(int i = 0; i < members.size; ++i) {
            members.get(i).draw(xRel, yRel, batch);
        }
    }

    public void drawBullets(ShapeRenderer sr){
        for(int i = 0; i < numBullets; ++i) {
            bullets[i].draw(sr);
        }
    }

    public void removeBullet(int index){
        // swap bullets[index] with bullets[numBullets-1]
        if(numBullets > 0 && numBullets < bullets.length) {
            Bullet temp = bullets[index];
            bullets[index] = bullets[numBullets - 1];
            bullets[numBullets - 1] = temp;
            --numBullets;
        }
    }

    public boolean move(){
        boolean gonePastEdge = false;

        for(int i = 0; i < members.size; ++i){
            gonePastEdge = members.get(i).move(direction) || gonePastEdge;
        }

        // if any went past edge of screen, then flip speed, move down, and move back twice
        // also capture if enemies have gone to low, then the game would be over
        boolean gameOver = false;
        if(gonePastEdge) {
            direction *= -1;
            for(int i = 0; i < members.size; ++i){
                members.get(i).move(direction);
                gameOver = members.get(i).moveDown() || gameOver;
            }

        }
        // returns true if the game is over
        return gameOver;
    }




    public void levelArrayToSwarm(int[][] level){
        entering = true;
//        enterPos = -SpaceInvaders.WIDTH;

        for(int row = 0; row < ROWS; ++row){
            for(int col = 0; col < COLS; ++col){
                if(level[row][col] == 1){
                    int x = (int)sideWidth + (col*SpaceInvaders.UNIT*4);
                    int y = START_HEIGHT - (row*SpaceInvaders.UNIT*3);
                    members.add( new EnemyOne(this, x, y) );
                }
                else if(level[row][col] == 2){
                    int x = (int)sideWidth + (col*SpaceInvaders.UNIT*4);
                    int y = START_HEIGHT - (row*SpaceInvaders.UNIT*3);
                    members.add( new EnemyTwo(this, x, y) );
                }
                else if(level[row][col] == 3){
                    int x = (int)sideWidth + (col*SpaceInvaders.UNIT*4) + (SpaceInvaders.UNIT/2);
                    int y = START_HEIGHT - (row*SpaceInvaders.UNIT*3);
                    members.add( new EnemyThree(this, x, y) );
                }
            }
        }


    }

    public void updateSpeedAndShootChance(){
        float percent = (float)(members.size-1)/(float)(ROWS*COLS);
//        float percentSqr = percent*percent;
        float percentSqrt = (float)Math.sqrt((double)percent);
        movePeriod = 0.1f + percentSqrt*0.9f;
        shootChance = 0.01f + (1f - percentSqrt)*0.1f;
    }

    public void setEntering(boolean entering){
        this.entering = entering;
    }

    public int getNumBullets(){
        return numBullets;
    }




}
