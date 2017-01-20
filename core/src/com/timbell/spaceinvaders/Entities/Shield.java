package com.timbell.spaceinvaders.Entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffectPool;
import com.timbell.spaceinvaders.SpaceInvaders;

/**
 * Created by timbell on 20/1/17.
 */
public class Shield {
    public static final float HEIGHT = ShieldPart.HEIGHT*4;
    public static final float WIDTH = ShieldPart.WIDTH*4;
    public static final float AT_HEIGHT = Player.Y + Player.HEIGHT + SpaceInvaders.UNIT*3;
    public static final float GAP_WIDTH = (SpaceInvaders.WIDTH - WIDTH*4)/5;
    private float x, y;
    private Rectangle rect;
    public ShieldPart shieldParts[];
    public Color color;

    public Shield(float x, float y, Color color){
        this.x = x;
        this.y = y;
        this.color = color.cpy();

        shieldParts = new ShieldPart[16];
        this.rect = new Rectangle(x, y, WIDTH, HEIGHT);

        int j = 0;
        for(int i = 0; i < shieldParts.length; i += 4){
            shieldParts[i] = new ShieldPart(x, y + (j * ShieldPart.HEIGHT), this);
            shieldParts[i + 1] = new ShieldPart(x + ShieldPart.WIDTH, y + (j * ShieldPart.HEIGHT), this);
            shieldParts[i + 2] = new ShieldPart(x + ShieldPart.WIDTH * 2, y + (j * ShieldPart.HEIGHT), this);
            shieldParts[i + 3] = new ShieldPart(x + ShieldPart.WIDTH * 3, y + (j * ShieldPart.HEIGHT), this);
            ++j;
        }
    }

    public void reset(){
        for(int i = 0; i < shieldParts.length; ++i){
            shieldParts[i].reset();
        }
    }

    public void draw(ShapeRenderer sr, float fadeTransparency){
        for(int i = 0; i < shieldParts.length; ++i) {
            sr.setColor(color.r, color.g, color.b, fadeTransparency);
            shieldParts[i].draw(sr);
        }
    }

    public ParticleEffect die(){
        for(int i = 0; i < shieldParts.length; ++i){
            shieldParts[i].die();
        }
        ParticleEffect answer = ParticleEffectPool.getLarge();
        answer.reset(0, (int)(x + HEIGHT/2), (int)(y + WIDTH/2), (int)WIDTH, (int)HEIGHT, color);
        return answer;
    }

    public Rectangle getBoundingRect(){
        return rect;
    }

    public boolean isDead(){
        boolean answer =  true;
        for(int i = 0; i < shieldParts.length; ++i){
            answer = answer && !shieldParts[i].isAlive();
        }
        return answer;
    }

}
