package com.timbell.spaceinvaders.Entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffectPool;
import com.timbell.spaceinvaders.SpaceInvaders;



/**
 * Created by timbell on 3/08/16.
 */
public abstract class Enemy {

    public static final int BULLET_WIDTH = 5;
    public static final int BULLET_HEIGHT = 10;
    public static final int BULLET_SPEED = -2;

    protected static boolean useImageOne;

    protected Rectangle rect;
    protected Swarm swarm;


    public Enemy(Swarm swarm, int x, int y, int width, int height){
        this.rect = new Rectangle(x, y, width, height);
        useImageOne = true;
        this.swarm = swarm;
    }

    public abstract void shoot(Bullet bullet);
    public abstract void draw(SpriteBatch batch);
    public abstract void draw(float xOff, float yOff, SpriteBatch batch);
    public abstract ParticleEffect hit();

    public void die(){
        swarm.members.removeValue(this, true);
        swarm.updateSpeedAndShootChance();
    }

    public boolean move(int direction){
        rect.x += SpaceInvaders.UNIT * direction;
        //return if it has gone past the edge of the screen
        return rect.x+rect.width > SpaceInvaders.WIDTH || rect.x < 0;
    }

    public boolean moveDown(){
        rect.y -= SpaceInvaders.UNIT;
        //return if it is to low, and therefore the player has lost the game
        return rect.y+rect.height < SpaceInvaders.LOSE_HEIGHT;
    }

    public Rectangle getRect(){
        return rect;
    }

    public int getX(){
        return (int)rect.x;
    }

    public int getY(){
        return (int)rect.y;
    }

    public int getWidth(){
        return (int)rect.width;
    }

    public int getHeight(){
        return (int)rect.height;
    }

    public static void changeImage(){
        useImageOne = !useImageOne;
    }

}
