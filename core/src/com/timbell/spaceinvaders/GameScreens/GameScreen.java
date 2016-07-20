package com.timbell.spaceinvaders.GameScreens;

import com.badlogic.gdx.Screen;

/**
 * Created by timbell on 18/07/16.
 */
public interface GameScreen extends Screen {
    public abstract void keyDown(int keyCode);
    public abstract void touchDown(int screenX, int screenY, int pointer, int button);
}
