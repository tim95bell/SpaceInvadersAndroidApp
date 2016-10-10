package com.timbell.spaceinvaders.GameScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
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
 * Created by timbell on 23/07/16.
 */
public class MenuScreen extends GameScreen {

    public static final Color BG_COLOR = new Color(0.6f, 0.1f, 0.1f, 0.6f);

    private Button playButton;

    private Player p1;

    private Array<ParticleEffect> particleEffects;

    private Color backgroundColor;

    private final float transitionPeriod = 5;
    private float transitionTime;

    private Collision collision;

    private State state;

    private BitmapFont highScoreFont;
    private GlyphLayout highScoreLayout;
    private String highScoreText;

    public enum State{
        ENTERING, NORMAL, TRANSITION_PLAY
    }


    public MenuScreen(SpaceInvaders game, Player p1, Array<ParticleEffect> particleEffects, Collision collision){

        this.game = game;
        this.p1 = p1;
        this.particleEffects = particleEffects;
        this.collision = collision;

        this.backgroundColor = new Color();

        playButton = new Button(SpaceInvaders.WIDTH/2f - 150f/2f, SpaceInvaders.HEIGHT/2f - 150f/2f + SpaceInvaders.UNIT*2, 150, new Color(0.576f, 0.769f, 0.49f, 1f), Color.BLACK, Button.ButtonSymbol.PLAY);

        collision.addMenuScreenObjects(this, playButton);

        this.highScoreFont = new BitmapFont();
        // smooth font
        highScoreFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        highScoreFont.getData().setScale(4);
        this.highScoreLayout = new GlyphLayout();
    }

    public void init(){
        playButton.reset();
        state = State.ENTERING;
        transitionTime = 0;

        backgroundColor.set(BG_COLOR);
        ParticleEffectPool.freeAll(particleEffects);
        p1.setCurrentScreen(this);

        this.highScoreText = "High Score: "+game.getHighScore();
    }

    @Override
    public void touchDown(float x, float y){
        p1.shoot();
    }

    public void changeScreen(int screen){
        if(screen < 0 || screen > 3)
            return;

        if(screen == SpaceInvaders.PLAY_STATE) {
            p1.setState(Player.State.ENTERING);
            state = State.TRANSITION_PLAY;
            transitionTime = 0;
        }
    }

    public void update(float delta){
        if(state == State.ENTERING){
            transitionTime += delta;
            if(transitionTime > transitionPeriod) {
                state = State.NORMAL;
            }
        }
        else if(state == State.TRANSITION_PLAY){
            transitionTime += delta;
            SpaceInvaders.mix(BG_COLOR, PlayScreen.BG_COLOR, transitionTime/transitionPeriod, backgroundColor);
            if(transitionTime > transitionPeriod) {
                game.changeScreen(SpaceInvaders.PLAY_STATE);
            }
        }

        p1.update(delta);
        particleEffects.addAll(collision.checkMenuCollision());
    }

    public void draw(float delta){
        float fadeTransparancy = 1f;
        if(state == State.ENTERING)
            fadeTransparancy = transitionTime/transitionPeriod;
        else if(state == State.TRANSITION_PLAY){
            fadeTransparancy = 1f - transitionTime/transitionPeriod;
        }

        Gdx.gl.glEnable(GL20.GL_BLEND);
            game.sr.begin(ShapeRenderer.ShapeType.Filled);
                game.sr.setColor(backgroundColor);
                game.sr.rect(-SpaceInvaders.xOff, -SpaceInvaders.yOff, SpaceInvaders.viewportWidth, SpaceInvaders.viewportHeight);
            game.sr.end();

            game.sr.begin(ShapeRenderer.ShapeType.Filled);
                // draw and free particle effects
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
                // player
                p1.drawBullets(game.sr);
                p1.draw(game.sr);
                // draw play button
        playButton.drawShape(game.sr, fadeTransparancy);
            game.sr.end();

            game.sb.begin();
                // draw play button symbol
        playButton.drawSymbol(game.sb, fadeTransparancy);
                // high score text
                highScoreFont.setColor(1,  1, 1, fadeTransparancy);
                highScoreLayout.setText(highScoreFont, highScoreText);
                highScoreFont.draw(game.sb, highScoreLayout, SpaceInvaders.WIDTH / 2 - highScoreLayout.width / 2, SpaceInvaders.HEIGHT - highScoreLayout.height / 2);
            game.sb.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    @Override
    public void render(float delta) {
        update(delta);
        draw(delta);
    }

    @Override
    public void dispose() {
//        testButton = null;
//        particleEffects = null;
//        buttons = null;
//        collision = null;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void keyDown(int keyCode) {
        if(keyCode == Input.Keys.SPACE)
            p1.shoot();
    }
}
