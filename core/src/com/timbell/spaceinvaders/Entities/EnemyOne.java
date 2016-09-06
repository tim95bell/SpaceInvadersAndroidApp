package com.timbell.spaceinvaders.Entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.timbell.spaceinvaders.Assets.AssetManager;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffectPool;
import com.timbell.spaceinvaders.SpaceInvaders;

/**
 * Created by timbell on 4/08/16.
 */
public class EnemyOne extends Enemy {

    public static final Color COLOR = new Color(1.0f, 0.4f, 0.4f, 1f);
    public static TextureRegion IMAGE_ONE;
    public static TextureRegion IMAGE_TWO;
    public static final int WIDTH = SpaceInvaders.UNIT*3;
    public static final int HEIGHT = SpaceInvaders.UNIT*2;


    public EnemyOne(Swarm swarm, int x, int y){
        super(swarm, x, y, WIDTH, HEIGHT);
    }

    public void draw(SpriteBatch batch){
        draw(0, 0, batch);
    }

    public void draw(float xOff, float yOff, SpriteBatch batch){
        batch.setColor(COLOR);
        if(useImageOne)
            batch.draw(IMAGE_ONE, xOff + rect.x, yOff + rect.y, rect.width, rect.height);
        else
            batch.draw(IMAGE_TWO, xOff + rect.x, yOff + rect.y, rect.width, rect.height);
    }

    public ParticleEffect hit() {
        die();
        ParticleEffect answer = ParticleEffectPool.getLarge();
        answer.reset(0, (int)(rect.x + rect.width / 2), (int)(rect.y + rect.height / 2), 10, COLOR);
        return answer;
    }

    public void shoot(Bullet bullet){
        bullet.reset((int)(rect.x + rect.width / 2 - BULLET_WIDTH / 2), (int)(rect.y + rect.height), BULLET_WIDTH, BULLET_HEIGHT, BULLET_SPEED, COLOR);
    }
}
