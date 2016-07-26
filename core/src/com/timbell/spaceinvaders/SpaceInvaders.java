package com.timbell.spaceinvaders;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.timbell.spaceinvaders.Assets.AssetManager;
import com.timbell.spaceinvaders.GameScreens.GameScreen;
import com.timbell.spaceinvaders.GameScreens.MenuScreen;
import com.timbell.spaceinvaders.GameScreens.PlayScreen;
import com.timbell.spaceinvaders.Input.InputHandler;

import static com.timbell.spaceinvaders.Assets.AssetManager.*;

public class SpaceInvaders extends Game {


	// STATIC
	public static final int MENU_STATE = 0;
	public static final int PLAY_STATE = 1;

	public static final int WIDTH = 640;
	public static final int HEIGHT = 360;

	private SpriteBatch sb;
	private SpriteBatch bgBatch;
	private ShapeRenderer sr;

	// CLASS
	private int currentState;
	private GameScreen[] screens;

	private OrthographicCamera cam;
	private OrthographicCamera bgCam;

	private FitViewport gameport;
	private FillViewport bgport;

	@Override
	public void create () {
		// dont want continuous rendering for menuScreen, which is first screen
		Gdx.graphics.setContinuousRendering(false);

		AssetManager.init();

		// Camera setups
		cam = new OrthographicCamera(WIDTH, HEIGHT);
		cam.translate(WIDTH / 2, HEIGHT / 2);
		cam.update();

		bgCam = new OrthographicCamera(WIDTH, HEIGHT);
		bgCam.translate(WIDTH / 2, HEIGHT / 2);
		bgCam.update();

		// Viewport Setups
		gameport = new FitViewport(WIDTH, HEIGHT, cam);
		bgport = new FillViewport(WIDTH, HEIGHT, bgCam);


		// Batches and Renderer setups
		sb = new SpriteBatch();
		sb.setProjectionMatrix(cam.combined);

		bgBatch = new SpriteBatch();
		bgBatch.setProjectionMatrix(bgCam.combined);

		sr = new ShapeRenderer();
		sr.setProjectionMatrix(bgCam.combined);

		// Screens setup
		screens = new GameScreen[2];
		currentState = MENU_STATE;
		screens[MENU_STATE] = new MenuScreen(this);
		screens[PLAY_STATE] = new PlayScreen(this, cam);
		setScreen(screens[currentState]);

		// InputHandler setup
		Gdx.input.setInputProcessor(new InputHandler(this));

		// Init Assets
		AssetManager.init();

		Gdx.gl.glClearColor(1, 0, 0, 1);
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Draw the current screen
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
		gameport.update(width, height);
		bgport.update(width, height);
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

	// Getters
	public SpriteBatch getSb(){ return sb; }
	public SpriteBatch getBgBatch(){ return bgBatch; }
	public ShapeRenderer getSr(){ return sr; }





}
