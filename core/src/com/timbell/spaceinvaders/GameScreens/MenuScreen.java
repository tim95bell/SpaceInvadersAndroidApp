package com.timbell.spaceinvaders.GameScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.timbell.spaceinvaders.SpaceInvaders;

import static com.timbell.spaceinvaders.Assets.AssetManager.background;


/**
 * Created by timbell on 23/07/16.
 */
public class MenuScreen implements GameScreen {


    private SpaceInvaders game;

    private SpriteBatch sb;
    private SpriteBatch bgBatch;
    private ShapeRenderer sr;



    public MenuScreen(SpaceInvaders game){
        this.game = game;

        this.sb = game.getSb();
        this.bgBatch = game.getBgBatch();
        this.sr = game.getSr();
    }

    @Override
    public void touchDown(int screenX, int screenY, int pointer, int button) {

        // TEST
        game.setState(game.PLAY_STATE);

    }

    @Override
    public void render(float delta) {

        // Draw Background
        bgBatch.begin();
        bgBatch.draw(background, 0, 0, SpaceInvaders.WIDTH, SpaceInvaders.HEIGHT);
        bgBatch.end();

        // darken background with a 30% transparent black square
        Gdx.gl.glEnable(GL20.GL_BLEND);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0f, 0f, 0f, 0.7f);
        sr.rect(0, 0, SpaceInvaders.WIDTH, SpaceInvaders.HEIGHT);
        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

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
    public void dispose() {

    }

    @Override
    public void keyDown(int keyCode) {

    }
}
