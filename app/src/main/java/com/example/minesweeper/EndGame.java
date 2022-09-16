package com.example.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class EndGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        // Retrieve the status of the game. If true --> Game won
        boolean gameStatus = getIntent().getBooleanExtra("gameStatus", false);

        // Retrieve timer of game
        int stopwatchTime = getIntent().getIntExtra("timer", 0);


        TextView timer = findViewById(R.id.timer_placeholder);
        timer.setText("You took " + stopwatchTime + " Seconds");
        TextView result = findViewById(R.id.result);
        TextView note = findViewById(R.id.note);


        if (gameStatus){
            result.setText("You Won");
            note.setText("Congratulations");
        } else {

            result.setText("You Lost");
            note.setText("Better luck next time");
        }



        System.out.println("WON");
    }

    public void onClickRestart(View view){
        Intent restart = new Intent(this,MainActivity.class);
        finish();
        startActivity(restart);
    }

}