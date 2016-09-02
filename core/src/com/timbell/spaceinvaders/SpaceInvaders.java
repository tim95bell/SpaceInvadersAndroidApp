package com.timbell.spaceinvaders;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.timbell.spaceinvaders.Assets.AssetManager;
import com.timbell.spaceinvaders.Entities.Player;
import com.timbell.spaceinvaders.GameScreens.GameScreen;
import com.timbell.spaceinvaders.GameScreens.MenuScreen;
import com.timbell.spaceinvaders.GameScreens.PlayScreen;
import com.timbell.spaceinvaders.Input.InputHandler;
import com.timbell.spaceinvaders.ParticleEffect.Particle;

import static com.timbell.spaceinvaders.Assets.AssetManager.*;

public class SpaceInvaders extends Game {


	// STATIC
	public static final int MENU_STATE = 0;
	public static final int PLAY_STATE = 1;

	public static final int WIDTH = 640;
	public static final int HEIGHT = 360;

	public static final int UNIT = 12;
	public static final int LOSE_HEIGHT = UNIT*6;

	// CLASS
	private int SIDE_BAR_WIDTH;

	public SpriteBatch sb;
	public SpriteBatch bgBatch;
	public ShapeRenderer sr;
	public ShapeRenderer bgSr;

	private int currentState;
	private GameScreen[] screens;

	private OrthographicCamera cam;
	private OrthographicCamera bgCam;

	private InputHandler inputHandler;

//	private FitViewport gameport;
	/*private*/public ScalingViewport gameport;
	/*private*/public FillViewport bgport;

	@Override
	public void create () {
		// dont want continuous rendering for menuScreen, which is first screen
//		Gdx.graphics.setContinuousRendering(false);
		Gdx.graphics.setContinuousRendering(true);

		AssetManager.init();

		// Camera setups
		cam = new OrthographicCamera(WIDTH, HEIGHT);
		cam.translate(WIDTH / 2, HEIGHT / 2);
		cam.update();

		bgCam = new OrthographicCamera(WIDTH, HEIGHT);
		bgCam.translate(WIDTH / 2, HEIGHT / 2);
		bgCam.update();

		// Viewport Setups
//		gameport = new FitViewport(WIDTH, HEIGHT, cam);
		gameport = new ScalingViewport(Scaling.fit, WIDTH, HEIGHT, cam);
		bgport = new FillViewport(WIDTH, HEIGHT, bgCam);

		// TEST
		calculateSideBarWidth(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


		// Batches and Renderer setups
		sb = new SpriteBatch();
		sb.setProjectionMatrix(cam.combined);

		bgBatch = new SpriteBatch();
		bgBatch.setProjectionMatrix(bgCam.combined);

		sr = new ShapeRenderer();
		sr.setProjectionMatrix(bgCam.combined);

		bgSr = new ShapeRenderer();
		bgSr.setProjectionMatrix(bgCam.combined);

		// Screens setup
		screens = new GameScreen[2];
		currentState = MENU_STATE;
		screens[MENU_STATE] = new MenuScreen(this);
		screens[PLAY_STATE] = null;
		setScreen(screens[currentState]);

		// InputHandler setup
		inputHandler = new InputHandler(this);
		Gdx.input.setInputProcessor(inputHandler);

		// Init Assets
		AssetManager.init();

		Gdx.gl.glClearColor(1, 0, 0, 1);
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Draw the current screen
		screen.render(Gdx.graphics.getDeltaTime());

		//TEST
		int l = gameport.getLeftGutterWidth();
		int r = gameport.getRightGutterWidth();
		bgport.apply();

		ShapeRenderer normalSr = new ShapeRenderer();

		// darken sidebars if screen is not 16:9
		//NOTE: need to use normalSr that isnt being stretched, it was messing up the widths, the measurments were acctually right
		Gdx.gl.glEnable(GL20.GL_BLEND);
		normalSr.begin(ShapeRenderer.ShapeType.Filled);
		normalSr.setColor(0f, 0f, 0f, 0.5f);
		normalSr.rect(0, 0, SIDE_BAR_WIDTH, HEIGHT);
		normalSr.rect(Gdx.graphics.getWidth() - SIDE_BAR_WIDTH, 0, SIDE_BAR_WIDTH, HEIGHT);
		normalSr.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
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
		bgport.update(width, height);
		gameport.update(width, height);


//		// TEST
//		if(width/height > 16/9){
//			double unit = height/9.0;
//			double actualWidth = 16*unit;
//			double extraWidth = width - actualWidth;
//			gameport.update((int)actualWidth, height, true);
////			gameport.setScreenX((int)extraWidth/2);
//		}
//		else if(width/height < 16/9){
//			double unit = width/16.0;
//			double actualHeight = 9*unit;
//			double extraHeight = height - actualHeight;
//			gameport.update(width, (int)actualHeight, true);
////			gameport.setScreenY((int)extraHeight/2);
//		}
//		else{
//			gameport.update(width, height);
//		}
//
//		cam.update();
////		bgCam.update();
		calculateSideBarWidth(width, height);
	}

//	public void setState(int state){
//		if(state >= 0 && state < screens.length) {
//			if(state == PLAY_STATE)
//				screens[state] = new PlayState();
//			currentState = state;
//			setScreen(screens[currentState]);
//			inputHandler.setScreen(screens[currentState]);
//
//			//turn off game loop for menu, or on otherwise
//			// NOTE: need to test this
////			if(state == MENU_STATE){
////				Gdx.graphics.setContinuousRendering(false);
////			}
////			else{
////				Gdx.graphics.setContinuousRendering(true);
////			}
//		}
//	}

	public void changeToPlayState(Player p1){
		screens[PLAY_STATE] = new PlayScreen(this, cam, p1);
		screens[currentState].dispose();
		currentState = PLAY_STATE;
		setScreen(screens[currentState]);
	}

	public void changeToMenuState(){
		screens[MENU_STATE] = new MenuScreen(this);
		screens[currentState].dispose();
		currentState = MENU_STATE;
		setScreen(screens[currentState]);
	}


	public void calculateSideBarWidth(int width, int height){

		if(((double)width)/((double)height) > 16.0/9.0){
			double unit = ((double)height)/9.0;
			double actualWidth = 16*unit;
			double extraWidth = ((double)width) - actualWidth;
			SIDE_BAR_WIDTH = (int)(extraWidth/2);
			System.out.println("w/h: " + ((double)width)/((double)height) + "   |   16/9: " + 16.0/9.0);
			System.out.println("new screen: " + actualWidth/(double)height + "16/9: " + 16.0/9.0);
			System.out.println("width: " + width + "   |   actualwidth + extrawidth: " + (actualWidth + extraWidth));
			System.out.println("actualWidth: " + actualWidth + "  extraWidth: " + extraWidth);
		}
		else{
			SIDE_BAR_WIDTH = 0;
		}



//		if(gameport.getWorldWidth() < bgport.getWorldWidth()){
//			SIDE_BAR_WIDTH = (int)(bgport.getScreenWidth() - gameport.getScreenWidth())/2;
//		}
//		else
//			SIDE_BAR_WIDTH = 0;

		System.out.println(SIDE_BAR_WIDTH);
	}





}
