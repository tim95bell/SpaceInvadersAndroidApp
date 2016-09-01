package com.timbell.spaceinvaders.Entities;

import com.badlogic.gdx.utils.Array;
import com.timbell.spaceinvaders.Assets.AssetManager;
import com.timbell.spaceinvaders.SpaceInvaders;

/**
 * Created by timbell on 4/08/16.
 */
public class EnemyThree extends Enemy {

    public EnemyThree(Swarm swarm, int x, int y){
        super(swarm, x, y);

        color = AssetManager.enemyThreeColor;
        imageOne = AssetManager.enemyThreeA;
        imageTwo = AssetManager.enemyThreeB;
        width = SpaceInvaders.UNIT*2;
        height = SpaceInvaders.UNIT*2;
    }

}
