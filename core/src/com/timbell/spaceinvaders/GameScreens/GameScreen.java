package com.timbell.spaceinvaders.GameScreens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.timbell.spaceinvaders.Entities.Player;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffectPool;
import com.timbell.spaceinvaders.SpaceInvaders;

/**
 * Created by timbell on 18/07/16.
 */
//public interface GameScreen extends Screen {
//    public abstract void keyDown(int keyCode);
//    public abstract void touchDown(int screenX, int screenY, int pointer, int button);
//}

public abstract class GameScreen implements Screen {

    //states
    public final int STATE_ENTERING = 0;
    public final int STATE_NORMAL = 1;
    public final int STATE_TRANSITION_MENUSCREEN = 2;
    public final int STATE_TRANSITION_PLAYSCREEN = 3;
    public final int STATE_TRANSITION_GAMEOVERSCREEN = 4;
    public final int STATE_TRANSITION_TUTORIALSCREEN = 5;
    protected int state;

    protected final float transitionPeriod = 5;
    protected SpaceInvaders game;
    protected Player p1;
    protected Array<ParticleEffect> particleEffects;
    protected Rectangle[] recievePlayerRectangles;
    protected Color backgroundColor;
    protected float transitionTime;

    public GameScreen(SpaceInvaders game, Player p1, Array<ParticleEffect> particleEffects){
        this.recievePlayerRectangles = new Rectangle[]{new Rectangle(), new Rectangle(), new Rectangle()};
        this.backgroundColor = new Color();
        this.game = game;
        this.p1 = p1;
        this.particleEffects = particleEffects;
        this.transitionTime = 0;
    }

    public void keyDown(int keyCode){

    }

    public void touchDown(float x, float y){

    }

    public void changeScreen(int screen){
        if(screen < 0 || screen > 4)
            return;

        transitionTime = 0;

        if(screen == SpaceInvaders.PLAY_STATE) {
            state = STATE_TRANSITION_PLAYSCREEN;
            p1.setState(Player.State.ENTERING);
        }
        else if(screen == SpaceInvaders.MENU_STATE) {
            state = STATE_TRANSITION_MENUSCREEN;
            p1.setState(Player.State.NORMAL);
        }
        else if(screen == SpaceInvaders.GAMEOVER_STATE) {
            state = STATE_TRANSITION_GAMEOVERSCREEN;
        }
        else if(screen == SpaceInvaders.TUTORIAL_STATE) {
            state = STATE_TRANSITION_TUTORIALSCREEN;
            p1.setState(Player.State.NORMAL);
        }
    }

    public abstract void init();
    public void init(final Color BG_COLOR){
        transitionTime = 0;
        backgroundColor.set(BG_COLOR);
        ParticleEffectPool.freeAll(particleEffects);
        p1.setCurrentScreen(this);
        state = STATE_ENTERING;
    }

    public void onPlayerRespawnStart(){}
    public void onPlayerRespawnEnd(){}


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        update(delta);
        draw(delta);
    }
    public abstract void update(float delta);
    public abstract void draw(float delta);

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
    public void dispose() {

    }
}

