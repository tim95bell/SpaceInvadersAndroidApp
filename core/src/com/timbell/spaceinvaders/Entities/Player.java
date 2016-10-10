package com.timbell.spaceinvaders.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.timbell.spaceinvaders.GameScreens.GameOverScreen;
import com.timbell.spaceinvaders.GameScreens.GameScreen;
import com.timbell.spaceinvaders.GameScreens.PlayScreen;
import com.timbell.spaceinvaders.ParticleEffect.AttractingParticleEffect;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffectPool;
import com.timbell.spaceinvaders.SpaceInvaders;

import com.badlogic.gdx.math.Rectangle;

/**
 * Created by timbell on 21/07/16.
 */
public class Player {

    public enum Powerup{
        NONE, DOUBLESHOT, LEFTSHOT, RIGHTSHOT, VERTICALSHOT
    }

    public enum State{
        ENTERING, NORMAL, RESPAWNING, DEAD
    }

    private State state;

    public static final int WIDTH = (int)(SpaceInvaders.UNIT*3.5);
    public static final int HEIGHT = (int)(SpaceInvaders.UNIT*2);
    public static final int Y = SpaceInvaders.UNIT*2;

    private final Color color = new Color(1.0f, 1.0f, 1.0f, 1f);

    public static Sound shootSound;
    public static Sound hitSound;

    private GameScreen currentScreen;

    // these are relative to loc
    private Rectangle[] rects;

    private Vector2 loc;
    private float xAcc, xVel;
    private float xAccelerometerVel;

    public Bullet[] bullets;
    private int numBullets;
    private SpecialBullet specialBullet;

    private int lives;
    private int score;

    private float shootPeriod = 1;
    private float bulletOneShootTime;
    private float bulletTwoShootTime;

    private Powerup powerup;
    private float powerupTimeLeft;

    private float respawnTimer;
    private final float respawnTime = 4;

    private PlayerHUD hud;
//    private AttractingParticleEffect attractingParticleEffect;

    public Player(){
        this.loc = new Vector2(SpaceInvaders.WIDTH/2 - WIDTH /2f, Y);

        this.rects = new Rectangle[] {
            new Rectangle(0, 0, WIDTH, (5f/8f)* HEIGHT),
            new Rectangle(WIDTH *0.5f - WIDTH *(3f/13f)/2f, (5f/8f)* HEIGHT, WIDTH *(3f/13f), (2f/8f)* HEIGHT),
            new Rectangle(WIDTH *0.5f - WIDTH *(1f/13f)/2f, (7f/8f)* HEIGHT, WIDTH *(1f/13f), (1f/8f)* HEIGHT)
        };

        this.bullets = new Bullet[]{new Bullet(), new Bullet()};
        this.specialBullet = new SpecialBullet();
        this.hud = new PlayerHUD(this);
//        this.attractingParticleEffect = new AttractingParticleEffect();

        reset();
    }

    public void reset(){
        this.lives = 3;
        this.xAcc = 0;
        this.xVel = 0;
        this.xAccelerometerVel = 0;
        this.numBullets = 0;
        this.specialBullet.die();
        this.powerup = Powerup.NONE;
        this.bulletOneShootTime = shootPeriod;
        this.bulletTwoShootTime = shootPeriod;
        this.state = State.NORMAL;
        this.score = 0;
//        this.attractingParticleEffect.die();
    }

    public void setCurrentScreen(GameScreen currentScreen){
        this.currentScreen =  currentScreen;
    }

    public void draw(ShapeRenderer sr){
        if( isRespawning() ){
            float alpha;
            float startFlashingAt = respawnTime/8;
            if(respawnTime < startFlashingAt){
                alpha = 0;
            }
            else {
                float flashTime = (respawnTime-startFlashingAt)/3;
                alpha = ((respawnTimer-startFlashingAt) % flashTime) * (2 / flashTime);
                if (alpha > 1)
                    alpha = 1 - (alpha - 1);
            }

            sr.setColor(color.r, color.g, color.b, alpha);
        }
        else if(state == State.DEAD){
            return;
        }
        else {
            sr.setColor(color);
        }

        for(int i = 0; i < rects.length; ++i) {
            sr.rect(loc.x + rects[i].getX(), loc.y + rects[i].getY(), rects[i].getWidth(), rects[i].getHeight());
        }

    }

    public void drawBullets(ShapeRenderer sr){
        for(int i = 0; i < numBullets; ++i){
            bullets[i].draw(sr);
        }
        if( !specialBullet.isDead() ) {
            specialBullet.draw(sr);
        }
    }

