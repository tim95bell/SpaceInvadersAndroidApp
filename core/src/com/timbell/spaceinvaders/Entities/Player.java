package com.timbell.spaceinvaders.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.timbell.spaceinvaders.SpaceInvaders;

/**
 * Created by timbell on 21/07/16.
 */
public class Player {

    // NOTE: Everything in here so far is just to test

    private Vector2 loc;

    public Player(){
        loc = new Vector2(100, 100);//SpaceInvaders.HEIGHT-60);
    }

    public void draw(ShapeRenderer sr){
        sr.setColor(0.0f, 1.0f, 0.0f, 1.0f);
        sr.rect(loc.x, loc.y, 20, 20);
    }

    public void update() {
        move(Gdx.input.getAccelerometerY());
    }

    public void move(float amount){
        loc.x += amount*3;
        if(loc.x > 620)
            loc.x = 620;
        else if(loc.x < 0)
            loc.x = 0;

    }

}
