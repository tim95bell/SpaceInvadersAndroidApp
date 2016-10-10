package com.timbell.spaceinvaders.GameScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.timbell.spaceinvaders.Collision.Collision;
import com.timbell.spaceinvaders.Entities.Bullet;
import com.timbell.spaceinvaders.Entities.Button;
import com.timbell.spaceinvaders.Entities.Player;
import com.timbell.spaceinvaders.ParticleEffect.Particle;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffectPool;
import com.timbell.spaceinvaders.SpaceInvaders;



/**
 * Created by timbell on 23/07/16.
 */
public class MenuScreen extends GameScreen {

    public static final Color BG_COLOR = new Color(0.6f, 0.1f, 0.1f, 0.6f);

    private Button playButton;

    private BitmapFont highScoreFont;
    private GlyphLayout highScoreLayout;
    private String highScoreText;


    public MenuScreen(SpaceInvaders game, Player p1, Array<ParticleEffect> particleEffects){
        super(game, p1, particleEffects);

        playButton = new Button(SpaceInvaders.WIDTH/2f - 150f/2f, SpaceInvaders.HEIGHT/2f - 150f/2f + SpaceInvaders.UNIT*2, 150, new Color(0.576f, 0.769f, 0.49f, 1f), Color.BLACK, Button.ButtonSymbol.PLAY);

        this.highScoreFont = new BitmapFont();
        // smooth font
        highScoreFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        highScoreFont.getData().setScale(4);
        this.highScoreLayout = new GlyphLayout();
    }

    public void init(){
        super.init(BG_COLOR);

        this.playButton.reset();
        this.highScoreText = "High Score: "+game.getHighScore();
        p1.setState(Player.State.NORMAL);
    }

    @Override
    public void touchDown(float x, float y){
        p1.shoot();
    }

    public void update(float delta){
        if(state == STATE_ENTERING){
            transitionTime += delta;
            if(transitionTime > transitionPeriod) {
                state = STATE_NORMAL;
            }
        }
        else if(state == STATE_TRANSITION_PLAYSCREEN){
            transitionTime += delta;
            SpaceInvaders.mix(BG_COLOR, PlayScreen.BG_COLOR, transitionTime/transitionPeriod, backgroundColor);
            if(transitionTime > transitionPeriod) {
                game.changeScreen(SpaceInvaders.PLAY_STATE);
            }
        }

        p1.update(delta);
        collision();
    }

    public void draw(float delta){
        float fadeTransparancy = 1f;
        if(state == STATE_ENTERING)
            fadeTransparancy = transitionTime/transitionPeriod;
        else if(state == STATE_TRANSITION_PLAYSCREEN){
            fadeTransparancy = 1f - transitionTime/transitionPeriod;
        }

        Gdx.gl.glEnable(GL20.GL_BLEND);
            game.sr.begin(ShapeRenderer.ShapeType.Filled);
                game.sr.setColor(backgroundColor);
                game.sr.rect(-SpaceInvaders.xOff, -SpaceInvaders.yOff, SpaceInvaders.viewportWidth, SpaceInvaders.viewportHeight);
            game.sr.end();

            game.sr.begin(ShapeRenderer.ShapeType.Filled);
                // draw and free particle effects
                for(int i = 0; i < particleEffects.size; ++i) {
                    if(particleEffects.get(i).isDead()) {
                        ParticleEffectPool.free(particleEffects.get(i));
                        particleEffects.removeIndex(i);
                    }
                    else {
                        particleEffects.get(i).update(delta);
                        particleEffects.get(i).bounds();
                        particleEffects.get(i).draw(game.sr);
                    }
                }
                // player
                p1.drawBullets(game.sr);
                p1.draw(game.sr);
                // side bars
                game.sr.setColor(1f,1f,1f,0.25f);
                game.sr.rect(0 - SpaceInvaders.xOff, 0 - SpaceInvaders.yOff, SpaceInvaders.xOff, SpaceInvaders.viewportHeight);
                game.sr.rect(SpaceInvaders.WIDTH, 0 - SpaceInvaders.yOff, SpaceInvaders.xOff, SpaceInvaders.viewportHeight);
                // draw play button
                playButton.drawShape(game.sr, fadeTransparancy);
            game.sr.end();

            game.sb.begin();
                // draw play button symbol
                playButton.drawSymbol(game.sb, fadeTransparancy);
                // high score text
                highScoreFont.setColor(1,  1, 1, fadeTransparancy);
                highScoreLayout.setText(highScoreFont, highScoreText);
                highScoreFont.draw(game.sb, highScoreLayout, SpaceInvaders.WIDTH / 2 - highScoreLayout.width / 2, SpaceInvaders.HEIGHT - highScoreLayout.height / 2);
            game.sb.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    @Override
    public void dispose() {
//        testButton = null;
//        particleEffects = null;
//        buttons = null;
//        collision = null;
    }

    public void collision(){
        //playerBullets and roof/walls
        particleEffects.addAll(p1.bulletsBounds());

        //particles and buttons, player, bounds, sheilds
        for(int i = 0; i < particleEffects.size; ++i){
            Particle[] particles = particleEffects.get(i).particles;

            for(int p = 0; p < particles.length; ++p) {
                float pX = particles[p].getX();
                float pY = particles[p].getY();

                //player
                p1.getLocationRects(recievePlayerRectangles);
                for(int j = 0; j < recievePlayerRectangles.length; ++j) {
                    if (recievePlayerRectangles[j].contains(pX, pY)) {
                        particles[p].bounce(recievePlayerRectangles[j].getX(), recievePlayerRectangles[j].getY(), (int)recievePlayerRectangles[j].getWidth(), (int)recievePlayerRectangles[j].getHeight());
                    }
                }

                //bounds
                particles[p].bounds();

                // buttons
                // looks awesome without visible
                if (playButton.visible && playButton.getRect().contains(pX, pY)) {
                    particles[p].bounce(playButton.getX(), playButton.getY(), (int) playButton.getSize(), (int) playButton.getSize());
                }

                //sheilds

            }
        } // end particles loop

        // player bullets and buttons,
        for(int i = p1.getNumBullets()-1; i >= 0; --i) {
            Rectangle bulletRect = p1.bullets[i].getRect();

            if (playButton.visible && bulletRect.overlaps(playButton.getRect())) {
                particleEffects.addAll(playButton.hit(), 0, 2);
                particleEffects.add(p1.bullets[i].hit());
                p1.removeBullet(i);
                changeScreen(SpaceInvaders.PLAY_STATE);
            }
        }
    } // end collision()



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
    public void keyDown(int keyCode) {
        if(keyCode == Input.Keys.SPACE)
            p1.shoot();
    }
}
