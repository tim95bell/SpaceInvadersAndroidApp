package com.timbell.spaceinvaders.Entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.timbell.spaceinvaders.GameScreens.PlayScreen;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.SpaceInvaders;



/**
 * Created by timbell on 3/08/16.
 */
public abstract class Enemy {

    protected int x, y;
    protected boolean useImageOne;

    protected Color color;
    protected TextureRegion imageOne;
    protected TextureRegion imageTwo;
    protected int width, height;

    protected Swarm swarm;

    public Enemy(Swarm swarm, int x, int y){
        this.x = x;
        this.y = y;
        useImageOne = true;

        this.swarm = swarm;
    }

    public void update(){
        useImageOne = !useImageOne;
    }

    public void draw(SpriteBatch batch){

        batch.setColor(color);
        if(useImageOne)
            batch.draw(imageOne, x, y, width, height);
        else
            batch.draw(imageTwo, x, y, width, height);

    }

    public ParticleEffect hit(){
        die();
        return new ParticleEffect(x, y, 10, 20, color);
    }

    public void die(){
        swarm.members.removeValue(this, true);
        swarm.updateSpeed();
    }

    public boolean move(int direction){
        useImageOne = !useImageOne;
        x += SpaceInvaders.UNIT * direction;
        //return if it has gone past the edge of the screen
        return x+width > SpaceInvaders.WIDTH || x < 0;
    }

    public boolean moveDown(){
        useImageOne = !useImageOne;
        y -= SpaceInvaders.UNIT;
        //return if it is to low, and therefore the player has lost the game
        return y+height < SpaceInvaders.LOSE_HEIGHT;
    }

    public Rectangle getRect(){
        return new Rectangle(x, y, width, height);
    }

}
