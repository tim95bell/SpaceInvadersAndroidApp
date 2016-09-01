package com.timbell.spaceinvaders.GameScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.timbell.spaceinvaders.Assets.AssetManager;
import com.timbell.spaceinvaders.Entities.Button;
import com.timbell.spaceinvaders.SpaceInvaders;

import static com.timbell.spaceinvaders.Assets.AssetManager.background;


/**
 * Created by timbell on 23/07/16.
 */
public class MenuScreen extends GameScreen {


//    private SpriteBatch sb;
//    private SpriteBatch bgBatch;
//    private ShapeRenderer sr;

    // TEST
    private Button testButton;



    public MenuScreen(SpaceInvaders game){

        super(game);

        // TEST
        testButton = new Button(200, 200, 100, new Color(1f, 0f, 0f, 1f), new Color(0f, 1f, 0f, 1f), AssetManager.playSymbol);

    }

    @Override
    public void touchDown(float x, float y){

        y = (y/Gdx.graphics.getHeight())*SpaceInvaders.HEIGHT;
        y = SpaceInvaders.HEIGHT-y;
        x = (x/Gdx.graphics.getWidth())*SpaceInvaders.WIDTH;

        // TEST
        if(testButton.contains((int)x, (int)y))
            game.setState(game.PLAY_STATE);

    }

    @Override
    public void render(float delta) {

        //TEST
        game.bgport.apply();

        // Draw Background
        game.bgBatch.begin();
        game.bgBatch.draw(background, 0, 0, SpaceInvaders.WIDTH, SpaceInvaders.HEIGHT);
        game.bgBatch.end();

        // darken background with a 30% transparent black square
        Gdx.gl.glEnable(GL20.GL_BLEND);
        game.sr.begin(ShapeRenderer.ShapeType.Filled);
        game.sr.setColor(0f, 0f, 0f, 0.7f);
        game.sr.rect(0, 0, SpaceInvaders.WIDTH, SpaceInvaders.HEIGHT);
        game.sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // TEST
//        testButton.draw(game.sr, game.sb);

        game.sr.begin(ShapeRenderer.ShapeType.Filled);
        testButton.drawShape(game.sr);
        game.sr.end();

        game.sb.begin();
        testButton.drawSymbol(game.sb);
        game.sb.end();

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
