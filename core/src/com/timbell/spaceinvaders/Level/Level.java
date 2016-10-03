package com.timbell.spaceinvaders.Level;

import com.badlogic.gdx.utils.Array;

/**
 * Created by timbell on 9/09/16.
 */
public class Level {
    private float minMovePeriod, maxMovePeriod;
    private float minShootChance, maxShootChance;
    public int[] members;
    private String name;

    // TODO: levels should shoot less. They should increase their speed more for harder levels rather than increasing shooting more.

    public Level(String name, float minMovePeriod, float maxMovePeriod,
                 float minShootChance, float maxShootChance,
                 int[] members){
        this.name = name;
        this.minMovePeriod = minMovePeriod;
        this.maxMovePeriod = maxMovePeriod;
        this.minShootChance = minShootChance;
        this.maxShootChance = maxShootChance;
        this.members = members;
    }

    public float getMinMovePeriod(){ return minMovePeriod; }
    public float getMaxMovePeriod(){ return maxMovePeriod; }
    public float getMinShootPeriod(){ return minShootChance; }
    public float getMaxShootPeriod(){ return maxShootChance; }

    public static Level level1(){
        int[] levelArray = {
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1
//                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                1, 1, 1, 0, 0, 0, 0, 0, 0, 0,
//                1, 1, 1, 0, 0, 0, 0, 0, 0, 0
        };
        Level level = new Level("Stage - 1.0", 0.1f, 0.5f, 0.01f, 0.05f, levelArray);
        return level;
    }

    public static Level level2(){
        int[] levelArray = {
                2, 2, 2, 1, 1, 1, 1, 2, 2, 2,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 2, 2, 2, 2, 1, 1, 1,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                2, 2, 2, 2, 2, 2, 2, 2, 2, 2
        };
        Level level = new Level("Stage - 2.0", 0.1f, 0.6f, 0.1f, 0.3f, levelArray);
        return level;
    }

    public static Level level3(){
        int[] levelArray = {
                2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
                2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
                2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
                2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
                2, 2, 2, 2, 2, 2, 2, 2, 2, 2
        };
        Level level = new Level("Stage - 2.1", 0.1f, 0.7f, 0.1f, 0.2f, levelArray);
        return level;
    }

    public static Level level4(){
        int[] levelArray = {
                3, 1, 1, 1, 1, 1, 1, 1, 1, 3,
                3, 1, 1, 1, 3, 3, 1, 1, 1, 3,
                3, 1, 1, 1, 3, 3, 1, 1, 1, 3,
                3, 1, 1, 1, 1, 1, 1, 1, 1, 3,
                1, 3, 3, 3, 1, 1, 3, 3, 3, 1
        };
        Level level = new Level("Stage - 3.0", 0.1f, 0.8f, 0.1f, 0.3f, levelArray);
        return level;
    }

    public static Level level5(){
        int[] levelArray = {
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
                2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
                3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
                3, 3, 3, 3, 3, 3, 3, 3, 3, 3
        };
        Level level = new Level("Stage - 3.1", 0.1f, 0.9f, 0.1f, 0.4f, levelArray);
        return level;
    }

    public static Level levelTest(){
        int[] levelArray = {
//                1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
//                2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
//                2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
//                3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
//                3, 3, 3, 3, 3, 3, 3, 3, 3, 3
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                1, 1, 1, 0, 0, 0, 0, 0, 0, 0,
                1, 1, 1, 0, 0, 0, 0, 0, 0, 0
        };
        Level level = new Level("Stage - 3.1", 0.09f, 0.2f, 0.01f, 0.04f, levelArray);
        return level;
    }

    public static Array<Level> getLevels(){
        Array<Level> levels = new Array<Level>(true, 5);
        levels.addAll( level5(), level4(), level3(), level2(), level1() );
//        levels.addAll( levelTest() );
        return levels;
    }

    public String getName(){
        return name;
    }

}
