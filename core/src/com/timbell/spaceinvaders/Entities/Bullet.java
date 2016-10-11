package com.timbell.spaceinvaders.Entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffectPool;

public class Bullet {
    protected int x, y, width, height;
    private Rectangle rect;
    protected float xSpeed, ySpeed;
    public Color color;
    public enum Type{
        RECT, ROUND
    }
    private Type type;

    public Bullet(){
        this.rect = new Rectangle();
    }

    public void reset(int x, int y, int width, int height, float ySpeed, Color color, Type type){
        reset(x, y, width, height, 0, ySpeed, color, type);
    }

    public void reset(int x, int y, int width, int height, float xSpeed, float ySpeed, Color color,Type type){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.color = color;
        this.type = type;
    }

    public void update(float delta){
        x += xSpeed *delta*60;
        y += ySpeed *delta*60;
    }

    public void draw(ShapeRenderer sr){
        sr.setColor(color);
        if(this.type == Type.ROUND)
            sr.ellipse(x, y, width, height);
        else if(this.type == Type.RECT)
            sr.rect(x, y, width, height);
    }

    public ParticleEffect hit(){
        float tempYSpeed = ySpeed > 0 ? 0 : -ySpeed*0.3f;
        ParticleEffect answer = ParticleEffectPool.getSmall();
        answer.reset(xSpeed, tempYSpeed, x + width / 2, y + height / 2, 3, 3, color);
        return answer;
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
        rect.set(x, y, width, height);
        return rect;
    }
}
