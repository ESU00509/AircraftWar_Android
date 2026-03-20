package com.example.aircraftwar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.aircraftwar.aircraft.*;
import com.example.aircraftwar.application.*;
import com.example.aircraftwar.basic.AbstractFlyingObject;
import com.example.aircraftwar.bullet.BaseBullet;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * 游戏主视图，基于 SurfaceView 实现游戏循环和渲染。
 * 合并了原 Game/EasyGame/CommonGame/InfernoGame 的逻辑。
 * 射击、伤害、碰撞等核心逻辑与原版完全一致。
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final String TAG = "GameView";

    // 虚拟游戏区域尺寸（与原版一致）
    private static final int GAME_WIDTH = Config.WINDOW_WIDTH;   // 512
    private static final int GAME_HEIGHT = Config.WINDOW_HEIGHT; // 768

    // ====== 线程与渲染 ======
    private Thread gameThread;
    private volatile boolean running = false;
    private float scaleX = 1f, scaleY = 1f;

    // ====== 难度：0=Easy, 1=Common, 2=Inferno ======
    private int difficulty;
    private int difficultyFactor = 0; // F 难度倍率（Common/Inferno 用）

    // ====== 背景滚动 ======
    private int backGroundTop = 0;

    // ====== 游戏对象 ======
    private HeroAircraft heroAircraft;
    private final List<Enemy> enemyAircrafts = new LinkedList<>();
    private final List<BaseBullet> heroBullets = new LinkedList<>();
    private final List<BaseBullet> enemyBullets = new LinkedList<>();
    private final List<Prop> props_blood = new LinkedList<>();
    private final List<Prop> props_bomb = new LinkedList<>();
    private final List<Prop> props_bullet = new LinkedList<>();
    private final List<Prop> props_bulletplus = new LinkedList<>();

    // ====== 游戏状态 ======
    private int score = 0;
    private int time = 0;
    private int timeInterval = 40;
    private int cycleDuration = 600;
    private int cycleTime = 0;
    private volatile boolean gameOverFlag = false;
    private int bossnum = 0;
    private boolean soundEnabled;

    // ====== 音乐 ======
    private MusicPlayer bgmPlayer;
    private MusicPlayer bossPlayer;
    private MusicPlayer sfxPlayer;

    // ====== 道具效果 ======
    private Bomb burst = new Bomb();
    private ScheduledExecutorService powerUpExecutor;
    private ScheduledFuture<?> powerUpRestoreFuture = null;
    private long POWERUP_DURATION_MS = 3000;

    // ====== 绘制 ======
    private final Paint textPaint = new Paint();
    private final Paint bitmapPaint = new Paint();

    // ====== 回调 ======
    public interface GameOverCallback {
        void onGameOver(int score, int difficulty);
    }

    private GameOverCallback gameOverCallback;

    // ====== 构造 ======
    public GameView(Context context, int difficulty, boolean soundEnabled, GameOverCallback callback) {
        super(context);
        this.difficulty = difficulty;
        this.soundEnabled = soundEnabled;
        this.gameOverCallback = callback;

        getHolder().addCallback(this);
        setFocusable(true);

        // 初始化图片管理器
        ImageManager.init(context);

        // 重置并获取英雄机单例
        HeroAircraft.resetInstance();
        heroAircraft = HeroAircraft.getInstance();
        heroAircraft.setStrategy(new Shoot());

        // 初始化音乐播放器
        bgmPlayer = new MusicPlayer(context);
        bossPlayer = new MusicPlayer(context);
        sfxPlayer = new MusicPlayer(context);

        // 道具持续时间线程池
        powerUpExecutor = Executors.newSingleThreadScheduledExecutor();

        // 文字画笔
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(22);
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);

        bitmapPaint.setFilterBitmap(true);
    }

    // ===================== SurfaceHolder.Callback =====================

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        scaleX = (float) getWidth() / GAME_WIDTH;
        scaleY = (float) getHeight() / GAME_HEIGHT;

        running = true;
        gameThread = new Thread(this);
        gameThread.start();

        // 启动背景音乐
        if (soundEnabled) {
            bgmPlayer.startLoop("videos/bgm.wav");
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        scaleX = (float) width / GAME_WIDTH;
        scaleY = (float) height / GAME_HEIGHT;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        running = false;
        try {
            if (gameThread != null) {
                gameThread.join(500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        bgmPlayer.release();
        bossPlayer.release();
        sfxPlayer.release();
        powerUpExecutor.shutdownNow();
    }

    // ===================== 游戏主循环 =====================

    @Override
    public void run() {
        while (running) {
            long startTime = System.currentTimeMillis();
            if (!gameOverFlag) {
                update();
            }
            render();
            long elapsed = System.currentTimeMillis() - startTime;
            long sleepTime = timeInterval - elapsed;
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    // ===================== 触摸控制 =====================

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gameOverFlag) return true;

        // 将屏幕坐标转换为游戏虚拟坐标
        float gameX = event.getX() / scaleX;
        float gameY = event.getY() / scaleY;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (gameX >= 0 && gameX <= GAME_WIDTH && gameY >= 0 && gameY <= GAME_HEIGHT) {
                    heroAircraft.setLocation(gameX, gameY);
                }
                break;
        }
        return true;
    }

    // ===================== 游戏逻辑更新 =====================

    private void update() {
        time += timeInterval;

        // 难度递增（Common 和 Inferno 模式）
        if (difficulty >= 1 && difficultyFactor * 800 < time) {
            difficultyFactor++;
        }

        // 周期性执行
        if (timeCountAndNewCycleJudge()) {
            spawnEnemies();
            shootAction();
        }

        bulletsMoveAction();
        aircraftsMoveAction();
        propsMoveAction();
        crashCheckAction();
        postProcessAction();

        // 游戏结束检查
        if (heroAircraft.getHp() <= 0) {
            gameOverFlag = true;
            if (soundEnabled) {
                sfxPlayer.playOnce("videos/game_over.wav");
            }
            bgmPlayer.stop();
            bossPlayer.stop();

            // 通知 UI 线程
            post(() -> {
                if (gameOverCallback != null) {
                    gameOverCallback.onGameOver(score, difficulty);
                }
            });
        }
    }

    private boolean timeCountAndNewCycleJudge() {
        cycleTime += timeInterval;
        if (cycleTime >= cycleDuration) {
            cycleTime %= cycleDuration;
            return true;
        }
        return false;
    }

    // ===================== 敌机生成 =====================

    private void spawnEnemies() {
        int maxEnemy = getEnemyMaxNumber();
        if (enemyAircrafts.size() >= maxEnemy) return;

        EnemyFactory enemyFactory;
        Random random = new Random();
        int type = random.nextInt(100);

        switch (difficulty) {
            case 0: // Easy
                spawnEasyEnemies(type, random);
                break;
            case 1: // Common
                spawnCommonEnemies(type, random);
                break;
            case 2: // Inferno
                spawnInfernoEnemies(type, random);
                break;
        }

        // PlusEnemy 周期性产生
        if (time % 1200 == 0) {
            enemyFactory = new PlusFactory();
            Random rand = new Random();
            Enemy newenemy = enemyFactory.createEnemy(
                    (int) (Math.random() * (GAME_WIDTH - ImageManager.PLUS_ENEMY_IMAGE.getWidth())),
                    (int) (Math.random() * GAME_HEIGHT * 0.05),
                    rand.nextInt(2) == 0 ? -1 : 1, 6, 90);
            newenemy.setStrategy(new ShootA());
            enemyAircrafts.add(newenemy);
        }
    }

    private void spawnEasyEnemies(int type, Random random) {
        EnemyFactory enemyFactory;
        if (type < 75) {
            enemyFactory = new MobFactory();
            Enemy newenemy = enemyFactory.createEnemy(
                    (int) (Math.random() * (GAME_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                    (int) (Math.random() * GAME_HEIGHT * 0.05), 0, 6, 30);
            newenemy.setStrategy(new Shoot());
            enemyAircrafts.add(newenemy);
        } else if (type >= 75) {
            enemyFactory = new SuperFactory();
            Enemy newenemy = enemyFactory.createEnemy(
                    (int) (Math.random() * (GAME_WIDTH - ImageManager.SUPER_ENEMY_IMAGE.getWidth())),
                    (int) (Math.random() * GAME_HEIGHT * 0.05), 0, 6, 30);
            newenemy.setStrategy(new Shoot());
            enemyAircrafts.add(newenemy);
        } else if (bossnum == 0 && score % 40 == 0) {
            spawnBoss(180);
        }
    }

    private void spawnCommonEnemies(int type, Random random) {
        EnemyFactory enemyFactory;
        if (type < 40) {
            enemyFactory = new MobFactory();
            Enemy newenemy = enemyFactory.createEnemy(
                    (int) (Math.random() * (GAME_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                    (int) (Math.random() * GAME_HEIGHT * 0.05), 0, 7, 30);
            newenemy.setStrategy(new Shoot());
            enemyAircrafts.add(newenemy);
        } else if (type >= 50) {
            enemyFactory = new SuperFactory();
            Enemy newenemy = enemyFactory.createEnemy(
                    (int) (Math.random() * (GAME_WIDTH - ImageManager.SUPER_ENEMY_IMAGE.getWidth())),
                    (int) (Math.random() * GAME_HEIGHT * 0.05), 0, 7, 30);
            newenemy.setStrategy(new Shoot());
            enemyAircrafts.add(newenemy);
        } else if (bossnum <= 1 && score >= 300) {
            spawnBoss(150 + difficultyFactor * 10);
        }
    }

    private void spawnInfernoEnemies(int type, Random random) {
        EnemyFactory enemyFactory;
        if (type < 30) {
            enemyFactory = new MobFactory();
            Enemy newenemy = enemyFactory.createEnemy(
                    (int) (Math.random() * (GAME_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                    (int) (Math.random() * GAME_HEIGHT * 0.05), 0, 8, 30);
            newenemy.setStrategy(new Shoot());
            enemyAircrafts.add(newenemy);
        } else if (type >= 40) {
            enemyFactory = new SuperFactory();
            Enemy newenemy = enemyFactory.createEnemy(
                    (int) (Math.random() * (GAME_WIDTH - ImageManager.SUPER_ENEMY_IMAGE.getWidth())),
                    (int) (Math.random() * GAME_HEIGHT * 0.05), 0, 8, 30);
            newenemy.setStrategy(new Shoot());
            enemyAircrafts.add(newenemy);
        } else if (bossnum <= 2 && score >= 200) {
            spawnBoss(270 + difficultyFactor * 10);
        }
    }

    private void spawnBoss(int bossHp) {
        EnemyFactory enemyFactory = new BossFactory();
        Random rand = new Random();
        Enemy newenemy = enemyFactory.createEnemy(
                (int) (Math.random() * (GAME_WIDTH - ImageManager.BOSS_ENEMY_IMAGE.getWidth())),
                (int) (Math.random() * GAME_HEIGHT * 0.05),
                rand.nextInt(2) == 0 ? -3 : 3, 0, bossHp);
        newenemy.setStrategy(new ShootB());
        enemyAircrafts.add(newenemy);
        bossnum += 1;

        // Boss 出场：暂停主 BGM 并启动 boss BGM
        if (bgmPlayer.isPlaying()) {
            bgmPlayer.stop();
        }
        if (!bossPlayer.isPlaying() && soundEnabled) {
            bossPlayer.startLoop("videos/bgm_boss.wav");
        }
    }

    // ===================== 射击（逻辑不变） =====================

    private void shootAction() {
        int enemyShootInterval = (difficulty == 0) ? 160 : 120;

        // 敌机射击（降低射速）
        if (time % enemyShootInterval == 0) {
            for (Enemy enemy : enemyAircrafts) {
                enemyBullets.addAll(enemy.executeStrategy((AbstractAircraft) enemy));
            }
        }
        // 英雄射击
        heroBullets.addAll(heroAircraft.executeStrategy(heroAircraft));
    }

    private int getEnemyMaxNumber() {
        return (difficulty == 0) ? 4 : 5;
    }

    // ===================== 移动 =====================

    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (Enemy enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }

    private void propsMoveAction() {
        for (Prop p : props_blood) p.forward();
        for (Prop p : props_bomb) p.forward();
        for (Prop p : props_bullet) p.forward();
        for (Prop p : props_bulletplus) p.forward();
    }

    // ===================== 碰撞检测（逻辑不变） =====================

    private void crashCheckAction() {
        // 敌机子弹攻击英雄
        for (BaseBullet enemybullet : enemyBullets) {
            if (enemybullet.notValid()) continue;
            if (heroAircraft.crash(enemybullet)) {
                heroAircraft.decreaseHp(enemybullet.getPower());
                enemybullet.vanish();
            }
        }

        // 英雄子弹攻击敌机
        PropFactory propFactory;
        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) continue;
            for (Enemy enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) continue;
                if (enemyAircraft.crash(bullet)) {
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    if (soundEnabled) {
                        sfxPlayer.playOnce("videos/bullet_hit.wav");
                    }
                    if (enemyAircraft.notValid()) {
                        score += 10;
                        if (enemyAircraft instanceof BossEnemy) {
                            bossnum -= 1;
                            if (bossPlayer.isPlaying()) bossPlayer.stop();
                            if (!gameOverFlag && !bgmPlayer.isPlaying() && soundEnabled) {
                                bgmPlayer.startLoop("videos/bgm.wav");
                            }
                        }
                    }
                    if (enemyAircraft.notValid() && (enemyAircraft instanceof SuperEnemy || enemyAircraft instanceof PlusEnemy)) {
                        score += 10;
                        Random random = new Random();
                        int type = random.nextInt(100);
                        if (type < 30) {
                            propFactory = new BloodFactory();
                            props_blood.add(propFactory.createProp(enemyAircraft.getLocationX(), enemyAircraft.getLocationY(), 0, 7, 30));
                        } else if (type >= 30 && type < 40) {
                            propFactory = new BombFactory();
                            props_bomb.add(propFactory.createProp(enemyAircraft.getLocationX(), enemyAircraft.getLocationY(), 0, 7, 30));
                        } else if (type >= 40 && type < 70) {
                            propFactory = new BulletPlusFactory();
                            props_bulletplus.add(propFactory.createProp(enemyAircraft.getLocationX(), enemyAircraft.getLocationY(), 0, 7, 30));
                        } else if (type >= 70) {
                            propFactory = new BulletFactory();
                            props_bullet.add(propFactory.createProp(enemyAircraft.getLocationX(), enemyAircraft.getLocationY(), 0, 7, 30));
                        }
                    }
                    // 击落boss机随机掉落三种道具
                    if (enemyAircraft.notValid() && enemyAircraft instanceof BossEnemy) {
                        score += 10;
                        Random rand1 = new Random();
                        if (rand1.nextInt(2) == 0) {
                            propFactory = new BloodFactory();
                            props_blood.add(propFactory.createProp(enemyAircraft.getLocationX(), enemyAircraft.getLocationY(), -1, 7, 30));
                        }
                        Random rand2 = new Random();
                        if (rand2.nextInt(2) == 0) {
                            propFactory = new BulletFactory();
                            props_bullet.add(propFactory.createProp(enemyAircraft.getLocationX(), enemyAircraft.getLocationY(), 0, 7, 30));
                        }
                        Random rand3 = new Random();
                        if (rand3.nextInt(2) == 0) {
                            propFactory = new BombFactory();
                            props_bomb.add(propFactory.createProp(enemyAircraft.getLocationX(), enemyAircraft.getLocationY(), 1, 7, 30));
                        }
                    }
                }
                // 英雄机 与 敌机 相撞，均损毁
                if (enemyAircraft.crash(heroAircraft)) {
                    enemyAircraft.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
        }

        // 道具效果
        for (Prop p : props_blood) {
            if (p.crash(heroAircraft)) {
                p.vanish();
                heroAircraft.increaseHp(1000);
                if (soundEnabled) sfxPlayer.playOnce("videos/get_supply.wav");
            }
        }
        for (Prop p : props_bomb) {
            if (p.crash(heroAircraft)) {
                p.vanish();
                if (soundEnabled) {
                    sfxPlayer.playOnce("videos/get_supply.wav");
                    sfxPlayer.playOnce("videos/bomb_explosion.wav");
                }
                for (Enemy ea : enemyAircrafts) {
                    if ((ea instanceof MobEnemy || ea instanceof SuperEnemy || ea instanceof PlusEnemy) && !ea.notValid()) {
                        burst.addBoomObserver((BombObserve) ea);
                    }
                }
                for (BaseBullet b : enemyBullets) {
                    burst.addBoomObserver((BombObserve) b);
                }
                score += burst.boomPlay();
            }
        }
        for (Prop p : props_bullet) {
            if (p.crash(heroAircraft)) {
                p.vanish();
                heroAircraft.setStrategy(new ShootA());
                if (soundEnabled) sfxPlayer.playOnce("videos/get_supply.wav");
                if (powerUpRestoreFuture != null && !powerUpRestoreFuture.isDone()) {
                    powerUpRestoreFuture.cancel(false);
                }
                powerUpRestoreFuture = powerUpExecutor.schedule(() -> {
                    heroAircraft.setStrategy(new Shoot());
                }, POWERUP_DURATION_MS, TimeUnit.MILLISECONDS);
            }
        }
        for (Prop p : props_bulletplus) {
            if (p.crash(heroAircraft)) {
                p.vanish();
                heroAircraft.setStrategy(new ShootB());
                if (soundEnabled) sfxPlayer.playOnce("videos/get_supply.wav");
                if (powerUpRestoreFuture != null && !powerUpRestoreFuture.isDone()) {
                    powerUpRestoreFuture.cancel(false);
                }
                powerUpRestoreFuture = powerUpExecutor.schedule(() -> {
                    heroAircraft.setStrategy(new Shoot());
                }, POWERUP_DURATION_MS, TimeUnit.MILLISECONDS);
            }
        }
    }

    // ===================== 后处理 =====================

    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(Enemy::notValid);
        props_blood.removeIf(Prop::notValid);
        props_bomb.removeIf(Prop::notValid);
        props_bullet.removeIf(Prop::notValid);
        props_bulletplus.removeIf(Prop::notValid);
    }

    // ===================== 渲染 =====================

    private void render() {
        Canvas canvas = getHolder().lockCanvas();
        if (canvas == null) return;
        try {
            canvas.save();
            canvas.scale(scaleX, scaleY);

            // 绘制背景（滚动）
            drawBackground(canvas);

            // 绘制子弹
            drawFlyingObjects(canvas, enemyBullets);
            drawFlyingObjects(canvas, heroBullets);

            // 绘制敌机
            for (Enemy e : enemyAircrafts) {
                drawObject(canvas, (AbstractFlyingObject) e);
            }

            // 绘制道具
            for (Prop p : props_blood) drawObject(canvas, (AbstractFlyingObject) p);
            for (Prop p : props_bomb) drawObject(canvas, (AbstractFlyingObject) p);
            for (Prop p : props_bullet) drawObject(canvas, (AbstractFlyingObject) p);
            for (Prop p : props_bulletplus) drawObject(canvas, (AbstractFlyingObject) p);

            // 绘制英雄机
            Bitmap heroImg = ImageManager.HERO_IMAGE;
            if (heroImg != null) {
                canvas.drawBitmap(heroImg,
                        heroAircraft.getLocationX() - heroImg.getWidth() / 2f,
                        heroAircraft.getLocationY() - heroImg.getHeight() / 2f,
                        bitmapPaint);
            }

            // 绘制得分和生命值
            drawScoreAndLife(canvas);

            canvas.restore();
        } catch (Exception e) {
            Log.e(TAG, "render error", e);
        } finally {
            try {
                getHolder().unlockCanvasAndPost(canvas);
            } catch (Exception e) {
                Log.e(TAG, "unlockCanvas error", e);
            }
        }
    }

    private void drawBackground(Canvas canvas) {
        Bitmap bgImage;
        switch (difficulty) {
            case 2:
                bgImage = ImageManager.INFERNOBACKGROUND_IMAGE;
                break;
            case 1:
                bgImage = ImageManager.COMMONBACKGROUND_IMAGE;
                break;
            default:
                bgImage = ImageManager.EASYBACKGROUND_IMAGE;
                break;
        }
        if (bgImage != null) {
            // 两幅背景图滚动
            Rect destTop = new Rect(0, backGroundTop - GAME_HEIGHT, GAME_WIDTH, backGroundTop);
            Rect destBottom = new Rect(0, backGroundTop, GAME_WIDTH, backGroundTop + GAME_HEIGHT);
            canvas.drawBitmap(bgImage, null, destTop, bitmapPaint);
            canvas.drawBitmap(bgImage, null, destBottom, bitmapPaint);
            backGroundTop += 1;
            if (backGroundTop >= GAME_HEIGHT) {
                backGroundTop = 0;
            }
        }
    }

    private <T extends AbstractFlyingObject> void drawFlyingObjects(Canvas canvas, List<T> objects) {
        for (T obj : objects) {
            drawObject(canvas, obj);
        }
    }

    private void drawObject(Canvas canvas, AbstractFlyingObject obj) {
        Bitmap img = obj.getImage();
        if (img != null) {
            canvas.drawBitmap(img,
                    obj.getLocationX() - img.getWidth() / 2f,
                    obj.getLocationY() - img.getHeight() / 2f,
                    bitmapPaint);
        }
    }

    private void drawScoreAndLife(Canvas canvas) {
        canvas.drawText("SCORE:" + score, 10, 25, textPaint);
        canvas.drawText("LIFE:" + heroAircraft.getHp(), 10, 45, textPaint);
    }

    // ===================== 公共方法 =====================

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        if (!enabled) {
            bgmPlayer.stop();
            bossPlayer.stop();
        } else if (!gameOverFlag) {
            if (bossnum > 0 && !bossPlayer.isPlaying()) {
                bossPlayer.startLoop("videos/bgm_boss.wav");
            } else if (!bgmPlayer.isPlaying()) {
                bgmPlayer.startLoop("videos/bgm.wav");
            }
        }
    }

    public int getScore() {
        return score;
    }

    public boolean isGameOver() {
        return gameOverFlag;
    }
}

