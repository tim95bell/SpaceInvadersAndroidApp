package com.timbell.spaceinvaders;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.timbell.spaceinvaders.Collision.Collision;
import com.timbell.spaceinvaders.Entities.Button;
import com.timbell.spaceinvaders.Entities.Enemy;
import com.timbell.spaceinvaders.Entities.EnemyOne;
import com.timbell.spaceinvaders.Entities.EnemyThree;
import com.timbell.spaceinvaders.Entities.EnemyTwo;
import com.timbell.spaceinvaders.Entities.MotherShip;
import com.timbell.spaceinvaders.Entities.Player;
import com.timbell.spaceinvaders.GameScreens.GameOverScreen;
import com.timbell.spaceinvaders.GameScreens.GameScreen;
import com.timbell.spaceinvaders.GameScreens.MenuScreen;
import com.timbell.spaceinvaders.GameScreens.PlayScreen;
import com.timbell.spaceinvaders.Input.InputHandler;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffectPool;

public class SpaceInvaders extends Game {

	// TODO: get rid of bgBatch, and bgSr. only one sb and sr is needed. it is the viewports that are needed.

	// TODO: thinking of making the darkened screen take up all the screen, and show the edges of the actual game with a white bar on each side
	// (to show the edge of where you can go. and then have everything happen withing the actual game part of the screen, that is theright aspect ratio.
	// except have bullets and particles go outside the screen.
	// could get rid of the gameport as now, and just have a normal one that takes up the whole screen. and inforce it myself. by having
	// xOff and yOff. so instead of being "x = WIDTH/2;" it would be "x = xOff + WIDTH/2;".
	// xOff or yOff would be 0, and the other would be 0 if the aspect ratio is perfect, otherwise it would be some positive value.


	// STATIC
	public static final int MENU_STATE = 0;
	public static final int PLAY_STATE = 1;
	public static final int GAMEOVER_STATE = 2;
	public static final int TUTORIAL_STATE = 3;

	public static final int WIDTH = 640;
	public static final int HEIGHT = 360;

	public static float xOff = 0;
	public static float yOff = 0;
	public static float viewportWidth = SpaceInvaders.WIDTH;
	public static float viewportHeight = SpaceInvaders.HEIGHT;

	public static final int UNIT = 10;//12;

	public static final int MAX_SCORE  = 3780;

	public static Texture BACKGROUND;
	public static Texture SPRITE_SHEET;

	public static float volume = 0.1f;//1.0f; // TODO: change volume back to 1.0

	// CLASS
	private int SIDE_BAR_WIDTH;

	public SpriteBatch sb;
	public ShapeRenderer sr;

	private int currentState;
	private GameScreen[] screens;

	private OrthographicCamera cam;

	private InputHandler inputHandler;

	public FitViewport gameport;

	private Preferences prefs;

