package com.timbell.spaceinvaders.GameScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.timbell.spaceinvaders.Collision.Collision;
import com.timbell.spaceinvaders.Entities.MotherShip;
import com.timbell.spaceinvaders.Entities.Player;
import com.timbell.spaceinvaders.Entities.Swarm;
import com.timbell.spaceinvaders.Level.Level;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffectPool;
import com.timbell.spaceinvaders.SpaceInvaders;

/**
 * Created by timbell on 18/07/16.
 */
public class PlayScreen extends GameScreen {

    public static final Color BG_COLOR = new Color(0f, 0f, 0f, 0.8f);
    // TODO: fully implement BG_COLOR and mix

    // game objects
    private Player p1;
    private Swarm swarm;

    private float enterPos;
    private State state;

    private final float transitionPeriod = 5;
    private float transitionTime;

    private Color backgroundColor;

    private BitmapFont font;
    private GlyphLayout layout;

    private MotherShip motherShip;

    private enum State{
        ENTERING, GAME, TRANSITION_GAMEOVER
    }

    private Array<Level> levels;

    private Array<ParticleEffect> particleEffects;

    private Collision collision;

    private String levelName;

    public PlayScreen(SpaceInvaders game, Player p1, Array<ParticleEffect> particleEffects, Collision collision){
        this.game = game;
        this.p1 = p1;
        this.particleEffects = particleEffects;
        this.collision = collision;

        this.swarm = new Swarm();
        this.motherShip = new MotherShip();

        this.backgroundColor = new Color();

        collision.addPlayScreenObjects(this, swarm, motherShip);

        this.font = new BitmapFont();
        font.getData().setScale(5);
        this.layout = new GlyphLayout();
    }

    public void init(){
        this.backgroundColor.set(BG_COLOR);
        levels = Level.getLevels();

        loadNextLevel();
        p1.reset();
    }

    public void loadNextLevel(){
        motherShip.dieNow();

        if(levels.size > 0) {

            this.state = State.ENTERING;
            swarm.reset();
            p1.setState(Player.State.ENTERING);
            this.enterPos = -SpaceInvaders.WIDTH*2;
            this.transitionTime = 0;
            freeAllParticleEffects();

            this.levelName = levels.peek().getName();
            layout.setText(font, levelName);
            swarm.loadLevel(levels.pop());
        }
        else{
            // TODO: handle game win
        }
    }

    public void update(float delta){
        // if all members are dead, then load the next level
        if(swarm.getNumMembersAlive() == 0)
            loadNextLevel();

        // if mothership is dead, swarm has moved down twice, and player is alive, then have a chance to spawn a mothership
        if( (motherShip.getState() == MotherShip.State.DEAD)  &&  swarm.getNumTimesMovedDown() > 2  && (!p1.isDead()) ) {
            if( Math.random()*10 <  ((double)0.5)*delta )
                motherShip.reset();
        }

        if(motherShip.getState() == MotherShip.State.ALIVE || motherShip.getState() == MotherShip.State.DYING)
            motherShip.update(delta);

        if(state == State.GAME) {
            swarm.update(delta);

            if (p1.isDead())
                changeScreen(SpaceInvaders.GAMEOVER_STATE);

            particleEffects.addAll(collision.checkPlayCollision());
        }
        else if(state == State.TRANSITION_GAMEOVER){
            transitionTime += delta;
            if(transitionTime > transitionPeriod) {
                p1.reset();
                freeAllParticleEffects();
                game.changeScreen(SpaceInvaders.GAMEOVER_STATE);
            }
            SpaceInvaders.mix(BG_COLOR, GameOverScreen.BG_COLOR, transitionTime / transitionPeriod, backgroundColor);
        }
        else if(state == State.ENTERING){
            enterPos += 4;
            if(enterPos >= 0){
                enterPos = 0;
                state = State.GAME;
                swarm.setEntering(false);
                p1.setState(Player.State.NORMAL);
                transitionTime = 0;
                return;
            }
            particleEffects.addAll(collision.checkPlayEnteringCollision());
        }

        // always update these
        p1.update(delta);

        // update particle effects, freeing dead ones
        for(int i = particleEffects.size-1; i >= 0; --i){
            if(particleEffects.get(i).isDead()) {
                ParticleEffectPool.free(particleEffects.get(i));
                particleEffects.removeIndex(i);
            }
            else
                particleEffects.get(i).update(delta);
        }
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
            game.sr.setColor(backgroundColor);
        game.sr.rect(0, 0, SpaceInvaders.WIDTH, SpaceInvaders.HEIGHT);
        game.sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

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
            if(!(state == State.ENTERING))
                swarm.draw(game.sb);
            else {
                swarm.draw(game.sb, enterPos, 0);
                font.draw(game.sb, layout, enterPos+SpaceInvaders.WIDTH, SpaceInvaders.HEIGHT/2f + layout.height/2);
            }
            if(motherShip.getState() == MotherShip.State.ALIVE || motherShip.getState() == MotherShip.State.DYING)
                motherShip.draw(game.sb);
            p1.drawScoreAndLives(game.sb);
        game.sb.end();


    }


    public void changeScreen(int screen){
        if(screen == SpaceInvaders.GAMEOVER_STATE){
            motherShip.dieNow();
            state = State.TRANSITION_GAMEOVER;
            transitionTime = 0;
        }
    }

    public void freeAllParticleEffects(){
        for(int i = particleEffects.size-1; i >= 0; --i){
            ParticleEffectPool.free(particleEffects.get(i));
            particleEffects.removeIndex(i);
        }
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
        if(state == State.GAME  &&  keyCode == Input.Keys.SPACE)
            p1.shoot();
        // TODO: remove this
        if(keyCode == Input.Keys.M)
            motherShip.reset();
        if(keyCode == Input.Keys.H)
            p1.hit();
    }

    @Override
    public void touchDown(float x, float y){
        if(state == State.GAME)
            p1.shoot();
    }

}
