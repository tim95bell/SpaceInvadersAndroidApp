package com.timbell.spaceinvaders.ParticleEffect;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.timbell.spaceinvaders.SpaceInvaders;

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

    public void reset(float yVel, float x, float y, float xSpread, float ySpread){
        reset(0, yVel, x, y, xSpread, ySpread);
    }

    public void reset(float xVel, float yVel, float x, float y, float xSpread, float ySpread){
        loc.set( (float)(x + (Math.random()*xSpread) - xSpread/2), (float)(y + (Math.random()*ySpread) - ySpread/2) );
        vel.setToRandomDirection();
        vel.x *= (float)(Math.random()*ParticleEffect.SPEED);
        vel.y *= (float)(Math.random()*ParticleEffect.SPEED);
        vel.y += yVel;
        vel.x += xVel;
    }

     public Particle(float x, float y){
        loc = new Vector2(x, y);
        vel = new Vector2( (float)Math.random()*0.4f-0.2f, (float)Math.random()*0.4f-0.2f );
    }

    public void update(float delta){
        if(vel.len() > 10) {
            vel.nor();
            vel.scl(10);
        }

        loc.add(vel.x * delta*60, vel.y * delta*60);
    }

    public void applyForce(float x, float y){
        vel.add(x, y);
    }

    public void applyGravity(float delta){
        vel.y -= 0.05 *delta*60;
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
        bounds(1);
    }

    public void bounds(float bouncyness){
        if(loc.x < 0) {
            loc.x = 0;
            vel.x *= -0.5;
            if(vel.x < 0)
                vel.x *= -bouncyness;
        }
        else if(loc.x > SpaceInvaders.WIDTH){
            loc.x = SpaceInvaders.WIDTH;
            vel.x *= -0.5;
            if(vel.x > 0)
                vel.x *= -bouncyness;
        }

        if(loc.y < -SpaceInvaders.yOff) {
            loc.y = -SpaceInvaders.yOff;
            vel.y *= -0.5;
            if(vel.y < 0)
                vel.y *= -bouncyness;
        }
        else if(loc.y > SpaceInvaders.HEIGHT+SpaceInvaders.yOff){
            loc.y = SpaceInvaders.HEIGHT+SpaceInvaders.yOff;
            vel.y *= -0.5;
            if(vel.y > 0)
                vel.y *= -bouncyness;
        }
    }

    public void boundsStop(){
        if(loc.x < 0) {
            loc.x = 0;
            vel.x *= -0.5;
            if(vel.x < 0)
                vel.x = 0;
        }
        else if(loc.x > SpaceInvaders.WIDTH){
            loc.x = SpaceInvaders.WIDTH;
            vel.x *= -0.5;
            if(vel.x > 0)
                vel.x = 0;
        }

        if(loc.y < -SpaceInvaders.yOff) {
            loc.y = -SpaceInvaders.yOff;
            vel.y *= -0.5;
            if(vel.y < 0)
                vel.y = 0;
        }
        else if(loc.y > SpaceInvaders.HEIGHT+SpaceInvaders.yOff){
            loc.y = SpaceInvaders.HEIGHT+SpaceInvaders.yOff;
            vel.y *= -0.5;
            if(vel.y > 0)
                vel.y = 0;
        }
    }

    public float getX(){
        return loc.x;
    }

    public float getY(){
        return loc.y;
    }

    public float getVelX(){
        return vel.x;
    }
    public float getVelY(){
        return vel.y;
    }

    public void slowXVel(float multiplier){
        vel.x *= multiplier;
    }
    public void slowYVel(float multiplier){
        vel.y *= multiplier;
    }

}// END Class Particle