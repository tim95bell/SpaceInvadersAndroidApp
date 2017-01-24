package com.timbell.spaceinvaders.Entities;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffectPool;
import com.timbell.spaceinvaders.SpaceInvaders;

/**
 * Created by timbell on 20/1/17.
 */
public class ShieldPart {
    public static Sound hitSound;
    public static final float WIDTH = SpaceInvaders.UNIT*1.5f;
    public static final float HEIGHT = SpaceInvaders.UNIT/2;
    private float x, y;
    private int health;
    private final int MAX_HEALTH = 1;
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

    public void draw(ShapeRenderer sr){
        if (health == 1)
            sr.rect(x, y, WIDTH, HEIGHT);
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
        answer.reset(0, 0,(int) (x + WIDTH / 2f),(int)( y ), (int)WIDTH, (int)HEIGHT, color);
        hitSound.play(SpaceInvaders.volume);
        return answer;
    }

    public ParticleEffect hitEnemyBullet(){
        --health;
        ParticleEffect answer = ParticleEffectPool.getSmall();
        answer.reset(0, 0,(int) (x + WIDTH / 2f),(int)( y + HEIGHT / 2f), (int)WIDTH, (int)HEIGHT, color);
        hitSound.play(SpaceInvaders.volume);
        return answer;
    }

    public ParticleEffect hitSpecialBullet(){
        health = 0;
        ParticleEffect answer = ParticleEffectPool.getSmall();
        answer.reset(0, 0,(int) (x + WIDTH / 2f),(int)( y + HEIGHT / 2f), (int)WIDTH, (int)HEIGHT, color);
        hitSound.play(SpaceInvaders.volume);
        return answer;
    }

    public ParticleEffect hitEnemy(){
        health = 0;
        ParticleEffect answer = ParticleEffectPool.getSmall();
        answer.reset(0, 0,(int) (x + WIDTH / 2f),(int)( y + HEIGHT / 2f), (int)WIDTH, (int)HEIGHT, color);
        return answer;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public void die(){
        health = 0;
    }
}
