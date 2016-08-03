package com.timbell.spaceinvaders.Assets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by timbell on 23/07/16.
 */
public class AssetManager {

    public static Texture background;
    public static Texture spriteSheet;

    public static TextureRegion enemyOneA;
    public static TextureRegion enemyOneB;
    public static TextureRegion enemyTwoA;
    public static TextureRegion enemyTwoB;
    public static TextureRegion enemyThreeA;
    public static TextureRegion enemyThreeB;
    public static TextureRegion playerImage;
    public static TextureRegion zigzagBulletA;
    public static TextureRegion zigzagBulletB;

    public static Vector3 enemyOneColor;
    public static Vector3 enemyTwoColor;
    public static Vector3 enemyThreeColor;
    public static Vector3 playerColor;

    public static void init(){
        background = new Texture("sunset6.png");
        spriteSheet = new Texture("spriteSheet2.png");

        enemyOneA = new TextureRegion(spriteSheet, 0, 0, 12, 8);
        enemyOneB = new TextureRegion(spriteSheet, 12, 0, 12, 8);
        enemyTwoA = new TextureRegion(spriteSheet, 24, 0, 11, 8);
        enemyTwoB = new TextureRegion(spriteSheet, 35, 0, 11, 8);
        enemyThreeA = new TextureRegion(spriteSheet, 0, 8, 8, 8);
        enemyThreeB = new TextureRegion(spriteSheet, 8, 8, 8, 8);
        playerImage = new TextureRegion(spriteSheet, 16, 8, 13, 8);
        zigzagBulletA = new TextureRegion(spriteSheet, 29, 8, 3, 7);
        zigzagBulletB = new TextureRegion(spriteSheet, 32, 8, 3, 7);

        enemyOneColor = new Vector3(1.0f, 0.2f, 0.2f);
        enemyTwoColor = new Vector3(0.2f, 1.0f, 0.2f);
        enemyThreeColor = new Vector3(0.2f, 0.2f, 1.0f);
        playerColor = new Vector3(1.0f, 1.0f, 1.0f);
    }

}
