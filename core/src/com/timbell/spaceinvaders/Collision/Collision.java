package com.timbell.spaceinvaders.Collision;

import com.badlogic.gdx.utils.Array;
import com.timbell.spaceinvaders.Entities.Bullet;
import com.timbell.spaceinvaders.Entities.Enemy;
import com.timbell.spaceinvaders.Entities.Player;
import com.timbell.spaceinvaders.Entities.Swarm;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.SpaceInvaders;

/**
 * Created by timbell on 1/09/16.
 */
public class Collision {

    private Array<Enemy> enemies;
    private Player p1;
    private Array<Bullet> playerBullets, enemyBullets;

    public Collision(Swarm swarm, Player p1){
        this.enemies = swarm.members;
        this.p1 = p1;
        this.playerBullets = p1.bullets;
        this.enemyBullets = swarm.bullets;
    }

    public Array<ParticleEffect> checkCollisions(){
        Array<ParticleEffect> particleEffects = new Array<ParticleEffect>(true, 0);
        //sheilds

        //enemyBullets and playerBullets
        for(int i = enemyBullets.size-1; i >= 0; --i){
            Bullet enemyBullet = enemyBullets.get(i);

            for(int j = playerBullets.size-1; j >= 0; --j){
                Bullet playerBullet = playerBullets.get(i);

                if(enemyBullet.getRect().contains(playerBullet.getRect())){
                    enemyBullet.hit();
                    playerBullet.hit();
                }

            }

        }

        //enemybullets and player
        for(int i = enemyBullets.size-1; i >= 0; --i){
            Bullet enemyBullet = enemyBullets.get(i);
            if( p1.getRect().contains(enemyBullet.getRect()) ){
                p1.hit();
                enemyBullet.hit();
            }
        }

        //enemyBullets and floor/walls

        //playerBullets and roof/walls
        for(int i = playerBullets.size-1; i >= 0; --i){
            Bullet bullet = playerBullets.get(i);

            if( bullet.getX() < 0 ||
                    bullet.getX()+bullet.getWidth() > SpaceInvaders.WIDTH ||
                    bullet.getY()+bullet.getHeight() > SpaceInvaders.HEIGHT) {
                bullet.hit();
            }

        }

        //playerBullets and enemies
        for(int i = playerBullets.size-1; i >= 0; --i){
            Bullet playerBullet = playerBullets.get(i);
            for(int j = enemies.size-1; j >= 0; --j){
                Enemy enemy = enemies.get(j);
                if(enemy.getRect().contains(playerBullet.getRect())){
                    particleEffects.add(enemy.hit());
                    playerBullet.hit();
                }
            }
        }

        //allBullets and shields

        //particles and enemies, player, bounds, sheilds

        return particleEffects;
    }

}
