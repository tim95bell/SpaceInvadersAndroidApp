package com.timbell.spaceinvaders.Entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.timbell.spaceinvaders.SpaceInvaders;

/**
 * Created by timbell on 20/1/17.
 */
public class Shield {
    public static final float HEIGHT = ShieldPart.HEIGHT*2;
    public static final float WIDTH = ShieldPart.WIDTH*4;
    public static final float AT_HEIGHT = Player.Y + Player.HEIGHT + SpaceInvaders.UNIT*3;
    public static final float GAP_WIDTH = (SpaceInvaders.WIDTH - WIDTH*4)/5;
    private float x, y;
    private Rectangle rect;
    public ShieldPart shieldParts[];
    public Color color;
    public Health health;

    public enum Health{
        FULL, DAMAGED, NONE
    }

    public Shield(float x, float y, Color color){
        this.x = x;
        this.y = y;
        this.color = color.cpy();
        shieldParts = new ShieldPart[8];
        health = Health.FULL;
        this.rect = new Rectangle(x, y, WIDTH, HEIGHT);

        shieldParts[0] = new ShieldPart(x, y, this);
        shieldParts[1] = new ShieldPart(x + ShieldPart.WIDTH, y, this);
        shieldParts[2] = new ShieldPart(x + ShieldPart.WIDTH*2, y, this);
        shieldParts[3] = new ShieldPart(x + ShieldPart.WIDTH*3, y, this);

        shieldParts[4] = new ShieldPart(x, y + ShieldPart.HEIGHT, this);
        shieldParts[5] = new ShieldPart(x + ShieldPart.WIDTH, y + ShieldPart.HEIGHT, this);
        shieldParts[6] = new ShieldPart(x + ShieldPart.WIDTH*2, y + ShieldPart.HEIGHT, this);
        shieldParts[7] = new ShieldPart(x + ShieldPart.WIDTH*3, y + ShieldPart.HEIGHT, this);
    }

    public void reset(){
        health = Health.FULL;
        for(int i = 0; i < shieldParts.length; ++i){
            shieldParts[i].reset();
        }
    }

    public void draw(SpriteBatch batch, float fadeTransparency){
        if(health != Health.NONE){
            for(int i = 0; i < shieldParts.length; ++i){
                batch.setColor(color.r, color.g, color.b, fadeTransparency);
                shieldParts[i].draw(batch);
            }
        }
    }

    public Rectangle getBoundingRect(){
        return rect;
    }

}
