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
import com.timbell.spaceinvaders.Entities.Swarm;
import com.timbell.spaceinvaders.GameScreens.GameScreen;
import com.timbell.spaceinvaders.ParticleEffect.Particle;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.SpaceInvaders;

/**
 * Created by timbell on 6/09/16.
 */
public class Collision {

    // TODO: instead of havving new particle effects array and returning it, i could just add them straight to particle effects here, as it is a refferance to the Array anyway

    // objects used by all
    private Player p1;
    private Rectangle[] recievePlayerRectangles;
    private Bullet[] playerBullets;
    private Array<ParticleEffect> particleEffects;
    private Array<ParticleEffect> newParticleEffects;

    // objects used by MenuScreen only
    private GameScreen menuScreen;
    private Array<Button> menuScreenButtons;

    // objects used by GameOverScreen only
    private GameScreen gameOverScreen;
    private Array<Button> gameOverScreenButtons;

    // objects used by PlayScreen only
    private GameScreen playScreen;
    private Swarm swarm;
    private Enemy[] enemies;
    private Bullet[] enemyBullets;
    private MotherShip motherShip;

    private Sound gong;

    public Collision(Player p1, Array<ParticleEffect> particleEffects){
        this.p1 = p1;
        this.recievePlayerRectangles = new Rectangle[]{ new Rectangle(), new Rectangle(), new Rectangle()};
        this.playerBullets = p1.bullets;

        this.particleEffects = particleEffects;

        this.newParticleEffects = new Array<ParticleEffect>(false, 2);

        this.gong = Gdx.audio.newSound(Gdx.files.internal("gongTrimmed.wav"));
    }

    public void addPlayScreenObjects(GameScreen screen, Swarm swarm, MotherShip motherShip){
        this.playScreen = screen;
        this.swarm = swarm;
        this.enemies = swarm.members;
        this.enemyBullets = swarm.bullets;
        this.motherShip = motherShip;
    }

    public void addMenuScreenObjects(GameScreen screen, Button playButton){ //}, Button settingsButton){
        this.menuScreen = screen;
        this.menuScreenButtons = new Array<Button>(false, 1);
        menuScreenButtons.add(playButton);
    }

    public void addGameOverScreenObjects(GameScreen screen, Button resetButton, Button homeButton){ //}, Button settingsButton){
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
                    bullet.getY()+bullet.getHeight() > SpaceInvaders.HEIGHT) {
                newParticleEffects.add(bullet.hit());
                p1.removeBullet(i);
                // TODO: fix up gong playing
                gong.play(SpaceInvaders.volume);
                continue;
            }
            //enemies
            for(int j = enemies.length-1; j >= 0; --j){
                Enemy enemy = enemies[j];
                if(!enemy.isDead()  &&  enemy.getRect().overlaps(bullet.getRect())){
                    if(enemy instanceof EnemyOne)
                        p1.addToScore(10);
                    else if(enemy instanceof EnemyTwo)
                        p1.addToScore(20);
                    else if(enemy instanceof EnemyThree)
                        p1.addToScore(30);
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
            if( !(motherShip.getState() == MotherShip.State.DEAD) ){
                if(motherShip.getRect().overlaps(bullet.getRect())){
                    newParticleEffects.add(motherShip.hit());
                    newParticleEffects.add(bullet.hit());
                    p1.removeBullet(i);
                    // TODO : Create powerup
                    p1.setPowerup(Player.Powerup.DOUBLESHOT);
                    continue;
                }
            }

        }

        //particles and enemies, player, bounds, sheilds
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
                p1.getRects(recievePlayerRectangles);
                for(int j = 0; j < recievePlayerRectangles.length; ++j) {
                    if (recievePlayerRectangles[j].contains(pX, pY)) {
                        particles[p].bounce(recievePlayerRectangles[j].getX(), recievePlayerRectangles[j].getY(), (int)recievePlayerRectangles[j].getWidth(), (int)recievePlayerRectangles[j].getHeight());
                    }
                }

                //bounds
                particles[p].bounds();

                //sheilds

            }

        }

