package com.timbell.spaceinvaders;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.timbell.spaceinvaders.GameScreens.GameScreen;
import com.timbell.spaceinvaders.GameScreens.PlayScreen;
import com.timbell.spaceinvaders.Input.InputHandler;

public class SpaceInvaders extends Game {


	// NOTE:  Gdx.graphics.getWidth() doesnt work how i thought it would, need to sort this out

	public final int MENU_STATE = 0;
	public final int PLAY_STATE = 1;
	private int currentState;
	private GameScreen[] screens;

	private OrthographicCamera cam;
	public static int WIDTH = 640;
	public static int HEIGHT = 360;
	
	@Override
	public void create () {

		cam = new OrthographicCamera(WIDTH, HEIGHT);
		cam.translate(WIDTH/2, HEIGHT/2);
		cam.update();

		screens = new GameScreen[2];
		currentState = PLAY_STATE;
		screens[PLAY_STATE] = new PlayScreen(cam);
		setScreen(screens[PLAY_STATE]);

		Gdx.input.setInputProcessor(new InputHandler(this));
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		screen.render(Gdx.graphics.getDeltaTime());
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

	public void setState(int state){
		if(state >= 0 && state < screens.length) {
			currentState = state;
			setScreen(screens[currentState]);

			//turn off game loop for menu, or on otherwise
			// NOTE: need to test this
			if(state == MENU_STATE){
				Gdx.graphics.setContinuousRendering(false);
			}
			else{
				Gdx.graphics.setContinuousRendering(true);
			}
		}
	}





}
