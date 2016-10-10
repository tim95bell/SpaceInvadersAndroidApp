package com.timbell.spaceinvaders.GameScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
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

    public static final Color BG_COLOR = new Color(0f, 0f, 0f, 1f);
    // TODO: fully implement BG_COLOR and mix
    public static final int LOSE_HEIGHT = Player.Y + Player.HEIGHT;
    private static final int START_ENTER_POS = -SpaceInvaders.WIDTH*2;

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
        ENTERING, GAME, TRANSITION_GAMEOVER, PLAYER_SPAWNING
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
        // smooth font
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.getData().setScale(5);
        this.layout = new GlyphLayout();
    }

    public void init(){
        this.backgroundColor.set(BG_COLOR);
        levels = Level.getLevels();

        loadNextLevel();
        p1.reset();
        p1.setCurrentScreen(this);
    }

    public void loadNextLevel(){
        motherShip.dieNow();

        if(levels.size > 0) {

            this.state = State.ENTERING;
            swarm.reset();
            p1.setState(Player.State.ENTERING);
            p1.killSpecialBullet();
            this.enterPos = START_ENTER_POS;
            this.transitionTime = 0;
            ParticleEffectPool.freeAll(particleEffects);

            this.levelName = levels.peek().getName();
            layout.setText(font, levelName);
            swarm.loadLevel(levels.pop());
        }
        else{
            changeScreen(SpaceInvaders.GAMEOVER_STATE);
        }
    }

    public void update(float delta){

        if(state == State.GAME) {
            // if all members are dead, then load the next level
            if(swarm.getNumMembersAlive() == 0) {
                loadNextLevel();
                return;
            }

            // spawn a mothership ??
            if( (motherShip.getState() == MotherShip.State.DEAD)  &&  swarm.getNumTimesMovedDown() > 2  && (!p1.isDead()) ) {
                if( Math.random()*10 <  ((double)0.5)*delta )
                    motherShip.reset();
            }
            // update mothership
            if( (motherShip.getState() == MotherShip.State.ALIVE || motherShip.getState() == MotherShip.State.DYING) )
                motherShip.update(delta);

            boolean gameOver = swarm.update(delta);
            swarm.updateBullets(delta);

            gameOver = gameOver || p1.isDead();

            if (gameOver)
                changeScreen(SpaceInvaders.GAMEOVER_STATE);

            particleEffects.addAll(collision.checkPlayCollision());
        }
        else if(state == State.TRANSITION_GAMEOVER){
            transitionTime += delta;
            if(transitionTime > transitionPeriod) {
//                p1.reset();
                ParticleEffectPool.freeAll(particleEffects);
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
        float fadeTransparancy = 1f;
        if(state == State.ENTERING)
            fadeTransparancy = (START_ENTER_POS-enterPos)/START_ENTER_POS;
        else if(state == State.TRANSITION_GAMEOVER){
            fadeTransparancy = 1f - transitionTime/transitionPeriod;
        }


        Gdx.gl.glEnable(GL20.GL_BLEND);
            if( !backgroundColor.equals(BG_COLOR) ) {
                game.sr.begin(ShapeRenderer.ShapeType.Filled);
                game.sr.setColor(backgroundColor);
                game.sr.rect(-SpaceInvaders.xOff, -SpaceInvaders.yOff, SpaceInvaders.viewportWidth, SpaceInvaders.viewportHeight);
                game.sr.end();
            }

            // Sprite Drawing
        game.sb.begin();
            // swarm
            if (!(state == State.ENTERING)){
                swarm.draw(game.sb);
            }
            else {
                swarm.draw(game.sb, enterPos, 0);
                font.draw(game.sb, layout, enterPos + SpaceInvaders.WIDTH, SpaceInvaders.HEIGHT/2f + layout.height/2);
            }

            if( !(motherShip.getState() == MotherShip.State.DEAD) )
                motherShip.draw(game.sb);
            p1.drawHUDText(game.sb, fadeTransparancy);
        game.sb.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
            // Shape Drawing
            game.sr.begin(ShapeRenderer.ShapeType.Filled);
                // particles
                for(int i = particleEffects.size-1; i >= 0; --i)
                    particleEffects.get(i).draw(game.sr);
                // swarm bullets
                swarm.drawBullets(game.sr);
                // player
                p1.drawBullets(game.sr);
                p1.draw(game.sr);
                p1.drawHUDShapes(game.sr, fadeTransparancy);
//                p1.drawPowerupCover(game.sr);
                // side bars
//                game.sr.setColor(1f, 1f, 1f, 1f);
//                game.sr.rect(-5, 0 - SpaceInvaders.yOff, 5, SpaceInvaders.viewportHeight);
//                game.sr.rect(SpaceInvaders.WIDTH, 0 - SpaceInvaders.yOff, 5, SpaceInvaders.viewportHeight);
                game.sr.setColor(1f,1f,1f,0.25f);
                game.sr.rect(0-SpaceInvaders.xOff, 0 - SpaceInvaders.yOff, SpaceInvaders.xOff, SpaceInvaders.viewportHeight);
                game.sr.rect(SpaceInvaders.WIDTH, 0 - SpaceInvaders.yOff, SpaceInvaders.xOff, SpaceInvaders.viewportHeight);
            game.sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void changeScreen(int screen){
        if(screen == SpaceInvaders.GAMEOVER_STATE){
            motherShip.dieNow();
            state = State.TRANSITION_GAMEOVER;
            transitionTime = 0;
            p1.clearBullets();
            swarm.clearBullets();
        }
    }

    public void onPlayerRespawnStart(){
        motherShip.pause();
        swarm.pause();
    }

    public void onPlayerRespawnEnd(){
        motherShip.resume();
        swarm.resume();
    }

    public Color getBackgroundColor(){
        return backgroundColor;
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
