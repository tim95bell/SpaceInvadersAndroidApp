package com.timbell.spaceinvaders.Entities;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.SpaceInvaders;

public class Button {

    public static Texture exitSymbol;
    public static Texture playSymbol;
    public static Texture settingsSymbol;
    public static Texture retrySymbol;
    public static Texture tutorialSymbol;
    public static Texture okaySymbol;

    public static Sound hitSound;

    public enum ButtonSymbol{
        EXIT, PLAY, SETTINGS, RETRY, TUTORIAL, OKAY
    }

    private float x, y, symX, symY;
    private Rectangle rect;
    private float size, symSize;
    private Color buttonColor, symbolColor;
    private Texture symbol;
    public boolean visible;

    private ButtonSymbol type;


    public Button(float x, float y, float size, Color buttonColor, Color symbolColor, ButtonSymbol buttonSymbol){
        this.x = x;
        this.y = y;
        this.size = size;
        this.buttonColor = buttonColor;
        this.symbolColor = symbolColor;
        visible = true;

        symX = x + size/6;
        symY = y + size/6;
        symSize = size/6*4;
        this.rect = new Rectangle();

        this.type = buttonSymbol;
        if(type == ButtonSymbol.EXIT)
            this.symbol = exitSymbol;
        else if(type == ButtonSymbol.PLAY)
            this.symbol = playSymbol;
        else if(type == ButtonSymbol.SETTINGS)
            this.symbol = settingsSymbol;
        else if(type == ButtonSymbol.RETRY)
            this.symbol = retrySymbol;
        else if(type == ButtonSymbol.TUTORIAL)
            this.symbol = tutorialSymbol;
        else if(type == ButtonSymbol.OKAY)
            this.symbol = okaySymbol;
    }

    public void reset(){
        this.visible = true;
    }

    public void dispose(){
        symbol.dispose();
    }

    public void drawShape(ShapeRenderer sr, float transparancy){
        if(!visible)
            return;

        sr.setColor(buttonColor.r, buttonColor.g, buttonColor.b, transparancy);
        sr.rect(x, y, size, size);
    }

    public void drawSymbol(SpriteBatch sb, float transparancy){
        if(!visible)
            return;

        sb.setColor(symbolColor.r, symbolColor.g, symbolColor.b, transparancy);
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
        hitSound.play(SpaceInvaders.volume);
        visible = false;
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
