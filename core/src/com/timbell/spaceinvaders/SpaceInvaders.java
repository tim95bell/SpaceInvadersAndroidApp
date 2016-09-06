package com.timbell.spaceinvaders;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.timbell.spaceinvaders.Assets.AssetManager;
import com.timbell.spaceinvaders.Collision.Collision;
import com.timbell.spaceinvaders.Entities.EnemyOne;
import com.timbell.spaceinvaders.Entities.EnemyThree;
import com.timbell.spaceinvaders.Entities.EnemyTwo;
import com.timbell.spaceinvaders.Entities.Player;
import com.timbell.spaceinvaders.GameScreens.GameOverScreen;
import com.timbell.spaceinvaders.GameScreens.GameScreen;
import com.timbell.spaceinvaders.GameScreens.MenuScreen;
import com.timbell.spaceinvaders.GameScreens.PlayScreen;
import com.timbell.spaceinvaders.GameScreens.Screen;
import com.timbell.spaceinvaders.Input.InputHandler;
import com.timbell.spaceinvaders.ParticleEffect.Particle;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffectPool;

import static com.timbell.spaceinvaders.Assets.AssetManager.*;

public class SpaceInvaders extends Game {

	// TODO: get rid of bgBatch, and bgSr. only one sb and sr is needed. it is the viewports that are needed.

	// TODO: thinking of making the darkened screen take up all the screen, and show the edges of the actual game with a white bar on each side
	// (to show the edge of where you can go. and then have everything happen withing the actual game part of the screen, that is theright aspect ratio.
	// except have bullets and particles go outside the screen.
	// could get rid of the gameport as now, and just have a normal one that takes up the whole screen. and inforce it myself. by having
	// xOff and yOff. so instead of being "x = width/2;" it would be "x = xOff + width/2;".
	// xOff or yOff would be 0, and the other would be 0 if the aspect ratio is perfect, otherwise it would be some positive value.


	// STATIC
	public static final int MENU_STATE = 0;
	public static final int PLAY_STATE = 1;
	public static final int GAMEOVER_STATE = 2;

	public static final int WIDTH = 640;
	public static final int HEIGHT = 360;

	public static final int UNIT = 12;
	public static final int LOSE_HEIGHT = UNIT*6;

	public static Texture BACKGROUND;
	public static Texture SPRITE_SHEET;


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
	public ScalingViewport gameport;
	public FillViewport bgport;

	@Override
	public void create () {
		// dont want continuous rendering for menuScreen, which is first screen
//		Gdx.graphics.setContinuousRendering(false);
		Gdx.graphics.setContinuousRendering(true);

		initAssets();

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
		Player p1 = new Player();
		Array<ParticleEffect> particleEffects = new Array<ParticleEffect>(false, 2);
		Collision collision = new Collision(p1, particleEffects);
		screens = new GameScreen[3];
		currentState = MENU_STATE;
		screens[MENU_STATE] = new MenuScreen(this, p1, particleEffects, collision);
		screens[PLAY_STATE] = new PlayScreen(this, p1, particleEffects, collision);
		screens[GAMEOVER_STATE] = new GameOverScreen(this, p1, particleEffects, collision);
		screens[currentState].init();
		setScreen(screens[currentState]);

		// InputHandler setup
		inputHandler = new InputHandler(this);
		Gdx.input.setInputProcessor(inputHandler);

		// Init Assets
		AssetManager.init();
		// Init Particle Effect Pool
		ParticleEffectPool.init();

		Gdx.gl.glClearColor(1, 0, 0, 1);
	}

	public void initAssets(){
		BACKGROUND = new Texture("sunset6.png");
		SPRITE_SHEET = new Texture("spriteSheet2.png");
		EnemyOne.IMAGE_ONE = new TextureRegion(SpaceInvaders.SPRITE_SHEET, 0, 0, 12, 8);
		EnemyOne.IMAGE_TWO = new TextureRegion(SpaceInvaders.SPRITE_SHEET, 12, 0, 12, 8);
		EnemyTwo.IMAGE_ONE = new TextureRegion(SpaceInvaders.SPRITE_SHEET, 24, 0, 11, 8);
		EnemyTwo.IMAGE_TWO = new TextureRegion(SpaceInvaders.SPRITE_SHEET, 35, 0, 11, 8);
		EnemyThree.IMAGE_ONE = new TextureRegion(SpaceInvaders.SPRITE_SHEET, 0, 8, 8, 8);
		EnemyThree.IMAGE_TWO = new TextureRegion(SpaceInvaders.SPRITE_SHEET, 8, 8, 8, 8);
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Draw the current screen
		screen.render(Gdx.graphics.getDeltaTime());

		//TEST
//		int l = gameport.getLeftGutterWidth();
//		int r = gameport.getRightGutterWidth();
//		bgport.apply();
//
//		ShapeRenderer normalSr = new ShapeRenderer();

		// darken sidebars if screen is not 16:9
		//NOTE: need to use normalSr that isnt being stretched, it was messing up the widths, the measurments were acctually right
//		Gdx.gl.glEnable(GL20.GL_BLEND);
//		normalSr.begin(ShapeRenderer.ShapeType.Filled);
//		normalSr.setColor(0f, 0f, 0f, 0.5f);
//		normalSr.rect(0, 0, SIDE_BAR_WIDTH, HEIGHT);
//		normalSr.rect(Gdx.graphics.getWidth() - SIDE_BAR_WIDTH, 0, SIDE_BAR_WIDTH, HEIGHT);
//		normalSr.end();
//		Gdx.gl.glDisable(GL20.GL_BLEND);
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

	public void changeScreen(int screen){ //Player p1, int screen){
		if(screen < 0 || screen > 3)
			return;

//		if(screen == MENU_STATE)
//			screens[screen] = new MenuScreen(this, p1);
//		else if(screen == PLAY_STATE)
//			screens[screen] = new PlayScreen(this, p1);
//		else if(screen == GAMEOVER_STATE)
//			screens[screen] = new GameOverScreen(this, p1);

//		screens[currentState].dispose();
		// TODO: should i dispose some things on the screen being switched out, then re make them on init?
		currentState = screen;
		inputHandler.setScreen(screens[currentState]);
		screens[currentState].init();
		setScreen(screens[currentState]);

	}

//	public void changeToPlayState(Player p1){
//		screens[PLAY_STATE] = new PlayScreen(this, p1);
//		screens[currentState].dispose();
//		currentState = PLAY_STATE;
//		setScreen(screens[currentState]);
//	}
//
//	public void changeToMenuState(){
//		screens[MENU_STATE] = new MenuScreen(this, p1);
//		screens[currentState].dispose();
//		currentState = MENU_STATE;
//		setScreen(screens[currentState]);
//	}
//
//	public void changeToGameOverState(Player p1){
//		screens[GAMEOVER_STATE] = new GameOverScreen(this, p1);
//		screens[currentState].dispose();
//		currentState = GAMEOVER_STATE;
//		setScreen(screens[currentState]);
//	}


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

	public static void mix(Color from, Color too, float percent, Color outColor){
		outColor.set(
				(1f - percent) * from.r + percent * too.r,
				(1f - percent) * from.g + percent * too.g,
				(1f - percent) * from.b + percent * too.b,
				(1f - percent) * from.a + percent * too.a
		);
	}





}
