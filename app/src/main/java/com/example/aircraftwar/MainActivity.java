package com.example.aircraftwar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

/**
 * 主菜单 Activity：选择难度和音效开关
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnEasy = findViewById(R.id.btn_easy);
        Button btnCommon = findViewById(R.id.btn_common);
        Button btnInferno = findViewById(R.id.btn_inferno);
        SwitchCompat cbSound = findViewById(R.id.cb_sound);

        btnEasy.setOnClickListener(v -> startGame(0, cbSound.isChecked()));
        btnCommon.setOnClickListener(v -> startGame(1, cbSound.isChecked()));
        btnInferno.setOnClickListener(v -> startGame(2, cbSound.isChecked()));
    }

    private void startGame(int difficulty, boolean soundEnabled) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("soundEnabled", soundEnabled);
        startActivity(intent);
    }
}