    public void drawHUDShapes(ShapeRenderer sr, float transparency){
        hud.drawShapes(sr, transparency);
    }

    public void drawHUDText(SpriteBatch sb, float transparency){
        hud.drawText(sb, transparency);
    }

    public void update(float delta) {
        if(isRespawning()){
            respawnTimer += delta;
            if(respawnTimer > respawnTime) {
                state = State.NORMAL;
                currentScreen.onPlayerRespawnEnd();
            }
            else
                return;
        }

        hud.update(delta);
        move(delta);

        // handle powerups
        if (powerup != Powerup.NONE) {
            // decrease powerup time if in normal state
            if(state == State.NORMAL) {
                powerupTimeLeft -= delta;
                if (powerupTimeLeft < 0) {
                    powerupTimeLeft = 0;
                    powerup = Powerup.NONE;
                }
            }

            // doubleshot bullet two shoot time regen
            if (powerup == Powerup.DOUBLESHOT) {
                if (bulletTwoShootTime < shootPeriod) {
                    bulletTwoShootTime += delta;
                    if (bulletTwoShootTime > shootPeriod)
                        bulletTwoShootTime = shootPeriod;
                }
            }
        }
        // reload bulletShootTime
        if (bulletOneShootTime < shootPeriod) {
            bulletOneShootTime += delta;
            if (bulletOneShootTime > shootPeriod)
                bulletOneShootTime = shootPeriod;
        }

        for(int i = 0; i < numBullets; ++i){
            bullets[i].update(delta);
        }

        if( !specialBullet.isDead() )
            specialBullet.update(delta);
    }

    public void friction(float force){
        xVel *= force;
    }

    public void move(float delta){
        if( isRespawning() || isDead() )
            return;

        xAccelerometerVel = Gdx.input.getAccelerometerY();
        xAccelerometerVel /= 5f;
        if(xAccelerometerVel > 1)
            xAccelerometerVel = 1;

        //apply friction
        friction(0.9f);

        //apoly accelerometer velocity
        xVel += xAccelerometerVel;

        //apply acceleration and velocity
        xVel += xAcc;
        loc.x += xVel * delta*60;

        //keys
        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean space = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        if(left && !right){
            xVel -= 1.0f;
        } else if(right && !left){
            xVel += 1.0f;
        }

        if(xVel > 5)
            xVel = 5;
        else if(xVel < -5)
            xVel = -5;

        //reset acceleration
        xAcc = 0;

        if(loc.x+ WIDTH > SpaceInvaders.WIDTH)
            loc.x = SpaceInvaders.WIDTH - WIDTH;
        else if(loc.x < 0)
            loc.x = 0;

    }

    public void shoot(){
        if( isRespawning() || isDead() )
            return;

        if( ( (numBullets == 1  &&  powerup == Powerup.DOUBLESHOT)  ||  numBullets == 0 )
                &&  state != State.ENTERING ){
            if(powerup == Powerup.DOUBLESHOT && bulletTwoShootTime >= shootPeriod){
                bullets[numBullets].reset((int) loc.x + WIDTH / 2 - (5 / 2), (int) loc.y + HEIGHT, 5, 10, /*2*/8, color, Bullet.Type.RECT);
                ++numBullets;
                shootSound.play(SpaceInvaders.volume);
                bulletTwoShootTime -= shootPeriod;
            }
            else if(bulletOneShootTime >= shootPeriod) {
                if(powerup == Powerup.RIGHTSHOT){
                    specialBullet.reset((int) loc.x + WIDTH / 2 - (5 / 2), (int) loc.y + HEIGHT, SpecialBullet.Direction.RIGHT);
                    powerup = Powerup.NONE;
                }
                else if(powerup == Powerup.LEFTSHOT){
                    specialBullet.reset((int) loc.x + WIDTH / 2 - (5 / 2), (int) loc.y + HEIGHT, SpecialBullet.Direction.LEFT);
                    powerup = Powerup.NONE;
                }
                else if(powerup == Powerup.VERTICALSHOT){
                    specialBullet.reset((int) loc.x + WIDTH / 2 - (5 / 2), (int) loc.y + HEIGHT, SpecialBullet.Direction.VERTICAL);
                    powerup = Powerup.NONE;
                }
                else{
                    bullets[numBullets].reset((int) loc.x + WIDTH / 2 - (5 / 2), (int) loc.y + HEIGHT, 5, 10, /*2*/8, color, Bullet.Type.RECT);
                    ++numBullets;
                    bulletOneShootTime -= shootPeriod;
                }
                shootSound.play(SpaceInvaders.volume);
            }
        }
    }

