package com.timbell.spaceinvaders.Entities;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffectPool;
import com.timbell.spaceinvaders.SpaceInvaders;


/**
 * Created by timbell on 9/09/16.
 */
public class MotherShip {

    public enum State{
        ALIVE, DYING, DEAD, PAUSED
    }

    private static final float WIDTH = SpaceInvaders.UNIT*4;
    private static final float HEIGHT = SpaceInvaders.UNIT*1.5f;
    public static TextureRegion image;
    public static Sound sound;
    public static final Color COLOR = Color.ORANGE;

    private Rectangle rect;
    private float xSpeed;

    private State state;

    private float startOffset = SpaceInvaders.UNIT;

    private BitmapFont font;
    private GlyphLayout layout;

    private final int dyingPeriod = 3;
    private float dyingTime;


    public MotherShip(){
        this.state = State.DEAD;
        this.rect = new Rectangle(0, SpaceInvaders.HEIGHT - SpaceInvaders.UNIT * 4, WIDTH, HEIGHT);
        startOffset = 30f;
        this.font = new BitmapFont();
        // smooth font
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.getData().setScale(1f);
        this.layout = new GlyphLayout();
    }

    public void reset(){
        sound.loop(SpaceInvaders.volume*0.5f);
        this.state = State.ALIVE;
        float x;
        if(Math.random() < 0.5){
            x = -startOffset;
            this.xSpeed = 80f;
        } else{
            x = SpaceInvaders.WIDTH + startOffset;
            this.xSpeed = -80f;
        }
        this.rect.setX(x);
        this.dyingTime = 0;
    }

    public void update(float delta){
        if(state == State.DEAD)
            return;

        if(state == State.DYING){
            dyingTime += delta;
            if(dyingTime > dyingPeriod)
                state = State.DEAD;
        }
        else if(state == State.ALIVE) {
            rect.x += delta * xSpeed;
            if (rect.x > SpaceInvaders.WIDTH + startOffset || rect.x + rect.width < 0 - startOffset)
                dieNow();
        }
    }

    public void draw(SpriteBatch sb) {
        if (state == State.ALIVE || state == State.PAUSED) {
            sb.setColor(COLOR);
            sb.draw(image, rect.x, rect.y, rect.width, rect.height);
        }
        else if(state == State.DYING){
            float x = rect.x+rect.width/2;//layout.WIDTH/4;
            if(x-layout.width/2 < 0)
                x = layout.width/2;
            else if(x+layout.width/2 > SpaceInvaders.WIDTH)
                x = SpaceInvaders.WIDTH-layout.width/2;

            font.draw(sb, layout, x, rect.y);
        }
    }

    public void die(String powerupStr){
        layout.setText(font, powerupStr, Color.WHITE, rect.width, 0, false);
        sound.stop();
        state = State.DYING;
    }

    public void dieNow(){
        sound.stop();
        state = State.DEAD;
    }


    public ParticleEffect hit(String powerupStr){
        die(powerupStr);
        ParticleEffect particleEffect = ParticleEffectPool.getSmall();
        particleEffect.reset(0, (int)rect.x, (int)rect.y, (int)rect.width, (int)rect.height, Color.ORANGE);
        return particleEffect;
    }

    public void pause(){
        if(state == State.ALIVE) {
            sound.pause();
            state = State.PAUSED;
        }
    }

    public void resume(){
        if(state == State.PAUSED) {
            sound.resume();
            state = State.ALIVE;
        }
    }

    public boolean isAlive(){
        return state == State.ALIVE;
    }

    public Rectangle getRect(){
        return rect;
    }

    public State getState(){
        return state;
    }

    public void setState(State state){
        this.state = state;
    }
}
