package com.timbell.spaceinvaders.GameScreens;

import com.badlogic.gdx.Screen;
import com.timbell.spaceinvaders.SpaceInvaders;

import sun.jvm.hotspot.memory.Space;

/**
 * Created by timbell on 18/07/16.
 */
//public interface GameScreen extends Screen {
//    public abstract void keyDown(int keyCode);
//    public abstract void touchDown(int screenX, int screenY, int pointer, int button);
//}

public abstract class GameScreen implements Screen {

    protected SpaceInvaders game;

    public void keyDown(int keyCode){

    }

    public void touchDown(int screenX, int screenY, int pointer, int button){

    }

    public GameScreen(SpaceInvaders game){
        this.game = game;
    }



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

