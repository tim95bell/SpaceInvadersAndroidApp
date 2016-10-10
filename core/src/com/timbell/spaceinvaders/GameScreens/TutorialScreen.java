package com.timbell.spaceinvaders.GameScreens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Array;
import com.timbell.spaceinvaders.Collision.Collision;
import com.timbell.spaceinvaders.Entities.Button;
import com.timbell.spaceinvaders.Entities.Player;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.SpaceInvaders;

/**
 * Created by timbell on 10/10/16.
 */
public class TutorialScreen extends GameScreen {
    public static final Color BG_COLOR = new Color(0.1f, 0.1f, 6.1f, 0.6f);

    private Player p1;
    private Array<ParticleEffect> particleEffects;
    private Color backgroundColor;
    private Collision collision;
    private State state;

    private final float transitionPeriod = 5;
    private float transitionTime;

    private BitmapFont font;
    private GlyphLayout layout;

    // move

    // shoot

    // select button
    private Button okButton;

    public enum State{
        ENTERING, NORMAL, TRANSITION_MENU
    }

    public TutorialScreen(SpaceInvaders game, Player p1, Array<ParticleEffect> particleEffects, Collision collision){

        this.game = game;
        this.p1 = p1;
        this.particleEffects = particleEffects;
//        this.collision = collision;

        this.backgroundColor = new Color();

        okButton = new Button(SpaceInvaders.WIDTH/2f - 150f/2f, SpaceInvaders.HEIGHT/2f - 150f/2f + SpaceInvaders.UNIT*2, 80, new Color(0.576f, 0.769f, 0.49f, 1f), Color.BLACK, Button.ButtonSymbol.PLAY);

        this.font = new BitmapFont();
        // smooth font
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.getData().setScale(4);
        this.layout = new GlyphLayout();
    }
}
