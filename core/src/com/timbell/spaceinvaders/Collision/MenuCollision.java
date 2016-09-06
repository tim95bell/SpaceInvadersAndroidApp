package com.timbell.spaceinvaders.Collision;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.timbell.spaceinvaders.Entities.Bullet;
import com.timbell.spaceinvaders.Entities.Button;
import com.timbell.spaceinvaders.Entities.Enemy;
import com.timbell.spaceinvaders.Entities.Player;
import com.timbell.spaceinvaders.Entities.Swarm;
import com.timbell.spaceinvaders.GameScreens.GameScreen;
import com.timbell.spaceinvaders.GameScreens.MenuScreen;
import com.timbell.spaceinvaders.ParticleEffect.Particle;
import com.timbell.spaceinvaders.ParticleEffect.ParticleEffect;
import com.timbell.spaceinvaders.SpaceInvaders;

/**
 * Created by timbell on 3/09/16.
 */
public class MenuCollision {
    private Player p1;
    private Bullet[] playerBullets;
    private Array<ParticleEffect> particleEffects;
    private Array<Button> buttons;

    private Rectangle[] recievePlayerRectangles;

    private Array<ParticleEffect> newParticleEffects;

    private GameScreen screen;


    public MenuCollision(Player p1){
        this.p1 = p1;
        this.playerBullets = p1.bullets;
    }

    public void set(GameScreen screen, Array<ParticleEffect> particleEffects, Array<Button> buttons){
        this.particleEffects = particleEffects;
        this.buttons = buttons;
        this.screen = screen;
        this.recievePlayerRectangles = new Rectangle[]{ new Rectangle(), new Rectangle(), new Rectangle() };
    }

    public Array<ParticleEffect> checkCollisions(){
        newParticleEffects = new Array<ParticleEffect>(true, 0);

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

        numBullets = p1.getNumBullets();
        for(int i = numBullets-1; i >= 0; --i) {
            Rectangle bulletRect = playerBullets[i].getRect();
            for(int j = 0; j < buttons.size; ++j){
                Button button = buttons.get(j);
                if(button.visible  &&  bulletRect.overlaps(button.getRect())) {
                    newParticleEffects.addAll(button.hit(), 0, 2);
                    newParticleEffects.add(playerBullets[i].hit());
                    p1.removeBullet(i);
                    if(button.getType() == Button.ButtonSymbol.PLAY || button.getType() == Button.ButtonSymbol.RETRY)
                        screen.changeScreen(SpaceInvaders.PLAY_STATE);
                    else if(button.getType() == Button.ButtonSymbol.EXIT)
                        screen.changeScreen(SpaceInvaders.MENU_STATE);
                }
            }
        }

        return newParticleEffects;
    }




}
