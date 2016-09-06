package com.timbell.spaceinvaders.GameScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.timbell.spaceinvaders.Collision.Collision;
import com.timbell.spaceinvaders.Collision.MenuCollision;
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

    private float backgroundTransparancy;
    private Color backgroundColor;

    private boolean switchingToPlay;
    private static final int switchTime = 5;
    private float switchCount;

    public GameOverScreen(SpaceInvaders game, Player p1, Array<ParticleEffect> particleEffects, Collision collision) {
        this.game = game;
        this.p1 = p1;
        this.particleEffects = particleEffects;
        this.collision = collision;

        this.backgroundColor = new Color();

        this.retryButton = new Button(146, 301, 128, new Color(0.576f, 0.769f, 0.49f, 1f), Color.BLACK, Button.ButtonSymbol.RETRY);
        this.mainMenuButton = new Button(568, 301, 128, new Color(0.7f, 0.65f, 0.84f, 1f), Color.BLACK, Button.ButtonSymbol.EXIT);

        collision.addGameOverScreenObjects(this, retryButton, mainMenuButton);
    }

    public void init(){
        retryButton.reset();
        mainMenuButton.reset();

        switchingToPlay = false;
        switchCount = 0;

//        backgroundTransparancy = 0.75f;
        backgroundColor.set(BG_COLOR);
    }

    public void update(float delta){
        if(switchingToPlay){
            switchCount += delta;
//            backgroundTransparancy = 0.75f-(switchCount/switchTime)*0.25f;
            SpaceInvaders.mix(BG_COLOR, PlayScreen.BG_COLOR, switchCount/switchTime, backgroundColor);
            if(switchCount > switchTime) {
                switchingToPlay = false;
                game.changeScreen(SpaceInvaders.PLAY_STATE);
            }
        }

        p1.update(delta);
        particleEffects.addAll(collision.checkMenuCollision());
    }

    public void draw(float delta){
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
        game.sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Shape Drawing
        game.sr.begin(ShapeRenderer.ShapeType.Filled);
        Gdx.gl.glEnable(GL20.GL_BLEND);
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
        Gdx.gl.glDisable(GL20.GL_BLEND);
        // player bullets
        p1.drawBullets(game.sr);
        // player
        p1.draw(game.sr);
        retryButton.drawShape(game.sr);
        mainMenuButton.drawShape(game.sr);
        game.sr.end();


        // Sprite Drawing
        game.sb.begin();
        retryButton.drawSymbol(game.sb);
        mainMenuButton.drawSymbol(game.sb);
        game.sb.end();
    }

    public void changeScreen(int screen){
        if(screen == SpaceInvaders.MENU_STATE){

        }
    }

    @Override
    public void render(float delta) {
        update(delta);
        draw(delta);
    }

    @Override
    public void keyDown(int keyCode) {
        super.keyDown(keyCode);
    }

    @Override
    public void touchDown(float x, float y) {
        super.touchDown(x, y);
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
