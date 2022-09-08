package com.example.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // Column count to derive position later
    private static final int COLUMN_COUNT = 8;

    // Array to save TVs
    private ArrayList<TextView> cell_tvs;

    // Array to save Mines
    private ArrayList<int[]> mineArray;

    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize new ArrayList
        cell_tvs = new ArrayList<TextView>();

        // Initialize grid layout. Retrieve using ID
        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout01);

        LayoutInflater li = LayoutInflater.from(this);

        for (int i = 0; i<10; i++) {
            for (int j=0; j<8; j++) {
                TextView tv = (TextView) li.inflate(R.layout.custom_cell_layout, grid, false);
                //tv.setText(String.valueOf(i)+String.valueOf(j));
                tv.setTextColor(Color.GRAY);
                tv.setBackgroundColor(Color.GRAY);
                tv.setOnClickListener(this::onClickTV);

                GridLayout.LayoutParams lp = (GridLayout.LayoutParams) tv.getLayoutParams();
                lp.rowSpec = GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);

                grid.addView(tv, lp);

                cell_tvs.add(tv);
            }
        }

        // Generate mines for tiles and save it in an ArrayList
        generateMines();

//        // Generate mines
//        ArrayList<int[]> mineArray = generateMines();
//
//        for (int i=0; i<4; i++){
//
//            // Retrieve mine
//
//            System.out.println(mineArray.get(i));
//            int[] currentMine = mineArray.get(i);
//
//            // Create TextView
//            TextView tv = (TextView) li.inflate(R.layout.mine_cell_layout, grid, false);
//            tv.setTextColor(Color.GRAY);
//
//            // TODO: Function will have to change to onClickMine
//            tv.setOnClickListener(this::onClickTV);
//
//            GridLayout.LayoutParams lp = (GridLayout.LayoutParams) tv.getLayoutParams();
//            lp.rowSpec = GridLayout.spec(currentMine[0]);
//            lp.columnSpec = GridLayout.spec(currentMine[1]);
//
//            grid.addView(tv,lp);
//
//        }
    }

    public void onClickTV(View view){

        // Check if mine is present. If mine is present --> mineHandler
        // Else --> safeHandler

        TextView tv = (TextView) view;
        int n = findIndexOfCellTextView(tv);
        int i = n/COLUMN_COUNT;
        int j = n%COLUMN_COUNT;

        // TODO: Insert if-else handler when mine is present vs absent

        int[] mine = new int[2];
        mine[0] = i;
        mine[1] = j;

        System.out.println("HEllo: " + mine[0] + mine[1]);
        Boolean minePresent = this.checkIfPresent(mine);

        if (minePresent){
            this.mineHandler(tv, i, j);
        } else {
            tv.setText(String.valueOf(i)+String.valueOf(j));
            if (tv.getCurrentTextColor() == Color.GRAY) {
                tv.setTextColor(Color.GREEN);
                tv.setBackgroundColor(Color.parseColor("lime"));
            }else {
                tv.setTextColor(Color.GRAY);
                tv.setBackgroundColor(Color.LTGRAY);
            }

        }
    }

    private int findIndexOfCellTextView(TextView tv) {
        for (int n=0; n<cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv)
                return n;
        }
        return -1;
    }


    // Function to generate ArrayList of Mines. Mines are in int[] format.
    private void generateMines(){

        mineArray = new ArrayList<int[]>();


        // Generate random number
        Random rand = new Random();

        for (int i=0; i<4; i++){

            // Int array to store coordinates of a mine
            int[] singleMine = new int[2];

            // Row Num
            int y = rand.nextInt(10);
            singleMine[0] = y;


            // Col Num
            int x = rand.nextInt(8);
            singleMine[1] = x;

            System.out.print(y + " " + x);

            // Add single mine to mines array
            mineArray.add(singleMine);
        }

        System.out.println("HELP:" + mineArray);
    }

    // Function to check if mine is present in selected square
    public Boolean checkIfPresent(int[] mine){
        int i = mine[0];
        int j = mine[1];

        for (int[] presentMine : mineArray){
            if ((i == presentMine[0]) && (j == presentMine[1])){
                return true;
            }
        }

        return false;
    }

    // Function to handle when user clicks on mines
    public void mineHandler(TextView view, int row, int col){

        // TODO: Expose all mines and end game.
        view.setText("\uD83D\uDCA3");
        if (view.getCurrentTextColor() == Color.GRAY) {
            view.setTextColor(Color.GREEN);
            view.setBackgroundColor(Color.parseColor("red"));
        }else {
            view.setTextColor(Color.GRAY);
            view.setBackgroundColor(Color.LTGRAY);
        }



    }

    // Function to handle when user clicks on safe spot
    public void emptyHandler(){
        // TODO: Transitively reveal all adjacent cells
    }
}