package com.timbell.spaceinvaders.GameScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.timbell.spaceinvaders.Collision.Collision;
import com.timbell.spaceinvaders.Entities.Button;
import com.timbell.spaceinvaders.Entities.Player;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffectPool;
import com.timbell.spaceinvaders.SpaceInvaders;


/**
 * Created by timbell on 5/09/16.
 */
public class GameOverScreen extends GameScreen {

    public static final Color BG_COLOR = new Color(0.88f, 0.4f, 0.4f, 0.75f);

    private SpaceInvaders game;

    private Player p1;
    private Button retryButton, mainMenuButton;

    private Array<ParticleEffect> particleEffects;

    private Collision collision;

    private Color backgroundColor;

    private final float transitionPeriod = 5;
    private float transitionTime;

    private State state;

//    private BitmapFont mainFont;
//    private GlyphLayout mainFontLayout;
//    private BitmapFont scoreFont;
//    private GlyphLayout scoreFontLayout;
//    private BitmapFont oldHighScoreFont;
//    private GlyphLayout oldHighScoreFontLayout;

    private BitmapFont font;
    private GlyphLayout fontLayout;
    private String mainText, thisScoreText, oldHighScoreText;
    private int oldHighScore, thisScore;

    public enum State{
        ENTERING, NORMAL, TRANSITION_PLAY, TRANSITION_MENU
    }

    public GameOverScreen(SpaceInvaders game, Player p1, Array<ParticleEffect> particleEffects, Collision collision) {
        this.game = game;
        this.p1 = p1;
        this.particleEffects = particleEffects;
        this.collision = collision;

        this.backgroundColor = new Color();

        this.retryButton = new Button(82, SpaceInvaders.HEIGHT-237, 70, new Color(0.576f, 0.769f, 0.49f, 1f), Color.BLACK, Button.ButtonSymbol.RETRY);
        this.mainMenuButton = new Button(SpaceInvaders.WIDTH-152, SpaceInvaders.HEIGHT-237, 70, new Color(0.7f, 0.65f, 0.84f, 1f), Color.BLACK, Button.ButtonSymbol.EXIT);

        collision.addGameOverScreenObjects(this, retryButton, mainMenuButton);

        this.font = new BitmapFont();
        this.fontLayout = new GlyphLayout();
    }

    public void init(){
        if(p1.getScore() == SpaceInvaders.MAX_SCORE)
            mainText = "You Won!";
        else
            mainText = "Game Over";
        thisScore = p1.getScore();
        oldHighScore = game.getHighScore();
        if(thisScore > oldHighScore) {
            thisScoreText = "New High Score: " + thisScore;
            game.setHighScore(thisScore);
        }
        else
            thisScoreText = "Score: " + thisScore;
        oldHighScoreText = "Previous High Score: " + oldHighScore;

        retryButton.reset();
        mainMenuButton.reset();

        backgroundColor.set(BG_COLOR);

        transitionTime = 0;
        state = State.ENTERING;
        particleEffects.clear();
    }

    public void update(float delta){
        if(state == State.ENTERING){
            transitionTime += delta;
            if(transitionTime > transitionPeriod) {
                transitionTime = 0;
                state = State.NORMAL;
            }
        }
        else if(state == State.TRANSITION_PLAY){
            transitionTime += delta;
            SpaceInvaders.mix(BG_COLOR, PlayScreen.BG_COLOR, transitionTime / transitionPeriod, backgroundColor);
            if(transitionTime > transitionPeriod) {
                game.changeScreen(SpaceInvaders.PLAY_STATE);
            }
        }
        else if(state == State.TRANSITION_MENU){
            transitionTime += delta;
            SpaceInvaders.mix(BG_COLOR, MenuScreen.BG_COLOR, transitionTime / transitionPeriod, backgroundColor);
            if(transitionTime > transitionPeriod) {
                game.changeScreen(SpaceInvaders.MENU_STATE);
            }
        }

        p1.update(delta);
        particleEffects.addAll(collision.checkGameOverCollision());
    }

