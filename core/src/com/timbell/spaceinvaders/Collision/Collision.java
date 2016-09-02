package com.timbell.spaceinvaders.Collision;

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
public class Collision {

    private Array<Enemy> enemies;
    private Player p1;
    private Array<Bullet> playerBullets, enemyBullets;
    private Array<ParticleEffect> particleEffects;

    public Collision(Swarm swarm, Player p1, Array<ParticleEffect> particleEffects){
        this.enemies = swarm.members;
        this.p1 = p1;
        this.playerBullets = p1.bullets;
        this.enemyBullets = swarm.bullets;
        this.particleEffects = particleEffects;
    }

    public Array<ParticleEffect> checkCollisions(){
        Array<ParticleEffect> newParticleEffects = new Array<ParticleEffect>(true, 0);

        //sheilds

        //enemyBullets and playerBullets
        for(int i = enemyBullets.size-1; i >= 0; --i){
            Bullet enemyBullet = enemyBullets.get(i);

            for(int j = playerBullets.size-1; j >= 0; --j){
                Bullet playerBullet = playerBullets.get(j);

                if(enemyBullet.getRect().overlaps(playerBullet.getRect())){
                    newParticleEffects.add(enemyBullet.hit() );
                    newParticleEffects.add(playerBullet.hit());
                }

            }

        }

        //enemybullets and player
        for(int i = enemyBullets.size-1; i >= 0; --i){
            Bullet enemyBullet = enemyBullets.get(i);
            if( p1.getRect().overlaps(enemyBullet.getRect()) ){
                newParticleEffects.add(p1.hit());
                newParticleEffects.add( enemyBullet.hit() );
            }
        }

        //enemyBullets and floor/walls
        for(int i = enemyBullets.size-1; i >= 0; --i){
            Bullet bullet = enemyBullets.get(i);

            if( bullet.getX() < 0 ||
                    bullet.getX()+bullet.getWidth() > SpaceInvaders.WIDTH ||
                    bullet.getY() < 0) {
                newParticleEffects.add( bullet.hit() );
            }

        }

        //playerBullets and roof/walls
        for(int i = playerBullets.size-1; i >= 0; --i){
            Bullet bullet = playerBullets.get(i);

            if( bullet.getX() < 0 ||
                    bullet.getX()+bullet.getWidth() > SpaceInvaders.WIDTH ||
                    bullet.getY()+bullet.getHeight() > SpaceInvaders.HEIGHT) {
                newParticleEffects.add( bullet.hit() );
            }

        }

        //playerBullets and enemies
        for(int i = playerBullets.size-1; i >= 0; --i){
            Bullet playerBullet = playerBullets.get(i);
            for(int j = enemies.size-1; j >= 0; --j){
                Enemy enemy = enemies.get(j);
                if(enemy.getRect().overlaps(playerBullet.getRect())){
                    newParticleEffects.add( enemy.hit() );
                    newParticleEffects.add( playerBullet.hit() );
                }
            }
        }

        //allBullets and shields

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
                if( p1.getRect().contains(pX, pY) ){
                    particles[p].bounce(p1.getX(), p1.getY(), p1.getWidth(), p1.getHeight());
                }

                //bounds
                particles[p].bounds();

                //sheilds

            }

        }

        return newParticleEffects;
    }

}
