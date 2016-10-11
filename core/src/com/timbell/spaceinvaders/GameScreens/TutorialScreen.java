package com.timbell.spaceinvaders.GameScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.timbell.spaceinvaders.Collision.Collision;
import com.timbell.spaceinvaders.Entities.Button;
import com.timbell.spaceinvaders.Entities.Player;
import com.timbell.spaceinvaders.ParticleEffect.Particle;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffectPool;
import com.timbell.spaceinvaders.SpaceInvaders;

/**
 * Created by timbell on 10/10/16.
 */
public class TutorialScreen extends GameScreen {
    public static final Color BG_COLOR = new Color(0.1f, 0.1f, 0.5f, 0.5f);

    public static final int TUTORIAL_MOVE = 0;
    public static final int TUTORIAL_SHOOT = 1;
    public static final int TUTORIAL_SELECT_BUTTON = 2;
    private int stage;

    private BitmapFont font;
    private GlyphLayout layout;

    // dialogue
    Rectangle dialogueRect = new Rectangle( 50, SpaceInvaders.HEIGHT/4 +70, SpaceInvaders.WIDTH-100, SpaceInvaders.HEIGHT/4*3-100 );
    private float h1Size = 3.1f;
    private float h2Size = 2.5f;
    private float h1Y = SpaceInvaders.HEIGHT/6*5;
    private float h2Y = SpaceInvaders.HEIGHT/6*4;

    // move
    private Rectangle beam;
    private int beamWidth = 100;
    private final float targetTimeInBeam = 3f;
    private float timeInBeam;
    private boolean inBeam;

    // shoot
    private Rectangle target;
    private Color targetColor;
    private float targetSize = 50;
    private boolean targetHit;
    private float changeSelectButtonTimer;
    private final float changeSelectButtonTime = 5;

    // select button
    private Button okButton;

    public TutorialScreen(SpaceInvaders game, Player p1, Array<ParticleEffect> particleEffects) {
        super(game, p1, particleEffects);

        okButton = new Button(SpaceInvaders.WIDTH / 2f - 150f / 2f, SpaceInvaders.HEIGHT/4f, 60, new Color(0.576f, 0.769f, 0.49f, 1f), Color.BLACK, Button.ButtonSymbol.PLAY);
        beam = new Rectangle();
        target = new Rectangle();

        this.font = new BitmapFont();
        // smooth font
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.layout = new GlyphLayout();
        this.targetColor = new Color();
    }

    public void init() {
        super.init(BG_COLOR);
        p1.setState(Player.State.NORMAL);
        this.stage = TUTORIAL_MOVE;
        transitionTime = 0;

        // MOVE
        float x = (MathUtils.random() * (SpaceInvaders.WIDTH/2 - beamWidth*2));
        if(MathUtils.random() < 0.5){
            x = SpaceInvaders.WIDTH - beamWidth - MathUtils.random()*(SpaceInvaders.WIDTH / 2 - beamWidth*2);
        }
        beam.set( x, 0, beamWidth, SpaceInvaders.HEIGHT);
        timeInBeam = targetTimeInBeam;
        inBeam = false;
        // SHOOT
        targetColor.set(0.7f, 0.7f, 0.7f, 1.0f);
        target.set( MathUtils.random()*(SpaceInvaders.WIDTH - targetSize), SpaceInvaders.HEIGHT/4, targetSize, targetSize );
        changeSelectButtonTimer = 0;
        targetHit = false;
        // SELECT BUTTON
        this.okButton.reset();
    }

    @Override
    public void touchDown(float x, float y){
        if(stage == TUTORIAL_SELECT_BUTTON || stage == TUTORIAL_SHOOT)
            p1.shoot();
    }

    @Override
    public void keyDown(int keyCode) {
        if(stage == TUTORIAL_SELECT_BUTTON || stage == TUTORIAL_SHOOT) {
            if (keyCode == Input.Keys.SPACE)
                p1.shoot();
        }
    }

    public void update(float delta){
        if(state == STATE_ENTERING){
            transitionTime += delta;
            if(transitionTime > transitionPeriod) {
                state = STATE_NORMAL;
            }
        }
        else if(state == STATE_TRANSITION_MENUSCREEN){
            transitionTime += delta;
            SpaceInvaders.mix(BG_COLOR, MenuScreen.BG_COLOR, transitionTime/transitionPeriod, backgroundColor);
            if(transitionTime > transitionPeriod) {
                game.changeScreen(SpaceInvaders.MENU_STATE);
            }
        }

        if(stage == TUTORIAL_MOVE){
            if(inBeam){
                timeInBeam -= delta;
                if(timeInBeam <= 0){
                    nextStage();
                }
            }
            else{
                timeInBeam = targetTimeInBeam;
            }
        }
        else if(stage == TUTORIAL_SHOOT){
            if(targetHit){
                changeSelectButtonTimer += delta;
                if(changeSelectButtonTimer > changeSelectButtonTime){
                    nextStage();
                }
            }
        }
        else if(stage == TUTORIAL_SELECT_BUTTON){

        }

        p1.update(delta);
        collision();
    }

