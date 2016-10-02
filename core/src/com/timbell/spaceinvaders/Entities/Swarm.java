package com.timbell.spaceinvaders.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.timbell.spaceinvaders.Level.Level;
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

    private int numTimesMovedDown;

    public Enemy[] members;
    public double sideWidth;
    public Bullet[] bullets;
    private int numBullets;
    private int numMembersAlive;


    private float movePeriod;
    private float timeSinceMove = 0;
    private float shootChance = 0.1f;

    private float minMovePeriod, maxMovePeriod, minShootChance, maxShootChance;

    private Sound moveSound;


    public Swarm(){

        members = new Enemy[ROWS*COLS];
        bullets = new Bullet[20];
        for(int i = bullets.length-1; i >= 0; --i)
            bullets[i] = new Bullet();

        int swarmWidth = (COLS*3*SpaceInvaders.UNIT) + (COLS-1)*SpaceInvaders.UNIT;
        sideWidth = (SpaceInvaders.WIDTH-swarmWidth)/2.0;
        this.moveSound = Gdx.audio.newSound(Gdx.files.internal("fastinvader116bit.wav"));
    }

    // TODO: complete reset
    public void reset(){
        entering = true;
        numBullets = 0;
        numTimesMovedDown = 0;
    }

    public boolean update(float delta){
        boolean gameOver = false;

        timeSinceMove += delta;
        while(timeSinceMove > movePeriod) {
            //TODO uncomment this
            moveSound.play(SpaceInvaders.volume);
            Enemy.changeImage();

            gameOver = move();

            for(int i = 0; i < members.length; ++i){
                if(numBullets < bullets.length  &&  Math.random() < shootChance){
                    if(members[i].isShooter() && !members[i].isDead()) {
                        if(members[i] instanceof EnemyThree && numBullets < bullets.length-1) {
                            Bullet[] tempBullets = new Bullet[]{bullets[numBullets], bullets[numBullets+1]};
                            ((EnemyThree)members[i]).shoot(tempBullets);
                            numBullets += 2;
                        }
                        else {
                            members[i].shoot(bullets[numBullets]);
                            ++numBullets;
                        }
                    }
                }
            }

            timeSinceMove -= movePeriod;
        }

        for(int i = 0; i < numBullets; ++i){
            bullets[i].update(delta);
        }

        return gameOver;
    }

    public void draw(SpriteBatch batch){
        for(int i = 0; i < members.length; ++i){
            if(!members[i].isDead())
                members[i].draw(batch);
        }
    }

    public void draw(SpriteBatch batch, float xRel, float yRel){
        for(int i = 0; i < members.length; ++i) {
            if(!members[i].isDead())
                members[i].draw(xRel, yRel, batch);
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

    public void clearBullets(){
        numBullets = 0;
    }

    public boolean move(){
        boolean gonePastEdge = false;

        for(int i = 0; i < members.length; ++i){
            if(!members[i].isDead())
                gonePastEdge = members[i].move(direction) || gonePastEdge;
        }

        // if any went past edge of screen, then flip speed, move down, and move back twice
        // also capture if enemies have gone to low, then the game would be over
        boolean gameOver = false;
        if(gonePastEdge) {
            ++numTimesMovedDown;
            direction *= -1;
            for(int i = 0; i < members.length; ++i){
                if(!members[i].isDead()) {
                    members[i].move(direction);
                    gameOver = members[i].moveDown() || gameOver;
                }
            }

        }
        // returns true if the game is over
        return gameOver;
    }


    public void loadLevel(Level level){
        numMembersAlive = 0;

        for(int x = 0; x < COLS; ++x){
            for(int y = 0; y < ROWS; ++y){
                int membersIndex = x*ROWS + y;
                int levelIndex = y*COLS + x;
                if(level.members[levelIndex] == 1){
                    int X = (int)sideWidth + (x*SpaceInvaders.UNIT*4);
                    int Y = START_HEIGHT - (y*SpaceInvaders.UNIT*3);
                    members[membersIndex] = new EnemyOne(this, X, Y, membersIndex);
                    ++numMembersAlive;
                }
                else if(level.members[levelIndex] == 2){
                    int X = (int)sideWidth + (x*SpaceInvaders.UNIT*4);
                    int Y = START_HEIGHT - (y*SpaceInvaders.UNIT*3);
                    members[membersIndex] = new EnemyTwo(this, X, Y, membersIndex);
                    ++numMembersAlive;
                }
                else if(level.members[levelIndex] == 3){
                    int X = (int)sideWidth + (x*SpaceInvaders.UNIT*4) + (SpaceInvaders.UNIT/2);
                    int Y = START_HEIGHT - (y*SpaceInvaders.UNIT*3);
                    members[membersIndex] = new EnemyThree(this, X, Y, membersIndex);
                    ++numMembersAlive;
                }
                else{
                    members[membersIndex] = new EnemyThree(this, 0, 0, membersIndex);
                    members[membersIndex].dead = true;
                }

                if(y == 4)
                    members[membersIndex].setShoots(true);
            }
        }

        this.minMovePeriod = level.getMinMovePeriod();
        this.maxMovePeriod = level.getMaxMovePeriod();
        this.minShootChance = level.getMinShootPeriod();
        this.maxShootChance = level.getMaxShootPeriod();
        this.movePeriod = maxMovePeriod;
        this.shootChance = minShootChance;

        this.numBullets = 0;
        movePeriod = maxMovePeriod;
    }

    public void memberDied(int index){
        --numMembersAlive;
        float percent = (float)(numMembersAlive)/(float)(ROWS*COLS);
//        float percentSqr = percent*percent;
        float percentSqrt = (float)Math.sqrt((double)percent);
//        movePeriod = 0.1f + percentSqrt*0.9f;
        movePeriod = minMovePeriod + percentSqrt*(maxMovePeriod-minMovePeriod);
        shootChance = minShootChance + (1f - percentSqrt)*(maxShootChance-minShootChance);


        if(members[index].isShooter()) {
            --index;
            while (index >= 0) {
                if (!(members[index].isDead() && members[index].isDead())) {
                    members[index].setShoots(true);
                    break;
                }
                --index;
            }
        }
    }

    public void setEntering(boolean entering){
        this.entering = entering;
    }

    public int getNumBullets(){
        return numBullets;
    }

    public int getNumMembersAlive(){
        return numMembersAlive;
    }

    public int getNumTimesMovedDown() { return numTimesMovedDown; }




}
