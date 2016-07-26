package com.timbell.spaceinvaders.GameScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.timbell.spaceinvaders.Entities.Player;
import com.timbell.spaceinvaders.SpaceInvaders;

import static com.timbell.spaceinvaders.Assets.AssetManager.background;

/**
 * Created by timbell on 18/07/16.
 */
public class PlayScreen implements GameScreen {

    // game
    private SpaceInvaders game;

    // batches
    private SpriteBatch sb;
    private SpriteBatch bgBatch;
    private ShapeRenderer sr;

    // game objects
    private Player p1;

    public PlayScreen(SpaceInvaders game, Camera cam){
        this.p1 = new Player();
        this.game = game;

        this.sb = game.getSb();
        this.bgBatch = game.getBgBatch();
        this.sr = game.getSr();
    }

    public void update(float delta){
        p1.update();
    }

    public void draw(){
        // Draw Background
        bgBatch.begin();
        bgBatch.draw(background, 0, 0, SpaceInvaders.WIDTH, SpaceInvaders.HEIGHT);
        bgBatch.end();

        // NOTE: Just a test
        sr.begin(ShapeRenderer.ShapeType.Filled);
        p1.draw(sr);
        sr.end();
    }

    //-----------------SCREEN-----------------//
    @Override
    public void render(float delta) {
        update(delta);
        draw();
    }

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

    //-----------------INPUT-----------------//
    @Override
    public void keyDown(int keyCode) {
        // NOTE: Just a test
        if(keyCode == Input.Keys.LEFT)
            p1.move(-0.1f);
        else if(keyCode == Input.Keys.RIGHT)
            p1.move(0.1f);
    }

    @Override
    public void touchDown(int screenX, int screenY, int pointer, int button) {

    }

}
