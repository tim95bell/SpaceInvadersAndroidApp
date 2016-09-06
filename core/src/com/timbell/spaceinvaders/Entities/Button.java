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

    private static Texture exitSymbol;
    private static Texture playSymbol;
    private static Texture settingsSymbol;
    private static Texture retrySymbol;

    public static enum ButtonSymbol{
        EXIT, PLAY, SETTINGS, RETRY
    }

    private float x, y, symX, symY;
    private Rectangle rect;
    private float size, symSize;
    private Color buttonColor, symbolColor;
    private Texture symbol;
    public boolean visible;

    private ButtonSymbol type;

    // TODO: consider whether this creating and disposing of images is nessisary, good, and will work. think multiple buttons.


    public Button(float x, float y, float size, Color buttonColor, Color symbolColor, ButtonSymbol buttonSymbol){
        this.x = x;
        this.y = y;
        this.size = size;
        this.buttonColor = buttonColor;
        this.symbolColor = symbolColor;
        visible = true;

        symX = x + size/6;
        symY = y + size/6;
//        symSize = size - size/3;
        symSize = size/6*4;
        this.rect = new Rectangle();

        if(buttonSymbol == ButtonSymbol.EXIT){
            exitSymbol = new Texture("exitSymbol.png");
            this.symbol = exitSymbol;
            this.type = ButtonSymbol.EXIT;
        } else if(buttonSymbol == ButtonSymbol.PLAY){
            playSymbol = new Texture("playSymbol.png");
            this.symbol = playSymbol;
            this.type = ButtonSymbol.PLAY;
        } else if(buttonSymbol == ButtonSymbol.SETTINGS){
            settingsSymbol = new Texture("settingsSymbol.png");
            this.symbol = settingsSymbol;
            this.type = ButtonSymbol.SETTINGS;
        } else if(buttonSymbol == ButtonSymbol.RETRY){
            retrySymbol = new Texture("retrySymbol.png");
            this.symbol = retrySymbol;
            this.type = ButtonSymbol.RETRY;
        }
    }

    // TODO: implement reset
    public void reset(){

    }

    public void dispose(){
        symbol.dispose();
    }


    public void drawShape(ShapeRenderer sr){
        if(!visible)
            return;

//        float circleRadius = size/5;//size/8;
//        //rect1
//        sr.setColor(buttonColor);
//        sr.rect(x, y + circleRadius, size, size - (circleRadius*2));
//        //rect2
//        sr.rect(x + circleRadius, y, size - (circleRadius * 2), size);
//        //bottom left circle
//        sr.circle(x+circleRadius, y+circleRadius, circleRadius);
//        //bottom right circle
//        sr.circle(x + size-circleRadius, y+circleRadius, circleRadius);
//        //top Left circle
//        sr.circle(x+circleRadius, y + size-circleRadius, circleRadius);
//        // top right circle
//        sr.circle(x + size - circleRadius, y + size - circleRadius, circleRadius);

        sr.setColor(buttonColor);
        sr.rect(x, y, size, size);

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
        rect.set(x, y, size, size);
        return rect;
    }

    public ParticleEffect[] hit(){
        visible = false;
        dispose();
        return ParticleEffect.buttonParticleEffect(x, y, size, symX, symY, symSize, 4000, buttonColor, symbolColor, symbol);
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

    public ButtonSymbol getType(){
        return type;
    }


}
