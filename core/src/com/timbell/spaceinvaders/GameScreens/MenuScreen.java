package com.timbell.spaceinvaders.GameScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.timbell.spaceinvaders.Assets.AssetManager;
import com.timbell.spaceinvaders.Collision.MenuCollision;
import com.timbell.spaceinvaders.Entities.Bullet;
import com.timbell.spaceinvaders.Entities.Button;
import com.timbell.spaceinvaders.Entities.Player;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.SpaceInvaders;
import com.timbell.spaceinvaders.ParticleEffect.Particle;

import static com.timbell.spaceinvaders.Assets.AssetManager.background;


/**
 * Created by timbell on 23/07/16.
 */
public class MenuScreen extends GameScreen {

    // TEST
    private Button testButton;

    private Player p1;

    private Array<ParticleEffect> particleEffects;
    private Array<Button> buttons;

    private boolean switchingToPlay;
    private static final int switchTime = 5;
    private float switchCount;

    private float backgroundTransparancy;

    private MenuCollision collision;


    public MenuScreen(SpaceInvaders game){

        super(game);

        // TEST
        this.buttons = new Array<Button>(false, 0);
        buttons.add(new Button(200, 200, 100, new Color(1f, 0f, 0f, 1f), new Color(0f, 1f, 0f, 1f), AssetManager.playSymbol));

        particleEffects = new Array<ParticleEffect>(false, 2);

        switchingToPlay = false;
        switchCount = 0;

        backgroundTransparancy = 0.7f;

        this.p1 = new Player();
        this.collision = new MenuCollision(this, p1, particleEffects, buttons);
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

    public void switchToPlayScreen(){
        switchingToPlay = true;
    }

    @Override
    public void render(float delta) {

        p1.update();
        particleEffects.addAll(collision.checkCollisions());

        if(switchingToPlay){
            switchCount += delta;
            backgroundTransparancy = 0.7f-(switchCount/switchTime)*0.7f;
            if(switchCount > switchTime) {
                switchingToPlay = false;
                game.changeToPlayState(p1);
            }
        }


        //TEST
        game.bgport.apply();

        // Draw Background
        game.bgBatch.begin();
        game.bgBatch.draw(background, 0, 0, SpaceInvaders.WIDTH, SpaceInvaders.HEIGHT);
        game.bgBatch.end();

        // darken background with a 30% transparent black square
        Gdx.gl.glEnable(GL20.GL_BLEND);

        game.sr.begin(ShapeRenderer.ShapeType.Filled);
        game.sr.setColor(0f, 0f, 0f, backgroundTransparancy);
        game.sr.rect(0, 0, SpaceInvaders.WIDTH, SpaceInvaders.HEIGHT);

        for(int i = 0; i < particleEffects.size; ++i){
            particleEffects.get(i).update(delta);
            particleEffects.get(i).bounds();
            particleEffects.get(i).draw(game.sr);
        }

        game.sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // TEST
//        testButton.draw(game.sr, game.sb);

        game.sr.begin(ShapeRenderer.ShapeType.Filled);
        for(int i = 0; i < buttons.size; ++i)
            buttons.get(i).drawShape(game.sr);
        p1.drawBullets(game.sr);
        game.sr.end();

        game.sb.begin();
        for(int i = 0; i < buttons.size; ++i)
            buttons.get(i).drawSymbol(game.sb);

        p1.draw(game.sb);
        game.sb.end();

//        game.sr.begin(ShapeRenderer.ShapeType.Filled);
//
//        game.sr.end();

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
        if(keyCode == Input.Keys.SPACE)
            p1.shoot();
    }
}
