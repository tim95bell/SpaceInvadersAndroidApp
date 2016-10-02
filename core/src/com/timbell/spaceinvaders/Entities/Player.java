package com.timbell.spaceinvaders.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffectPool;
import com.timbell.spaceinvaders.SpaceInvaders;

import com.badlogic.gdx.math.Rectangle;

/**
 * Created by timbell on 21/07/16.
 */
public class Player {

    public enum Powerup{
        NONE, DOUBLESHOT
    }

    public enum State{
        ENTERING, NORMAL, RESPAWNING, DEAD
    }

    private State state;

    private final int width = (int)(SpaceInvaders.UNIT*3.5);
    private final int height = (int)(SpaceInvaders.UNIT*2);

    private final Color color = new Color(1.0f, 1.0f, 1.0f, 1f);

    private static final Sound shootSound = Gdx.audio.newSound( Gdx.files.internal("shoot16bit.wav"));
    private static final Sound hitSound = Gdx.audio.newSound( Gdx.files.internal("explosion16bit.wav"));

    // these are relative to loc
    private Rectangle[] rects;

    private Vector2 loc;
    private float xAcc, xVel;
    private float xAccelerometerVel;

    public Bullet[] bullets;
    private int numBullets;

    private int lives;
    private int score;

    private float shootPeriod = 1;
    private float bulletOneShootTime;
    private float bulletTwoShootTime;

    private Powerup powerup;

    private BitmapFont font;
    private GlyphLayout layout;

    public Player(){
        this.loc = new Vector2(SpaceInvaders.WIDTH/2 - width/2f, SpaceInvaders.UNIT*2);

        this.rects = new Rectangle[] {
//            new Rectangle(0, 0, width, width / 3),
//            new Rectangle(width / 2f - width / 8f, width / 3f, width / 4f, width / 5f),
//            new Rectangle(width / 2f - width / 26f, width / 3f + width / 5f, width / 13f, width / 13f)
            new Rectangle(0, 0, width, (5f/8f)*height),
            new Rectangle(width*0.5f - width*(3f/13f)/2f, (5f/8f)*height, width*(3f/13f), (2f/8f)*height),
            new Rectangle(width*0.5f - width*(1f/13f)/2f, (7f/8f)*height, width*(1f/13f), (1f/8f)*height)
        };

        this.bullets = new Bullet[]{new Bullet(), new Bullet()};

        this.font = new BitmapFont();
        font.getData().setScale(1.1f);
        this.layout = new GlyphLayout();
        reset();
    }

    public void reset(){
        this.lives = 3;
        this.xAcc = 0;
        this.xVel = 0;
        this.xAccelerometerVel = 0;
        this.numBullets = 0;
        this.powerup = Powerup.NONE;
        this.bulletOneShootTime = shootPeriod;
        this.bulletTwoShootTime = shootPeriod;
        this.state = State.NORMAL;
        this.score = 0;
    }

    public void draw(ShapeRenderer sr){
        sr.setColor(color);

        for(int i = 0; i < rects.length; ++i) {
            sr.rect(loc.x + rects[i].getX(), loc.y + rects[i].getY(), rects[i].getWidth(), rects[i].getHeight());
        }

    }

    public void drawBullets(ShapeRenderer sr){
        for(int i = 0; i < numBullets; ++i){
            bullets[i].draw(sr);
        }
    }

