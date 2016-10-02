package com.timbell.spaceinvaders.ParticleEffect;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by timbell on 1/09/16.
 */
public class ParticleEffect {

    public static final float SPEED = 0.5f;
    public static final int LIFETIME = 5;//2;

    private float timeElapsed = 0;

    public Particle[] particles;
    public Color color;
    private boolean dead;


    public ParticleEffect(){
        this.dead = false;
        this.color = null;
        this.particles = null;
    }

    // for pool
    public ParticleEffect(int manyParticles){
        this.dead = false;
        this.color = null;
        this.particles = new Particle[manyParticles];
        for(int i = 0; i < manyParticles; ++i)
            particles[i] = new Particle();
    }

    public void reset(float yVel, int x, int y, int xSpread, int ySpread, Color color){
        reset(0, yVel, x, y, xSpread, ySpread, color);
    }

    public void reset(float xVel, float yVel, int x, int y, int xSpread, int ySpread, Color color){
        for(int i = 0; i < particles.length; ++i)
            particles[i].reset(xVel, yVel, x, y, xSpread, ySpread);
        this.color = color;
        this.dead = false;
        timeElapsed = 0;
    }

    public ParticleEffect(float yVel, int x, int y, int spread, int manyParticles, Color color){
        particles = new Particle[manyParticles];
        this.color = new Color(color);
        this.dead = false;

        for(int i = 0; i < manyParticles; ++i){
            particles[i] = new Particle(yVel, x, y, spread);
        }
    }

    // For BUTTONS
    // sym must have white for the symbol part of it
    public static ParticleEffect[] buttonParticleEffect(float x, float y, float size, float symX, float symY, float symSize, int manyParticles, Color outsideColor, Color insideColor, Texture sym) {
        sym.getTextureData().prepare();
        Pixmap pMap = sym.getTextureData().consumePixmap();

        Array<Particle> outsideArr = new Array<Particle>(false, 0);
        Array<Particle> insideArr = new Array<Particle>(false, 0);
        ParticleEffect outside = new ParticleEffect();
        ParticleEffect inside = new ParticleEffect();
        float scale = sym.getWidth()/symSize;
        for(int i = 0; i < manyParticles; ++i){
            Vector2 loc = new Vector2( (float)(x + (Math.random()*size)), (float)(y + (Math.random()*size)));

            // if loc is inside sym
            if(loc.x > symX && loc.x < symX+symSize && loc.y > symY && loc.y < symY+symSize) {
                Color color = new Color();
                Color.rgba8888ToColor(color, pMap.getPixel((int) (scale*(loc.x - symX)), (int) (scale*(loc.y - symY)) ));
                if (color.equals(Color.BLACK))
                    insideArr.add(new Particle(loc.x, loc.y));
                else
                    outsideArr.add(new Particle(loc.x, loc.y));
            }
            else{
                outsideArr.add(new Particle(loc.x, loc.y));
            }
        }

        pMap.dispose();

        outside.set(outsideArr, outsideColor);
        inside.set(insideArr, insideColor);

        return new ParticleEffect[]{outside, inside};
    }

    public void set(Array<Particle> pArr, Color color){
        this.particles = new Particle[pArr.size];
        for(int i = 0; i < particles.length; ++i) {
            particles[i] = pArr.get(i);
        }
        this.color = color;
        this.dead = false;
    }


    public void update(float delta){
        timeElapsed += delta;
        if(timeElapsed > LIFETIME)
            dead = true;

        for(int i = 0; i < particles.length; ++i){
            particles[i].update(delta);
        }
    }

    public void draw(ShapeRenderer sr){
        if(LIFETIME-timeElapsed < 2.5)
            sr.setColor( color.r, color.g, color.b, (LIFETIME-timeElapsed)/2.5f );
        else
            sr.setColor(color);
        for(int i = 0; i < particles.length; ++i){
            particles[i].draw(sr);
        }
    }

    public void bounds(){
        for(int i = 0; i < particles.length; ++i){
            particles[i].bounds();
        }
    }

    public boolean isDead(){
        return dead;
    }




}// END Class ParticleEffect
