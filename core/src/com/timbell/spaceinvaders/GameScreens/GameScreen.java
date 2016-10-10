package com.timbell.spaceinvaders.GameScreens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.timbell.spaceinvaders.SpaceInvaders;

/**
 * Created by timbell on 18/07/16.
 */
//public interface GameScreen extends Screen {
//    public abstract void keyDown(int keyCode);
//    public abstract void touchDown(int screenX, int screenY, int pointer, int button);
//}

public abstract class GameScreen implements Screen {

    protected SpaceInvaders game;
    public static final Color BG_COLOR = Color.BLACK;

    public void keyDown(int keyCode){

    }

    public void touchDown(float x, float y){

    }


    public abstract void changeScreen(int screen);
    public abstract void init();

    public void onPlayerRespawnStart(){}
    public void onPlayerRespawnEnd(){}


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

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
}

