package com.example.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    // TODO: Separate code to another class. Initialize with num rows, cols and mines

    // Column count to derive position later
    private static final int COLUMN_COUNT = 8;

    // Track the number of flags
    int numFlags = 4;

    // Track the number of mines
    int numMines = 4;

    // Array to save TVs
    private ArrayList<TextView> cell_tvs;

    // Hashmap to check status of cell (Open/Close)
    private HashMap<TextView, String> cellStatus = new HashMap<TextView, String>();

    // Array to save Mines
    private ArrayList<int[]> mineArray;

    // Array to save Mine TVs
    private ArrayList<TextView> mine_tvs;

    // Array to save chosen cells as flags
    private ArrayList<TextView> selectedMines;

    // Hashmap to store what chosen cell's text was before changing it to flag
    private HashMap<TextView, String> storedText;

    // Stopwatch timer to track how long user plays the game
    private int clock = 0;
    private boolean running = false;


    // To track mode of the game
    private String mode = "pick";

    // Boolean to keep track if game is over
    boolean gameOver = false;

    // Boolean to keep track of gameStatus
    boolean gameStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize new ArrayList
        cell_tvs = new ArrayList<TextView>();

        // Initialize mine_tvs
        mine_tvs = new ArrayList<TextView>();

        // Initialize hashmap
        cellStatus = new HashMap<TextView, String>();

        // Initialize ArrayList to save chosen mines
        selectedMines = new ArrayList<TextView>();

        // Initialize HashMap to save TextView's string before
        storedText = new HashMap<TextView, String>();

        // Stopwatch timer --> Check savedInstanceState
        if (savedInstanceState != null) {
            clock = savedInstanceState.getInt("clock");
            running = savedInstanceState.getBoolean("running");
        }

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

                int id = Integer.parseInt(Integer.toString(i) + Integer.toString(j));

                // Set ID to check cells later
                tv.setId(id);
                cell_tvs.add(tv);

                // Check if the current cell is a mine and add it to a new ArrayList for checking later
                if (this.checkIfMine(i,j)){
                    mine_tvs.add(tv);
                }

                // Add to cell status hashmap
                cellStatus.put(tv, "close");

            }
        }

        // Retrieve TV of mode by id and set onClick
        TextView mode = findViewById(R.id.mode);
        mode.setOnClickListener(this::onClickMode);

        // Set flag text
        TextView flag = findViewById(R.id.flagCount);
        flag.setText(Integer.toString(this.numFlags));
    }

    private void onClickMode(View view){
        TextView tv = (TextView) view;

        if (tv.getText() == getString(R.string.pickaxe)){
            tv.setText(getString(R.string.flag));
            this.mode = "flag";
        } else {
            tv.setText(getString(R.string.pickaxe));
            this.mode = "pick";
        }
    }

    public void onClickTV(View view){

        // Handle according to the mode of the game

        TextView tv = (TextView) view;

        if (!running){
            running = true;
            runTimer();
        }

        if (gameOver){
            redirect(view);
        }

        if (this.mode == "pick") {
            int n = findIndexOfCellTextView(tv);
            int i = n/COLUMN_COUNT;
            int j = n%COLUMN_COUNT;


            // Check if Mine is present
            int[] mine = new int[2];
            mine[0] = i;
            mine[1] = j;

            Boolean minePresent = this.checkIfPresent(mine);

            // Activate handlers depending on whether there is a mine or not
            if (minePresent){
                this.mineHandler();
            } else {
                this.emptyHandler(tv, i, j);
            }
        } else {

            // If flag is not present --> Add flag and add tv to selectedMines
            if (tv.getText() != getString(R.string.flag) && (cellStatus.get(tv) != "open")){
                this.numFlags -= 1;
                this.selectedMines.add(tv);
                storedText.put(tv, (String) tv.getText());
                tv.setText(getString(R.string.flag));
                tv.setBackgroundColor(Color.GRAY);

                // Update FlagCount
                TextView flagCount = findViewById(R.id.flagCount);
                flagCount.setText(Integer.toString(this.numFlags));


                // Function to check if game is over
                // TODO: Implement functionality to check if the game is over
                if (checkGameWon() && numFlags == 0){
                    this.gameOver = true;
                    this.gameStatus = true;
                    this.running = false;
                }
            } else {

                // If flag is present --> Remove flag and remove tv from selectedMines

                if (cellStatus.get(tv) != "open"){
                    this.numFlags += 1;
                    this.selectedMines.remove(tv);
                    tv.setText(storedText.get(tv));
                    tv.setBackgroundColor(Color.GRAY);

                    // Update FlagCount
                    TextView flagCount = findViewById(R.id.flagCount);
                    flagCount.setText(Integer.toString(this.numFlags));

                    if (checkGameWon() && numFlags == 0){
                        this.gameOver = true;
                        this.gameStatus = true;
                    }
                }
            }
        }
    }


    // Function to generate ArrayList of Mines. Mines are in int[] format.
    private void generateMines(){

        mineArray = new ArrayList<int[]>();


        // Generate random number
        Random rand = new Random();


        // Generate mines until numMines == numFlags
        while (mineArray.size() != numFlags){

            // Int array to store coordinates of a mine
            int[] singleMine = new int[2];

            // Row Num
            int y = rand.nextInt(10);
            singleMine[0] = y;


            // Col Num
            int x = rand.nextInt(8);
            singleMine[1] = x;

            // Only add the mine if it is unique
            if (!checkIfMinePresent(singleMine)){
                // Add single mine to mines array
                mineArray.add(singleMine);
            }


        }
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

        // End Game. Game over
        this.gameOver = true;
        this.running = false;


    }

    // Function to handle when user clicks on safe spot
    public void emptyHandler(TextView tv, int i, int j){

        // Check the number of mines around cell
        int noOfMines = noOfMinesAroundCell(i, j);

        // Set the number to the no. of bombs around

        // If num of bombs > 0, show num. Else Transitive opening
        if (noOfMines > 0){
            tv.setText(Integer.toString(noOfMines));
            tv.setTextColor(Color.GRAY);
            tv.setBackgroundColor(Color.LTGRAY);
        } else {
            // Open current one
            tv.setText("");
            tv.setBackgroundColor(Color.LTGRAY);
            this.recursiveOpening(tv,i,j);
        }
    }

    // Function to recursively open adjacent cells
    private void recursiveOpening(TextView tv, int row, int col){


        // End if numOfMines around cell is != 0 or if cell is flagged
        if ((noOfMinesAroundCell(row,col) != 0) || (tv.getText() == getString(R.string.flag)) || !isInGrid(row,col)){
            return;
        }
        else {
            // Recurse through all 8 directions
            // Check N
            if ((noOfMinesAroundCell(row-1, col) == 0) && isInGrid(row-1, col) && notOpen(row-1, col)){
                int id = Integer.parseInt(Integer.toString(row-1) + Integer.toString(col));
                for (TextView view : cell_tvs){
                    if ((view.getId() == id) && (cellStatus.get(view) == "close")){
                        view.setText("");
                        cellStatus.put(view, "open");
                        view.setBackgroundColor(Color.LTGRAY);
                        recursiveOpening(view, row - 1, col);
                    }
                }
            }
            else {

                if (isInGrid(row-1, col)){

                    int id = Integer.parseInt(Integer.toString(row-1) + Integer.toString(col));
                    for (TextView view : cell_tvs) {
                        if ((view.getId() == id) && (cellStatus.get(view) == "close")) {
                            cellStatus.put(view, "open");
                            view.setText(Integer.toString(noOfMinesAroundCell(row-1,col)));
                            view.setBackgroundColor(Color.LTGRAY);;
                        }
                    }
                }

            }

            // Check E
            if ((noOfMinesAroundCell(row, col+1) == 0) && isInGrid(row,col+1) && notOpen(row, col+1)){
                int id = Integer.parseInt(Integer.toString(row) + Integer.toString(col+1));
                for (TextView view : cell_tvs){
                    if ((view.getId() == id) && (cellStatus.get(view) == "close")){
                        view.setText("");
                        cellStatus.put(view, "open");
                        view.setBackgroundColor(Color.LTGRAY);
                        recursiveOpening(view, row, col+1);
                    }
                }
            }
            else {
                if (isInGrid(row, col+1)) {
                    int id = Integer.parseInt(Integer.toString(row) + Integer.toString(col+1));
                    for (TextView view : cell_tvs) {
                        if ((view.getId() == id) && (cellStatus.get(view) == "close")) {
                            cellStatus.put(view, "open");
                            view.setText(Integer.toString(noOfMinesAroundCell(row,col+1)));
                            view.setBackgroundColor(Color.LTGRAY);;
                        }
                    }
                }

            }

            // Check S
            if ((noOfMinesAroundCell(row+1, col) == 0) && isInGrid(row+1, col) && notOpen(row+1, col)){
                int id = Integer.parseInt(Integer.toString(row+1) + Integer.toString(col));
                for (TextView view : cell_tvs){
                    if ((view.getId() == id) && (cellStatus.get(view) == "close")){
                        view.setText("");
                        cellStatus.put(view, "open");
                        view.setBackgroundColor(Color.LTGRAY);
                        recursiveOpening(view, row + 1, col);
                    }
                }
            }
            else {
                if (isInGrid(row+1, col)) {
                    int id = Integer.parseInt(Integer.toString(row+1) + Integer.toString(col));
                    for (TextView view : cell_tvs) {
                        if ((view.getId() == id) && (cellStatus.get(view) == "close")) {
                            cellStatus.put(view, "open");
                            view.setText(Integer.toString(noOfMinesAroundCell(row+1,col)));
                            view.setBackgroundColor(Color.LTGRAY);;
                        }
                    }
                }

            }

            // Check W
            if ((noOfMinesAroundCell(row, col-1) == 0) && isInGrid(row,col-1) && notOpen(row, col-1)){
                int id = Integer.parseInt(Integer.toString(row) + Integer.toString(col-1));
                for (TextView view : cell_tvs){
                    if ((view.getId() == id) && (cellStatus.get(view) == "close")){
                        view.setText("");
                        cellStatus.put(view, "open");
                        view.setBackgroundColor(Color.LTGRAY);
                        recursiveOpening(view, row, col-1);
                    }
                }
            }
            else {
                if (isInGrid(row, col-1)) {

                    int id = Integer.parseInt(Integer.toString(row) + Integer.toString(col-1));
                    for (TextView view : cell_tvs) {
                        if ((view.getId() == id) && (cellStatus.get(view) == "close")) {
                            cellStatus.put(view, "open");
                            view.setText(Integer.toString(noOfMinesAroundCell(row,col-1)));
                            view.setBackgroundColor(Color.LTGRAY);;
                        }
                    }
                }

            }

            // Check NE
            if ((noOfMinesAroundCell(row-1, col+1) == 0) && isInGrid(row-1,col+1) && notOpen(row-1, col+1)){
                int id = Integer.parseInt(Integer.toString(row-1) + Integer.toString(col+1));
                for (TextView view : cell_tvs){
                    if ((view.getId() == id) && (cellStatus.get(view) == "close")){
                        view.setText("");
                        cellStatus.put(view, "open");
                        view.setBackgroundColor(Color.LTGRAY);
                        recursiveOpening(view, row-1, col+1);
                    }
                }
            }
            else {
                if (isInGrid(row-1, col+1)) {

                    int id = Integer.parseInt(Integer.toString(row-1) + Integer.toString(col+1));
                    for (TextView view : cell_tvs) {
                        if ((view.getId() == id) && (cellStatus.get(view) == "close")) {
                            cellStatus.put(view, "open");
                            view.setText(Integer.toString(noOfMinesAroundCell(row-1,col+1)));
                            view.setBackgroundColor(Color.LTGRAY);;
                        }
                    }
                }

            }

            // Check SE
            if ((noOfMinesAroundCell(row+1, col+1) == 0) && isInGrid(row+1,col+1) && notOpen(row+1, col+1)){
                int id = Integer.parseInt(Integer.toString(row+1) + Integer.toString(col+1));
                for (TextView view : cell_tvs){
                    if ((view.getId() == id) && (cellStatus.get(view) == "close")){
                        view.setText("");
                        cellStatus.put(view, "open");
                        view.setBackgroundColor(Color.LTGRAY);
                        recursiveOpening(view, row+1, col+1);
                    }
                }
            }
            else {
                if (isInGrid(row+1, col+1)) {

                    int id = Integer.parseInt(Integer.toString(row+1) + Integer.toString(col+1));
                    for (TextView view : cell_tvs) {
                        if ((view.getId() == id) && (cellStatus.get(view) == "close")) {
                            cellStatus.put(view, "open");
                            view.setText(Integer.toString(noOfMinesAroundCell(row+1,col+1)));
                            view.setBackgroundColor(Color.LTGRAY);;
                        }
                    }
                }

            }

            // Check SW
            if ((noOfMinesAroundCell(row+1, col-1) == 0) && isInGrid(row+1,col-1) && notOpen(row+1, col-1)){
                int id = Integer.parseInt(Integer.toString(row+1) + Integer.toString(col-1));
                for (TextView view : cell_tvs){
                    if ((view.getId() == id) && (cellStatus.get(view) == "close")){
                        view.setText("");
                        cellStatus.put(view, "open");
                        view.setBackgroundColor(Color.LTGRAY);
                        recursiveOpening(view, row+1, col-1);
                    }
                }
            }
            else {
                if (isInGrid(row+1, col-1)) {

                    int id = Integer.parseInt(Integer.toString(row+1) + Integer.toString(col-1));
                    for (TextView view : cell_tvs) {
                        if ((view.getId() == id) && (cellStatus.get(view) == "close")) {
                            cellStatus.put(view, "open");
                            view.setText(Integer.toString(noOfMinesAroundCell(row+1,col-1)));
                            view.setBackgroundColor(Color.LTGRAY);;
                        }
                    }
                }

            }

            // Check NW
            if ((noOfMinesAroundCell(row-1, col-1) == 0) && isInGrid(row-1,col-1) && notOpen(row-1, col-1)){
                int id = Integer.parseInt(Integer.toString(row-1) + Integer.toString(col-1));
                for (TextView view : cell_tvs){
                    if ((view.getId() == id) && (cellStatus.get(view) == "close")){
                        view.setText("");
                        cellStatus.put(view, "open");
                        view.setBackgroundColor(Color.LTGRAY);
                        recursiveOpening(view, row-1, col-1);
                    }
                }
            }
            else {
                if (isInGrid(row-1, col-1)) {

                    int id = Integer.parseInt(Integer.toString(row-1) + Integer.toString(col-1));
                    for (TextView view : cell_tvs) {
                        if ((view.getId() == id) && (cellStatus.get(view) == "close")) {
                            cellStatus.put(view, "open");
                            view.setText(Integer.toString(noOfMinesAroundCell(row-1,col-1)));
                            view.setBackgroundColor(Color.LTGRAY);;
                        }
                    }
                }

            }
        }
    }

    /// HELPER FUNCTIONS ///

    // Check if the mine is already present in the generated mines
    private boolean checkIfMinePresent(int[] singleMine){

        int x = singleMine[0];
        int y = singleMine[1];

        for (int[] mine : mineArray){
            int currX = mine[0];
            int currY = mine[1];

            if (x == currX && y == currY){
                return true;
            }
        }

        return false;

    }

    // Check if game is over
    private boolean checkGameWon(){

        int sum = 0;

        for (TextView tv : selectedMines){
            for (TextView mine_tv : mine_tvs){
                if (tv == mine_tv){
                    sum += 1;
                }
            }
        }

        System.out.println(this.numFlags);
        if (sum == this.numMines){
            return true;
        } else {
            return false;
        }

    }

    // To check if the cell is open or closed
    private boolean notOpen(int row, int col){
        int id = Integer.parseInt(Integer.toString(row) + Integer.toString(col));
        for (TextView view : cell_tvs){
            if ((view.getId() == id) && (cellStatus.get(view) == "close")){
                return true;
            }
        }

        return false;
    }

    // Check if coordinates is in the grid
    private boolean isInGrid(int i, int j){
        if (i < 0 || i > 9){
            return false;
        } else if (j < 0 || j > 7){
            return false;
        } else {
            return true;
        }
    }

    // Function to check the number of mines while receiving row and col as input
    private int noOfMinesAroundCell(int i, int j){
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

    // Function to check index of cell using TextView
    private int findIndexOfCellTextView(TextView tv) {

        for (int n=0; n<cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv)
                return n;
        }
        return -1;
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("clock", clock);
        savedInstanceState.putBoolean("running", running);
    }

    // Stopwatch timer function
    private void runTimer() {
        final Handler handler = new Handler();
        final TextView timerView = (TextView) findViewById(R.id.timerCount);

        handler.post(new Runnable() {
            @Override
            public void run() {
                int seconds = clock%60;

                clock = seconds;
                timerView.setText(Integer.toString(clock));

                if (running) {
                    clock++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    // Function to redirect to new page
    private void redirect(View view){
        Intent intent = new Intent(view.getContext(), EndGame.class);
        intent.putExtra("gameStatus", this.gameStatus);
        intent.putExtra("timer",clock);
        startActivity(intent);
    }


}