package com.timbell.spaceinvaders.ParticleEffect;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.timbell.spaceinvaders.SpaceInvaders;

/**
 * Created by timbell on 2/09/16.
 */
public class Particle{
    private Vector2 loc;
    private Vector2 vel;

    public Particle(float yVel, float x, float y, float spread){
        loc = new Vector2( (float)(x + (Math.random()*spread) - spread/2),
                (float)(y + (Math.random()*spread) - spread/2));
        vel = new Vector2();
        vel.setToRandomDirection();
        vel.x *= (float)(Math.random()*ParticleEffect.SPEED);
        vel.y *= (float)(Math.random()*ParticleEffect.SPEED);
        vel.y += yVel*-0.5f;
    }

    // for pool
    public Particle(){
        this.loc = new Vector2();
        this.vel = new Vector2();
    }

    public void reset(float yVel, float x, float y, float spread){
        loc.set( (float)(x + (Math.random()*spread) - spread/2), (float)(y + (Math.random()*spread) - spread/2) );
        vel.setToRandomDirection();
        vel.x *= (float)(Math.random()*ParticleEffect.SPEED);
        vel.y *= (float)(Math.random()*ParticleEffect.SPEED);
        vel.y += yVel;
    }

     public Particle(float x, float y){
        loc = new Vector2(x, y);
        vel = new Vector2( (float)Math.random()*0.4f-0.2f, (float)Math.random()*0.4f-0.2f );
    }


    public void update(){
        //gravity
        vel.y -= 0.05;
        if(vel.y > 10)
            vel.y = 10;
        else if(vel.y < -10)
            vel.y = -10;

        loc.add(vel);
    }

    public void draw(ShapeRenderer sr){
        sr.circle(loc.x, loc.y, 1.5f);
    }

    public void bounce(float otherX, float otherY, int otherWidth, int otherHeight){
        float minXDist = otherX+otherWidth - loc.x;
        if(loc.x - otherX < minXDist)
            minXDist = loc.x - otherX;

        float minYDist = otherY+otherHeight - loc.y;
        if(loc.y - otherY < minYDist)
            minYDist = loc.y - otherY;

        if(minXDist <= minYDist){
            vel.x *= -0.5;
        }
        if(minYDist <= minXDist){
            vel.y *= -0.5;
        }
    }

    public void bounds(){
        if(loc.x < 0) {
            loc.x = 0;
            vel.x *= -0.5;
            if(vel.x < 0)
                vel.x *= -1;
        }
        else if(loc.x > SpaceInvaders.WIDTH){
            loc.x = SpaceInvaders.WIDTH;
            vel.x *= -0.5;
            if(vel.x > 0)
                vel.x *= -1;
        }

        if(loc.y < 0) {
            loc.y = 0;
            vel.y *= -0.5;
            if(vel.y < 0)
                vel.y *= -1;
        }
        else if(loc.y > SpaceInvaders.HEIGHT){
            loc.y = SpaceInvaders.HEIGHT;
            vel.y *= -0.5;
            if(vel.y > 0)
                vel.y *= -1;
        }

    }

    public float getX(){
        return loc.x;
    }

    public float getY(){
        return loc.y;
    }

}// END Class Particle