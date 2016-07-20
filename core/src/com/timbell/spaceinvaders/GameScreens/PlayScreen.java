package com.timbell.spaceinvaders.GameScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.timbell.spaceinvaders.Entities.Player;

/**
 * Created by timbell on 18/07/16.
 */
public class PlayScreen implements GameScreen {

    Player p1;

    // NOTE: sr is just to test
    ShapeRenderer sr;

    public PlayScreen(Camera cam){
        // NOTE: Just a test
        sr = new ShapeRenderer();
        sr.setProjectionMatrix(cam.combined);

        p1 = new Player();
        System.out.println(Gdx.graphics.getWidth());
    }

    public void update(float delta){
        p1.update();
    }

    //-----------------SCREEN-----------------//
    @Override
    public void render(float delta) {
        update(delta);

        //Rendering
        // NOTE: Just a test
        sr.begin(ShapeRenderer.ShapeType.Filled);
        p1.draw(sr);
        sr.setColor(0.0f, 0.0f, 1.0f, 1.0f);
        sr.rect(0, 0, 50, 50);
        sr.end();
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    //-----------------INPUT-----------------//
    @Override
    public void keyDown(int keyCode) {
        // NOTE: Just a test
        if(keyCode == Input.Keys.LEFT)
            p1.move(-0.1f);
        else if(keyCode == Input.Keys.RIGHT)
            p1.move(0.1f);
    }

    @Override
    public void touchDown(int screenX, int screenY, int pointer, int button) {

    }

}
