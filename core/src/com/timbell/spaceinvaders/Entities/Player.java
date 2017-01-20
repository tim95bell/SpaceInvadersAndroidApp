package com.timbell.spaceinvaders.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.timbell.spaceinvaders.GameScreens.GameScreen;
import com.timbell.spaceinvaders.GameScreens.PlayScreen;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffectPool;
import com.timbell.spaceinvaders.SpaceInvaders;

import com.badlogic.gdx.math.Rectangle;

public class Player {
    public enum Powerup{
        NONE, DOUBLESHOT, LEFTSHOT, RIGHTSHOT, VERTICALSHOT, EXTRA_LIVE
    }
    public enum State{
        ENTERING, NORMAL, RESPAWNING, DEAD
    }
    private State state;
    private Powerup powerup;
    private float powerupTimeLeft;

    public static final int WIDTH = (int)(SpaceInvaders.UNIT*3.5);
    public static final int HEIGHT = (int)(SpaceInvaders.UNIT*2);
    public static final int Y = SpaceInvaders.UNIT*2;

    private final Color color = new Color(1.0f, 1.0f, 1.0f, 1f);

    public static Sound shootSound;
    public static Sound hitSound;
    public static Sound gong;

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

    private float respawnTimer;
    private final float respawnTime = 4;

    private PlayerHUD hud;


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
    }

    public void setCurrentScreen(GameScreen currentScreen){
        this.currentScreen =  currentScreen;
    }

    public void draw(ShapeRenderer sr){
        if( isRespawning() ){
            float alpha;
            float startFlashingAt = respawnTime/8;
            float flashTime = (respawnTime-startFlashingAt)/3;
            alpha = ((respawnTimer-startFlashingAt) % flashTime) * (2 / flashTime);
            if (alpha > 1)
                alpha = 1 - (alpha - 1);

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

    public void die(){
        lives = 0;
        state = State.DEAD;
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
        center();
    }

    public ParticleEffect[] bulletsBounds(){
        ParticleEffect[] particleEffects = new ParticleEffect[3];
        int count = 0;

        // bullet 2
        if(numBullets > 1) {
            if (bullets[1].getX() < 0 ||
                    bullets[1].getX() + bullets[1].getWidth() > SpaceInvaders.WIDTH ||
                    bullets[1].getY() + bullets[1].getHeight() > SpaceInvaders.HEIGHT+SpaceInvaders.yOff) {
                particleEffects[0] = bullets[1].hit();
                removeBullet(1);
                gong.play(SpaceInvaders.volume);
                count++;
            }
        }
        // bullet 1
        if(numBullets > 0){
            if (bullets[0].getX() < 0 ||
                    bullets[0].getX() + bullets[0].getWidth() > SpaceInvaders.WIDTH ||
                    bullets[0].getY() + bullets[0].getHeight() > SpaceInvaders.HEIGHT+SpaceInvaders.yOff) {
                particleEffects[1] = bullets[0].hit();
                removeBullet(0);
                gong.play(SpaceInvaders.volume);
                count++;
            }
        }
        // special bullet
        if( !specialBullet.isDead() ) {
            if (specialBullet.getX() < 0 ||
                    specialBullet.getX() + specialBullet.getWidth() > SpaceInvaders.WIDTH ||
                    specialBullet.getY() + specialBullet.getHeight() > SpaceInvaders.HEIGHT + SpaceInvaders.yOff) {
                particleEffects[2] = specialBullet.hit();
                specialBullet.die();
                gong.play(SpaceInvaders.volume);
                count++;
            }
        }

        ParticleEffect[] answer = new ParticleEffect[count];
        int j = 0;
        for(int i = 0; i < particleEffects.length; ++i){
            if(particleEffects[i] != null){
                answer[j] = particleEffects[i];
                ++j;
            }
        }

        return answer;
    }

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
        float dividor = 6f;
        if(lives < 3)
            dividor = 8f;
        if(r  < 2f/6f)
            powerup = Powerup.DOUBLESHOT;
        else if(r  < 4f/dividor)
            powerup = Powerup.VERTICALSHOT;
        else if(r  < 5f/dividor)
            powerup = Powerup.LEFTSHOT;
        else if(r <= 6f/dividor)
            powerup = Powerup.RIGHTSHOT;
        else {
            powerupTimeLeft = 5;
            if(lives < 3)
                lives++;
            powerup = Powerup.EXTRA_LIVE;
        }

        this.powerup = powerup;
        return getPowerupString(this.powerup);
    }

    public SpecialBullet getSpecialBullet(){
        return specialBullet;
    }

    public void setAttractingParticleEffect(float x, float y, float xSpread, float ySpread, Color color){
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
        else if(powerup == Powerup.EXTRA_LIVE){
            s = "Extra Life";
        }

        return s;
    }

    public void clearBullets(){
        numBullets = 0;
        specialBullet.die();
    }

    public boolean isRespawning(){
        return state == State.RESPAWNING;
    }

    public float getBulletOneShootTimePercent(){
        return bulletOneShootTime / shootPeriod;
    }
    public float getBulletTwoShootTimePercent(){
        return bulletTwoShootTime / shootPeriod;
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
    public State getState() { return state; }

}
