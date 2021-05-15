package com.phil.chess;

import android.graphics.Point;

public class Square {

    char column_;
    char row_;
    char square_color_;
    char piece_;
    char piece_color_;
    boolean legal_;

    Square(char column, char row, char square_color) {
        column_ = column;
        row_ = row;
        square_color_ = square_color;
        piece_ = ' ';
        piece_color_ = ' ';
        legal_ = false;
    }

    Square(char column, char row, char square_color, char piece, char piece_color) {
        column_ = column;
        row_ = row;
        square_color_ = square_color;
        piece_ = piece;
        piece_color_ = piece_color;
        legal_ = false;
    }
}
