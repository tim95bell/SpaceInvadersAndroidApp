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

    public void draw(float xRel, float yRel, SpriteBatch batch){

        batch.setColor(color);
        if(useImageOne)
            batch.draw(imageOne, xRel+x, yRel+y, width, height);
        else
            batch.draw(imageTwo, xRel+x, xRel+y, width, height);

    }

    public ParticleEffect hit() {
        die();
        ParticleEffect answer = ParticleEffectPool.getLarge();
        answer.reset(0, x + width / 2, y + height / 2, 10, color);
        return answer;
    }

    public void die(){
        swarm.members.removeValue(this, true);
        swarm.updateSpeedAndShootChance();
    }

    public boolean move(int direction){
        x += SpaceInvaders.UNIT * direction;
        //return if it has gone past the edge of the screen
        return x+width > SpaceInvaders.WIDTH || x < 0;
    }

    public void shoot(Bullet bullet){
        bullet.reset(x + width / 2 - BULLET_WIDTH / 2, y + height, BULLET_WIDTH, BULLET_HEIGHT, BULLET_SPEED, color);
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
