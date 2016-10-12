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
import com.timbell.spaceinvaders.Entities.Button;
import com.timbell.spaceinvaders.Entities.Player;
import com.timbell.spaceinvaders.ParticleEffect.Particle;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffectPool;
import com.timbell.spaceinvaders.SpaceInvaders;

public class GameOverScreen extends GameScreen {

    public static final Color BG_COLOR = new Color(0.88f, 0.4f, 0.4f, 0.75f);
    private Button retryButton, mainMenuButton;

    private BitmapFont font;
    private GlyphLayout fontLayout;
    private String mainText, thisScoreText, oldHighScoreText;
    private int oldHighScore, thisScore;

    public GameOverScreen(SpaceInvaders game, Player p1, Array<ParticleEffect> particleEffects) {
        super(game, p1, particleEffects);

        this.retryButton = new Button(82, SpaceInvaders.HEIGHT-237, 70, new Color(0.576f, 0.769f, 0.49f, 1f), Color.BLACK, Button.ButtonSymbol.RETRY);
        this.mainMenuButton = new Button(SpaceInvaders.WIDTH-152, SpaceInvaders.HEIGHT-237, 70, new Color(0.7f, 0.65f, 0.84f, 1f), Color.BLACK, Button.ButtonSymbol.EXIT);

        this.font = new BitmapFont();
        // smooth font
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.fontLayout = new GlyphLayout();
    }

    public void init(){
        super.init(BG_COLOR);

        p1.clearBullets();

        if(p1.getScore() == SpaceInvaders.MAX_SCORE)
            mainText = "You Won!";
        else
            mainText = "Game Over";
        thisScore = p1.getScore();
        oldHighScore = game.getHighScore();
        if(thisScore > oldHighScore) {
            thisScoreText = "New High Score: " + thisScore;
            game.setHighScore(thisScore);
        }
        else
            thisScoreText = "Score: " + thisScore;
        oldHighScoreText = "Previous High Score: " + oldHighScore;

        retryButton.reset();
        mainMenuButton.reset();

        p1.reset();
        p1.respawn();
    }

    public void update(float delta){
        if(state == STATE_ENTERING){
            transitionTime += delta;
            if(transitionTime > transitionPeriod) {
                transitionTime = 0;
                state = STATE_NORMAL;
            }
        }
        else if(state == STATE_TRANSITION_PLAYSCREEN){
            transitionTime += delta;
            SpaceInvaders.mix(BG_COLOR, PlayScreen.BG_COLOR, transitionTime / transitionPeriod, backgroundColor);
            if(transitionTime > transitionPeriod) {
                game.changeScreen(SpaceInvaders.PLAY_STATE);
            }
        }
        else if(state == STATE_TRANSITION_MENUSCREEN){
            transitionTime += delta;
            SpaceInvaders.mix(BG_COLOR, MenuScreen.BG_COLOR, transitionTime / transitionPeriod, backgroundColor);
            if(transitionTime > transitionPeriod) {
                game.changeScreen(SpaceInvaders.MENU_STATE);
            }
        }

        p1.update(delta);
        collision();
    }

    public void draw(float delta){
        float fadeTransparancy = 1f;
        if(state == STATE_ENTERING)
            fadeTransparancy = transitionTime/transitionPeriod;
        else if(state == STATE_TRANSITION_PLAYSCREEN || state == STATE_TRANSITION_MENUSCREEN)
            fadeTransparancy = 1f-(transitionTime/transitionPeriod);

        Gdx.gl.glEnable(GL20.GL_BLEND);
            game.sr.begin(ShapeRenderer.ShapeType.Filled);
                game.sr.setColor(backgroundColor);
                game.sr.rect(-SpaceInvaders.xOff, -SpaceInvaders.yOff, SpaceInvaders.viewportWidth, SpaceInvaders.viewportHeight);
                retryButton.drawShape(game.sr, fadeTransparancy);
                mainMenuButton.drawShape(game.sr, fadeTransparancy);
            game.sr.end();

            game.sr.begin(ShapeRenderer.ShapeType.Filled);
                // particles
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
            game.sr.end();

            game.sb.begin();
                retryButton.drawSymbol(game.sb, fadeTransparancy);
                mainMenuButton.drawSymbol(game.sb, fadeTransparancy);
                // draw text
                font.setColor(1,1,1,fadeTransparancy);
                // Game Over / You Won
                font.getData().setScale(4f);
                fontLayout.setText(font, mainText);
                float mainTextHeight = fontLayout.height;
                float mainTextX = SpaceInvaders.WIDTH/2 - fontLayout.width/2;
                float mainTextY = SpaceInvaders.HEIGHT - mainTextHeight / 2;
                font.draw(game.sb, fontLayout, mainTextX, mainTextY);
                // your score
                font.getData().setScale(2f);
                fontLayout.setText(font, thisScoreText);
                float thisScoreTextHeight = fontLayout.height;
                float thisScoreTextX = SpaceInvaders.WIDTH/2 - fontLayout.width/2;
                float thisScoreTextY = mainTextY - mainTextHeight*2;
                font.draw(game.sb, fontLayout, thisScoreTextX, thisScoreTextY);
                // old high score
                font.getData().setScale(1.5f);
                fontLayout.setText(font, oldHighScoreText);
                float oldHighScoreTextX = SpaceInvaders.WIDTH/2 - fontLayout.width/2;
                float oldHighScoreTextY = thisScoreTextY - thisScoreTextHeight*2;
                font.draw( game.sb, fontLayout, oldHighScoreTextX, oldHighScoreTextY );
            game.sb.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    @Override
    public void keyDown(int keyCode) {
        if( (state == STATE_NORMAL || state == STATE_ENTERING)  &&  keyCode == Input.Keys.SPACE)
            p1.shoot();
    }

    @Override
    public void touchDown(float x, float y) {
        if(state == STATE_NORMAL  ||  state == STATE_ENTERING)
            p1.shoot();
    }

    @Override
    public void dispose() {
        font.dispose();
        retryButton.dispose();
        mainMenuButton.dispose();
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
                if (retryButton.visible && retryButton.getRect().contains(pX, pY)) {
                    particles[p].bounce(retryButton.getX(), retryButton.getY(), (int) retryButton.getSize(), (int) retryButton.getSize());
                }
                if (mainMenuButton.visible && mainMenuButton.getRect().contains(pX, pY)) {
                    particles[p].bounce(mainMenuButton.getX(), mainMenuButton.getY(), (int) mainMenuButton.getSize(), (int) mainMenuButton.getSize());
                }
                //sheilds
            }
        } // end particles loop

        // player bullets and buttons,
        for(int i = p1.getNumBullets()-1; i >= 0; --i) {
            Rectangle bulletRect = p1.bullets[i].getRect();

            if (retryButton.visible && bulletRect.overlaps(retryButton.getRect())) {
                particleEffects.addAll(retryButton.hit());
                particleEffects.add(p1.bullets[i].hit());
                p1.removeBullet(i);
                changeScreen(SpaceInvaders.PLAY_STATE);
            }
            if (mainMenuButton.visible && bulletRect.overlaps(mainMenuButton.getRect())) {
                particleEffects.addAll(mainMenuButton.hit());
                particleEffects.add(p1.bullets[i].hit());
                p1.removeBullet(i);
                changeScreen(SpaceInvaders.MENU_STATE);
            }
        }
    } // end collision()


    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void hide() {
        super.hide();
    }
}
