package com.timbell.spaceinvaders.GameScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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

    public static final Color BG_COLOR = new Color(0f, 0f, 0f, 0.9f);

    private Button playButton;

    private Player p1;

    private Array<ParticleEffect> particleEffects;

    private Color backgroundColor;

    private final float transitionPeriod = 5;
    private float transitionTime;

    private Collision collision;

    private State state;

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
    }

    public void init(){
        playButton.reset();
        state = State.ENTERING;
        transitionTime = 0;

        backgroundColor.set(BG_COLOR);
        particleEffects.clear();
    }

    @Override
    public void touchDown(float x, float y){

//        y = (y/Gdx.graphics.getHeight())*SpaceInvaders.HEIGHT;
//        y = SpaceInvaders.HEIGHT-y;
//        x = (x/Gdx.graphics.getWidth())*SpaceInvaders.WIDTH;
//
//        // TEST
//        if(testButton.contains((int)x, (int)y)) {
//            particleEffects.addAll(testButton.hit(), 0, 2);
//            switchingToPlay = true;
//        }


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
                state = State.NORMAL;
                game.changeScreen(SpaceInvaders.PLAY_STATE);
            }
        }

        p1.update(delta);
        particleEffects.addAll(collision.checkMenuCollision());
    }

    public void draw(float delta){
        float buttonTransparancy = 1f;
        if(state == State.ENTERING)
            buttonTransparancy = transitionTime/transitionPeriod;

        game.bgport.apply();
        // Draw Background
        game.bgBatch.begin();
        game.bgBatch.draw(SpaceInvaders.BACKGROUND, 0, 0, SpaceInvaders.WIDTH, SpaceInvaders.HEIGHT);
        game.bgBatch.end();

        game.gameport.apply(); // to show the actual screen being used, for other screen ratios
        // darken background with a 30% transparent black square

        Gdx.gl.glEnable(GL20.GL_BLEND);
        // TODO: replace this with darkening the texture image
        game.sr.begin(ShapeRenderer.ShapeType.Filled);
        game.sr.setColor(backgroundColor);
        game.sr.rect(0, 0, SpaceInvaders.WIDTH, SpaceInvaders.HEIGHT);
        game.sr.end();


//        game.gameport.apply();
        game.sr.begin(ShapeRenderer.ShapeType.Filled);
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

        playButton.drawShape(game.sr, buttonTransparancy);
        game.sr.end();

        game.sb.begin();
        playButton.drawSymbol(game.sb, buttonTransparancy);
        game.sb.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        // TEST
//        testButton.draw(game.sr, game.sb);

        game.sr.begin(ShapeRenderer.ShapeType.Filled);
        p1.drawBullets(game.sr);
        p1.draw(game.sr);
        game.sr.end();

//        game.sr.begin(ShapeRenderer.ShapeType.Filled);
//
//        game.sr.end();

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
