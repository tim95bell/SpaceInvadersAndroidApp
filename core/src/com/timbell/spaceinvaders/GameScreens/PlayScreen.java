package com.timbell.spaceinvaders.GameScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.timbell.spaceinvaders.Entities.Player;
import com.timbell.spaceinvaders.SpaceInvaders;

import static com.timbell.spaceinvaders.Assets.AssetManager.*;

/**
 * Created by timbell on 18/07/16.
 */
public class PlayScreen extends GameScreen {

    // game
    // game objects
    private Player p1;

    public PlayScreen(SpaceInvaders game, Camera cam){
        super(game);
        this.p1 = new Player();
    }

    public void update(float delta){
        p1.update();
    }

    public void draw(){
        // Draw Background

        //TEST
        game.bgport.apply();

        game.bgBatch.begin();
        game.bgBatch.draw(background, 0, 0, SpaceInvaders.WIDTH, SpaceInvaders.HEIGHT);
        game.bgBatch.end();

        //TEST
        game.gameport.apply();

        game.sb.begin();
        game.sb.setColor(1f, 0.2f, 0.2f, 1f);
        game.sb.draw(enemyOneA, 0, 0, 60, 40);
        game.sb.draw(enemyOneB, 170, 100, 60, 40);
        game.sb.setColor(0.2f, 0.2f, 1f, 1f);
        game.sb.draw(enemyTwoA, 250, 100, 55, 40);
        game.sb.draw(enemyTwoB, 320, 100, 55, 40);
        game.sb.setColor(0.2f, 1f, 0.2f, 1f);
        game.sb.draw(enemyThreeA, 100, 200, 40, 40);
        game.sb.draw(enemyThreeB, 150, 200, 40, 40);
        game.sb.setColor(1f, 1f, 1f, 1f);
        game.sb.draw(playerImage, 200, 200, 65, 40);
        game.sb.draw(zigzagBulletA, 280, 200, 15, 35);
        game.sb.draw(zigzagBulletB, 295, 200, 15, 35);
        game.sb.end();

        // NOTE: Just a test
        game.sr.begin(ShapeRenderer.ShapeType.Filled);
        p1.draw(game.sr);
        game.sr.end();
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
