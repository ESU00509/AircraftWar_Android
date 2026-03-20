package com.example.aircraftwar.application;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.aircraftwar.aircraft.*;
import com.example.aircraftwar.bullet.EnemyBullet;
import com.example.aircraftwar.bullet.HeroBullet;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 综合管理图片的加载，访问
 * 提供图片的静态访问方法（适配 Android assets 加载）
 *
 * @author hitsz
 */
public class ImageManager {

    private static final Map<String, Bitmap> CLASSNAME_IMAGE_MAP = new HashMap<>();

    public static Bitmap EASYBACKGROUND_IMAGE;
    public static Bitmap COMMONBACKGROUND_IMAGE;
    public static Bitmap INFERNOBACKGROUND_IMAGE;
    public static Bitmap HERO_IMAGE;
    public static Bitmap HERO_BULLET_IMAGE;
    public static Bitmap ENEMY_BULLET_IMAGE;
    public static Bitmap MOB_ENEMY_IMAGE;
    public static Bitmap SUPER_ENEMY_IMAGE;
    public static Bitmap PLUS_ENEMY_IMAGE;
    public static Bitmap PROP_IMAGE;
    public static Bitmap PROP_BOMB_IMAGE;
    public static Bitmap PROP_BULLET_IMAGE;
    public static Bitmap PROP_BULLETPLUS_IMAGE;
    public static Bitmap BOSS_ENEMY_IMAGE;

    private static boolean initialized = false;

    /**
     * 初始化图片资源，从 assets 目录加载
     *
     * @param context Android Context
     */
    public static void init(Context context) {
        if (initialized) return;
        AssetManager assets = context.getAssets();
        try {
            EASYBACKGROUND_IMAGE = loadBitmap(assets, "images/bg.jpg");
            COMMONBACKGROUND_IMAGE = loadBitmap(assets, "images/bg2.jpg");
            INFERNOBACKGROUND_IMAGE = loadBitmap(assets, "images/bg3.jpg");

            HERO_IMAGE = loadBitmap(assets, "images/hero.png");
            MOB_ENEMY_IMAGE = loadBitmap(assets, "images/mob.png");
            HERO_BULLET_IMAGE = loadBitmap(assets, "images/bullet_hero.png");
            ENEMY_BULLET_IMAGE = loadBitmap(assets, "images/bullet_enemy.png");
            SUPER_ENEMY_IMAGE = loadBitmap(assets, "images/elite.png");
            PLUS_ENEMY_IMAGE = loadBitmap(assets, "images/elitePlus.png");
            BOSS_ENEMY_IMAGE = loadBitmap(assets, "images/boss.png");
            PROP_IMAGE = loadBitmap(assets, "images/prop_blood.png");
            PROP_BOMB_IMAGE = loadBitmap(assets, "images/prop_bomb.png");
            PROP_BULLET_IMAGE = loadBitmap(assets, "images/prop_bullet.png");
            PROP_BULLETPLUS_IMAGE = loadBitmap(assets, "images/prop_bulletPlus.png");

            CLASSNAME_IMAGE_MAP.put(HeroAircraft.class.getName(), HERO_IMAGE);
            CLASSNAME_IMAGE_MAP.put(MobEnemy.class.getName(), MOB_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(HeroBullet.class.getName(), HERO_BULLET_IMAGE);
            CLASSNAME_IMAGE_MAP.put(EnemyBullet.class.getName(), ENEMY_BULLET_IMAGE);
            CLASSNAME_IMAGE_MAP.put(SuperEnemy.class.getName(), SUPER_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(Prop_Blood.class.getName(), PROP_IMAGE);
            CLASSNAME_IMAGE_MAP.put(Prop_Bomb.class.getName(), PROP_BOMB_IMAGE);
            CLASSNAME_IMAGE_MAP.put(Prop_Bullet.class.getName(), PROP_BULLET_IMAGE);
            CLASSNAME_IMAGE_MAP.put(Prop_BulletPlus.class.getName(), PROP_BULLETPLUS_IMAGE);
            CLASSNAME_IMAGE_MAP.put(PlusEnemy.class.getName(), PLUS_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(BossEnemy.class.getName(), BOSS_ENEMY_IMAGE);

            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Bitmap loadBitmap(AssetManager assets, String path) throws IOException {
        InputStream is = assets.open(path);
        Bitmap bmp = BitmapFactory.decodeStream(is);
        is.close();
        return bmp;
    }

    public static Bitmap get(String className) {
        return CLASSNAME_IMAGE_MAP.get(className);
    }

    public static Bitmap get(Object obj) {
        if (obj == null) {
            return null;
        }
        return get(obj.getClass().getName());
    }

    /**
     * 重置初始化状态（用于重新加载）
     */
    public static void reset() {
        initialized = false;
        CLASSNAME_IMAGE_MAP.clear();
    }
}