    public void draw(float delta){
        float fadeTransparancy = 1f;
        if(state == STATE_ENTERING)
            fadeTransparancy = transitionTime/transitionPeriod;
        else if(state == STATE_TRANSITION_MENUSCREEN){
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
        game.sr.setColor(1f, 1f, 1f, 0.25f);
        game.sr.rect(0 - SpaceInvaders.xOff, 0 - SpaceInvaders.yOff, SpaceInvaders.xOff, SpaceInvaders.viewportHeight);
        game.sr.rect(SpaceInvaders.WIDTH, 0 - SpaceInvaders.yOff, SpaceInvaders.xOff, SpaceInvaders.viewportHeight);

        // TUTORIAL_MOVE
        if(stage == TUTORIAL_MOVE){
            if(inBeam)
                game.sr.setColor(0.1f, 0.7f, 0.1f, 0.7f * fadeTransparancy*fadeTransparancy);
            else
                game.sr.setColor(0.7f, 0.7f, 0.7f, 0.7f * fadeTransparancy*fadeTransparancy);
            game.sr.rect(beam.getX(), beam.getY(), beam.getWidth(), beam.getHeight());
        }
        // TUTORIAL_SHOOT
        else if(stage == TUTORIAL_SHOOT){
            game.sr.setColor(targetColor);
            game.sr.rect(target.getX(), target.getY(), target.getWidth(), target.getHeight());
        }
        // TUTORIAL_SELECT_BUTTON
        else if(stage == TUTORIAL_SELECT_BUTTON) {
            // draw play button
            okButton.drawShape(game.sr, fadeTransparancy);
        }

        // dialogue
        game.sr.setColor(0.5f, 0.5f, 0.2f, fadeTransparancy);
        game.sr.rect(dialogueRect.getX(), dialogueRect.getY(), dialogueRect.getWidth(), dialogueRect.getHeight());
        float outlineWidth = 10;
        game.sr.setColor(0.7f, 0.7f, 0.7f, fadeTransparancy);
        game.sr.rect(dialogueRect.getX() + outlineWidth / 2, dialogueRect.getY() + outlineWidth / 2, dialogueRect.getWidth() - outlineWidth, dialogueRect.getHeight() - outlineWidth);
        game.sr.end();

        game.sb.begin();
        if(stage == TUTORIAL_MOVE) {
            font.setColor(0f, 0f, 0f, fadeTransparancy);

            font.getData().setScale(h1Size);
            layout.setText(font, "Tilt device to move");
            font.draw(game.sb, layout, SpaceInvaders.WIDTH / 2 - layout.width / 2, h1Y);

            font.getData().setScale(h2Size);
            layout.setText(font, "Move into the beam to continue...");
            font.draw( game.sb, layout, SpaceInvaders.WIDTH/2-layout.width/2, h2Y);
        }
        else if(stage == TUTORIAL_SHOOT) {
            font.setColor(0f, 0f, 0f, fadeTransparancy);

            font.getData().setScale(h1Size);
            layout.setText(font, "Tap screen to shoot");
            font.draw(game.sb, layout, SpaceInvaders.WIDTH / 2 - layout.width / 2, h1Y);

            font.getData().setScale(h2Size);
            layout.setText(font, "Shoot the target to continue...");
            font.draw( game.sb, layout, SpaceInvaders.WIDTH/2-layout.width/2, h2Y);
        }
        else if(stage == TUTORIAL_SELECT_BUTTON) {
            // draw play button symbol
            okButton.drawSymbol(game.sb, fadeTransparancy);

            font.setColor(0f, 0f, 0f, fadeTransparancy);

            font.getData().setScale(h1Size);
            layout.setText(font, "Shoot a button to select it");
            font.draw(game.sb, layout, SpaceInvaders.WIDTH / 2 - layout.width / 2, h1Y);

            font.getData().setScale(h2Size);
            layout.setText(font, "Select the button to continue...");
            font.draw(game.sb, layout, SpaceInvaders.WIDTH / 2 - layout.width / 2, h2Y);
        }
        game.sb.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
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

                if(stage == TUTORIAL_SHOOT){
                    if(target.contains(pX, pY)) {
                        particles[p].bounce(target.getX(), target.getY(), (int) target.getWidth(), (int) target.getHeight());
                    }
                }
                else if(stage == TUTORIAL_SELECT_BUTTON) {
                    // buttons
                    if (okButton.visible && okButton.getRect().contains(pX, pY)) {
                        particles[p].bounce(okButton.getX(), okButton.getY(), (int) okButton.getSize(), (int) okButton.getSize());
                    }
                }

                //sheilds

            }
        } // end particles loop

        // player bullets and buttons,
        for (int i = p1.getNumBullets() - 1; i >= 0; --i) {
            Rectangle bulletRect = p1.bullets[i].getRect();
            if(stage == TUTORIAL_SHOOT){
                if(bulletRect.overlaps(target)){
                    particleEffects.add(p1.bullets[i].hit());
                    p1.removeBullet(i);
                    targetColor.set(0.1f, 0.7f, 0.1f, 1.0f);
                    targetHit = true;
                    transitionTime = 0;
                }
            }
            else if(stage == TUTORIAL_SELECT_BUTTON) {
                if (okButton.visible && bulletRect.overlaps(okButton.getRect())) {
                    particleEffects.addAll(okButton.hit(), 0, 2);
                    particleEffects.add(p1.bullets[i].hit());
                    p1.removeBullet(i);
                    nextStage();
                }
            }
        }

        // player and beam
        if(stage == TUTORIAL_MOVE){
            inBeam = false;
            if( p1.getX() < beam.getX()+beam.getWidth()  &&  p1.getX()+p1.getWidth() > beam.getX() ){
                inBeam = true;
            }
        }

    } // end collision()

    public void nextStage(){
        if(stage == TUTORIAL_MOVE){
            stage = TUTORIAL_SHOOT;
        }
        else if(stage == TUTORIAL_SHOOT){
            stage = TUTORIAL_SELECT_BUTTON;
        }
        else if(stage == TUTORIAL_SELECT_BUTTON){
            state = STATE_TRANSITION_MENUSCREEN;
        }
    }



}
