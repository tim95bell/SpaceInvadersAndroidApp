package com.timbell.spaceinvaders.Collision;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.timbell.spaceinvaders.Entities.Bullet;
import com.timbell.spaceinvaders.Entities.Button;
import com.timbell.spaceinvaders.Entities.Enemy;
import com.timbell.spaceinvaders.Entities.Player;
import com.timbell.spaceinvaders.Entities.Swarm;
import com.timbell.spaceinvaders.GameScreens.MenuScreen;
import com.timbell.spaceinvaders.ParticleEffect.Particle;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.SpaceInvaders;

/**
 * Created by timbell on 3/09/16.
 */
public class MenuCollision {
    private Player p1;
    private Array<Bullet> playerBullets;
    private Array<ParticleEffect> particleEffects;
    private Array<Button> buttons;

    private Array<ParticleEffect> newParticleEffects;

    private MenuScreen menuScreen;


    public MenuCollision(MenuScreen menuScreen, Player p1, Array<ParticleEffect> particleEffects, Array<Button> buttons){
        this.p1 = p1;
        this.playerBullets = p1.bullets;
        this.particleEffects = particleEffects;
        this.buttons = buttons;
        this.menuScreen = menuScreen;
    }

    public Array<ParticleEffect> checkCollisions(){
        newParticleEffects = new Array<ParticleEffect>(true, 0);

        playerBulletsAndBounds();
        particles();
        playerBulletsAndButtons();

        return newParticleEffects;
    }

    public void playerBulletsAndBounds(){
        //playerBullets and roof/walls
        for(int i = playerBullets.size-1; i >= 0; --i){
            Bullet bullet = playerBullets.get(i);

            if( bullet.getX() < 0 ||
                    bullet.getX()+bullet.getWidth() > SpaceInvaders.WIDTH ||
                    bullet.getY()+bullet.getHeight() > SpaceInvaders.HEIGHT) {
                newParticleEffects.add( bullet.hit() );
            }

        }
    }

    //particles and buttons, player, bounds, sheilds
    public void particles(){
        for(int i = 0; i < particleEffects.size; ++i){
            Particle[] particles = particleEffects.get(i).particles;

            for(int p = 0; p < particles.length; ++p) {
                float pX = particles[p].getX();
                float pY = particles[p].getY();

                //player
                if( p1.getRect().contains(pX, pY) ){
                    particles[p].bounce(p1.getX(), p1.getY(), p1.getWidth(), p1.getHeight());
                }

                //bounds
                particles[p].bounds();

                //buttons
                for(int j = 0; j < buttons.size; ++j){
                    Button button = buttons.get(j);
                    // looks awesome without visible
                    if( button.visible  &&  button.getRect().contains(pX, pY) ){
                        particles[p].bounce(button.getX(), button.getY(), (int)button.getSize(), (int)button.getSize());
                    }
                }

                //sheilds

            }

        }

    }

    public void playerBulletsAndButtons(){
        for(int i = playerBullets.size-1; i >= 0; --i) {
            Rectangle bulletRect = playerBullets.get(i).getRect();
            for(int j = 0; j < buttons.size; ++j){
                if(buttons.get(j).visible  &&  bulletRect.overlaps(buttons.get(j).getRect())) {
                    newParticleEffects.addAll(buttons.get(j).hit(), 0, 2);
                    newParticleEffects.add(playerBullets.get(i).hit());
                    menuScreen.switchToPlayScreen();
                    return;
                }
            }
        }

    }



}
