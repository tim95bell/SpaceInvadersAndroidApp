package com.timbell.spaceinvaders.Collision;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.timbell.spaceinvaders.Entities.Bullet;
import com.timbell.spaceinvaders.Entities.Button;
import com.timbell.spaceinvaders.Entities.Enemy;
import com.timbell.spaceinvaders.Entities.EnemyOne;
import com.timbell.spaceinvaders.Entities.EnemyThree;
import com.timbell.spaceinvaders.Entities.EnemyTwo;
import com.timbell.spaceinvaders.Entities.MotherShip;
import com.timbell.spaceinvaders.Entities.Player;
import com.timbell.spaceinvaders.Entities.SpecialBullet;
import com.timbell.spaceinvaders.Entities.Swarm;
import com.timbell.spaceinvaders.GameScreens.GameOverScreen;
import com.timbell.spaceinvaders.GameScreens.GameScreen;
import com.timbell.spaceinvaders.GameScreens.PlayScreen;
import com.timbell.spaceinvaders.GameScreens.MenuScreen;
import com.timbell.spaceinvaders.ParticleEffect.Particle;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.SpaceInvaders;

/**
 * Created by timbell on 6/09/16.
 */
public class Collision {

    // TODO: instead of havving new particle effects array and returning it, i could just add them straight to particle effects here, as it is a refferance to the Array anyway

    public static Sound gong;

    // objects used by all
    private Player p1;
    private Rectangle[] recievePlayerRectangles;
    private Bullet[] playerBullets;
    private Array<ParticleEffect> particleEffects;
    private Array<ParticleEffect> newParticleEffects;

    // objects used by MenuScreen only
    private MenuScreen menuScreen;
    private Array<Button> menuScreenButtons;

    // objects used by GameOverScreen only
    private GameOverScreen gameOverScreen;
    private Array<Button> gameOverScreenButtons;

    // objects used by PlayScreen only
    private PlayScreen playScreen;
    private Swarm swarm;
    private Enemy[] enemies;
    private Bullet[] enemyBullets;
    private MotherShip motherShip;

    public Collision(Player p1, Array<ParticleEffect> particleEffects){
        this.p1 = p1;
        this.recievePlayerRectangles = new Rectangle[]{ new Rectangle(), new Rectangle(), new Rectangle()};
        this.playerBullets = p1.bullets;

        this.particleEffects = particleEffects;

        this.newParticleEffects = new Array<ParticleEffect>(false, 2);
    }

    public void addPlayScreenObjects(PlayScreen screen, Swarm swarm, MotherShip motherShip){
        this.playScreen = screen;
        this.swarm = swarm;
        this.enemies = swarm.members;
        this.enemyBullets = swarm.bullets;
        this.motherShip = motherShip;
    }

    public void addMenuScreenObjects(MenuScreen screen, Button playButton){ //}, Button settingsButton){
        this.menuScreen = screen;
        this.menuScreenButtons = new Array<Button>(false, 1);
        menuScreenButtons.add(playButton);
    }

    public void addGameOverScreenObjects(GameOverScreen screen, Button resetButton, Button homeButton){ //}, Button settingsButton){
        this.gameOverScreen = screen;
        this.gameOverScreenButtons = new Array<Button>(false, 2);
        gameOverScreenButtons.add(resetButton);
        gameOverScreenButtons.add(homeButton);
    }

