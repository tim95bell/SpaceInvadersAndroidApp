package com.timbell.spaceinvaders.GameScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.timbell.spaceinvaders.Collision.Collision;
import com.timbell.spaceinvaders.Collision.PlayCollision;
import com.timbell.spaceinvaders.Entities.Player;
import com.timbell.spaceinvaders.Entities.Swarm;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffectPool;
import com.timbell.spaceinvaders.SpaceInvaders;

import static com.timbell.spaceinvaders.Assets.AssetManager.*;

/**
 * Created by timbell on 18/07/16.
 */
public class PlayScreen extends GameScreen {

    public static final Color BG_COLOR = new Color(0f, 0f, 0f, 0.5f);
    // TODO: fully implement BG_COLOR and mix

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

    private boolean entering;
    private float enterPos;



    private Array<ParticleEffect> particleEffects;

    private Collision collision;

    public PlayScreen(SpaceInvaders game, Player p1, Array<ParticleEffect> particleEffects, Collision collision){
        this.game = game;
        this.p1 = p1;
        this.particleEffects = particleEffects;
        this.collision = collision;

        this.swarm = new Swarm(level1);

        collision.addPlayScreenObjects(this, swarm);

//        this.entering = true;
//        this.enterPos = -SpaceInvaders.WIDTH;
//        p1.setEntering(true);
//
//        this.swarm = new Swarm(level1);
//
//        this.particleEffects = new Array<ParticleEffect>(true, 0);
//
//        this.playCollision = new PlayCollision(swarm, p1, particleEffects);
    }

    public void init(){
        this.entering = true;
        this.enterPos = -SpaceInvaders.WIDTH;
//        p1.setEntering(true);
        p1.reset();
        swarm.reset();
    }

    public void update(float delta){

        p1.update(delta);
        swarm.update(delta);

        if(p1.isDead())
            game.changeScreen(SpaceInvaders.GAMEOVER_STATE);

        for(int i = particleEffects.size-1; i >= 0; --i){
            if(particleEffects.get(i).isDead()) {
                ParticleEffectPool.free(particleEffects.get(i));
                particleEffects.removeIndex(i);
            }
            else
                particleEffects.get(i).update(delta);
        }
        particleEffects.addAll( collision.checkPlayCollision() );
    }

    public void draw(){
        // draw background
        game.bgport.apply();
        game.bgBatch.begin();
        game.bgBatch.draw(SpaceInvaders.BACKGROUND, 0, 0, SpaceInvaders.WIDTH, SpaceInvaders.HEIGHT);
        game.bgBatch.end();

        game.gameport.apply();
        // TODO: replase this with jusp changing the texture to be darker
        Gdx.gl.glEnable(GL20.GL_BLEND);
        game.sr.begin(ShapeRenderer.ShapeType.Filled);
        game.sr.setColor(BG_COLOR);
        game.sr.rect(0, 0, SpaceInvaders.WIDTH, SpaceInvaders.HEIGHT);
        game.sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);


        // draw the rest
//        game.gameport.apply();


        // Shape Drawing

        game.sr.begin(ShapeRenderer.ShapeType.Filled);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        // particles
        for(int i = particleEffects.size-1; i >= 0; --i)
            particleEffects.get(i).draw(game.sr);
        Gdx.gl.glDisable(GL20.GL_BLEND);
        // player bullets
        p1.drawBullets(game.sr);
        // swarm bullets
        swarm.drawBullets(game.sr);
        // player
        p1.draw(game.sr);
        p1.drawLives(game.sr);
        game.sr.end();


        // Sprite Drawing
        game.sb.begin();
        // swarm
        if(!entering)
            swarm.draw(game.sb);
        else
            swarm.draw(game.sb, enterPos, 0);
        p1.drawScore(game.sb);
        game.sb.end();


    }

    public void updateEntering(float delta){
        enterPos += 4;
        if(enterPos >= 0){
            enterPos = 0;
            entering = false;
            swarm.setEntering(false);
            p1.setEntering(false);
            return;
        }

//        swarm.updateEntering(delta);

        p1.update(delta);

        for(int i = particleEffects.size-1; i >= 0; --i){
            if(particleEffects.get(i).isDead()) {
                ParticleEffectPool.free(particleEffects.get(i));
                particleEffects.removeIndex(i);
            }
            else
                particleEffects.get(i).update(delta);
        }
        particleEffects.addAll( collision.checkPlayCollision() );
    }

    public void changeScreen(int screen){
        if(screen == SpaceInvaders.GAMEOVER_STATE){
            game.changeScreen(SpaceInvaders.GAMEOVER_STATE);
        }
    }


    //-----------------SCREEN-----------------//
    @Override
    public void render(float delta) {
        if(!entering)
            update(delta);
        else
            updateEntering(delta);

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
