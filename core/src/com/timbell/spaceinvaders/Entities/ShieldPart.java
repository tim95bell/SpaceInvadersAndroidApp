package com.timbell.spaceinvaders.Entities;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffectPool;
import com.timbell.spaceinvaders.SpaceInvaders;

/**
 * Created by timbell on 20/1/17.
 */
public class ShieldPart {
    public static Texture HIT0_IMAGE;
    public static Texture HIT1_IMAGE;
    public static Texture HIT2_IMAGE;
    public static Sound hitSound;
    public static final float WIDTH = SpaceInvaders.UNIT*1.5f;
    public static final float HEIGHT = SpaceInvaders.UNIT;
    private float x, y;
    private int health;
    private final int MAX_HEALTH = 3;
    private Rectangle rect;
    private Color color;

    public ShieldPart(float x, float y, Shield owner){
        this.x = x;
        this.y = y;
        this.health = MAX_HEALTH;
        rect = new Rectangle(x, y, WIDTH, HEIGHT);
        color = owner.color;
    }

    public void reset(){
        this.health = MAX_HEALTH;
    }

    public void draw(SpriteBatch batch){
        if(health == 3)
            batch.draw(HIT0_IMAGE, x, y, WIDTH, HEIGHT);
        else if(health == 2)
            batch.draw(HIT1_IMAGE, x, y, WIDTH, HEIGHT);
        else if(health == 1)
            batch.draw(HIT2_IMAGE, x, y, WIDTH, HEIGHT);
    }

    public boolean isAlive(){
        return health > 0;
    }

    public Rectangle getRect(){
        return rect;
    }

    public ParticleEffect hitPlayerBullet(){
        --health;
        ParticleEffect answer = ParticleEffectPool.getSmall();
        answer.reset(0, 0,(int) (x + WIDTH / 2f),(int)( y ), 3, 3, color);
        hitSound.play(SpaceInvaders.volume);
        return answer;
    }

    public ParticleEffect hitEnemyBullet(){
        --health;
        ParticleEffect answer = ParticleEffectPool.getSmall();
        answer.reset(0, 0,(int) (x + WIDTH / 2f),(int)( y + HEIGHT / 2f), 3, 3, color);
        hitSound.play(SpaceInvaders.volume);
        return answer;
    }

    public ParticleEffect hitSpecialBullet(){
        health = 0;
        ParticleEffect answer = ParticleEffectPool.getSmall();
        answer.reset(0, 0,(int) (x + WIDTH / 2f),(int)( y + HEIGHT / 2f), 3, 3, color);
        hitSound.play(SpaceInvaders.volume);
        return answer;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }
}