    // PLAY COLLISION
    public Array<ParticleEffect> checkPlayCollision(){
        newParticleEffects.clear();

        //playerBullets and roof/walls, | and enemies | and enemyBullets  |  motherShips
        int numPlayerBullets = p1.getNumBullets();
        int numEnemyBullets = swarm.getNumBullets();
        for(int i = numPlayerBullets-1; i >= 0; --i){
            Bullet bullet = playerBullets[i];
            // roof/walls
            if( bullet.getX() < 0 ||
                    bullet.getX()+bullet.getWidth() > SpaceInvaders.WIDTH ||
                    bullet.getY()+bullet.getHeight() > SpaceInvaders.HEIGHT + SpaceInvaders.yOff) { // TODO: why does yoff need to by multiplied by 2???
                newParticleEffects.add(bullet.hit());
                p1.removeBullet(i);
                gong.play(SpaceInvaders.volume);
                continue;
            }
            //enemies
            for(int j = enemies.length-1; j >= 0; --j){
                Enemy enemy = enemies[j];
                if(!enemy.isDead()  &&  enemy.getRect().overlaps(bullet.getRect())){
                    p1.addToScore(enemy.getScoreAdd());
                    ParticleEffect enemyParticlleEffect = enemy.hit();
                    if(enemyParticlleEffect != null)
                        newParticleEffects.add( enemyParticlleEffect );
                    newParticleEffects.add( bullet.hit() );
                    p1.removeBullet(i);
                    continue;
                }
            }
            // enemy bullets
            for(int j = numEnemyBullets-1; j >= 0; --j){
                if(enemyBullets[j].getRect().overlaps(bullet.getRect())){
                    newParticleEffects.add( enemyBullets[j].hit() );
                    swarm.removeBullet(j);
                    newParticleEffects.add(playerBullets[i].hit());
                    p1.removeBullet(i);
                    continue;
                }
            }
            // mothership
            if( motherShip.getState() == MotherShip.State.ALIVE ){
                if(motherShip.getRect().overlaps(bullet.getRect())){
                    newParticleEffects.add(bullet.hit());
                    p1.removeBullet(i);
                    String powerupStr = p1.generatePowerup();
//                    newParticleEffects.add(motherShip.hit(powerupStr));
                    motherShip.hit(powerupStr);
                    float x = motherShip.getRect().getX();
                    float y = motherShip.getRect().getY();
                    float width = motherShip.getRect().getWidth();
                    float height = motherShip.getRect().getHeight();
                    p1.setAttractingParticleEffect(x+width/2, y+height/2, width, height, MotherShip.COLOR);
                    continue;
                }
            }

        }

        //particles AND enemies, player, bounds, sheilds
        for(int i = 0; i < particleEffects.size; ++i){
            Particle[] particles = particleEffects.get(i).particles;

            for(int p = 0; p < particles.length; ++p) {
                float pX = particles[p].getX();
                float pY = particles[p].getY();

                //enemies
                for (int e = 0; e < enemies.length; ++e) {
                    Enemy enemy = enemies[e];
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

            }

        }

        // p1 Special bullet,  and edges, and enemies, and mothership, and enemy bullets
        SpecialBullet p1SpecialBullet = p1.getSpecialBullet();
        if( !p1SpecialBullet.isDead() ){
            // edges
            if( p1SpecialBullet.getX() < 0 ||
                    p1SpecialBullet.getX()+p1SpecialBullet.getWidth() > SpaceInvaders.WIDTH ||
                    p1SpecialBullet.getY()+p1SpecialBullet.getHeight() > SpaceInvaders.HEIGHT + SpaceInvaders.yOff) {
                newParticleEffects.add(p1SpecialBullet.hit());
                p1SpecialBullet.die();
                gong.play(SpaceInvaders.volume);
            }
            // enemies
            for (int e = enemies.length-1; e >= 0; --e) {
                Enemy enemy = enemies[e];
                if( !enemy.isDead() ) {
                    if (enemy.getRect().overlaps(p1SpecialBullet.getRect())) {
                        p1.addToScore(enemy.getScoreAdd());
                        newParticleEffects.add(p1SpecialBullet.hit());
//                    p1SpecialBullet.hit();
                        ParticleEffect enemyParticlleEffect = enemy.hit();
                        if (enemyParticlleEffect != null)
                            newParticleEffects.add(enemyParticlleEffect);
                    }
                }
            }
            // mothership
            if(motherShip.isAlive()) {
                if (motherShip.getRect().overlaps(p1SpecialBullet.getRect())) {
//                    newParticleEffects.add(p1SpecialBullet.hit());
//                    String powerup = p1.generatePowerup();
//                    newParticleEffects.add(motherShip.hit(powerup));

                    newParticleEffects.add(p1SpecialBullet.hit());
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
            for(int j = numEnemyBullets-1; j >= 0; --j){
                if(enemyBullets[j].getRect().overlaps(p1SpecialBullet.getRect())){
                    newParticleEffects.add( enemyBullets[j].hit() );
                    swarm.removeBullet(j);
                    newParticleEffects.add(p1SpecialBullet.hitBullet());
                }
            }
        }

        //enemyBullets and floor/walls | and player
        int numBullets = swarm.getNumBullets();
        for(int i = numBullets-1; i >= 0; --i){
            Bullet bullet = enemyBullets[i];
            //floor/walls
            if( bullet.getX() < 0 ||
                    bullet.getX()+bullet.getWidth() > SpaceInvaders.WIDTH ||
                    bullet.getY() < -SpaceInvaders.yOff) {
                newParticleEffects.add( bullet.hit() );
                swarm.removeBullet(i);
            }
            // player
            p1.getLocationRects(recievePlayerRectangles);
            for(int j = 0; j < recievePlayerRectangles.length; ++j) {
                if (recievePlayerRectangles[j].overlaps(bullet.getRect())) {
                    newParticleEffects.add(p1.hit());
                    newParticleEffects.add(bullet.hit());
                    swarm.removeBullet(i);
                    break;
                }
            }
        }

        return newParticleEffects;
    }

    public Array<ParticleEffect> checkMenuCollision(){
        checkMenuOrGameOverCollision(SpaceInvaders.MENU_STATE);
        return newParticleEffects;
    }

    public Array<ParticleEffect> checkGameOverCollision(){
        checkMenuOrGameOverCollision(SpaceInvaders.GAMEOVER_STATE);
        return newParticleEffects;
    }

    // MENU AND GAME_OVER COLLISION
    public Array<ParticleEffect> checkMenuOrGameOverCollision(int state){
        newParticleEffects.clear();

        //playerBullets and roof/walls
        int numBullets = p1.getNumBullets();
        for(int i = numBullets-1; i >= 0; --i){
            Bullet bullet = playerBullets[i];
            if( bullet.getX() < 0 ||
                    bullet.getX()+bullet.getWidth() > SpaceInvaders.WIDTH ||
                    bullet.getY()+bullet.getHeight() > SpaceInvaders.HEIGHT) {
                newParticleEffects.add(bullet.hit());
                p1.removeBullet(i);
                gong.play(SpaceInvaders.volume);
            }
        }

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

                //menu buttons
                if(state == SpaceInvaders.MENU_STATE) {
                    for (int j = 0; j < menuScreenButtons.size; ++j) {
                        Button button = menuScreenButtons.get(j);
                        // looks awesome without visible
                        if (button.visible && button.getRect().contains(pX, pY)) {
                            particles[p].bounce(button.getX(), button.getY(), (int) button.getSize(), (int) button.getSize());
                        }
                    }
                }
                else if(state == SpaceInvaders.GAMEOVER_STATE) {
                    //gameOver buttons
                    for (int j = 0; j < gameOverScreenButtons.size; ++j) {
                        Button button = gameOverScreenButtons.get(j);
                        // looks awesome without visible
                        if (button.visible && button.getRect().contains(pX, pY)) {
                            particles[p].bounce(button.getX(), button.getY(), (int) button.getSize(), (int) button.getSize());
                        }
                    }
                }

                //sheilds

            }

        }

        numBullets = p1.getNumBullets();
        for(int i = numBullets-1; i >= 0; --i) {
            Rectangle bulletRect = playerBullets[i].getRect();
            // TODO: fic up what happens if each button is hit

            // menu buttons
            if(state == SpaceInvaders.MENU_STATE) {
                for (int j = 0; j < menuScreenButtons.size; ++j) {
                    Button button = menuScreenButtons.get(j);
                    if (button.visible && bulletRect.overlaps(button.getRect())) {
                        newParticleEffects.addAll(button.hit(), 0, 2);
                        newParticleEffects.add(playerBullets[i].hit());
                        p1.removeBullet(i);
                        if (button.getType() == Button.ButtonSymbol.PLAY || button.getType() == Button.ButtonSymbol.RETRY)
                            menuScreen.changeScreen(SpaceInvaders.PLAY_STATE);
                        else if (button.getType() == Button.ButtonSymbol.EXIT)
                            menuScreen.changeScreen(SpaceInvaders.MENU_STATE);
                    }
                }
            }
            else if(state == SpaceInvaders.GAMEOVER_STATE) {
                // gameOver buttons
                for (int j = 0; j < gameOverScreenButtons.size; ++j) {
                    Button button = gameOverScreenButtons.get(j);
                    if (button.visible && bulletRect.overlaps(button.getRect())) {
                        newParticleEffects.addAll(button.hit(), 0, 2);
                        newParticleEffects.add(playerBullets[i].hit());
                        p1.removeBullet(i);
                        if (button.getType() == Button.ButtonSymbol.RETRY)
                            gameOverScreen.changeScreen(SpaceInvaders.PLAY_STATE);
                        else if (button.getType() == Button.ButtonSymbol.EXIT)
                            gameOverScreen.changeScreen(SpaceInvaders.MENU_STATE);
                    }
                }
            }

        }

        return newParticleEffects;
    }

}
