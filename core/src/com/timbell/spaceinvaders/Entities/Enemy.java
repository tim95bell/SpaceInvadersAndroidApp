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

    public static final int BULLET_WIDTH = 3;
    public static final int BULLET_HEIGHT = 6;
    public static final int BULLET_SPEED = -2;
    public static final int MANY_PARTICLES = 75;
    public static boolean useImageOne;


    protected int x, y;

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

    public void draw(SpriteBatch batch){

        batch.setColor(color);
        if(useImageOne)
            batch.draw(imageOne, x, y, width, height);
        else
            batch.draw(imageTwo, x, y, width, height);

    }

    public ParticleEffect hit(){
        die();
        return new ParticleEffect(x+width/2, y+height/2, 10, MANY_PARTICLES, color);
    }

    public void die(){
        swarm.members.removeValue(this, true);
        swarm.updateSpeed();
    }

    public boolean move(int direction){
        x += SpaceInvaders.UNIT * direction;
        //return if it has gone past the edge of the screen
        return x+width > SpaceInvaders.WIDTH || x < 0;
    }

    public Bullet shoot(){
        return new Bullet(swarm.bullets, x+width/2-BULLET_WIDTH/2, y+height, BULLET_WIDTH, BULLET_HEIGHT, BULLET_SPEED, color);
    }

    public boolean moveDown(){
        y -= SpaceInvaders.UNIT;
        //return if it is to low, and therefore the player has lost the game
        return y+height < SpaceInvaders.LOSE_HEIGHT;
    }

    public Rectangle getRect(){
        return new Rectangle(x, y, width, height);
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public static void changeImage(){
        useImageOne = !useImageOne;
    }

}
