package com.timbell.spaceinvaders.Entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffectPool;
import com.badlogic.gdx.graphics.Color;

/**
 * Created by timbell on 2/10/16.
 */
public class SpecialBullet extends Bullet {

    public enum Direction{
        VERTICAL, LEFT, RIGHT
    }

    private final int SIZE = 10;

    private boolean isDead;
    private boolean hasHadFirstHit;
    private Direction direction;

    public SpecialBullet(){
        super();
        this.isDead = true;
    }

    //public void reset(int x, int y, int WIDTH, int HEIGHT, float xSpeed, float ySpeed, Color color,Type type){
    public void reset(int x, int y, Direction direction){
        this.isDead = false;
        this.hasHadFirstHit = false;
        this.direction = direction;
        super.reset( x, y, SIZE, SIZE, 0, 8, Color.WHITE, Type.ROUND);
    }

    public ParticleEffect hit(){
        if(!hasHadFirstHit){
            hasHadFirstHit = true;
            width = height;
            if(direction == Direction.RIGHT) {
                xSpeed = ySpeed;
                ySpeed = 0;
            }
            else if(direction == Direction.LEFT) {
                xSpeed = -ySpeed;
                ySpeed = 0;
            }
            else if(direction == Direction.VERTICAL) {

            }
        }

        float tempYSpeed = ySpeed > 0 ? 0 : -ySpeed*0.3f;
        ParticleEffect answer = ParticleEffectPool.getSmall();
        answer.reset(xSpeed, tempYSpeed, x + width / 2, y + height / 2, 3, 3, color);
        return answer;
    }

    public ParticleEffect hitBullet(){
        float tempYSpeed = ySpeed > 0 ? 0 : -ySpeed*0.3f;
        ParticleEffect answer = ParticleEffectPool.getSmall();
        answer.reset(xSpeed, tempYSpeed, x + width / 2, y + height / 2, 3, 3, color);
        return answer;
    }

    public void die(){
        this.isDead = true;
    }

    public void update(float delta){
        if(!isDead)
            super.update(delta);
    }

    public void draw(ShapeRenderer sr){
        if(!isDead)
            super.draw(sr);
    }

    public boolean isDead(){
        return isDead;
    }

}
