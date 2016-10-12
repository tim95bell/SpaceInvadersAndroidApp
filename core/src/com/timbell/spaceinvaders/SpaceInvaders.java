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
import com.timbell.spaceinvaders.Entities.Button;
import com.timbell.spaceinvaders.Entities.Enemy;
import com.timbell.spaceinvaders.Entities.EnemyOne;
import com.timbell.spaceinvaders.Entities.EnemyThree;
import com.timbell.spaceinvaders.Entities.EnemyTwo;
import com.timbell.spaceinvaders.Entities.MotherShip;
import com.timbell.spaceinvaders.Entities.Player;
import com.timbell.spaceinvaders.Entities.Swarm;
import com.timbell.spaceinvaders.GameScreens.GameOverScreen;
import com.timbell.spaceinvaders.GameScreens.GameScreen;
import com.timbell.spaceinvaders.GameScreens.MenuScreen;
import com.timbell.spaceinvaders.GameScreens.PlayScreen;
import com.timbell.spaceinvaders.GameScreens.TutorialScreen;
import com.timbell.spaceinvaders.Input.InputHandler;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffectPool;

public class SpaceInvaders extends Game {
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

	public static final int UNIT = 10;

	public static final int MAX_SCORE  = 3780;

	public static Texture SPRITE_SHEET;

	public static float volume = 1.0f;

	// CLASS
	public SpriteBatch sb;
	public ShapeRenderer sr;

	private int currentState;
	private GameScreen[] screens;

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
		OrthographicCamera cam = new OrthographicCamera(viewportWidth, viewportHeight);
		cam.translate(viewportWidth / 2, viewportHeight / 2);
		cam.translate(-xOff, -yOff);
		cam.update();

		// Viewport Setups
		gameport = new FitViewport(viewportWidth, viewportHeight, cam);

		// Batches and Renderer setups
		sb = new SpriteBatch();
		sb.setProjectionMatrix(cam.combined);

		sr = new ShapeRenderer();
		sr.setProjectionMatrix(cam.combined);

		// Screens setup
		Player p1 = new Player();
		Array<ParticleEffect> particleEffects = new Array<ParticleEffect>(false, 2);
		screens = new GameScreen[4];
		if(getHighScore() == 0)
			currentState = TUTORIAL_STATE;
		else
			currentState = MENU_STATE;
		currentState = MENU_STATE; // TODO: remove this line
		screens[MENU_STATE] = new MenuScreen(this, p1, particleEffects);
		screens[PLAY_STATE] = new PlayScreen(this, p1, particleEffects);
		screens[GAMEOVER_STATE] = new GameOverScreen(this, p1, particleEffects);
		screens[TUTORIAL_STATE] = new TutorialScreen(this, p1, particleEffects);
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
		Swarm.moveSound = Gdx.audio.newSound( Gdx.files.internal("fastinvader116bit.wav"));

		Button.exitSymbol = new Texture("exitSymbol.png");
		Button.playSymbol = new Texture("playSymbol512.png");
		Button.settingsSymbol = new Texture("settingsSymbol.png");
		Button.retrySymbol = new Texture("retrySymbol.png");
		Button.tutorialSymbol = new Texture("questionSymbol512.png");
		Button.okaySymbol = new Texture("thumbsUp512.png");
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
		Swarm.moveSound.dispose();

		SPRITE_SHEET.dispose();
		Button.exitSymbol.dispose();
		Button.playSymbol.dispose();
		Button.settingsSymbol.dispose();
		Button.retrySymbol.dispose();

		sb.dispose();
		sr.dispose();
		for(int i = 0; i < screens.length; ++i)
			screens[i].dispose();
	}

	@Override
	public void resume(){}
	@Override
	public void pause(){}

	@Override
	public void resize(int width, int height){
		gameport.update(width, height);
	}

	public void changeScreen(int screen){
		if(screen < 0 || screen > 4)
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
		}
		else if(neededRatio > deviceRatio){
			// device has extra height
			float ourHeight = deviceWidth*(9f/16f);
			float heightScale = deviceHeight/ourHeight;
			xOff = 0;
			viewportWidth = SpaceInvaders.WIDTH;
			viewportHeight = SpaceInvaders.HEIGHT * heightScale;
			yOff = (viewportHeight-SpaceInvaders.HEIGHT)/2;
		}
	}

}