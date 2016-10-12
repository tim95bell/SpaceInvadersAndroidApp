package com.timbell.spaceinvaders.Entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.timbell.spaceinvaders.ParticleEffect.AttractingParticleEffect;
import com.timbell.spaceinvaders.SpaceInvaders;
import com.timbell.spaceinvaders.Entities.Player.Powerup;

public class PlayerHUD {

    private Player p1;
    private Rectangle[] playerRects;

    private BitmapFont font;
    private GlyphLayout layout;

    private float scale;
    private float topOfUpper;

    private AttractingParticleEffect attractingParticleEffect;
    private float powerupTextX, powerupTextY, powerupTextWidth, powerupTextHeight;
    private float percentAlive;

    public PlayerHUD(Player p1){
        this.p1 = p1;
        this.playerRects = p1.getShapeRects();

        this.font = new BitmapFont();
        // smooth font
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.getData().setScale(1.1f);
        this.layout = new GlyphLayout();

        this.scale = 0.6f;
        this.topOfUpper = SpaceInvaders.HEIGHT - SpaceInvaders.UNIT * (scale / 2) + SpaceInvaders.yOff;

        this.attractingParticleEffect = new AttractingParticleEffect();
    }

    public void update(float delta){
        if(attractingParticleEffect.getNumAlive() > 0) {
            attractingParticleEffect.update(delta);
        }

        percentAlive = ((float)attractingParticleEffect.getNumAlive()) / ((float)AttractingParticleEffect.NUM_PARTICLES);
        attractingParticleEffect.setAttractor( powerupTextX + (1f-percentAlive)*powerupTextWidth, powerupTextY + powerupTextHeight/2 );
    }

    public void setAttractingParticleEffect(float x, float y, float xSpread, float ySpread, Color color){
        attractingParticleEffect.reset((int)x, (int)y, (int)xSpread, (int)ySpread, color, powerupTextX + (1f-percentAlive)*powerupTextWidth, powerupTextY + powerupTextHeight/2);

        layout.setText(font, String.format("%s : %.0f", p1.getPowerupString(p1.getPowerup()), p1.getPowerupTimeLeft()));
        powerupTextX = SpaceInvaders.WIDTH - layout.width / 4 * 5;
        powerupTextY = topOfUpper-layout.height;
        powerupTextWidth = layout.width*1.1f;
        powerupTextHeight = layout.height;

        attractingParticleEffect.setAttractor( powerupTextX, powerupTextY + powerupTextHeight/2 );
    }

    public void drawShapes(ShapeRenderer sr, float transparency) {
        // LIVES
        float x = SpaceInvaders.UNIT;
        float y = topOfUpper - (p1.HEIGHT*scale);
        sr.setColor(1,1,1,transparency);
        for(int i = 0; i < p1.getLives(); ++i){
            for(int j = 0; j < playerRects.length; ++j) {
                sr.rect(x + playerRects[j].getX()* scale, y + playerRects[j].getY()* scale, playerRects[j].getWidth()* scale, playerRects[j].getHeight()* scale);
            }
            x += p1.WIDTH *scale + SpaceInvaders.UNIT;
        }

        // BULLET ONE
        sr.rect(10, SpaceInvaders.UNIT / 3f - SpaceInvaders.yOff, SpaceInvaders.UNIT * 12 * p1.getBulletOneShootTimePercent(), SpaceInvaders.UNIT / 3f);
        // BULLET TWO
        if(p1.getPowerup() == Player.Powerup.DOUBLESHOT)
            sr.rect(10, SpaceInvaders.UNIT-SpaceInvaders.yOff, SpaceInvaders.UNIT * 12 * p1.getBulletTwoShootTimePercent(), SpaceInvaders.UNIT / 3f);
        // ATTRACTING PARTICLES
        if(attractingParticleEffect.getNumAlive() > 0) {
            attractingParticleEffect.draw(sr);
        }
        // POWERUP COVER
        if(p1.getPowerup() != Powerup.NONE){
            sr.setColor(p1.getBackgroundColor());
            sr.rect(powerupTextX + (1f-percentAlive)*powerupTextWidth, powerupTextY, percentAlive*powerupTextWidth, powerupTextHeight);
        }
    }

    public void drawText(SpriteBatch sb, float transparency){
        float x = (p1.WIDTH * scale + SpaceInvaders.UNIT) * p1.getLives() + SpaceInvaders.UNIT/2f;
        font.setColor(1,1,1,transparency);
        // LIVES
        if(p1.getLives() > 0) {
            font.draw(sb, "x " + p1.getLives(), x, topOfUpper, 20, 0, false);
        }
        // SCORE
        layout.setText(font, "Score: " + p1.getScore());
        x = SpaceInvaders.WIDTH/2 - layout.width / 2;
        font.draw(sb, layout, x, topOfUpper);
        // POWERUP
        Powerup powerup = p1.getPowerup();
        if(powerup != Powerup.NONE){
            font.setColor(Color.ORANGE);
            layout.setText(font, String.format("%s : %.0f", p1.getPowerupString(powerup), p1.getPowerupTimeLeft() ));
            font.draw(sb, layout, SpaceInvaders.WIDTH - layout.width / 4 * 5, topOfUpper);
        }
    }

}
