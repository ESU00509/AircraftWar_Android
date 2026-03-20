package com.example.aircraftwar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aircraftwar.application.Data;
import com.example.aircraftwar.application.DataDao;
import com.example.aircraftwar.application.DataDaoImpl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 游戏 Activity，承载 GameView
 */
public class GameActivity extends AppCompatActivity implements GameView.GameOverCallback {

    private GameView gameView;
    private int difficulty;
    private boolean soundEnabled;
    private DataDao dataDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 全屏沉浸式
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        difficulty = getIntent().getIntExtra("difficulty", 0);
        soundEnabled = getIntent().getBooleanExtra("soundEnabled", true);

        dataDao = new DataDaoImpl(getFilesDir());

        gameView = new GameView(this, difficulty, soundEnabled, this);
        setContentView(gameView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gameView != null) {
            gameView.setSoundEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameView != null && soundEnabled && !gameView.isGameOver()) {
            gameView.setSoundEnabled(true);
        }
    }

    // ===================== 游戏结束回调 =====================

    @Override
    public void onGameOver(int score, int difficulty) {
        String difficultyName;
        switch (difficulty) {
            case 1:
                difficultyName = "COMMON";
                break;
            case 2:
                difficultyName = "INFERNO";
                break;
            default:
                difficultyName = "EASY";
                break;
        }

        // 弹出输入玩家名对话框
        AlertDialog.Builder nameBuilder = new AlertDialog.Builder(this);
        nameBuilder.setTitle("游戏结束");
        nameBuilder.setMessage("难度：" + difficultyName + "\n得分：" + score + "\n\n请输入你的名字：");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("玩家名");
        nameBuilder.setView(input);

        nameBuilder.setCancelable(false);
        nameBuilder.setPositiveButton("确定", (dialog, which) -> {
            String playerName = input.getText().toString().trim();
            if (playerName.isEmpty()) playerName = "玩家1";

            // 保存数据
            Data data = new Data(playerName, score, System.currentTimeMillis());
            dataDao.doAdd(data);

            // 显示排行榜
            showRankingDialog(difficultyName);
        });

        nameBuilder.show();
    }

    private void showRankingDialog(String difficultyName) {
        List<Data> allData = dataDao.getAll();

        // 构建排行榜视图
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        TextView header = new TextView(this);
        header.setText("🏆 排行榜 - 难度：" + difficultyName);
        header.setTextSize(18);
        header.setPadding(0, 0, 0, 16);
        layout.addView(header);

        TableLayout table = new TableLayout(this);
        table.setStretchAllColumns(true);

        // 表头
        TableRow headerRow = new TableRow(this);
        headerRow.addView(makeCell("名次", true));
        headerRow.addView(makeCell("玩家", true));
        headerRow.addView(makeCell("得分", true));
        headerRow.addView(makeCell("时间", true));
        table.addView(headerRow);

        // 数据行
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
        for (int i = 0; i < allData.size(); i++) {
            Data d = allData.get(i);
            TableRow row = new TableRow(this);
            row.addView(makeCell(String.valueOf(i + 1), false));
            row.addView(makeCell(d.getName(), false));
            row.addView(makeCell(String.valueOf(d.getScore()), false));
            row.addView(makeCell(sdf.format(new Date(d.getTime())), false));
            table.addView(row);
        }

        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(table);
        layout.addView(scrollView);

        AlertDialog.Builder rankBuilder = new AlertDialog.Builder(this);
        rankBuilder.setView(layout);
        rankBuilder.setCancelable(false);
        rankBuilder.setPositiveButton("返回主菜单", (dialog, which) -> finish());
        rankBuilder.setNegativeButton("再来一局", (dialog, which) -> {
            // 重新开始同难度游戏
            finish();
            getIntent().putExtra("difficulty", difficulty);
            getIntent().putExtra("soundEnabled", soundEnabled);
            startActivity(getIntent());
        });
        rankBuilder.show();
    }

    private TextView makeCell(String text, boolean bold) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(8, 8, 8, 8);
        if (bold) {
            tv.setTextSize(14);
            tv.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            tv.setTextSize(13);
        }
        return tv;
    }
}

