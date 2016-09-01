package com.timbell.spaceinvaders.Input;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.InputProcessor;
import com.timbell.spaceinvaders.GameScreens.GameScreen;

/**
 * Created by timbell on 21/07/16.
 */
public class InputHandler implements InputProcessor {

    private GameScreen screen;

    public InputHandler(Game game){
        this.screen = (GameScreen)game.getScreen();
    }

    @Override
    public boolean keyDown(int keycode) {
        screen.keyDown(keycode);
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        screen.touchDown(screenX, screenY);

        return true;
    }

    public void setScreen(GameScreen screen){
        this.screen = screen;
    }

    //--------------------------------------------------------------------------------

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }
}
