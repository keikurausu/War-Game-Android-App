package com.example.caleb.wargame;

import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    public static final int GAME_DIMENSION = 6, CPU_DEPTH_LIMIT = 5, CPU_END_LIMIT = 10;
    private GoogleApiClient client;
    int user_x, user_y, blueScore = 0, greenScore = 0, blocksOccupied = 0;
    char current_team = 'b', opponent = 'g'; //start with blue
    char gameboard[][] = new char[GAME_DIMENSION][GAME_DIMENSION];
    int gameMode = 0, gameModeTemp = 0; // 0 for human, 1 for AI
    int best_x, best_y; //best location found so far
    int values[][] = new int[][]{
            { 10, 1, 1, 1, 1, 10 },
        { 1, 3, 4, 4, 3, 1 },
        { 1, 4, 2, 2, 4, 1 },
        { 1, 4, 2, 2, 4, 1 },
        { 1, 3, 4, 4, 3, 1 },
        { 10, 1, 1, 1, 1, 10 }};
    //resets game to play again
    public void GameReset(){
        Button button;
        int buttonID;
        blocksOccupied = 0;
        blueScore = 0;
        greenScore = 0;
        current_team = 'b';
        opponent = 'g';
        TextView blueScoreField = (TextView)findViewById(R.id.blueScoreField);
        TextView greenScoreField = (TextView)findViewById(R.id.greenScoreField);
        blueScoreField.setText("Blue: " + blueScore);
        greenScoreField.setText("Green: " + greenScore);
        TextView gameOverField = (TextView)findViewById(R.id.gameOverField);
        gameOverField.setText(""); //clear game over message
        if(gameModeTemp == 0){
            gameMode = 0;
        }
        else{
            gameMode = 1;
        }
        for(int i = 0; i < GAME_DIMENSION; i++)
        {
            for(int j = 0; j<GAME_DIMENSION; j++)
            {
                gameboard[i][j] = 'o';
                buttonID = getResources().getIdentifier("button" + i + j, "id", getPackageName());
                button = (Button)findViewById(buttonID);
                button.setBackgroundColor(Color.LTGRAY);
            }
        }
    }

    //toggle mode
    public void mode(View view){
        if(gameModeTemp == 0)
            gameModeTemp = 1;
        else
            gameModeTemp = 0;
    }

    //restart game
    public void reStart(View view){
        GameReset();
    }

    //get user input dimensions
    public void ButtonClick(View view) {
        switch (view.getId()) {
            //top row
            case R.id.button00: user_x = 0; user_y = 0; break;
            case R.id.button01: user_x = 1; user_y = 0; break;
            case R.id.button02: user_x = 2; user_y = 0; break;
            case R.id.button03: user_x = 3; user_y = 0; break;
            case R.id.button04: user_x = 4; user_y = 0; break;
            case R.id.button05: user_x = 5; user_y = 0; break;
            //1st row
            case R.id.button10: user_x = 0; user_y = 1; break;
            case R.id.button11: user_x = 1; user_y = 1; break;
            case R.id.button12: user_x = 2; user_y = 1; break;
            case R.id.button13: user_x = 3; user_y = 1; break;
            case R.id.button14: user_x = 4; user_y = 1; break;
            case R.id.button15: user_x = 5; user_y = 1; break;
            //second row
            case R.id.button20: user_x = 0; user_y = 2; break;
            case R.id.button21: user_x = 1; user_y = 2; break;
            case R.id.button22: user_x = 2; user_y = 2; break;
            case R.id.button23: user_x = 3; user_y = 2; break;
            case R.id.button24: user_x = 4; user_y = 2; break;
            case R.id.button25: user_x = 5; user_y = 2; break;
            //3rd row
            case R.id.button30: user_x = 0; user_y = 3; break;
            case R.id.button31: user_x = 1; user_y = 3; break;
            case R.id.button32: user_x = 2; user_y = 3; break;
            case R.id.button33: user_x = 3; user_y = 3; break;
            case R.id.button34: user_x = 4; user_y = 3; break;
            case R.id.button35: user_x = 5; user_y = 3; break;
            //4th row
            case R.id.button40: user_x = 0; user_y = 4; break;
            case R.id.button41: user_x = 1; user_y = 4; break;
            case R.id.button42: user_x = 2; user_y = 4; break;
            case R.id.button43: user_x = 3; user_y = 4; break;
            case R.id.button44: user_x = 4; user_y = 4; break;
            case R.id.button45: user_x = 5; user_y = 4; break;
            //5th row
            case R.id.button50: user_x = 0; user_y = 5; break;
            case R.id.button51: user_x = 1; user_y = 5; break;
            case R.id.button52: user_x = 2; user_y = 5; break;
            case R.id.button53: user_x = 3; user_y = 5; break;
            case R.id.button54: user_x = 4; user_y = 5; break;
            case R.id.button55: user_x = 5; user_y = 5;
        }

        //check for human turn
        if((gameMode == 0 || current_team == 'b')) {
            //check if tile is open
            if (gameboard[user_y][user_x] == 'o') {
                blocksOccupied++;
                Button button = (Button) findViewById(view.getId());
                if (current_team == 'b') {
                    button.setBackgroundColor(Color.BLUE);
                    gameboard[user_y][user_x] = 'b';
                    blueScore += values[user_y][user_x];
                } else {
                    button.setBackgroundColor(Color.GREEN);
                    gameboard[user_y][user_x] = 'g';
                    greenScore += values[user_y][user_x];
                }
                int buttonID;
                //check for neighbors
                if ((user_y > 0 && gameboard[user_y - 1][user_x] == current_team) || (user_y < GAME_DIMENSION - 1 && gameboard[user_y + 1][user_x] == current_team) || (user_x > 0 && gameboard[user_y][user_x - 1] == current_team) || (user_x < GAME_DIMENSION - 1 && gameboard[user_y][user_x + 1] == current_team)) {
                    if (user_y > 0 && gameboard[user_y - 1][user_x] == opponent) {
                        gameboard[user_y - 1][user_x] = current_team;
                        //String buttonId = "button"+user_y+user_x;
                        buttonID = getResources().getIdentifier("button" + (user_y - 1) + user_x, "id", getPackageName());
                        button = (Button) findViewById(buttonID);
                        if (current_team == 'b') {
                            button.setBackgroundColor(Color.BLUE);
                            blueScore += values[user_y - 1][user_x];
                            greenScore -= values[user_y - 1][user_x];
                        } else {
                            button.setBackgroundColor(Color.GREEN);
                            greenScore += values[user_y - 1][user_x];
                            blueScore -= values[user_y - 1][user_x];
                        }

                    }
                    if (user_y < GAME_DIMENSION - 1 && gameboard[user_y + 1][user_x] == opponent) {
                        gameboard[user_y + 1][user_x] = current_team;
                        buttonID = getResources().getIdentifier("button" + (user_y + 1) + user_x, "id", getPackageName());
                        button = (Button) findViewById(buttonID);
                        if (current_team == 'b') {
                            button.setBackgroundColor(Color.BLUE);
                            blueScore += values[user_y + 1][user_x];
                            greenScore -= values[user_y + 1][user_x];
                        } else {
                            button.setBackgroundColor(Color.GREEN);
                            greenScore += values[user_y + 1][user_x];
                            blueScore -= values[user_y + 1][user_x];
                        }
                    }
                    if (user_x > 0 && gameboard[user_y][user_x - 1] == opponent) {
                        gameboard[user_y][user_x - 1] = current_team;
                        buttonID = getResources().getIdentifier("button" + (user_y) + (user_x - 1), "id", getPackageName());
                        button = (Button) findViewById(buttonID);
                        if (current_team == 'b') {
                            button.setBackgroundColor(Color.BLUE);
                            blueScore += values[user_y][user_x - 1];
                            greenScore -= values[user_y][user_x - 1];
                        } else {
                            button.setBackgroundColor(Color.GREEN);
                            greenScore += values[user_y][user_x - 1];
                            blueScore -= values[user_y][user_x - 1];
                        }
                    }
                    if (user_x < GAME_DIMENSION - 1 && gameboard[user_y][user_x + 1] == opponent) {
                        gameboard[user_y][user_x + 1] = current_team;
                        buttonID = getResources().getIdentifier("button" + (user_y) + (user_x + 1), "id", getPackageName());
                        button = (Button) findViewById(buttonID);
                        if (current_team == 'b') {
                            button.setBackgroundColor(Color.BLUE);
                            blueScore += values[user_y][user_x + 1];
                            greenScore -= values[user_y][user_x + 1];
                        } else {
                            button.setBackgroundColor(Color.GREEN);
                            greenScore += values[user_y][user_x + 1];
                            blueScore -= values[user_y][user_x + 1];
                        }
                    }
                }
                TextView blueScoreField = (TextView) findViewById(R.id.blueScoreField);
                TextView greenScoreField = (TextView) findViewById(R.id.greenScoreField);
                blueScoreField.setText("Blue: " + blueScore);
                greenScoreField.setText("Green: " + greenScore);
                //toggle teams
                if (current_team == 'b') {
                    current_team = 'g';
                    opponent = 'b';
                } else {
                    current_team = 'b';
                    opponent = 'g';
                }
                //handle end of game message
                if (blocksOccupied == GAME_DIMENSION * GAME_DIMENSION) {
                    TextView gameOverField = (TextView) findViewById(R.id.gameOverField);
                    if (blueScore > greenScore) {
                        gameOverField.setTextColor(Color.BLUE);
                        gameOverField.setText("Blue Wins!!");
                    } else if (blueScore < greenScore) {
                        gameOverField.setTextColor(Color.GREEN);
                        gameOverField.setText("Green Wins!!");
                    } else {
                        gameOverField.setTextColor(Color.RED);
                        gameOverField.setText("It's a Tie!");
                    }
                    Toast.makeText(getApplicationContext(), "Tap RESTART to play again!", Toast.LENGTH_LONG).show();
                }
            } else if(blocksOccupied < GAME_DIMENSION*GAME_DIMENSION){
                Toast.makeText(getApplicationContext(), "Tap an unoccupied tile", Toast.LENGTH_SHORT).show();
            }
        }
        /*
        try {
            Thread.sleep(1000);                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        */
        //AI turn
        if(gameMode == 1 && current_team == 'g' && blocksOccupied < GAME_DIMENSION*GAME_DIMENSION)
        {
            //MAKE COPY EACH TIME
            char[][] gamecopy = new char[GAME_DIMENSION][];
            for (int ii = 0; ii < GAME_DIMENSION; ii++) {
                gamecopy[ii] = Arrays.copyOf(gameboard[ii], GAME_DIMENSION);
            }
            int[] bestLocations = new int[2];
            maxSearch(gamecopy, bestLocations, 1, 10000);
            best_x = bestLocations[0];
            best_y = bestLocations[1];

            blocksOccupied++;
            int buttonID = getResources().getIdentifier("button" + best_y + best_x, "id", getPackageName());
            Button button = (Button) findViewById(buttonID);
            button.setBackgroundColor(Color.GREEN);
            gameboard[best_y][best_x] = 'g';
            greenScore += values[best_y][best_x];

            //check for neighbors
            if ((best_y > 0 && gameboard[best_y - 1][best_x] == current_team) || (best_y < GAME_DIMENSION - 1 && gameboard[best_y + 1][best_x] == current_team) || (best_x > 0 && gameboard[best_y][best_x - 1] == current_team) || (best_x < GAME_DIMENSION - 1 && gameboard[best_y][best_x + 1] == current_team)) {
                if (best_y > 0 && gameboard[best_y - 1][best_x] == opponent) {
                    gameboard[best_y - 1][best_x] = current_team;
                    //String buttonId = "button"+best_y+best_x;
                    buttonID = getResources().getIdentifier("button" + (best_y - 1) + best_x, "id", getPackageName());
                    button = (Button) findViewById(buttonID);
                    button.setBackgroundColor(Color.GREEN);
                    greenScore += values[best_y - 1][best_x];
                    blueScore -= values[best_y - 1][best_x];
                }
                if (best_y < GAME_DIMENSION - 1 && gameboard[best_y + 1][best_x] == opponent) {
                    gameboard[best_y + 1][best_x] = current_team;
                    buttonID = getResources().getIdentifier("button" + (best_y + 1) + best_x, "id", getPackageName());
                    button = (Button) findViewById(buttonID);
                    button.setBackgroundColor(Color.GREEN);
                    greenScore += values[best_y + 1][best_x];
                    blueScore -= values[best_y + 1][best_x];
                }
                if (best_x > 0 && gameboard[best_y][best_x - 1] == opponent) {
                    gameboard[best_y][best_x - 1] = current_team;
                    buttonID = getResources().getIdentifier("button" + (best_y) + (best_x - 1), "id", getPackageName());
                    button = (Button) findViewById(buttonID);
                    button.setBackgroundColor(Color.GREEN);
                    greenScore += values[best_y][best_x - 1];
                    blueScore -= values[best_y][best_x - 1];
                }
                if (best_x < GAME_DIMENSION - 1 && gameboard[best_y][best_x + 1] == opponent) {
                    gameboard[best_y][best_x + 1] = current_team;
                    buttonID = getResources().getIdentifier("button" + (best_y) + (best_x + 1), "id", getPackageName());
                    button = (Button) findViewById(buttonID);
                    button.setBackgroundColor(Color.GREEN);
                    greenScore += values[best_y][best_x + 1];
                    blueScore -= values[best_y][best_x + 1];
                }
            }
            TextView blueScoreField = (TextView) findViewById(R.id.blueScoreField);
            TextView greenScoreField = (TextView) findViewById(R.id.greenScoreField);
            blueScoreField.setText("Blue: " + blueScore);
            greenScoreField.setText("Green: " + greenScore);
            current_team = 'b';
            opponent = 'g';
            //handle end of game message
            if (blocksOccupied == GAME_DIMENSION * GAME_DIMENSION) {
                TextView gameOverField = (TextView) findViewById(R.id.gameOverField);
                if (blueScore > greenScore) {
                    gameOverField.setTextColor(Color.BLUE);
                    gameOverField.setText("Blue Wins!!");
                } else if (blueScore < greenScore) {
                    gameOverField.setTextColor(Color.GREEN);
                    gameOverField.setText("Green Wins!!");
                } else {
                    gameOverField.setTextColor(Color.RED);
                    gameOverField.setText("It's a Tie!");
                }
                Toast.makeText(getApplicationContext(), "Tap RESTART to play again!", Toast.LENGTH_LONG).show();
            }
        }
    }
    //performs max operation of the minimax algorithm
    public int maxSearch(char[][] game_board, int[] location, int depth, int upperLimit){
        int best = -1000; //best value (in this case lowest value) so far is held here
        int best_evaluation = -1000; //used for evaluation function

        for (int i = 0; i < GAME_DIMENSION; i++)
        {
            for (int j = 0; j < GAME_DIMENSION; j++)
            {
                //check if we have reached a terminal node
                if (blocksOccupied + depth - 1 == GAME_DIMENSION*GAME_DIMENSION - 1)
                {
                    if (game_board[i][j] == 'o')
                    {
                        //set location values which will be sent back
                        location[0] = j;
                        location[1] = i;
                        int utility = values[i][j];
                        //check for neighbors
                        if ((i > 0 && game_board[i - 1][j] == current_team) || (i < GAME_DIMENSION - 1 && game_board[i + 1][j] == current_team) || (j > 0 && game_board[i][j - 1] == current_team) || (j < GAME_DIMENSION - 1 && game_board[i][j + 1] == current_team))
                        {
                            if (i > 0 && game_board[i - 1][j] == opponent)
                            {
                                utility += values[i - 1][j] * 2;
                            }
                            if (i < GAME_DIMENSION - 1 && game_board[i + 1][j] == opponent)
                            {
                                utility += values[i + 1][j] * 2;
                            }
                            if (j > 0 && game_board[i][j - 1] == opponent)
                            {
                                utility += values[i][j - 1] * 2;
                            }
                            if (j < GAME_DIMENSION - 1 && game_board[i][j + 1] == opponent)
                            {
                                utility += values[i][j + 1] * 2;
                            }
                        }
                        return utility;
                    }
                }
                //not at terminal node and depth limit not reached
                else if (depth < CPU_DEPTH_LIMIT || GAME_DIMENSION*GAME_DIMENSION - blocksOccupied < CPU_END_LIMIT)
                {
                    int local_best;
                    int[] loc = new int[2]; //used to hold return value
                    if (game_board[i][j] == 'o')
                    {
                        //MAKE COPY EACH TIME
                        char[][] copy = new char[GAME_DIMENSION][];
                        for (int ii = 0; ii < GAME_DIMENSION; ii++) {
                            copy[ii] = Arrays.copyOf(game_board[ii], GAME_DIMENSION);
                        }

                        //perform para drop
                        copy[i][j] = current_team;
                        //check for neighbors
                        if ((i > 0 && copy[i - 1][j] == current_team) || (i < GAME_DIMENSION - 1 && copy[i + 1][j] == current_team) || (j > 0 && copy[i][j - 1] == current_team) || (j < GAME_DIMENSION - 1 && copy[i][j + 1] == current_team))
                        {
                            if (i > 0 && copy[i - 1][j] == opponent)
                            {
                                copy[i - 1][j] = current_team;
                            }
                            if (i < GAME_DIMENSION - 1 && copy[i + 1][j] == opponent)
                            {
                                copy[i + 1][j] = current_team;
                            }
                            if (j > 0 && copy[i][j - 1] == opponent)
                            {
                                copy[i][j - 1] = current_team;
                            }
                            if (j < GAME_DIMENSION - 1 && copy[i][j + 1] == opponent)
                            {
                                copy[i][j + 1] = current_team;
                            }
                        }
                        local_best = minSearch(copy, loc, depth + 1, best);

                        //update best location if necessary
                        if (local_best > best) {
                            best = local_best;
                            location[0] = loc[0];
                            location[1] = loc[1];
                        }
                        if (local_best >= upperLimit) {
                            return local_best;
                        }
                    }
                }
			/*perform evaluation function since depth limit reached*/
                else if (depth == CPU_DEPTH_LIMIT)
                {
                    int max_total = 0;
                    int min_total = 0;
                    if (game_board[i][j] == 'o')
                    {
                        int local_best;
                        //MAKE COPY EACH TIME
                        char[][] copy = new char[GAME_DIMENSION][];
                        for (int ii = 0; ii < GAME_DIMENSION; ii++) {
                            copy[ii] = Arrays.copyOf(game_board[ii], GAME_DIMENSION);
                        }
                        //perform para drop
                        copy[i][j] = current_team;
                        //check for neighbors
                        if ((i > 0 && copy[i - 1][j] == current_team) || (i < GAME_DIMENSION - 1 && copy[i + 1][j] == current_team) || (j > 0 && copy[i][j - 1] == current_team) || (j < GAME_DIMENSION - 1 && copy[i][j + 1] == current_team))
                        {
                            if (i > 0 && copy[i - 1][j] == opponent)
                            {
                                copy[i - 1][j] = current_team;
                            }
                            if (i < GAME_DIMENSION - 1 && copy[i + 1][j] == opponent)
                            {
                                copy[i + 1][j] = current_team;
                            }
                            if (j > 0 && copy[i][j - 1] == opponent)
                            {
                                copy[i][j - 1] = current_team;
                            }
                            if (j < GAME_DIMENSION - 1 && copy[i][j + 1] == opponent)
                            {
                                copy[i][j + 1] = current_team;
                            }
                        }
					/*add up all current values on board of max_team and min_team and compute the difference*/
                        for (int k = 0; k < GAME_DIMENSION; k++)
                        {
                            for (int m = 0; m < GAME_DIMENSION; m++)
                            {
                                if (copy[k][m] == opponent)
                                {
                                    max_total += values[k][m];
                                }
                                else if (copy[k][m] == current_team)
                                {
                                    min_total += values[k][m];
                                }
                            }
                        }
					/*we want to maximize the difference*/
                        local_best = max_total - min_total;
                        if (local_best > best_evaluation) {
                            best_evaluation = local_best;
                            location[0] = j;
                            location[1] = i;
                        }
                        if (local_best >= upperLimit) {
                            return local_best;
                        }
                    }
                }
            }
        }
        if (depth < CPU_DEPTH_LIMIT || GAME_DIMENSION*GAME_DIMENSION - blocksOccupied < CPU_END_LIMIT)
        {
            return best;
        }
        else
        {
            return best_evaluation;
        }

    }
    //performs min operation of the minimax algorithm
    public int minSearch(char[][] game_board, int[] location, int depth, int lowerLimit){
        int best = 1000; //best value (in this case lowest value) so far is held here
        int best_evaluation = 1000; //used for evaluation function

        for (int i = 0; i < GAME_DIMENSION; i++)
        {
            for (int j = 0; j < GAME_DIMENSION; j++)
            {
                //check if we have reached a terminal node
                if (blocksOccupied + depth - 1 == GAME_DIMENSION*GAME_DIMENSION - 1)
                {
                    if (game_board[i][j] == 'o')
                    {
                        //set location values which will be sent back
                        location[0] = j;
                        location[1] = i;
                        int utility = values[i][j];
                        //check for neighbors
                        if ((i > 0 && game_board[i - 1][j] == opponent) || (i < GAME_DIMENSION - 1 && game_board[i + 1][j] == opponent) || (j > 0 && game_board[i][j - 1] == opponent) || (j < GAME_DIMENSION - 1 && game_board[i][j + 1] == opponent))
                        {
                            if (i > 0 && game_board[i - 1][j] == current_team)
                            {
                                utility += values[i - 1][j] * 2;
                            }
                            if (i < GAME_DIMENSION - 1 && game_board[i + 1][j] == current_team)
                            {
                                utility += values[i + 1][j] * 2;
                            }
                            if (j > 0 && game_board[i][j - 1] == current_team)
                            {
                                utility += values[i][j - 1] * 2;
                            }
                            if (j < GAME_DIMENSION - 1 && game_board[i][j + 1] == current_team)
                            {
                                utility += values[i][j + 1] * 2;
                            }
                        }
                        return utility;
                    }
                }
                //not at terminal node and depth limit not reached
                else if (depth < CPU_DEPTH_LIMIT || GAME_DIMENSION*GAME_DIMENSION - blocksOccupied < CPU_END_LIMIT)
                {
                    int local_best;
                    int[] loc = new int[2]; //used to hold return value
                    if (game_board[i][j] == 'o')
                    {
                        //MAKE COPY EACH TIME
                        char[][] copy = new char[GAME_DIMENSION][];
                        for (int ii = 0; ii < GAME_DIMENSION; ii++) {
                            copy[ii] = Arrays.copyOf(game_board[ii], GAME_DIMENSION);
                        }
                        //perform para drop
                        copy[i][j] = opponent;
                        //check for neighbors
                        if ((i > 0 && copy[i - 1][j] == opponent) || (i < GAME_DIMENSION - 1 && copy[i + 1][j] == opponent) || (j > 0 && copy[i][j - 1] == opponent) || (j < GAME_DIMENSION - 1 && copy[i][j + 1] == opponent))
                        {
                            if (i > 0 && copy[i - 1][j] == current_team)
                            {
                                copy[i - 1][j] = opponent;
                            }
                            if (i < GAME_DIMENSION - 1 && copy[i + 1][j] == current_team)
                            {
                                copy[i + 1][j] = opponent;
                            }
                            if (j > 0 && copy[i][j - 1] == current_team)
                            {
                                copy[i][j - 1] = opponent;
                            }
                            if (j < GAME_DIMENSION - 1 && copy[i][j + 1] == current_team)
                            {
                                copy[i][j + 1] = opponent;
                            }
                        }
                        local_best = maxSearch(copy, loc, depth + 1, best);

                        //update best location if necessary
                        if (local_best < best) {
                            best = local_best;
                            location[0] = loc[0];
                            location[1] = loc[1];
                        }
                        if (local_best <= lowerLimit) {
                            return local_best;
                        }
                    }
                }
			/*perform evaluation function since depth limit reached*/
                else if (depth == CPU_DEPTH_LIMIT)
                {
                    int max_total = 0;
                    int min_total = 0;
                    if (game_board[i][j] == 'o')
                    {
                        int local_best;
                        //MAKE COPY EACH TIME
                        char[][] copy = new char[GAME_DIMENSION][];
                        for (int ii = 0; ii < GAME_DIMENSION; ii++) {
                            copy[ii] = Arrays.copyOf(game_board[ii], GAME_DIMENSION);
                        }
                        //perform para drop
                        copy[i][j] = opponent;
                        //check for neighbors
                        if ((i > 0 && copy[i - 1][j] == opponent) || (i < GAME_DIMENSION - 1 && copy[i + 1][j] == opponent) || (j > 0 && copy[i][j - 1] == opponent) || (j < GAME_DIMENSION - 1 && copy[i][j + 1] == opponent))
                        {
                            if (i > 0 && copy[i - 1][j] == current_team)
                            {
                                copy[i - 1][j] = opponent;
                            }
                            if (i < GAME_DIMENSION - 1 && copy[i + 1][j] == current_team)
                            {
                                copy[i + 1][j] = opponent;
                            }
                            if (j > 0 && copy[i][j - 1] == current_team)
                            {
                                copy[i][j - 1] = opponent;
                            }
                            if (j < GAME_DIMENSION - 1 && copy[i][j + 1] == current_team)
                            {
                                copy[i][j + 1] = opponent;
                            }
                        }
					/*add up all current values on board of max_team and min_team and compute the difference*/
                        for (int k = 0; k < GAME_DIMENSION; k++)
                        {
                            for (int m = 0; m < GAME_DIMENSION; m++)
                            {
                                if (copy[k][m] == current_team)
                                {
                                    max_total += values[k][m];
                                }
                                else if (copy[k][m] == opponent)
                                {
                                    min_total += values[k][m];
                                }
                            }
                        }
					/*we want to minimize the difference*/
                        local_best = max_total - min_total;
                        if (local_best < best_evaluation) {
                            best_evaluation = local_best;
                            location[0] = j;
                            location[1] = i;
                        }
                        if (local_best <= lowerLimit) {
                            return local_best;
                        }
                    }
                }
            }
        }
        if (depth < CPU_DEPTH_LIMIT || GAME_DIMENSION*GAME_DIMENSION - blocksOccupied < CPU_END_LIMIT)
        {
            return best;
        }
        else
        {
            return best_evaluation;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        GameReset();

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.caleb.wargame/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.caleb.wargame/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
