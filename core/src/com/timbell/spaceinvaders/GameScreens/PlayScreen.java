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
import com.timbell.spaceinvaders.Entities.Bullet;
import com.timbell.spaceinvaders.Entities.Enemy;
import com.timbell.spaceinvaders.Entities.MotherShip;
import com.timbell.spaceinvaders.Entities.Player;
import com.timbell.spaceinvaders.Entities.ShieldPart;
import com.timbell.spaceinvaders.Entities.SpecialBullet;
import com.timbell.spaceinvaders.Entities.Swarm;
import com.timbell.spaceinvaders.Entities.Shield;
import com.timbell.spaceinvaders.Level.Level;
import com.timbell.spaceinvaders.ParticleEffect.Particle;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffectPool;
import com.timbell.spaceinvaders.SpaceInvaders;

public class PlayScreen extends GameScreen {

    public static final Color BG_COLOR = new Color(0f, 0f, 0f, 1f);
    public static final int LOSE_HEIGHT = Player.Y + Player.HEIGHT;
    private static final int START_ENTER_POS = -SpaceInvaders.WIDTH*2;

    // game objects
    private Swarm swarm;

    private float enterPos;

    private BitmapFont font;
    private GlyphLayout layout;

    private MotherShip motherShip;

    private Array<Level> levels;

    private String levelName;

    private Shield shields[];

    public PlayScreen(SpaceInvaders game, Player p1, Array<ParticleEffect> particleEffects){
        super(game, p1, particleEffects);

        this.swarm = new Swarm();
        this.motherShip = new MotherShip();

        shields = new Shield[4];
        for(int i = 0; i < shields.length; ++i){
            float x = (i+1)*Shield.GAP_WIDTH + i*Shield.WIDTH;
            shields[i] = new Shield(x, Shield.AT_HEIGHT, new Color(0.3f, 0.6f, 0.2f, 1.0f));
        }

        this.font = new BitmapFont();
        // smooth font
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.getData().setScale(5);
        this.layout = new GlyphLayout();
    }

    public void init(){
        super.init(BG_COLOR);
        levels = Level.getLevels();

        loadNextLevel();
        p1.reset();
        for(int i = 0; i < shields.length; ++i){
            shields[i].reset();
        }
    }

    public void loadNextLevel(){
        motherShip.dieNow();

        if(levels.size > 0) {

            this.state = STATE_ENTERING;
            p1.setState(Player.State.ENTERING);
            p1.killSpecialBullet();
            this.enterPos = START_ENTER_POS;
            this.transitionTime = 0;
            ParticleEffectPool.freeAll(particleEffects);

            this.levelName = levels.peek().getName();
            layout.setText(font, levelName);
            swarm.loadLevel(levels.pop());
            for(int i = 0; i < shields.length; ++i){
                shields[i].reset();
            }
        }
        else{
            changeScreen(SpaceInvaders.GAMEOVER_STATE);
        }
    }