    public void drawLives(ShapeRenderer sr) {
        float scale = 0.6f;
        float x = SpaceInvaders.UNIT;
        float y = SpaceInvaders.HEIGHT - SpaceInvaders.UNIT*(scale/2) - height*scale;
        sr.setColor(Color.WHITE);
        for(int i = 0; i < lives; ++i){
            for(int j = 0; j < rects.length; ++j) {
                sr.rect(x + rects[j].getX()*scale, y + rects[j].getY()*scale, rects[j].getWidth()*scale, rects[j].getHeight()*scale);
            }
            x += width*scale + SpaceInvaders.UNIT;
        }

        // bullet one
        sr.rect(0, SpaceInvaders.UNIT/3f, SpaceInvaders.UNIT * 12 * (bulletOneShootTime / shootPeriod), SpaceInvaders.UNIT / 3f);
        // bullet two
        if(powerup == Powerup.DOUBLESHOT)
            sr.rect(0, SpaceInvaders.UNIT, SpaceInvaders.UNIT * 12 * ( bulletTwoShootTime / shootPeriod), SpaceInvaders.UNIT / 3f);

    }

    public void drawScoreAndLives(SpriteBatch sb){
        float scale = 0.6f;
        float x = (width * scale + SpaceInvaders.UNIT) * lives + SpaceInvaders.UNIT/2f;
        float y = SpaceInvaders.HEIGHT - SpaceInvaders.UNIT * (scale / 2);
        if(lives > 0) {
            //livesText
            font.draw(sb, "x " + lives, x, y, 20, 0, false);
        }
        //score
        layout.setText(font, "Score: " + score);
        x = SpaceInvaders.WIDTH/2 - layout.width / 2;
        font.draw(sb, layout, x, y);
    }

    public void update(float delta) {
        if(powerup == Powerup.DOUBLESHOT) {
            if (bulletTwoShootTime < shootPeriod) {
                bulletTwoShootTime += delta;
                if (bulletTwoShootTime > shootPeriod)
                    bulletTwoShootTime = shootPeriod;
            }
        }

        if (bulletOneShootTime < shootPeriod) {
            bulletOneShootTime += delta;
            if (bulletOneShootTime > shootPeriod)
                bulletOneShootTime = shootPeriod;
        }


        move(delta);

        for(int i = 0; i < numBullets; ++i){
            bullets[i].update(delta);
        }
    }

    public void friction(float force){
        xVel *= force;
    }

    public void move(float delta){
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

        if(loc.x+width > SpaceInvaders.WIDTH)
            loc.x = SpaceInvaders.WIDTH - width;
        else if(loc.x < 0)
            loc.x = 0;

    }

    public void shoot(){
        if( ( (numBullets == 1  &&  powerup == Powerup.DOUBLESHOT)  ||  numBullets == 0 )
                &&  state != State.ENTERING ){
            if(powerup == Powerup.DOUBLESHOT && bulletTwoShootTime >= shootPeriod){
                bullets[numBullets].reset((int) loc.x + width / 2 - (5 / 2), (int) loc.y + height, 5, 10, /*2*/8, color, Bullet.Type.RECT);
                ++numBullets;
                shootSound.play(SpaceInvaders.volume);
                bulletTwoShootTime -= shootPeriod;
            }
            else if(bulletOneShootTime >= shootPeriod) {
                bullets[numBullets].reset((int) loc.x + width / 2 - (5 / 2), (int) loc.y + height, 5, 10, /*2*/8, color, Bullet.Type.RECT);
                ++numBullets;
                shootSound.play(SpaceInvaders.volume);
                bulletOneShootTime -= shootPeriod;
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
        answer.reset(0, (int)loc.x+width/2, (int)loc.y+height/2, 10, 10, color);

//        if(lives > 0){
//            state = State.RESPAWNING;
//        }

        return answer;
    }

    // TODO: stop these news
    public Rectangle[] getRects(Rectangle[] outRects){
        outRects[0].set(rects[0]);
        outRects[1].set(rects[1]);
        outRects[2].set(rects[2]);
        for(int i = 0; i < outRects.length; ++i){
            outRects[i].x += loc.x;
            outRects[i].y += loc.y;
        }
        return outRects;
    }

    public float getX(){
        return loc.x;
    }

    public float getY(){
        return loc.y;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
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

    public void setPowerup(Powerup powerup){
        this.powerup = powerup;
    }

}
