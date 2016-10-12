package com.timbell.spaceinvaders.Entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffectPool;
import com.timbell.spaceinvaders.SpaceInvaders;

public class EnemyTwo extends Enemy {

    public static final Color COLOR = new Color(0.4f, 1.0f, 0.4f, 1f);
    public static TextureRegion IMAGE_ONE;
    public static TextureRegion IMAGE_TWO;
    public static final int WIDTH = SpaceInvaders.UNIT * 3;
    public static final int HEIGHT = SpaceInvaders.UNIT * 2;

    private int lives;

    public EnemyTwo(Swarm swarm, int x, int y, int index) {
        super(swarm, x, y, WIDTH, HEIGHT, index);
        this.lives = 2;
    }

    public void draw(SpriteBatch batch) {
        if(lives == 2)
            batch.setColor(COLOR);
        else
            batch.setColor(Color.WHITE);

        if (useImageOne)
            batch.draw(IMAGE_ONE, rect.x, rect.y, rect.width, rect.height);
        else
            batch.draw(IMAGE_TWO, rect.x, rect.y, rect.width, rect.height);
    }

    public void draw(float xOff, float yOff, SpriteBatch batch){
        if(lives == 2)
            batch.setColor(COLOR);
        else {
            batch.setColor(Color.WHITE);
        }
        if(useImageOne)
            batch.draw(IMAGE_ONE, xOff + rect.x, yOff + rect.y, rect.width, rect.height);
        else
            batch.draw(IMAGE_TWO, xOff + rect.x, yOff + rect.y, rect.width, rect.height);
    }

    public ParticleEffect hit() {
        --lives;
        hitSound.play(SpaceInvaders.volume);
        if(lives <= 0){
            die();
            ParticleEffect answer = ParticleEffectPool.getLarge();
            answer.reset(0, (int) (rect.x + rect.width / 2), (int) (rect.y + rect.height / 2), 10, 10, Color.WHITE);
            return answer;
        }
        return null;
    }

    public void shoot(Bullet bullet) {
        bullet.reset((int) (rect.x + rect.width / 2 - BULLET_WIDTH / 2), (int) (rect.y + rect.height), BULLET_WIDTH, BULLET_HEIGHT, BULLET_SPEED, Color.GREEN, Bullet.Type.RECT);
    }

    public int getScoreAdd(){
        if(lives == 1)
            return 20;
        else
            return 0;
    }

}