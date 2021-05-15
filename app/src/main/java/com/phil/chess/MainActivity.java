package com.phil.chess;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    MyCanvas my_canvas_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        my_canvas_ = new MyCanvas(this);
        my_canvas_.setBackgroundColor(Color.BLACK);
        setContentView(my_canvas_);
    }
}
