package com.timbell.spaceinvaders.Entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by timbell on 6/08/16.
 */
public class Button {

    private float x, y;
    private float size;
    private Color buttonColor, symbolColor;
    private Texture symbol;

    public Button(float x, float y, float size, Color buttonColor, Color symbolColor, Texture symbol){
        this.x = x;
        this.y = y;
        this.size = size;
        this.buttonColor = buttonColor;
        this.symbolColor = symbolColor;
        this.symbol = symbol;
    }

    public void update(){

    }

//    public void draw(ShapeRenderer sr, SpriteBatch sb){
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
//        //symbol
//        sb.setColor(symbolColor);
//        float difference = size/6;
//        sb.draw(symbol, x+difference, y+difference, size-(difference*2), size-(difference*2));
//
//    }

    public void drawShape(ShapeRenderer sr){
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
        //symbol
        sb.setColor(symbolColor);
        float difference = size/6;
        sb.draw(symbol, x + difference, y + difference, size - (difference * 2), size - (difference * 2));
    }

    public boolean contains(int x, int y){
        return (this.x < x && this.x+size > x)&&(this.y < y && this.y+size > y);
    }





}