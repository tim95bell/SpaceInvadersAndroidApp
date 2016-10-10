package com.timbell.spaceinvaders.ParticleEffect;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by timbell on 10/10/16.
 */
public class AttractingParticleEffect {
    public static final int NUM_PARTICLES = 200;

    private Particle[] particles;
    private Array<Integer> aliveIndexes;
    public Color color;
    private float attractorX, attractorY;

    public AttractingParticleEffect(){
        particles = new Particle[NUM_PARTICLES];
        aliveIndexes = new Array<Integer>(NUM_PARTICLES);
        for(int i = 0; i < NUM_PARTICLES; ++i){
            particles[i] = new Particle();
        }
    }

    public void reset(int x, int y, int xSpread, int ySpread, Color color, float attractorX, float attractorY){
        aliveIndexes.clear();
        for(int i = 0; i < particles.length; ++i) {
            particles[i].reset(0, 0, x, y, xSpread, ySpread);
            aliveIndexes.add(i);
        }
        this.color = color;
        this.attractorX = attractorX;
        this.attractorY = attractorY;
    }

    public void setAttractor(float x, float y){
        this.attractorX = x;
        this.attractorY = y;
    }

    public void update(float delta){
        for(int i = 0; i < particles.length; ++i){
            Vector2 dir = new Vector2( attractorX-particles[i].getX(), attractorY-particles[i].getY() );
            float dist = dir.len();
            if(dist > 300)
                dist = 300;
            float mul = dist/300;
            mul *= 0.1f;
            mul = 1f-mul;
            dir.nor();
            if( (dir.x > 0 && particles[i].getVelX() < 0)  ||  (dir.x < 0 && particles[i].getVelX() > 0) )
                particles[i].slowXVel();
            particles[i].applyForce(dir.x * 0.05f * delta * 60f * mul, dir.y * 0.05f * delta * 60f * mul);
            particles[i].update(delta);

            dir.set(attractorX - particles[i].getX(), attractorY-particles[i].getY());
            if(dir.len() < 20)
                aliveIndexes.removeValue(i, false);

            particles[i].bounds();
        }
    }

    public void draw(ShapeRenderer sr){
        sr.setColor( color );
        for(int i = 0; i < aliveIndexes.size; ++i){
            particles[aliveIndexes.get(i)].draw(sr);
        }
//        sr.setColor(Color.GREEN);
//        sr.circle(attractorX, attractorY, 20);
    }

    public void bounds(){
        for(int i = 0; i < particles.length; ++i){
            particles[i].bounds();
        }
    }

    public void die(){
        aliveIndexes.clear();
    }

    public int getNumAlive(){
        return aliveIndexes.size;
    }
}
