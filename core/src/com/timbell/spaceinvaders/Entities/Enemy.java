package com.timbell.spaceinvaders.Entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.timbell.spaceinvaders.GameScreens.PlayScreen;
import com.timbell.spaceinvaders.SpaceInvaders;

/**
 * Created by timbell on 3/08/16.
 */
public abstract class Enemy {

    protected static int direction = 1;

    protected int x, y;
    protected boolean useImageOne;

    protected Vector3 color;
    protected TextureRegion imageOne;
    protected TextureRegion imageTwo;
    protected int width, height;

    public Enemy(int x, int y){
        this.x = x;
        this.y = y;
        useImageOne = true;
    }

    public void update(){
        useImageOne = !useImageOne;
    }

    public void draw(SpriteBatch batch){
        batch.begin();
        batch.setColor(color.x, color.y, color.z, 1f);
        if(useImageOne)
            batch.draw(imageOne, x, y, width, height);
        else
            batch.draw(imageTwo, x, y, width, height);
        batch.end();
    }

    public boolean move(){
        x += SpaceInvaders.UNIT * direction;
        //return if it has gone past the edge of the screen
        return x+width > SpaceInvaders.WIDTH || x < 0;
    }

    public boolean handleEdgeCollision(){
        x += SpaceInvaders.UNIT*2 * direction;
        y -= SpaceInvaders.UNIT;
        //return if it is to low, and therefore the player has lost the game
        return y+height < SpaceInvaders.LOSE_HEIGHT;
    }

    public boolean moveDown(){
        y -= SpaceInvaders.UNIT;
        //return if it is to low, and therefore the player has lost the game
        return y+height < SpaceInvaders.LOSE_HEIGHT;
    }

    public static void flipDirection(){
        if(direction == 1)
            direction = -1;
        else
            direction = 1;
    }

}
