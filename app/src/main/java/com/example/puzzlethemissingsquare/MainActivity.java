package com.example.puzzlethemissingsquare;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    GridLayout gameGrid;
    TextView[][] tiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createBoard(4);

        SeekBar seekBar = findViewById(R.id.seekBar);

        int progress = seekBar.getProgress();
        TextView tvProgressLabel = findViewById(R.id.textView);
        tvProgressLabel.setText("Size: " + progress);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvProgressLabel.setText("Size: " + progress);
                createBoard(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @SuppressLint("DefaultLocale")
    void createBoard(int size) {
        gameGrid = findViewById(R.id.game_grid);

        tiles = new TextView[size][size];

        String[] tiles1D = new String[size * size];
        for (int i = 0; i < tiles1D.length - 1; i++)
            tiles1D[i] = String.format("%02d", i + 1);
        tiles1D[tiles1D.length - 1] = "";

        randomize(tiles1D, tiles1D.length);

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                tiles[i][j] = new TextView(this);

                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();

                layoutParams.setMargins(
                        (int) convertDpToPixel(10f / size, this),
                        (int) convertDpToPixel(10f / size, this),
                        (int) convertDpToPixel(10f / size, this),
                        (int) convertDpToPixel(10f / size, this));
                layoutParams.setGravity(Gravity.FILL);
                layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                layoutParams.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);

                tiles[i][j].setLayoutParams(layoutParams);
                tiles[i][j].setGravity(Gravity.CENTER);

                if (tiles1D[i * size + j].equals("")) {
                    tiles[i][j].setText("");
                    tiles[i][j].setBackground(ContextCompat.getDrawable(this, R.drawable.empty_tile));
                } else {
                    tiles[i][j].setText(tiles1D[i * size + j]);
                    tiles[i][j].setBackground(ContextCompat.getDrawable(this, R.drawable.tile));
                }

                tiles[i][j].setTextSize(120f / (size));
                tiles[i][j].setTextColor(ContextCompat.getColor(this, R.color.purple_500));
                tiles[i][j].setTypeface(tiles[i][j].getTypeface(), Typeface.BOLD);

                int finalI = i;
                int finalJ = j;
                tiles[i][j].setOnClickListener((View view) -> {
                    if (finalJ != 0 && tiles[finalI][finalJ - 1].getText() == "") {
                        String num = tiles[finalI][finalJ - 1].getText().toString();
                        tiles[finalI][finalJ - 1].setText(tiles[finalI][finalJ].getText());
                        tiles[finalI][finalJ].setBackground(ContextCompat.getDrawable(this, R.drawable.empty_tile));
                        tiles[finalI][finalJ].setText(num);
                        tiles[finalI][finalJ - 1].setBackground(ContextCompat.getDrawable(this, R.drawable.tile));
                        renderBoard(size);
                    } else if (finalJ != tiles[finalI].length - 1 && tiles[finalI][finalJ + 1].getText() == "") {
                        String num = tiles[finalI][finalJ + 1].getText().toString();
                        tiles[finalI][finalJ + 1].setText(tiles[finalI][finalJ].getText());
                        tiles[finalI][finalJ].setBackground(ContextCompat.getDrawable(this, R.drawable.empty_tile));
                        tiles[finalI][finalJ].setText(num);
                        tiles[finalI][finalJ + 1].setBackground(ContextCompat.getDrawable(this, R.drawable.tile));
                        renderBoard(size);
                    } else if (finalI != 0 && tiles[finalI - 1][finalJ].getText() == "") {
                        String num = tiles[finalI - 1][finalJ].getText().toString();
                        tiles[finalI - 1][finalJ].setText(tiles[finalI][finalJ].getText());
                        tiles[finalI][finalJ].setBackground(ContextCompat.getDrawable(this, R.drawable.empty_tile));
                        tiles[finalI][finalJ].setText(num);
                        tiles[finalI - 1][finalJ].setBackground(ContextCompat.getDrawable(this, R.drawable.tile));
                        renderBoard(size);
                    } else if (finalI != tiles.length - 1 && tiles[finalI + 1][finalJ].getText() == "") {
                        String num = tiles[finalI + 1][finalJ].getText().toString();
                        tiles[finalI + 1][finalJ].setText(tiles[finalI][finalJ].getText());
                        tiles[finalI][finalJ].setBackground(ContextCompat.getDrawable(this, R.drawable.empty_tile));
                        tiles[finalI][finalJ].setText(num);
                        tiles[finalI + 1][finalJ].setBackground(ContextCompat.getDrawable(this, R.drawable.tile));
                        renderBoard(size);
                    }

                    if (check(size))
                        Toast.makeText(getApplicationContext(), "You Won!", Toast.LENGTH_LONG).show();
                });
            }
        }
        renderBoard(size);
    }

    void renderBoard(int size) {
        gameGrid.removeAllViews();
        gameGrid.setColumnCount(size);

        for (TextView[] tileRow : tiles) {
            for (TextView tile : tileRow) {
                gameGrid.addView(tile);
            }
        }
    }

    @SuppressLint("DefaultLocale")
    boolean check(int size) {
        for (int i = 0; i < tiles.length; i++)
            for (int j = 0; j < tiles[i].length; j++) {
                if (tiles[i][j].getText() == "") continue;
                if (!tiles[i][j].getText().toString().equals(String.format("%02d", i * size + j + 1)))
                    return false;
            }
        return true;
    }

    <T> void randomize(T[] arr, int n) {
        Random r = new Random();
        for (int i = n - 1; i > 0; i--) {
            int j = r.nextInt(i + 1);
            T temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    public static float convertDpToPixel(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}