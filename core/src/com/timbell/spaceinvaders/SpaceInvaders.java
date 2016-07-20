package com.timbell.spaceinvaders;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.timbell.spaceinvaders.GameScreens.GameScreen;
import com.timbell.spaceinvaders.GameScreens.PlayScreen;
import com.badlogic.gdx.InputProcessor;

public class SpaceInvaders implements ApplicationListener, InputProcessor {

	public final int PLAY_STATE = 1;
	private int currentState;
	private GameScreen[] screens;

	private OrthographicCamera cam;
	public int WIDTH = 640;
	public int HEIGHT = 360;
	
	@Override
	public void create () {

		cam = new OrthographicCamera(WIDTH, HEIGHT);

		screens = new GameScreen[2];
		currentState = PLAY_STATE;
		screens[PLAY_STATE] = new PlayScreen();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		screens[currentState].update();
		screens[currentState].draw();
	}

	@Override
	public void dispose(){

	}

	@Override
	public void resume(){

	}

	@Override
	public void pause(){

	}

	@Override
	public void resize(int width, int height){

	}

	public void changeState(int state){
		// NOTE: check if valid state
		currentState = state;
	}

	public int getState(){
		return currentState;
	}


	//----------------- HANDLE INPUT ----------------------------------------------------------------//

	@Override
	public boolean keyDown(int keycode) {
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

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

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




}
