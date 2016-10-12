package com.timbell.spaceinvaders.ParticleEffect;

import com.badlogic.gdx.utils.Array;


public class ParticleEffectPool {

    public static final int SMALL_SIZE = 50;
    public static final int LARGE_SIZE = 75;

    private static Array<ParticleEffect> smallEffect, largeEffect;

    public static void init(){
        smallEffect = new Array<ParticleEffect>(true, 5);
        largeEffect = new Array<ParticleEffect>(true, 5);

        for(int i = 0; i  < 5; ++i){
            smallEffect.add( new ParticleEffect(SMALL_SIZE) );
            largeEffect.add( new ParticleEffect(LARGE_SIZE) );
        }
    }

    public static void free(ParticleEffect pe){
        if(pe.particles.length == SMALL_SIZE)
            smallEffect.add(pe);
        else if(pe.particles.length == LARGE_SIZE)
            largeEffect.add(pe);
    }

    public static void freeAll(Array<ParticleEffect> particleEffects){
        for(int i = particleEffects.size-1; i >= 0; --i){
            free(particleEffects.get(i));
            particleEffects.removeIndex(i);
        }
    }

    public static ParticleEffect getSmall(){
        if(smallEffect.size > 0)
            return smallEffect.pop();
        else
            return new ParticleEffect(SMALL_SIZE);
    }

    public static ParticleEffect getLarge(){
        if(largeEffect.size > 0)
            return largeEffect.pop();
        else
            return new ParticleEffect(LARGE_SIZE);
    }
}