    public void update(float delta){

        if(state == STATE_NORMAL) {
            // if all members are dead, then load the next level
            if(swarm.getNumMembersAlive() == 0) {
                loadNextLevel();
                return;
            }

            // spawn a mothership ??
            if( (motherShip.getState() == MotherShip.State.DEAD)  &&  swarm.getNumTimesMovedDown() > 2  && (!p1.isDead()) ) {
                if( Math.random()*10 <  (0.5)*delta )
                    motherShip.reset();
            }
            // update mothership
            if( (motherShip.getState() == MotherShip.State.ALIVE || motherShip.getState() == MotherShip.State.DYING) )
                motherShip.update(delta);

            boolean gameOver = swarm.update(delta);
            swarm.updateBullets(delta);

            gameOver = gameOver || p1.isDead();

            if (gameOver) {
                if(!p1.isDead())
                    p1.die();
                changeScreen(SpaceInvaders.GAMEOVER_STATE);
            }
        }
        else if(state == STATE_TRANSITION_GAMEOVERSCREEN){
            transitionTime += delta;
            if(transitionTime > transitionPeriod) {
                ParticleEffectPool.freeAll(particleEffects);
                game.changeScreen(SpaceInvaders.GAMEOVER_STATE);
            }
            motherShip.dieNow();
            SpaceInvaders.mix(BG_COLOR, GameOverScreen.BG_COLOR, transitionTime / transitionPeriod, backgroundColor);
        }
        else if(state == STATE_ENTERING){
            enterPos += 4;
            if(enterPos >= 0){
                enterPos = 0;
                state = STATE_NORMAL;
                p1.setState(Player.State.NORMAL);
                transitionTime = 0;
                return;
            }
        }

        // always update these
        p1.update(delta);
        collision();

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

    public void draw(float delta){
        float fadeTransparancy = 1f;
        if(state == STATE_ENTERING)
            fadeTransparancy = (START_ENTER_POS-enterPos)/START_ENTER_POS;
        else if(state == STATE_TRANSITION_GAMEOVERSCREEN){
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
            if (!(state == STATE_ENTERING)){
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
                // side bars
                game.sr.setColor(1f, 1f, 1f, 0.25f);
                game.sr.rect(0 - SpaceInvaders.xOff, 0 - SpaceInvaders.yOff, SpaceInvaders.xOff, SpaceInvaders.viewportHeight);
                game.sr.rect(SpaceInvaders.WIDTH, 0 - SpaceInvaders.yOff, SpaceInvaders.xOff, SpaceInvaders.viewportHeight);

                for(int i = 0; i < shields.length; ++i){
                    shields[i].draw(game.sr, fadeTransparancy);
                }
            game.sr.end();

            game.sb.begin();
            game.sb.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
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

    public void collision(){
        // player bullets and bounds
        particleEffects.addAll( p1.bulletsBounds() );

        //playerBullets and enemies | and enemyBullets  |  motherShips, shields
        boolean bulletHit;
        for(int i = p1.getNumBullets()-1; i >= 0; --i){
            Bullet bullet = p1.bullets[i];
            bulletHit = false;
            //enemies
            for(int j = swarm.members.length-1; j >= 0 && !bulletHit; --j){
                Enemy enemy = swarm.members[j];
                if(!enemy.isDead()  &&  enemy.getRect().overlaps(bullet.getRect())){
                    p1.addToScore(enemy.getScoreAdd());
                    ParticleEffect enemyParticlleEffect = enemy.hit();
                    if(enemyParticlleEffect != null)
                        particleEffects.add( enemyParticlleEffect );
                    particleEffects.add( bullet.hit() );
                    p1.removeBullet(i);
                    bulletHit = true;
                }
            }
            // enemy bullets
            Bullet[] enemyBullets = swarm.bullets;
            int numBullets = swarm.getNumBullets();
            for(int j = numBullets-1; j >= 0 && !bulletHit; --j){
                if(enemyBullets[j].getRect().overlaps(bullet.getRect())){
                    particleEffects.add( enemyBullets[j].hit() );
                    swarm.removeBullet(j);
                    particleEffects.add(p1.bullets[i].hit());
                    p1.removeBullet(i);
                    bulletHit = true;
                }
            }
            // mothership
            if( motherShip.getState() == MotherShip.State.ALIVE  &&  !bulletHit ){
                if(motherShip.getRect().overlaps(bullet.getRect())){
                    particleEffects.add(bullet.hit());
                    p1.removeBullet(i);
                    String powerupStr = p1.generatePowerup();
                    motherShip.hit(powerupStr);
                    float x = motherShip.getRect().getX();
                    float y = motherShip.getRect().getY();
                    float width = motherShip.getRect().getWidth();
                    float height = motherShip.getRect().getHeight();
                    p1.setAttractingParticleEffect(x+width/2, y+height/2, width, height, MotherShip.COLOR);
                    bulletHit = true;
                }
            }
            // shields
            for(int s = 0; s < shields.length; ++s){
                Rectangle shieldRect = shields[s].getBoundingRect();
                if(shieldRect.overlaps(bullet.getRect())){
                    ShieldPart shieldParts[] = shields[s].shieldParts;
                    for(int sp = 0; sp < shieldParts.length && bulletHit == false; ++sp) {
                        if(shieldParts[sp].isAlive()) {
                            Rectangle shieldPartRect = shieldParts[sp].getRect();
                            if(shieldPartRect.overlaps(bullet.getRect())) {
                                particleEffects.add(bullet.hit());
                                particleEffects.add(shieldParts[sp].hitPlayerBullet());
                                p1.removeBullet(i);
                                bulletHit = true;
                            }
                        }
                    }
                }
            }
        } // end player bullets loop

        //particles AND enemies, player, bounds, sheilds
        for(int i = 0; i < particleEffects.size; ++i){
            Particle[] particles = particleEffects.get(i).particles;

            for(int p = 0; p < particles.length; ++p) {
                float pX = particles[p].getX();
                float pY = particles[p].getY();
                //enemies
                for (int e = 0; e < swarm.members.length; ++e) {
                    Enemy enemy = swarm.members[e];
                    if( !enemy.isDead()  &&  enemy.getRect().contains(pX, pY) ){
                        particles[p].bounce(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight());
                    }
                }
                //player
                if( !(p1.isDead() || p1.isRespawning())) {
                    p1.getLocationRects(recievePlayerRectangles);
                    for (int j = 0; j < recievePlayerRectangles.length; ++j) {
                        if (recievePlayerRectangles[j].contains(pX, pY)) {
                            particles[p].bounce(recievePlayerRectangles[j].getX(), recievePlayerRectangles[j].getY(), (int) recievePlayerRectangles[j].getWidth(), (int) recievePlayerRectangles[j].getHeight());
                        }
                    }
                }
                //bounds
                particles[p].bounds();
                //sheilds
                for(int s = 0; s < shields.length; ++s){
                    Rectangle shieldRect = shields[s].getBoundingRect();
                    if (shieldRect.contains(pX, pY)) {
                        ShieldPart shieldParts[] = shields[s].shieldParts;
                        for(int sp = 0; sp < shieldParts.length; ++sp) {
                            if(shieldParts[sp].isAlive()) {
                                if(shieldParts[sp].getRect().contains(pX, pY)) {
                                    particles[p].bounce(shieldParts[sp].getX(), shieldParts[sp].getY(), (int) ShieldPart.WIDTH, (int) ShieldPart.HEIGHT);
                                }
                            }
                        }
                    }
                }
            }
        } // end particles loop

        // p1 Special bullet,  and enemies, and mothership, and enemy bullets
        SpecialBullet p1SpecialBullet = p1.getSpecialBullet();
        if( !p1SpecialBullet.isDead() ){
            // enemies
            for (int e = swarm.members.length-1; e >= 0; --e) {
                Enemy enemy = swarm.members[e];
                if( !enemy.isDead() ) {
                    if (enemy.getRect().overlaps(p1SpecialBullet.getRect())) {
                        p1.addToScore(enemy.getScoreAdd());
                        particleEffects.add(p1SpecialBullet.hit());
                        ParticleEffect enemyParticlleEffect = enemy.hit();
                        if (enemyParticlleEffect != null)
                            particleEffects.add(enemyParticlleEffect);
                    }
                }
            }
            // mothership
            if(motherShip.isAlive()) {
                if (motherShip.getRect().overlaps(p1SpecialBullet.getRect())) {
                    particleEffects.add(p1SpecialBullet.hit());
                    String powerupStr = p1.generatePowerup();
                    motherShip.hit(powerupStr);
                    float x = motherShip.getRect().getX();
                    float y = motherShip.getRect().getY();
                    float width = motherShip.getRect().getWidth();
                    float height = motherShip.getRect().getHeight();
                    p1.setAttractingParticleEffect(x+width/2, y+height/2, width, height, MotherShip.COLOR);
                }
            }
            // enemy bullets
            int numBullets = swarm.getNumBullets();
            for(int j = numBullets-1; j >= 0; --j){
                if(swarm.bullets[j].getRect().overlaps(p1SpecialBullet.getRect())){
                    particleEffects.add(swarm.bullets[j].hit());
                    swarm.removeBullet(j);
                    particleEffects.add(p1SpecialBullet.hitBullet());
                }
            }
            //shields
            for(int s = 0; s < shields.length; ++s){
                Rectangle shieldRect = shields[s].getBoundingRect();
                if(shieldRect.overlaps(p1SpecialBullet.getRect())){
                    ShieldPart shieldParts[] = shields[s].shieldParts;
                    for(int sp = 0; sp < shieldParts.length; ++sp) {
                        if(shieldParts[sp].isAlive()) {
                            Rectangle shieldPartRect = shieldParts[sp].getRect();
                            if(shieldPartRect.overlaps(p1SpecialBullet.getRect())) {
                                particleEffects.add(p1SpecialBullet.hit());
                                particleEffects.add(shieldParts[sp].hitSpecialBullet());
                            }
                        }
                    }
                }
            }
        } // end special bullet

        //enemyBullets and bounds and player
        int numBullets = swarm.getNumBullets();
        for(int i = numBullets-1; i >= 0; --i){
            Bullet bullet = swarm.bullets[i];
            // bounds
            if( bullet.getX() < 0 ||
                    bullet.getX()+bullet.getWidth() > SpaceInvaders.WIDTH ||
                    bullet.getY() < -SpaceInvaders.yOff) {
                particleEffects.add( bullet.hit() );
                swarm.removeBullet(i);
            }
            // player
            if(p1.getState() == Player.State.NORMAL) {
                p1.getLocationRects(recievePlayerRectangles);
                for (int j = 0; j < recievePlayerRectangles.length; ++j) {
                    if (recievePlayerRectangles[j].overlaps(bullet.getRect())) {
                        particleEffects.add(p1.hit());
                        particleEffects.add(bullet.hit());
                        swarm.removeBullet(i);
                        break;
                    }
                }
            }
            // shields
            boolean enemyBulletHit = false;
            for(int s = 0; s < shields.length; ++s){
                Rectangle shieldRect = shields[s].getBoundingRect();
                if(shieldRect.overlaps(bullet.getRect())){
                    ShieldPart shieldParts[] = shields[s].shieldParts;
                    for(int sp = 0; sp < shieldParts.length; ++sp) {
                        if(shieldParts[sp].isAlive()) {
                            Rectangle shieldPartRect = shieldParts[sp].getRect();
                            if(shieldPartRect.overlaps(bullet.getRect())) {
                                particleEffects.add(bullet.hit());
                                particleEffects.add(shieldParts[sp].hitEnemyBullet());
                                swarm.removeBullet(i);
                                break;
                            }
                        }
                    }
                }
            }
        } // end enemyBullets

        // enemies and shields
        boolean shieldHit;
        for(int s = 0; s < shields.length; ++s){
            if(!shields[s].isDead()) {
                shieldHit = false;
                for (int e = swarm.members.length - 1; e >= 0; --e) {
                    if(!swarm.members[e].isDead()) {
                        if (shields[s].getBoundingRect().overlaps(swarm.members[e].getRect())) {
                            ShieldPart shieldParts[] = shields[s].shieldParts;
                            boolean shieldPartHit = false;
                            for(int sp = 0; sp < shieldParts.length && !shieldPartHit; ++sp){
                                if(shieldParts[sp].isAlive() && shieldParts[sp].getRect().overlaps(swarm.members[e].getRect())){
                                    shieldPartHit = true;
                                }
                            }
                            if(shieldPartHit) {
                                shieldHit = true;
                                ParticleEffect enemyParticlleEffect = swarm.members[e].hit();
                                if (enemyParticlleEffect != null)
                                    particleEffects.add(enemyParticlleEffect);
                            }
                        }
                    }
                }
                if (shieldHit) {
                    ParticleEffect[] shieldParticleEffects = shields[s].die();
                    for(int spe = 0; spe < shieldParticleEffects.length; ++spe){
                        particleEffects.add(shieldParticleEffects[spe]);
                    }
                }
            }
        }



    } // end collision()

    //-----------------SCREEN-----------------//

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
        font.dispose();
    }

    //-----------------INPUT-----------------//
    @Override
    public void keyDown(int keyCode) {
        if(state == STATE_NORMAL  &&  keyCode == Input.Keys.SPACE)
            p1.shoot();
    }

    @Override
    public void touchDown(float x, float y){
        if(state == STATE_NORMAL)
            p1.shoot();
    }

}
