package com.example.aircraftwar.aircraft;

import com.example.aircraftwar.Config;
import com.example.aircraftwar.application.ImageManager;
import com.example.aircraftwar.bullet.BaseBullet;
import com.example.aircraftwar.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * 英雄飞机，游戏玩家操控
 * @author hitsz
 */
public class HeroAircraft extends AbstractAircraft {

    private HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.shootNum = 1;
        this.power = 30;
        this.direction = -1;
    }

    // 延迟初始化的单例模式
    private static HeroAircraft instance;

    public static HeroAircraft getInstance() {
        if (instance == null) {
            int heroHeight = 0;
            if (ImageManager.HERO_IMAGE != null) {
                heroHeight = ImageManager.HERO_IMAGE.getHeight();
            }
            instance = new HeroAircraft(
                    Config.WINDOW_WIDTH / 2,
                    Config.WINDOW_HEIGHT - heroHeight,
                    0, 0, 1000);
        }
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }

    @Override
    public List<BaseBullet> shoot() {
        List<BaseBullet> res = new LinkedList<>();
        int x = this.getLocationX();
        int y = this.getLocationY() + direction * 2;
        int speedX = 0;
        int speedY = this.getSpeedY() + direction * 5;
        BaseBullet bullet;
        for (int i = 0; i < shootNum; i++) {
            bullet = new HeroBullet(x + (i * 2 - shootNum + 1) * 10, y, speedX, speedY, power);
            res.add(bullet);
        }
        return res;
    }
}

