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

    // Array to save Mine TVs
    private ArrayList<TextView> mine_tvs;

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

        // Initialize mine_tvs
        mine_tvs = new ArrayList<TextView>();

        // Initialize grid layout. Retrieve using ID
        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout01);

        LayoutInflater li = LayoutInflater.from(this);

        // Generate mines for tiles and save it in an ArrayList
        generateMines();

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

                // Add TextView to the grid and to cell_tvs
                grid.addView(tv, lp);
                cell_tvs.add(tv);

                // Check if the current cell is a mine and add it to a new ArrayList for checking later
                if (this.checkIfMine(i,j)){
                    mine_tvs.add(tv);
                }
            }
        }
    }

    public void onClickTV(View view){

        TextView tv = (TextView) view;
        int n = findIndexOfCellTextView(tv);
        int i = n/COLUMN_COUNT;
        int j = n%COLUMN_COUNT;


        // Check if Mine is present
        int[] mine = new int[2];
        mine[0] = i;
        mine[1] = j;

        System.out.println("HEllo: " + mine[0] + mine[1]);
        Boolean minePresent = this.checkIfPresent(mine);

        // Activate handlers depending on whether there is a mine or not
        if (minePresent){
            this.mineHandler();
        } else {
            this.emptyHandler(tv, i, j);
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

    // Function to check if the current cell is a mine
    private Boolean checkIfMine(int i, int j){
        for (int[] mine: mineArray){
            int row = mine[0];
            int col = mine[1];

            if ((i == row) && (j == col)){
                return true;
            }
        }

        return false;
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
    public void mineHandler(){
        // TODO: End Game/Restart Game


        for (TextView tv: mine_tvs){
            tv.setText("\uD83D\uDCA3");
            if (tv.getCurrentTextColor() == Color.GRAY) {
                tv.setTextColor(Color.GREEN);
                tv.setBackgroundColor(Color.parseColor("red"));
            }
        }
    }

    // Function to handle when user clicks on safe spot
    public void emptyHandler(TextView tv, int i, int j){

        // TODO: Transitively reveal all adjacent cells

        // Check the number of mines around cell
        int noOfMines = noOfMinesAroundCell(i, j);

        // Set the number to the no. of bombs around
        tv.setText(Integer.toString(noOfMines));
        tv.setTextColor(Color.GRAY);
        tv.setBackgroundColor(Color.LTGRAY);

    }

    // Function to check the number of mines while receiving row and col as input
    public int noOfMinesAroundCell(int i, int j){

        int sumOfMines = 0;

        for (int[] mine: mineArray){
            int row = mine[0];
            int col = mine[1];

            // Check if all directions have mines
            if ((i == row) || (i+1 == row) || (i-1 == row)){
                if ((j == col) || (j+1 == col) || (j-1 == col)){
                    sumOfMines += 1;
                }
            }
        }
        return sumOfMines;

    }

}