	@Override
	public void create () {
		initXOffYOffVpWidthVpHeight();
		prefs = Gdx.app.getPreferences("HighScore");
		Gdx.graphics.setContinuousRendering(true);

		initAssets();

		// Camera setups
		cam = new OrthographicCamera(viewportWidth, viewportHeight);
//		cam = new OrthographicCamera(WIDTH, HEIGHT);
		cam.translate(viewportWidth / 2, viewportHeight / 2);
		cam.translate(-xOff, -yOff);
//		cam.translate(WIDTH / 2, HEIGHT / 2);
		cam.update();

//		bgCam = new OrthographicCamera(WIDTH, HEIGHT);
//		bgCam.translate(WIDTH / 2, HEIGHT / 2);
//		bgCam.update();

		// Viewport Setups
		gameport = new FitViewport((float)viewportWidth, (float)viewportHeight, cam);
//		gameport = new FitViewport(WIDTH, HEIGHT, cam);
//		gameport = new ScalingViewport(Scaling.fit, (float)viewportWidth, (float)viewportHeight, cam);
//		gameport = new ScalingViewport(Scaling.fit, WIDTH, HEIGHT, cam);
//		bgport = new FillViewport(WIDTH, HEIGHT, bgCam);

		//calculateSideBarWidth(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		// Batches and Renderer setups
		sb = new SpriteBatch();
		sb.setProjectionMatrix(cam.combined);

//		bgBatch = new SpriteBatch();
//		bgBatch.setProjectionMatrix(bgCam.combined);

		sr = new ShapeRenderer();
		sr.setProjectionMatrix(cam.combined);
//		sr.setProjectionMatrix(bgCam.combined);

//		bgSr = new ShapeRenderer();
//		bgSr.setProjectionMatrix(bgCam.combined);

		// Screens setup
		Player p1 = new Player();
		Array<ParticleEffect> particleEffects = new Array<ParticleEffect>(false, 2);
		Collision collision = new Collision(p1, particleEffects);
		screens = new GameScreen[3];
		currentState = MENU_STATE;
		screens[MENU_STATE] = new MenuScreen(this, p1, particleEffects);
		screens[PLAY_STATE] = new PlayScreen(this, p1, particleEffects);
		screens[GAMEOVER_STATE] = new GameOverScreen(this, p1, particleEffects);
		screens[currentState].init();
		setScreen(screens[currentState]);

		// InputHandler setup
		inputHandler = new InputHandler(this);
		Gdx.input.setInputProcessor(inputHandler);

		// Init Particle Effect Pool
		ParticleEffectPool.init();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		// so blending works in first frame
		Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		gameport.apply();
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
		MotherShip.image = new TextureRegion(SpaceInvaders.SPRITE_SHEET, 29, 8, 16, 7);

		Button.hitSound = Enemy.hitSound = Gdx.audio.newSound(Gdx.files.internal("light_bulb_smash.wav"));
		MotherShip.sound = Gdx.audio.newSound(Gdx.files.internal("motherShip2Trimmed.wav"));
		Player.gong = Gdx.audio.newSound(Gdx.files.internal("gongTrimmed.wav"));
		Player.shootSound = Gdx.audio.newSound( Gdx.files.internal("enemyShoot.wav"));
		Player.hitSound = Gdx.audio.newSound( Gdx.files.internal("explosion16bit.wav"));

		Button.exitSymbol = new Texture("exitSymbol.png");
		Button.playSymbol = new Texture("playSymbol.png");
		Button.settingsSymbol = new Texture("settingsSymbol.png");
		Button.retrySymbol = new Texture("retrySymbol.png");
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Draw the current screen
		screen.render(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void dispose(){
		Button.hitSound.dispose();
		MotherShip.sound.dispose();
		Player.gong.dispose();
		Player.shootSound.dispose();
		Player.hitSound.dispose();

		BACKGROUND.dispose();
		SPRITE_SHEET.dispose();
		Button.exitSymbol.dispose();
		Button.playSymbol.dispose();
		Button.settingsSymbol.dispose();
		Button.retrySymbol.dispose();

		sb.dispose();
//		bgBatch.dispose();
		sr.dispose();
//		bgSr.dispose();
		for(int i = 0; i < screens.length; ++i)
			screens[i].dispose();
	}

	@Override
	public void resume(){

	}

	@Override
	public void pause(){

	}

	@Override
	public void resize(int width, int height){
//		bgport.update(width, height);

		// TODO: do i need to hangle this?
		gameport.update(width, height);
//		gameport.update((int)viewportWidth, (int)viewportHeight);


//		// TEST
//		if(WIDTH/HEIGHT > 16/9){
//			double unit = HEIGHT/9.0;
//			double actualWidth = 16*unit;
//			double extraWidth = WIDTH - actualWidth;
//			gameport.update((int)actualWidth, HEIGHT, true);
////			gameport.setScreenX((int)extraWidth/2);
//		}
//		else if(WIDTH/HEIGHT < 16/9){
//			double unit = WIDTH/16.0;
//			double actualHeight = 9*unit;
//			double extraHeight = HEIGHT - actualHeight;
//			gameport.update(WIDTH, (int)actualHeight, true);
////			gameport.setScreenY((int)extraHeight/2);
//		}
//		else{
//			gameport.update(WIDTH, HEIGHT);
//		}
//
//		cam.update();
////		bgCam.update();


//		calculateSideBarWidth(width, height);
	}

	public void changeScreen(int screen){
		if(screen < 0 || screen > 3)
			return;

		currentState = screen;
		inputHandler.setScreen(screens[currentState]);
		screens[currentState].init();
		setScreen(screens[currentState]);
	}

	public static void mix(Color from, Color too, float percent, Color outColor){
		outColor.set(
				(1f - percent) * from.r + percent * too.r,
				(1f - percent) * from.g + percent * too.g,
				(1f - percent) * from.b + percent * too.b,
				(1f - percent) * from.a + percent * too.a
		);
	}

	public int getHighScore(){
		return prefs.getInteger("Score", 0);
	}

	public void setHighScore(int score){
		prefs.clear();
		prefs.putInteger("Score", score);
		prefs.flush();
	}

	public void initXOffYOffVpWidthVpHeight(){
		// TODO: extra width is working, now copy same logic into extra height
		float deviceWidth = Gdx.graphics.getWidth();
		float deviceHeight = Gdx.graphics.getHeight();
		float deviceRatio = deviceWidth/deviceHeight;
		float neededRatio = 16f/9f;
		if(neededRatio < deviceRatio){
			// device has extra width
			float ourWidth = deviceHeight*(16f/9f);
			float widthScale = deviceWidth/ourWidth;
			xOff = (deviceWidth - ourWidth)/2f;
			yOff = 0;
			viewportHeight = SpaceInvaders.HEIGHT;
			viewportWidth = SpaceInvaders.WIDTH * widthScale;
//			xOff = (viewportWidth-SpaceInvaders.HEIGHT)/2;
		}
		else if(neededRatio > deviceRatio){
			// device has extra height
			float ourHeight = deviceWidth*(9f/16f);
			float heightScale = deviceHeight/ourHeight;
			xOff = 0;
//			yOff = (deviceHeight - ourHeight)/2f;
			viewportWidth = SpaceInvaders.WIDTH;
			viewportHeight = SpaceInvaders.HEIGHT * heightScale;
			yOff = (viewportHeight-SpaceInvaders.HEIGHT)/2;
		}

	}

}