    public void draw(float delta){
        float buttonTransparancy = 1f;
        if(state == State.ENTERING)
            buttonTransparancy = transitionTime/transitionPeriod;
        else if(state == State.TRANSITION_PLAY || state == State.TRANSITION_MENU)
            buttonTransparancy = 1f-(transitionTime/transitionPeriod);

        // draw background
        game.bgport.apply();
        game.bgBatch.begin();
        game.bgBatch.draw(SpaceInvaders.BACKGROUND, 0, 0, SpaceInvaders.WIDTH, SpaceInvaders.HEIGHT);
        game.bgBatch.end();

        // transparancy over background
        game.gameport.apply();
        // TODO: replase this with jusp changing the texture to be darker

        Gdx.gl.glEnable(GL20.GL_BLEND);
        game.sr.begin(ShapeRenderer.ShapeType.Filled);
        game.sr.setColor(backgroundColor);
        game.sr.rect(0, 0, SpaceInvaders.WIDTH, SpaceInvaders.HEIGHT);
        retryButton.drawShape(game.sr, buttonTransparancy);
        mainMenuButton.drawShape(game.sr, buttonTransparancy);
        game.sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);


        game.sr.begin(ShapeRenderer.ShapeType.Filled);
        // Shape Drawing
        // particles
        for(int i = 0; i < particleEffects.size; ++i) {
            if(particleEffects.get(i).isDead()) {
                ParticleEffectPool.free(particleEffects.get(i));
                particleEffects.removeIndex(i);
            }
            else {
                particleEffects.get(i).update(delta);
                particleEffects.get(i).bounds();
                particleEffects.get(i).draw(game.sr);
            }
        }
        // player bullets
        p1.drawBullets(game.sr);
        // player
        p1.draw(game.sr);
        game.sr.end();


//         Sprite Drawing
        Gdx.gl.glEnable(GL20.GL_BLEND);
        game.sb.begin();
        retryButton.drawSymbol(game.sb, buttonTransparancy);
        mainMenuButton.drawSymbol(game.sb, buttonTransparancy);
        game.sb.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // draw text
        game.sb.begin();
            // Game Over / You Won
            font.getData().setScale(4f);
            fontLayout.setText(font, mainText);
            float mainTextHeight = fontLayout.height;
            float mainTextX = SpaceInvaders.WIDTH/2 - fontLayout.width/2;
            float mainTextY = SpaceInvaders.HEIGHT - mainTextHeight / 2;
            font.draw(game.sb, fontLayout, mainTextX, mainTextY);

            // your score
            font.getData().setScale(2f);
            fontLayout.setText(font, thisScoreText);
            float thisScoreTextHeight = fontLayout.height;
            float thisScoreTextX = SpaceInvaders.WIDTH/2 - fontLayout.width/2;
            float thisScoreTextY = mainTextY - mainTextHeight*2;
            font.draw(game.sb, fontLayout, thisScoreTextX, thisScoreTextY);

            // old high score
            font.getData().setScale(1.5f);
            fontLayout.setText(font, oldHighScoreText);
            float oldHighScoreTextX = SpaceInvaders.WIDTH/2 - fontLayout.width/2;
            float oldHighScoreTextY = thisScoreTextY - thisScoreTextHeight*2;
            font.draw( game.sb, fontLayout, oldHighScoreTextX, oldHighScoreTextY );
        game.sb.end();

    }

    public void changeScreen(int screen){
        if(screen == SpaceInvaders.MENU_STATE){
            state = State.TRANSITION_MENU;
        }
        else if(screen == SpaceInvaders.PLAY_STATE){
            state = State.TRANSITION_PLAY;
        }
    }

    @Override
    public void render(float delta) {
        update(delta);
        draw(delta);
    }

    @Override
    public void keyDown(int keyCode) {
        if( (state == State.NORMAL || state == State.ENTERING)  &&  keyCode == Input.Keys.SPACE)
            p1.shoot();
    }

    @Override
    public void touchDown(float x, float y) {
        if(state == State.NORMAL  ||  state == State.ENTERING)
            p1.shoot();
    }

    @Override
    public void dispose() {

    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void hide() {
        super.hide();
    }
}
