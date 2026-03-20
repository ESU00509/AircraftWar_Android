package com.example.aircraftwar.basic;

import android.graphics.Bitmap;

import com.example.aircraftwar.Config;
import com.example.aircraftwar.aircraft.AbstractAircraft;
import com.example.aircraftwar.application.ImageManager;

/**
 * 可飞行对象的父类
 *
 * @author hitsz
 */
public abstract class AbstractFlyingObject {

    protected int locationX;
    protected int locationY;
    protected int speedX;
    protected int speedY;

    /**
     * 图片 Bitmap
     */
    protected Bitmap image = null;

    protected int width = -1;
    protected int height = -1;

    protected boolean isValid = true;

    public AbstractFlyingObject() {
    }

    public AbstractFlyingObject(int locationX, int locationY, int speedX, int speedY) {
        this.locationX = locationX;
        this.locationY = locationY;
        this.speedX = speedX;
        this.speedY = speedY;
    }

    public void forward() {
        locationX += speedX;
        locationY += speedY;
        if (locationX <= 0 || locationX >= Config.WINDOW_WIDTH) {
            speedX = -speedX;
        }
    }

    public boolean crash(AbstractFlyingObject flyingObject) {
        int factor = this instanceof AbstractAircraft ? 2 : 1;
        int fFactor = flyingObject instanceof AbstractAircraft ? 2 : 1;

        int x = flyingObject.getLocationX();
        int y = flyingObject.getLocationY();
        int fWidth = flyingObject.getWidth();
        int fHeight = flyingObject.getHeight();

        return x + (fWidth + this.getWidth()) / 2 > locationX
                && x - (fWidth + this.getWidth()) / 2 < locationX
                && y + (fHeight / fFactor + this.getHeight() / factor) / 2 > locationY
                && y - (fHeight / fFactor + this.getHeight() / factor) / 2 < locationY;
    }

    public int getLocationX() {
        return locationX;
    }

    public int getLocationY() {
        return locationY;
    }

    public void setLocation(double locationX, double locationY) {
        this.locationX = (int) locationX;
        this.locationY = (int) locationY;
    }

    public int getSpeedY() {
        return speedY;
    }

    public Bitmap getImage() {
        if (image == null) {
            image = ImageManager.get(this);
        }
        return image;
    }

    public int getWidth() {
        if (width == -1) {
            Bitmap bmp = ImageManager.get(this);
            if (bmp != null) {
                width = bmp.getWidth();
            }
        }
        return width;
    }

    public int getHeight() {
        if (height == -1) {
            Bitmap bmp = ImageManager.get(this);
            if (bmp != null) {
                height = bmp.getHeight();
            }
        }
        return height;
    }

    public boolean notValid() {
        return !this.isValid;
    }

    public void vanish() {
        isValid = false;
    }
}

