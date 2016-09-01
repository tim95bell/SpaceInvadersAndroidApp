package com.timbell.spaceinvaders.ParticleEffect;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by timbell on 1/09/16.
 */
public class ParticleEffect {

    public static final int SPEED = 5;
    public static final int LIFETIME = 2;

    private float timeElapsed = 0;

    private Particle[] particles;
    private Color color;
    private boolean dead;



    public ParticleEffect(int x, int y, int spread, int manyParticles, Color color){
        particles = new Particle[manyParticles];
        this.color = color;
        this.dead = false;

        for(int i = 0; i < manyParticles; ++i){
            particles[i] = new Particle(x, y, spread);
        }
    }

    public void update(float delta){
        timeElapsed += delta;
        if(timeElapsed > LIFETIME)
            dead = true;

        for(int i = 0; i < particles.length; ++i){
            particles[i].update();
        }
    }

    public void draw(ShapeRenderer sr){
        for(int i = 0; i < particles.length; ++i){
            particles[i].draw(sr);
        }
    }

    public boolean isDead(){
        return dead;
    }


    public class Particle{
        Vector2 loc;
        Vector2 vel;
        public Particle(float x, float y, float spread){
            loc = new Vector2( (float)(x + (Math.random()*spread) - spread/2),
                    (float)(y + (Math.random()*spread) - spread/2));
            vel = new Vector2( (float)(Math.random()*SPEED*2 - SPEED),
                    (float)(Math.random()*SPEED*2 - SPEED));
        }

        public void update(){
            loc.add(vel);
        }

        public void draw(ShapeRenderer sr){
            sr.setColor(color);
            sr.circle(loc.x, loc.y, 3);
        }

    }// END Class Particle

}// END Class ParticleEffect
