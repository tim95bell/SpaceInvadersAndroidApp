package com.timbell.spaceinvaders.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.timbell.spaceinvaders.GameScreens.PlayScreen;
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

    public static Sound hitSound;

    protected static boolean useImageOne;

    protected boolean shoots;
    protected boolean dead;

    protected Rectangle rect;
    protected Swarm swarm;

    protected int index;


    public Enemy(Swarm swarm, int x, int y, int width, int height, int index){
        this.rect = new Rectangle(x, y, width, height);
        useImageOne = true;
        this.swarm = swarm;
        this.index = index;
//        this.hitSound = Gdx.audio.newSound(Gdx.files.internal("invaderkilled16bit.wav"));
//        this.hitSound = Gdx.audio.newSound(Gdx.files.internal("glassSmashTrimmed.wav"));
    }

    public abstract void shoot(Bullet bullet);
    public abstract void draw(SpriteBatch batch);
    public abstract void draw(float xOff, float yOff, SpriteBatch batch);
    public abstract ParticleEffect hit();

    public void die(){
//        swarm.members.removeValue(this, true);
        dead = true;
        swarm.memberDied(index);
    }

    public boolean move(int direction){
        rect.x += SpaceInvaders.UNIT * direction;
        //return if it has gone past the edge of the screen
        return rect.x+rect.width > SpaceInvaders.WIDTH || rect.x < 0;
    }

    public boolean moveDown(){
        rect.y -= SpaceInvaders.UNIT*2;
        //return if it is to low, and therefore the player has lost the game
        return rect.y < PlayScreen.LOSE_HEIGHT;
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

    public void setShoots(boolean val){
        shoots = val;
    }
    public boolean isShooter(){
        return shoots;
    }

    public boolean isDead(){
        return dead;
    }

    public abstract int getScoreAdd();

}
