package com.timbell.spaceinvaders.Entities;

import com.badlogic.gdx.utils.Array;
import com.timbell.spaceinvaders.Assets.AssetManager;
import com.timbell.spaceinvaders.SpaceInvaders;

/**
 * Created by timbell on 4/08/16.
 */
public class EnemyTwo extends Enemy {

    public EnemyTwo(Swarm swarm, int x, int y){
        super(swarm, x, y);

        color = AssetManager.enemyTwoColor;
        imageOne = AssetManager.enemyTwoA;
        imageTwo = AssetManager.enemyTwoB;
        width = SpaceInvaders.UNIT*3;
        height = SpaceInvaders.UNIT*2;
    }

}
