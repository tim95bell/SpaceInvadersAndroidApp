package com.timbell.spaceinvaders.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.timbell.spaceinvaders.Assets.AssetManager;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffectPool;
import com.timbell.spaceinvaders.SpaceInvaders;
import com.timbell.spaceinvaders.ParticleEffect.Particle;

import com.badlogic.gdx.math.Rectangle;

/**
 * Created by timbell on 21/07/16.
 */
public class Player {

    private final int width = (int)(SpaceInvaders.UNIT*3.5);
    private final int height = (int)(SpaceInvaders.UNIT*2.5);

    // these are relative to loc
    private Rectangle[] rects;

    private Vector2 loc;
    private float xAcc, xVel;
    private float xAccelerometerVel;

    private boolean entering;

    public Bullet[] bullets;
    private int numBullets;

    public Player(){
        this.entering = false;
        this.loc = new Vector2(SpaceInvaders.WIDTH/2, SpaceInvaders.UNIT*2);

        int w = width;
        this.rects = new Rectangle[3];
        rects[0] = new Rectangle(0, 0, w, w/3);
        rects[1] = new Rectangle(w/2f - w/8f, w/3f, w/4f, w/5f);
        rects[2] = new Rectangle(w/2f-w/26f, w/3f + w/5f, w/13f, w/13f);

        this.xAcc = 0;
        this.xVel = 0;
        this.xAccelerometerVel = 0;

        this.bullets = new Bullet[]{new Bullet(), new Bullet()};
        this.numBullets = 0;
    }

    public void draw(ShapeRenderer sr){

//        sb.setColor(AssetManager.playerColor);
//        sb.draw(AssetManager.playerImage, loc.x, loc.y, width, height);

        sr.setColor(AssetManager.playerColor);

        for(int i = 0; i < rects.length; ++i) {
            sr.rect(loc.x + rects[i].getX(), loc.y + rects[i].getY(), rects[i].getWidth(), rects[i].getHeight());
        }

    }

    public void drawBullets(ShapeRenderer sr){
        for(int i = 0; i < numBullets; ++i){
            bullets[i].draw(sr);
        }
    }

    public void update() {
        move();

        for(int i = 0; i < numBullets; ++i){
            bullets[i].update();
        }
    }

    public void applyXForce(float force){
        xAcc += force;
    }

    public void friction(float force){
        xVel *= force;
    }

    public void move(){
        xAccelerometerVel = Gdx.input.getAccelerometerY();

        //apply friction
        friction(0.9f);

        //apply acceleration and velocity
        xVel += xAcc;
        loc.x += xVel;

        //apoly accelerometer velocity
        xVel += xAccelerometerVel;

        //keys
        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean space = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        if(left && !right){
            xVel -= 1.0f;
        } else if(right && !left){
            xVel += 1.0f;
        }


        if(xVel > 5)
            xVel = 5;
        else if(xVel < -5)
            xVel = -5;

        //reset acceleration
        xAcc = 0;

        if(loc.x+width > SpaceInvaders.WIDTH)
            loc.x = SpaceInvaders.WIDTH - width;
        else if(loc.x < 0)
            loc.x = 0;

    }

    public void shoot(){
        if( numBullets == 0 && !entering ){
            bullets[0].reset((int)loc.x+width/2-(5/2), (int)loc.y+height, 5, 10, /*2*/8, AssetManager.playerColor);
            ++numBullets;
        }
    }

    public void removeBullet(int index){
        // swap bullets[index] with bullets[numBullets-1]
        if(numBullets > 0 && numBullets < 3) {
            Bullet temp = bullets[index];
            bullets[index] = bullets[numBullets - 1];
            bullets[numBullets - 1] = temp;
            --numBullets;
        }
    }

    public ParticleEffect hit(){
        ParticleEffect answer = ParticleEffectPool.getLarge();
        answer.reset(0, (int)loc.x+width/2, (int)loc.y+height/2, 10, AssetManager.playerColor);
        return answer;
    }

    // TODO: stop these news
    public Rectangle[] getRects(){
        Rectangle[] answer = new Rectangle[]{new Rectangle(rects[0]), new Rectangle(rects[1]), new Rectangle(rects[2])};
        for(int i = 0; i < rects.length; ++i){
            answer[i].x += loc.x;
            answer[i].y += loc.y;
        }
        return answer;
    }

    public float getX(){
        return loc.x;
    }

    public float getY(){
        return loc.y;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public int getNumBullets(){
        return numBullets;
    }

    public void setEntering(boolean entering){
        this.entering = entering;
    }

}