//        //playerBullets and enemies
//        numBullets = p1.getNumBullets();
//        for(int i = numBullets-1; i >= 0; --i){
//            Bullet playerBullet = playerBullets[i];
//            for(int j = enemies.size-1; j >= 0; --j){
//                Enemy enemy = enemies.get(j);
//                if(enemy.getRect().overlaps(playerBullet.getRect())){
//                    newParticleEffects.add( enemy.hit() );
//                    newParticleEffects.add( playerBullet.hit() );
//                    p1.removeBullet(i);
//                }
//            }
//        }

        //enemyBullets and floor/walls | and player
        int numBullets = swarm.getNumBullets();
        for(int i = numBullets-1; i >= 0; --i){
            Bullet bullet = enemyBullets[i];
            //floor/walls
            if( bullet.getX() < 0 ||
                    bullet.getX()+bullet.getWidth() > SpaceInvaders.WIDTH ||
                    bullet.getY() < 0) {
                newParticleEffects.add( bullet.hit() );
                swarm.removeBullet(i);
            }
            // player
            p1.getRects(recievePlayerRectangles);
            for(int j = 0; j < recievePlayerRectangles.length; ++j) {
                if (recievePlayerRectangles[j].overlaps(bullet.getRect())) {
                    newParticleEffects.add(p1.hit());
                    newParticleEffects.add(bullet.hit());
                    swarm.removeBullet(i);
                    break;
                }
            }
        }

//        //enemybullets and player
//        for(int i = swarm.getNumBullets()-1; i >= 0; --i){
//            Bullet enemyBullet = enemyBullets[i];
//            Rectangle[] playerRects = p1.getRects();
//            for(int j = 0; j < playerRects.length; ++j) {
//                if (playerRects[j].overlaps(enemyBullet.getRect())) {
//                    newParticleEffects.add(p1.hit());
//                    newParticleEffects.add(enemyBullet.hit());
//                    swarm.removeBullet(i);
//                    break;
//                }
//            }
//        }


        //enemyBullets and playerBullets
//        int numPlayerBullets = p1.getNumBullets();
//
//        for(int i = swarm.getNumBullets()-1; i >= 0; --i){
//            Bullet enemyBullet = enemyBullets[i];
//
//            for(int j = numPlayerBullets-1; j >= 0; --j){
//                Bullet playerBullet = playerBullets[j];
//
//                if(enemyBullet.getRect().overlaps(playerBullet.getRect())){
//                    newParticleEffects.add(enemyBullet.hit() );
//                    swarm.removeBullet(i);
//                    newParticleEffects.add(playerBullet.hit());
//                    p1.removeBullet(j);
//                }
//
//            }
//
//        }

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
                p1.getRects(recievePlayerRectangles);
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

    public Array<ParticleEffect> checkPlayEnteringCollision(){
        newParticleEffects.clear();

        //playerBullets and roof/walls
        int numBullets = p1.getNumBullets();
        for(int i = 0; i < numBullets; ++i){
            Bullet bullet = playerBullets[i];
            if( bullet.getX() < 0 ||
                    bullet.getX()+bullet.getWidth() > SpaceInvaders.WIDTH ||
                    bullet.getY()+bullet.getHeight() > SpaceInvaders.HEIGHT) {
                newParticleEffects.add( bullet.hit() );
                p1.removeBullet(i);
                gong.play(SpaceInvaders.volume);
            }
        }

        //particles and player, bounds
        for(int i = 0; i < particleEffects.size; ++i){
            Particle[] particles = particleEffects.get(i).particles;

            for(int p = 0; p < particles.length; ++p) {
                float pX = particles[p].getX();
                float pY = particles[p].getY();

                //player
                p1.getRects(recievePlayerRectangles);
                for(int j = 0; j < recievePlayerRectangles.length; ++j) {
                    if (recievePlayerRectangles[j].contains(pX, pY)) {
                        particles[p].bounce(recievePlayerRectangles[j].getX(), recievePlayerRectangles[j].getY(), (int)recievePlayerRectangles[j].getWidth(), (int)recievePlayerRectangles[j].getHeight());
                    }
                }

                //bounds
                particles[p].bounds();
            }
        }

        return newParticleEffects;
    }

}
