package com.timbell.spaceinvaders.GameScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
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

    private PlayCollision playCollision;

    public PlayScreen(SpaceInvaders game, Camera cam, Player p1){
        super(game);
        this.p1 = p1;
        this.entering = true;
        this.enterPos = -SpaceInvaders.WIDTH;
        p1.setEntering(true);

        this.swarm = new Swarm(level1);

        this.particleEffects = new Array<ParticleEffect>(true, 0);

        this.playCollision = new PlayCollision(swarm, p1, particleEffects);
    }

    public void update(float delta){
        p1.update();
        swarm.update(delta);

        for(int i = particleEffects.size-1; i >= 0; --i){
            if(particleEffects.get(i).isDead()) {
                ParticleEffectPool.free(particleEffects.get(i));
                particleEffects.removeIndex(i);
            }
            else
                particleEffects.get(i).update(delta);
        }
        particleEffects.addAll( playCollision.checkCollisions() );
    }

    public void draw(){
        // draw background
        game.bgport.apply();
        game.bgBatch.begin();
        game.bgBatch.draw(background, 0, 0, SpaceInvaders.WIDTH, SpaceInvaders.HEIGHT);
        game.bgBatch.end();

        game.gameport.apply();
        // TODO: replase this with jusp changing the texture to be darker
        Gdx.gl.glEnable(GL20.GL_BLEND);
        game.sr.begin(ShapeRenderer.ShapeType.Filled);
        game.sr.setColor(0f, 0f, 0f, 0.5f);
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
        game.sr.end();


        // Sprite Drawing
        game.sb.begin();
        // swarm
        if(!entering)
            swarm.draw(game.sb);
        else
            swarm.draw(game.sb, enterPos, 0);
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

        p1.update();

        for(int i = particleEffects.size-1; i >= 0; --i){
            if(particleEffects.get(i).isDead()) {
                ParticleEffectPool.free(particleEffects.get(i));
                particleEffects.removeIndex(i);
            }
            else
                particleEffects.get(i).update(delta);
        }
        particleEffects.addAll( playCollision.checkEnteringCollisions() );
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
