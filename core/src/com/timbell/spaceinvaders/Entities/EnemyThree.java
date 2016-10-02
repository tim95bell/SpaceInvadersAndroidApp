package com.timbell.spaceinvaders.Entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffectPool;
import com.timbell.spaceinvaders.SpaceInvaders;

/**
 * Created by timbell on 4/08/16.
 */

public class EnemyThree extends Enemy {

    public static final Color COLOR = new Color(0.4f, 0.4f, 1.0f, 1f);
    public static TextureRegion IMAGE_ONE;
    public static TextureRegion IMAGE_TWO;
    public static final int WIDTH = SpaceInvaders.UNIT * 2;
    public static final int HEIGHT = SpaceInvaders.UNIT * 2;

    public static final int BULLET_WIDTH = 8;//5;
    public static final int BULLET_HEIGHT = 8;//10;


    public EnemyThree(Swarm swarm, int x, int y, int index) {
        super(swarm, x, y, WIDTH, HEIGHT, index);
    }

    public void draw(SpriteBatch batch) {
        batch.setColor(COLOR);

        if (useImageOne)
            batch.draw(IMAGE_ONE, rect.x, rect.y, rect.width, rect.height);
        else
            batch.draw(IMAGE_TWO, rect.x, rect.y, rect.width, rect.height);
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
        hitSound.play(SpaceInvaders.volume);
        ParticleEffect answer = ParticleEffectPool.getLarge();
        answer.reset(0, (int) (rect.x + rect.width / 2), (int) (rect.y + rect.height / 2), 10, 10, COLOR);
        return answer;
    }

    public void shoot(Bullet bullet){}

    public void shoot(Bullet[] bullet) {
//        shootSound.play();
//        bullet.reset((int) (rect.x + rect.WIDTH / 2 - BULLET_WIDTH / 2), (int) (rect.y + rect.HEIGHT), BULLET_WIDTH, BULLET_HEIGHT, BULLET_SPEED, COLOR);
        // TODO: this has been changed for an experiment , Color changed to blue insteand of COLOR
        bullet[0].reset((int) (rect.x + rect.width / 2 - BULLET_WIDTH / 2), (int) (rect.y), BULLET_WIDTH, BULLET_HEIGHT, BULLET_SPEED / 2f, BULLET_SPEED / 2f, Color.BLUE, Bullet.Type.ROUND);
        bullet[1].reset((int) (rect.x + rect.width / 2 - BULLET_WIDTH / 2), (int) (rect.y), BULLET_WIDTH, BULLET_HEIGHT, -BULLET_SPEED/2f, BULLET_SPEED/2f, Color.BLUE, Bullet.Type.ROUND);
    }

    public int getScoreAdd(){
        return 30;
    }

}
