package com.timbell.spaceinvaders.Collision;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.timbell.spaceinvaders.Entities.Bullet;
import com.timbell.spaceinvaders.Entities.Enemy;
import com.timbell.spaceinvaders.Entities.Player;
import com.timbell.spaceinvaders.Entities.Swarm;
import com.timbell.spaceinvaders.ParticleEffect.Particle;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.SpaceInvaders;

/**
 * Created by timbell on 1/09/16.
 */
public class PlayCollision {

    private Array<Enemy> enemies;
    private Player p1;
    private Bullet[] playerBullets, enemyBullets;
    private Array<ParticleEffect> particleEffects;
    private Swarm swarm;

    private Array<ParticleEffect> newParticleEffects;

    public PlayCollision(Swarm swarm, Player p1, Array<ParticleEffect> particleEffects){
        this.swarm = swarm;
        this.enemies = swarm.members;
        this.p1 = p1;
        this.playerBullets = p1.bullets;
        this.enemyBullets = swarm.bullets;
        this.particleEffects = particleEffects;
        this.newParticleEffects = new Array<ParticleEffect>(true, 0);
    }

    public Array<ParticleEffect> checkCollisions(){
        newParticleEffects.clear();

        //playerBullets and roof/walls, | and enemies | and enemyBullets
        int numPlayerBullets = p1.getNumBullets();
        int numEnemyBullets = swarm.getNumBullets();
        for(int i = numPlayerBullets-1; i >= 0; --i){
            Bullet bullet = playerBullets[i];
            // roof/walls
            if( bullet.getX() < 0 ||
                    bullet.getX()+bullet.getWidth() > SpaceInvaders.WIDTH ||
                    bullet.getY()+bullet.getHeight() > SpaceInvaders.HEIGHT) {
                newParticleEffects.add( bullet.hit() );
                p1.removeBullet(i);
            }
            //enemies
            for(int j = enemies.size-1; j >= 0; --j){
                Enemy enemy = enemies.get(j);
                if(enemy.getRect().overlaps(bullet.getRect())){
                    newParticleEffects.add( enemy.hit() );
                    newParticleEffects.add( bullet.hit() );
                    p1.removeBullet(i);
                }
            }
            // enemy bullets
            for(int j = numEnemyBullets-1; j >= 0; --j){
                if(enemyBullets[j].getRect().overlaps(bullet.getRect())){
                    newParticleEffects.add( enemyBullets[j].hit() );
                    swarm.removeBullet(j);
                    newParticleEffects.add(playerBullets[i].hit());
                    p1.removeBullet(i);
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
                for (int e = 0; e < enemies.size; ++e) {
                    Enemy enemy = enemies.get(e);
                    if( enemy.getRect().contains(pX, pY) ){
                        particles[p].bounce(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight());
                    }
                }

                //player
                Rectangle[] playerRects = p1.getRects();
                for(int j = 0; j < playerRects.length; ++j) {
                    if (playerRects[j].contains(pX, pY)) {
                        particles[p].bounce(playerRects[j].getX(), playerRects[j].getY(), (int)playerRects[j].getWidth(), (int)playerRects[j].getHeight());
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
            Rectangle[] playerRects = p1.getRects();
            for(int j = 0; j < playerRects.length; ++j) {
                if (playerRects[j].overlaps(bullet.getRect())) {
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


    public Array<ParticleEffect> checkEnteringCollisions(){
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
            }
        }

        //particles and player, bounds
        for(int i = 0; i < particleEffects.size; ++i){
            Particle[] particles = particleEffects.get(i).particles;

            for(int p = 0; p < particles.length; ++p) {
                float pX = particles[p].getX();
                float pY = particles[p].getY();

                //player
                Rectangle[] playerRects = p1.getRects();
                for(int j = 0; j < playerRects.length; ++j) {
                    if (playerRects[j].contains(pX, pY)) {
                        particles[p].bounce(playerRects[j].getX(), playerRects[j].getY(), (int)playerRects[j].getWidth(), (int)playerRects[j].getHeight());
                    }
                }

                //bounds
                particles[p].bounds();
            }
        }

        return newParticleEffects;
    }




    //sheilds

    //allBullets and shields

}