    public void removeBullet(int index){
        // swap bullets[index] with bullets[numBullets-1]
        if(numBullets > 0 && numBullets < 3) {
            Bullet temp = bullets[index];
            bullets[index] = bullets[numBullets - 1];
            bullets[numBullets - 1] = temp;
            --numBullets;
        }
    }

    // TODO: make player respawn, flash, while swarm is paused and its bullets are removed. then  everything resumes. WHEN HIT

    public ParticleEffect hit(){
        if(lives > 0)
            --lives;

        hitSound.play(SpaceInvaders.volume);
        ParticleEffect answer = ParticleEffectPool.getLarge();
        answer.reset(0, (int)loc.x+ WIDTH /2, (int)loc.y+ HEIGHT /2, 10, 10, color);

        if(lives > 0){
            respawn();
        }
        else{
            state = State.DEAD;
        }

        return answer;
    }

    public void center(){
        loc.x = SpaceInvaders.WIDTH/2 - getWidth()/2;
    }

    public void respawn(){
        currentScreen.onPlayerRespawnStart();
        state = State.RESPAWNING;
        respawnTimer = 0;
        clearBullets();
        powerup = Powerup.NONE;
        xVel = 0;
        // center player
        center();
    }

    // TODO: stop these news
    public void getLocationRects(Rectangle[] outRects){
        outRects[0].set(rects[0]);
        outRects[1].set(rects[1]);
        outRects[2].set(rects[2]);
        for(int i = 0; i < outRects.length; ++i){
            outRects[i].x += loc.x;
            outRects[i].y += loc.y;
        }
    }

    public Rectangle[] getShapeRects(){
        return rects;
    }

    public Color getBackgroundColor(){
        return ((PlayScreen)currentScreen).getBackgroundColor();
    }



    public float getX(){
        return loc.x;
    }

    public float getY(){
        return loc.y;
    }

    public int getWidth(){
        return WIDTH;
    }

    public int getHeight(){
        return HEIGHT;
    }

    public int getNumBullets(){
        return numBullets;
    }

    public void setState(State state){
        this.state = state;
    }

    public boolean isDead(){
        return lives <= 0;
    }
    public int getScore(){
        return score;
    }

    public void addToScore(int val){
        score += val;
    }

    public String generatePowerup(){
        Powerup powerup;
        this.powerupTimeLeft = 30;

        double r = MathUtils.random();
        if(r  < 1f/3f)
            powerup = Powerup.DOUBLESHOT;
        else if(r  < 1f/3f*2f)
            powerup = Powerup.VERTICALSHOT;
        else if(r  < 1f/6f*5f)
            powerup = Powerup.LEFTSHOT;
        else
            powerup = Powerup.RIGHTSHOT;

        this.powerup = powerup;
        return getPowerupString(this.powerup);
    }

    public SpecialBullet getSpecialBullet(){
        return specialBullet;
    }

    public void setAttractingParticleEffect(float x, float y, float xSpread, float ySpread, Color color){
//        this.attractingParticleEffect.reset((int)x, (int)y, (int)xSpread, (int)ySpread, color, hud.getAttractorX(), hud.getAttractorY());
//        hud.setAttractingParticleEffect(this.attractingParticleEffect);
        hud.setAttractingParticleEffect((int) x, (int) y, (int) xSpread, (int) ySpread, color);
    }

    public void killSpecialBullet() {
        specialBullet.die();
    }

    public String getPowerupString(Powerup powerup){
        String s = "";
        if(powerup == Powerup.DOUBLESHOT)
            s = "Double Shot";
        else if(powerup == Powerup.RIGHTSHOT)
            s = "Right Shot";
        else if(powerup == Powerup.LEFTSHOT)
            s = "Left Shot";
        else if(powerup == Powerup.VERTICALSHOT)
            s = "Vertical Shot";

        return s;
    }

    public void clearBullets(){
        numBullets = 0;
        specialBullet.die();
    }

    public boolean isRespawning(){
        return state == State.RESPAWNING;
    }

    public float getShootTimePercent(){
        return bulletOneShootTime / shootPeriod;
    }

    public Powerup getPowerup(){
        return powerup;
    }

    public int getLives(){
        return lives;
    }

    public float getPowerupTimeLeft(){
        return powerupTimeLeft;
    }

}
