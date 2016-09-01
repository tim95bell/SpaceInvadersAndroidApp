package com.timbell.spaceinvaders.Entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import com.badlogic.gdx.math.Rectangle;

/**
 * Created by timbell on 1/09/16.
 */
public class Bullet {
    private int x, y, width, height;
    private float ySpeed;

    public Color color;

    private Array owningList;

    public Bullet(Array owningList, int x, int y, int width, int height, float ySpeed, Color color){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.ySpeed = ySpeed;
        this.color = color;
        this.owningList = owningList;
    }

    public void update(){
        y += ySpeed;
    }

    public void draw(ShapeRenderer sr){
        sr.setColor(color);
        sr.rect(x, y, width, height);
    }

    public void hit(){
        owningList.removeValue(this, true);
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public Rectangle getRect(){
        return new Rectangle(x, y, width, height);
    }
}