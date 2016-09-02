package com.timbell.spaceinvaders.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.timbell.spaceinvaders.Assets.AssetManager;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.SpaceInvaders;
import com.timbell.spaceinvaders.ParticleEffect.Particle;

import com.badlogic.gdx.math.Rectangle;

/**
 * Created by timbell on 21/07/16.
 */
public class Player {

    private final int width = (int)(SpaceInvaders.UNIT*3.5);
    private final int height = (int)(SpaceInvaders.UNIT*2.5);

    private Vector2 loc;
    private float xAcc, xVel;
    private float xAccelerometerVel;

    private int maxBullets = 20;
    public Array<Bullet> bullets;

    public Player(){
        this.loc = new Vector2(SpaceInvaders.WIDTH/2, SpaceInvaders.UNIT*2);

        this.xAcc = 0;
        this.xVel = 0;
        this.xAccelerometerVel = 0;

        this.bullets = new Array<Bullet>(true, 0);
    }

    public void draw(SpriteBatch sb){

        sb.setColor(AssetManager.playerColor);
        sb.draw(AssetManager.playerImage, loc.x, loc.y, width, height);

    }

    public void drawBullets(ShapeRenderer sr){
        for(int i = 0; i < bullets.size; ++i) {
            bullets.get(i).draw(sr);
        }
    }

    public void update() {
        move();

        for(int i = 0; i < bullets.size; ++i) {
            bullets.get(i).update();
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
            loc.x = SpaceInvaders.WIDTH-width;
        else if(loc.x < 0)
            loc.x = 0;

    }

    public void shoot(){
        if( bullets.size < maxBullets ){
            bullets.add( new Bullet(bullets, (int)loc.x+width/2-(5/2), (int)loc.y+height, 5, 10, /*2*/8, AssetManager.playerColor) );
        }
    }

    public ParticleEffect hit(){
        return new ParticleEffect((int)loc.x+width/2, (int)loc.y+height/2, 10, 75, AssetManager.playerColor);
    }

    public Rectangle getRect(){
        return new Rectangle((int)loc.x, (int)loc.y, width ,height);
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

}
