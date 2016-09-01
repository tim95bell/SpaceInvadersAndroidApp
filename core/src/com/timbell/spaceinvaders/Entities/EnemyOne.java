package com.timbell.spaceinvaders.Entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.timbell.spaceinvaders.Assets.AssetManager;
import com.timbell.spaceinvaders.SpaceInvaders;

/**
 * Created by timbell on 4/08/16.
 */
public class EnemyOne extends Enemy {


    public EnemyOne(Swarm swarm, int x, int y){
        super(swarm, x, y);

        color = AssetManager.enemyOneColor;
        imageOne = AssetManager.enemyOneA;
        imageTwo = AssetManager.enemyOneB;
        width = SpaceInvaders.UNIT*3;
        height = SpaceInvaders.UNIT*2;
    }
}
