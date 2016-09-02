package com.timbell.spaceinvaders.ParticleEffect;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.timbell.spaceinvaders.Assets.AssetManager;
import com.timbell.spaceinvaders.SpaceInvaders;
import com.timbell.spaceinvaders.ParticleEffect.Particle;

import javafx.stage.Screen;

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




    public ParticleEffect(Array<Particle> pArr, Color color){
        particles = new Particle[pArr.size];
        for(int i = 0; i < particles.length; ++i) {
            particles[i] = pArr.get(i);
            particles[i].setOwner(this);
        }
        this.color = new Color(color);
        this.dead = false;
    }

    public ParticleEffect(float yVel, int x, int y, int spread, int manyParticles, Color color){
        particles = new Particle[manyParticles];
        this.color = new Color(color);
        this.dead = false;

        for(int i = 0; i < manyParticles; ++i){
            particles[i] = new Particle(this, yVel, x, y, spread);
        }
    }

    public ParticleEffect(int x, int y, int spread, int manyParticles, Color color){
        particles = new Particle[manyParticles];
        this.color = new Color(color);
        this.dead = false;

        for(int i = 0; i < manyParticles; ++i){
            particles[i] = new Particle(this, 0, x, y, spread);
        }
    }

    // For BUTTONS
    public static ParticleEffect[] buttonParticleEffect(int x, int y, int spread, int manyParticles, Color outsideColor, Color insideColor, float symbolX, float symbolY, float symbolSize){
//        Pixmap pMap = ScreenUtils.getFrameBufferPixmap(0, 0, SpaceInvaders.WIDTH, SpaceInvaders.HEIGHT);
        AssetManager.playSymbol.getTextureData().prepare();
        Pixmap pMap = AssetManager.playSymbol.getTextureData().consumePixmap();

        Array<Particle> outsideArr = new Array<Particle>(false, 0);
        Array<Particle> insideArr = new Array<Particle>(false, 0);
        for(int i = 0; i < manyParticles; ++i){

            Vector2 loc = new Vector2( (float)(x + (Math.random()*spread)), (float)(y + (Math.random()*spread)));

            if(loc.x > symbolX && loc.x < symbolX+symbolSize && loc.y > symbolY && loc.y < symbolY+symbolSize) {
                Color color = new Color();
//            Color.rgba8888ToColor(color, pMap.getPixel((int)loc.x, (int)loc.y));
                Color.rgba8888ToColor(color, pMap.getPixel((int) (loc.x - symbolX), (int) (loc.y - symbolY) ));
//            if( color.equals(insideColor) ){
                if (color.equals(Color.WHITE)) {
                    insideArr.add(new Particle(loc.x, loc.y));
                } else {
                    outsideArr.add(new Particle(loc.x, loc.y));
                }
            }
            else{
                outsideArr.add(new Particle(loc.x, loc.y));
            }

        }
        ParticleEffect outside = new ParticleEffect(outsideArr, outsideColor);
        ParticleEffect inside = new ParticleEffect(insideArr, insideColor);

        return new ParticleEffect[]{outside, inside};
    }

    public ParticleEffect(int x, int y, int spread, int manyParticles){
        particles = new Particle[manyParticles];
        this.dead = false;

        for(int i = 0; i < manyParticles; ++i){
            particles[i] = new Particle(this, 0, x, y, spread);
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
        if(LIFETIME-timeElapsed < 2.5)
            color.set(color.r, color.g, color.b, (LIFETIME-timeElapsed)/2.5f );

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
