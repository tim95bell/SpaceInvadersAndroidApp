package com.timbell.spaceinvaders.Entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.ParticleEffect.Particle;

/**
 * Created by timbell on 6/08/16.
 */
public class Button {

    private float x, y, symX, symY;
    private float size, symSize;
    private Color buttonColor, symbolColor;
    private Texture symbol;
    public boolean visible;


    public Button(float x, float y, float size, Color buttonColor, Color symbolColor, Texture symbol){
        this.x = x;
        this.y = y;
        this.size = size;
        this.buttonColor = buttonColor;
        this.symbolColor = symbolColor;
        this.symbol = symbol;
        visible = true;

        symX = x + size/6;
        symY = x + size/6;
        symSize = size - size/3;
    }

    public void drawShape(ShapeRenderer sr){
        if(!visible)
            return;

        float circleRadius = size/5;//size/8;
        //rect1
        sr.setColor(buttonColor);
        sr.rect(x, y + circleRadius, size, size - (circleRadius*2));
        //rect2
        sr.rect(x + circleRadius, y, size - (circleRadius * 2), size);
        //bottom left circle
        sr.circle(x+circleRadius, y+circleRadius, circleRadius);
        //bottom right circle
        sr.circle(x + size-circleRadius, y+circleRadius, circleRadius);
        //top Left circle
        sr.circle(x+circleRadius, y + size-circleRadius, circleRadius);
        // top right circle
        sr.circle(x + size - circleRadius, y + size - circleRadius, circleRadius);
    }

    public void drawSymbol(SpriteBatch sb){
        if(!visible)
            return;

        sb.setColor(symbolColor);
        sb.draw(symbol, symX, symY, symSize, symSize);
    }

    public boolean contains(int x, int y){
        return (this.x < x && this.x+size > x)&&(this.y < y && this.y+size > y);
    }

    public Rectangle getRect(){
        return new Rectangle(x, y, size, size);
    }

    public ParticleEffect[] hit(){
        visible = false;
//        return ParticleEffect.buttonParticleEffect((int)x, (int)y, (int)size, 10000, buttonColor, symbolColor, x+difference, y+difference, size-(difference*2) );
        return ParticleEffect.buttonParticleEffect(x, y, size, symX, symY, symSize, 10000, buttonColor, symbolColor, symbol);
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public float getSize(){
        return size;
    }


}
