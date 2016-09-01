package com.timbell.spaceinvaders.GameScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.timbell.spaceinvaders.Collision.Collision;
import com.timbell.spaceinvaders.Entities.Enemy;
import com.timbell.spaceinvaders.Entities.Player;
import com.timbell.spaceinvaders.Entities.Swarm;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.SpaceInvaders;

import static com.timbell.spaceinvaders.Assets.AssetManager.*;

/**
 * Created by timbell on 18/07/16.
 */
public class PlayScreen extends GameScreen {

    // game
    // game objects
    private Player p1;
    private Swarm swarm;
    private int[][] level1 = {
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
        {2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
        {3, 3, 3, 3, 3, 3, 3, 3, 3, 3},
        {3, 3, 3, 3, 3, 3, 3, 3, 3, 3}
    };

    private Array<ParticleEffect> particleEffects;

    private Collision collision;

    public PlayScreen(SpaceInvaders game, Camera cam){
        super(game);
        this.p1 = new Player();
        this.swarm = new Swarm(level1);

        this.particleEffects = new Array<ParticleEffect>(true, 0);

        this.collision = new Collision(swarm, p1);
    }

    public void update(float delta){
        p1.update();
        swarm.update(delta);

        for(int i = particleEffects.size-1; i >= 0; --i){
            if(particleEffects.get(i).isDead())
                particleEffects.removeIndex(i);
            else
                particleEffects.get(i).update(delta);
        }
        particleEffects.addAll( collision.checkCollisions() );
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

        game.sr.begin(ShapeRenderer.ShapeType.Filled);
        game.sb.begin();

        swarm.draw(game.sb);
        p1.draw(game.sb, game.sr);
        for(int i = particleEffects.size-1; i >= 0; --i)
            particleEffects.get(i).draw(game.sr);


        game.sb.end();
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
        if(keyCode == Input.Keys.SPACE)
            p1.shoot();
    }

    @Override
    public void touchDown(float x, float y){
        p1.shoot();
    }

}
