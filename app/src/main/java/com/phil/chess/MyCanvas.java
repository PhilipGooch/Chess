package com.phil.chess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import static java.lang.Math.abs;
import static java.lang.Math.exp;

public class MyCanvas extends View {

    char player;


    boolean replay = false;

    int move = 0;
    int move_count = 0;

    Point selected_square;

    Paint white_paint;
    Paint black_paint;
    Paint selected_paint;
    Paint legal_paint;
    Paint replay_paint;

    Bitmap black_king;
    Bitmap black_queen;
    Bitmap black_rook;
    Bitmap black_bishop;
    Bitmap black_knight;
    Bitmap black_pawn;
    Bitmap white_king;
    Bitmap white_queen;
    Bitmap white_rook;
    Bitmap white_bishop;
    Bitmap white_knight;
    Bitmap white_pawn;

    Bitmap left_arrow;
    Bitmap right_arrow;

    float square_size;
    ArrayList<Square[][]> board_states;

    public MyCanvas(Context context) {
        super(context);
        player = 'w';
        selected_square = null;
        white_paint = new Paint();
        black_paint = new Paint();
        selected_paint = new Paint();
        replay_paint = new Paint();
        legal_paint = new Paint();
        square_size = 0;
        board_states = new ArrayList<Square[][]>();

        black_king = BitmapFactory.decodeResource(getResources(), R.drawable.black_king);
        black_queen = BitmapFactory.decodeResource(getResources(), R.drawable.black_queen);
        black_rook = BitmapFactory.decodeResource(getResources(), R.drawable.black_rook);
        black_bishop = BitmapFactory.decodeResource(getResources(), R.drawable.black_bishop);
        black_knight = BitmapFactory.decodeResource(getResources(), R.drawable.black_knight);
        black_pawn = BitmapFactory.decodeResource(getResources(), R.drawable.black_pawn);
        white_king = BitmapFactory.decodeResource(getResources(), R.drawable.white_king);
        white_queen = BitmapFactory.decodeResource(getResources(), R.drawable.white_queen);
        white_rook = BitmapFactory.decodeResource(getResources(), R.drawable.white_rook);
        white_bishop = BitmapFactory.decodeResource(getResources(), R.drawable.white_bishop);
        white_knight = BitmapFactory.decodeResource(getResources(), R.drawable.white_knight);
        white_pawn = BitmapFactory.decodeResource(getResources(), R.drawable.white_pawn);

        left_arrow = BitmapFactory.decodeResource(getResources(), R.drawable.left_arrow);
        right_arrow = BitmapFactory.decodeResource(getResources(), R.drawable.right_arrow);

        white_paint.setColor(ContextCompat.getColor(context, R.color.boardSquareLight));
        black_paint.setColor(ContextCompat.getColor(context, R.color.boardSquareDark));
        selected_paint.setColor(ContextCompat.getColor(context, R.color.selectedHighlight));
        legal_paint.setColor(ContextCompat.getColor(context, R.color.legalHighlight));
        replay_paint.setColor(ContextCompat.getColor(context, R.color.replay));

        Square board [][] = new Square[8][8];
        for(int i = 0; i < 8; i++) {
            for( int j = 0; j < 8; j++) {
                board[i][j] = new Square((char)(j + 97), (char)(i + 48), ((i + j) % 2 == 0) ? 'w' : 'b');
            }
        }
//        board[0][0].piece_ = 'k';
//        board[0][0].piece_color_ = 'b';
//        board[0][3].piece_ = 'r';
//        board[0][3].piece_color_ = 'b';

//        board[7][4].piece_ = 'k';
//        board[7][4].piece_color_ = 'w';

        board[7][7].piece_ = 'r';
        board[7][7].piece_color_ = 'w';
        board[0][0].piece_ = 'r';
        board[0][1].piece_ = 'n';
        board[0][2].piece_ = 'b';
        board[0][3].piece_ = 'q';
        board[0][4].piece_ = 'k';
        board[0][5].piece_ = 'b';
        board[0][6].piece_ = 'n';
        board[0][7].piece_ = 'r';
        board[7][0].piece_ = 'r';
        board[7][1].piece_ = 'n';
        board[7][2].piece_ = 'b';
        board[7][3].piece_ = 'q';
        board[7][4].piece_ = 'k';
        board[7][5].piece_ = 'b';
        board[7][6].piece_ = 'n';
        board[7][7].piece_ = 'r';
        for(int i = 0; i < 8; i++) {
           board[1][i].piece_ = 'p';
           board[6][i].piece_ = 'p';
           board[0][i].piece_color_ = 'b';
           board[1][i].piece_color_ = 'b';
           board[6][i].piece_color_ = 'w';
           board[7][i].piece_color_ = 'w';
        }
        board_states.add(copyBoard(board));
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if(e.getAction() == MotionEvent.ACTION_DOWN) {
            float mouse_x = e.getX();
            float mouse_y = e.getY();
            int x = (int) (mouse_x / square_size);
            int y = (int) (mouse_y / square_size);

            if(!replay) {
                if (x < 8 && y < 8) {
                    // If no square is selected
                    if (selected_square == null) {
                        // If there is a piece on the square
                        if (board_states.get(move)[y][x].piece_ != ' ') {
                            // If the piece is the same color as the player who's turn it is
                            if (board_states.get(move)[y][x].piece_color_ == player) {
                                // Select the square
                                selected_square = new Point(x, y);
                            }
                        }
                    }
                    // If a square is already selected
                    else {
                        // If there is no piece on the targeted square
                        if (board_states.get(move)[y][x].piece_ == ' ') {
                            // If the targeted square is a legal move
                            if (board_states.get(move)[y][x].legal_) {
                                movePiece(x, y);
                                swapTurn();
                            }
                        }
                        // If there is a piece on the targeted square
                        else {
                            // If the piece on the targeted square is the same colour as the piece on the selected square
                            if (board_states.get(move)[y][x].piece_color_ == board_states.get(move)[selected_square.y][selected_square.x].piece_color_) {
                                // Select this square instead
                                selected_square = new Point(x, y);
                            }
                            // If the piece on the targeted square is a different colour to the piece on the selected square
                            else {
                                // Take the piece
                                if (board_states.get(move)[y][x].legal_) {
                                    movePiece(x, y);
                                    swapTurn();
                                }
                            }
                        }
                    }
                }
            }
            if(x == 2 && y == 9) {
                if (move > 0) {
                    selected_square = null;
                    replay = true;
                    move--;
                    swapTurn();
                }
            }
            if(replay) {
                if (x == 5 && y == 9) {
                    selected_square = null;
                    if (move < move_count) {
                        move++;
                        swapTurn();
                    }
                    if (move == move_count) {
                        replay = false;
                    }
                }
            }
            setLegalSquares();

            // Re-draw the canvas
            invalidate();
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        square_size = Math.min(w, h) / 8;
        black_king = Bitmap.createScaledBitmap(black_king, (int)square_size, (int)square_size, false);
        black_queen = Bitmap.createScaledBitmap(black_queen, (int)square_size, (int)square_size, false);
        black_rook = Bitmap.createScaledBitmap(black_rook, (int)square_size, (int)square_size, false);
        black_bishop = Bitmap.createScaledBitmap(black_bishop, (int)square_size, (int)square_size, false);
        black_knight = Bitmap.createScaledBitmap(black_knight, (int)square_size, (int)square_size, false);
        black_pawn = Bitmap.createScaledBitmap(black_pawn, (int)square_size, (int)square_size, false);
        white_king = Bitmap.createScaledBitmap(white_king, (int)square_size, (int)square_size, false);
        white_queen = Bitmap.createScaledBitmap(white_queen, (int)square_size, (int)square_size, false);
        white_rook = Bitmap.createScaledBitmap(white_rook, (int)square_size, (int)square_size, false);
        white_bishop = Bitmap.createScaledBitmap(white_bishop, (int)square_size, (int)square_size, false);
        white_knight = Bitmap.createScaledBitmap(white_knight, (int)square_size, (int)square_size, false);
        white_pawn = Bitmap.createScaledBitmap(white_pawn, (int)square_size, (int)square_size, false);
        left_arrow = Bitmap.createScaledBitmap(left_arrow, (int)square_size, (int)square_size, false);
        right_arrow = Bitmap.createScaledBitmap(right_arrow, (int)square_size, (int)square_size, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.d("test", "" + board_states.get(move)[6][5].piece_);

        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                float x = j * square_size;
                float y = i * square_size;
                float x2 = x + square_size;
                float y2 = y + square_size;
                canvas.drawRect(x, y, x2, y2, ((i + j) % 2 == 0) ? white_paint : black_paint);
                if(board_states.get(move)[i][j].legal_) {
                    canvas.drawCircle(x + square_size / 2, y + square_size / 2, square_size / 4, legal_paint);
                }
            }
        }

        if(selected_square != null) {
            canvas.drawRect(selected_square.x * square_size,
                            selected_square.y * square_size,
                           selected_square.x * square_size + square_size,
                         selected_square.y * square_size + square_size,
                                 selected_paint);
        }

        for(int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board_states.get(move)[i][j].piece_ != ' ') {
                    float x = j * square_size;
                    float y = i * square_size;
                    if(board_states.get(move)[i][j].piece_color_ == 'b') {
                        switch(board_states.get(move)[i][j].piece_) {
                            case 'k': canvas.drawBitmap(black_king, x, y, null); break;
                            case 'q': canvas.drawBitmap(black_queen, x, y, null); break;
                            case 'r': canvas.drawBitmap(black_rook, x, y, null); break;
                            case 'b': canvas.drawBitmap(black_bishop, x, y, null); break;
                            case 'n': canvas.drawBitmap(black_knight, x, y, null); break;
                            case 'p': canvas.drawBitmap(black_pawn, x, y, null); break;
                        }
                    }
                    else {
                        switch(board_states.get(move)[i][j].piece_) {
                            case 'k': canvas.drawBitmap(white_king, x, y, null); break;
                            case 'q': canvas.drawBitmap(white_queen, x, y, null); break;
                            case 'r': canvas.drawBitmap(white_rook, x, y, null); break;
                            case 'b': canvas.drawBitmap(white_bishop, x, y, null); break;
                            case 'n': canvas.drawBitmap(white_knight, x, y, null); break;
                            case 'p': canvas.drawBitmap(white_pawn, x, y, null); break;
                        }
                    }
                }
            }
        }

        if(canvas.getWidth() < canvas.getHeight()) {
            canvas.drawBitmap(left_arrow, square_size * 2,canvas.getHeight() - (canvas.getHeight() - square_size * 9), null);
            canvas.drawBitmap(right_arrow, square_size * 5,canvas.getHeight() - (canvas.getHeight() - square_size * 9), null);
        }

        if(replay) {
            canvas.drawCircle(square_size * 0 + square_size / 2, square_size * 8 + square_size / 2, square_size / 4, replay_paint);
        }
    }

    Square[][] copyBoard(Square[][] source) {
        Square new_board [][] = new Square[8][8];
        for(int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                new_board[i][j] = new Square(source[i][j].column_,
                                               source[i][j].row_,
                                               source[i][j].square_color_,
                                               source[i][j].piece_,
                                               source[i][j].piece_color_);
            }
        }
        return new_board;
    }

    void setLegalSquares() {
        int lkj = 0;
        for(int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board_states.get(move)[i][j].legal_ = false;
            }
        }
        if(selected_square == null) {
            return;
        }
        for(int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                            int p = 0;
                if(isLegalMovement(selected_square, new Point(j, i), board_states.get(move))) {
                    if(isPathClear(selected_square, new Point(j, i), board_states.get(move))) {
                        if (board_states.get(move)[i][j].piece_ == ' ' || board_states.get(move)[i][j].piece_color_ != player) {
                            if (!exposeKing(j, i)) {
                                board_states.get(move)[i][j].legal_ = true;
                            }
                        }
                    }
                }
            }
        }
    }

    Point kingPosition(Square [][] board) {
        for(int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(board[i][j].piece_ != ' ') {
                    if (board[i][j].piece_color_ != player) {
                        if (board[i][j].piece_ == 'k') {
                            return new Point(j, i);
                        }
                    }
                }
            }
        }
        return null;
    }

    boolean exposeKing(int x, int y) {
        swapTurn();
        Square test_board[][] = copyBoard(board_states.get(move));
        // This if statement is to handle when castling out of check.
        if(!(x == selected_square.x && y == selected_square.y)) {
            test_board[y][x].piece_ = test_board[selected_square.y][selected_square.x].piece_;
            test_board[y][x].piece_color_ = test_board[selected_square.y][selected_square.x].piece_color_;
            test_board[selected_square.y][selected_square.x].piece_ = ' ';
            test_board[selected_square.y][selected_square.x].piece_color_ = ' ';
        }
        Point king_position = kingPosition(test_board);
        for(int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(test_board[i][j].piece_ != ' ') {
                    if (test_board[i][j].piece_color_ == player) {
                        if (isLegalMovement(new Point(j, i), king_position, test_board)) {
                            if (isPathClear(new Point(j, i), king_position, test_board)) {
                                swapTurn();
                                return true;
                            }
                        }
                    }
                }
            }
        }
        swapTurn();
        return false;
    }

    void movePiece(int x, int y) {
        board_states.add(copyBoard(board_states.get(move)));
        move++;
        move_count++;
        board_states.get(move)[y][x].piece_ = board_states.get(move)[selected_square.y][selected_square.x].piece_;
        board_states.get(move)[y][x].piece_color_ = board_states.get(move)[selected_square.y][selected_square.x].piece_color_;

        // Pawns
        if(board_states.get(move)[selected_square.y][selected_square.x].piece_ == 'p') {
            if (player == 'w') {
                // En-poisson
                if (y + 1 == selected_square.y && (x == selected_square.x - 1 || x == selected_square.x + 1)) {
                    if (board_states.get(move)[y + 1][x].piece_ == 'p') {
                        if (board_states.get(move)[y + 1][x].piece_color_ != player) {
                            if (board_states.get(move - 2)[1][x].piece_ == 'p') {
                                board_states.get(move)[y + 1][x].piece_ = ' ';
                                board_states.get(move)[y + 1][x].piece_color_ = ' ';
                            }
                        }
                    }
                }
                // Promotion
                if (y == 0) {
                    board_states.get(move)[y][x].piece_ = 'q';
                }
            } else if (player == 'b') {
                // En-poisson
                if (y + 1 == selected_square.y && (x == selected_square.x - 1 || x == selected_square.x + 1)) {
                    if (board_states.get(move)[y][x].piece_ == 'p') {
                        if (board_states.get(move)[y][x].piece_color_ != player) {
                            if (board_states.get(move - 2)[6][x].piece_ == 'p') {
                                board_states.get(move)[y][x].piece_ = ' ';
                                board_states.get(move)[y][x].piece_color_ = ' ';
                            }
                        }
                    }
                }
                // Promotion
                if (y == 7) {
                    board_states.get(move)[y][x].piece_ = 'q';
                }
            }
        }

        int po = 0;
        // King
        if(board_states.get(move)[selected_square.y][selected_square.x].piece_ == 'k') {
            if(player == 'w') {
                if(y == 7 && (x == 2 || x == 6)) {
                    if(move_count > 2) {
                        if (board_states.get(move - 2)[7][4].piece_ == 'k') {
                            if (x == 6) {
                                board_states.get(move)[7][5].piece_ = board_states.get(move)[7][7].piece_;
                                board_states.get(move)[7][5].piece_color_ = board_states.get(move)[7][7].piece_color_;
                                board_states.get(move)[7][7].piece_ = ' ';
                                board_states.get(move)[7][7].piece_color_ = ' ';
                            } else if (x == 2) {
                                board_states.get(move)[7][3].piece_ = board_states.get(move)[7][0].piece_;
                                board_states.get(move)[7][3].piece_color_ = board_states.get(move)[7][0].piece_color_;
                                board_states.get(move)[7][0].piece_ = ' ';
                                board_states.get(move)[7][0].piece_color_ = ' ';
                            }
                        }
                    }
                }
            }
            else if (player == 'b') {
                if(y == 0 && (x == 2 || x == 6)) {
                    if(move_count > 2) {
                        if (board_states.get(move - 2)[0][4].piece_ == 'k') {
                            if (x == 6) {
                                board_states.get(move)[0][5].piece_ = board_states.get(move)[0][7].piece_;
                                board_states.get(move)[0][5].piece_color_ = board_states.get(move)[0][7].piece_color_;
                                board_states.get(move)[0][7].piece_ = ' ';
                                board_states.get(move)[0][7].piece_color_ = ' ';
                            } else if (x == 2) {
                                board_states.get(move)[0][3].piece_ = board_states.get(move)[0][0].piece_;
                                board_states.get(move)[0][3].piece_color_ = board_states.get(move)[0][0].piece_color_;
                                board_states.get(move)[0][0].piece_ = ' ';
                                board_states.get(move)[0][0].piece_color_ = ' ';
                            }
                        }
                    }
                }
            }
        }

        board_states.get(move)[selected_square.y][selected_square.x].piece_ = ' ';
        board_states.get(move)[selected_square.y][selected_square.x].piece_color_ = ' ';
        selected_square = null;
    }

    void swapTurn() {
        if(player == 'w') {
            player = 'b';
        }
        else {
            player = 'w';
        }
    }

    boolean canCastle(char side) {
        for(int i = 0; i < board_states.size(); i++) {
            if(player == 'w') {
                if (board_states.get(i)[7][4].piece_ == ' ') {
                    return false;
                }
                if(side == 'k' && board_states.get(i)[7][7].piece_ == ' ') {
                    return false;
                }
                if(side == 'q' && board_states.get(i)[7][0].piece_ == ' ') {
                    return false;
                }
            }
            else if(player == 'b') {
                if (board_states.get(i)[0][4].piece_ == ' ') {
                    return false;
                }
                if(side == 'k' && board_states.get(i)[0][7].piece_ == ' ') {
                    return false;
                }
                if(side == 'q' && board_states.get(i)[0][0].piece_ == ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    boolean isLegalMovement(Point origin, Point target, Square[][] board) {
        switch(board[origin.y][origin.x].piece_) {
            case 'k':
                if ((target.x == origin.x - 1 && (target.y >= origin.y - 1 && target.y <= origin.y + 1))
                        || (target.x == origin.x && target.y == origin.y - 1)
                        || (target.x == origin.x && target.y == origin.y + 1)
                        || (target.x == origin.x + 1 && (target.y >= origin.y - 1 && target.y <= origin.y + 1))) {
                    return true;
                }
                // Castling
                if((target.y == origin.y && target.x == origin.x + 2)) {
                    if (canCastle('k')) {
                        boolean through_check = false;
                        for (int i = 0; i < 3; i++) {
                            if (exposeKing(origin.x + i, origin.y)) {
                                through_check = true;
                            }
                        }
                        if (through_check) {
                            return false;
                        } else return true;
                    }
                }
                if((target.y == origin.y && target.x == origin.x - 2)) {
                    if(canCastle('q')) {
                        boolean through_check = false;
                        for(int i = 0; i < 3; i++) {
                            if(exposeKing(origin.x - i, origin.y)) {
                                through_check = true;
                            }
                        }
                        if(through_check) {
                            return false;
                        }
                        else return true;
                    }
                }
                return false;
            case 'q':
                if ((abs(origin.x - target.x) == abs(origin.y - target.y)) || ((target.x == origin.x || target.y == origin.y) && target != origin)) {
                    return true;
                }
                return false;
            case 'r':
                if ((target.x == origin.x || target.y == origin.y) && target != origin) {
                    return true;
                }
                return false;
            case 'b':
                if (abs(origin.x - target.x) == abs(origin.y - target.y)) {
                    return true;
                }
                return false;
            case 'n':
                if (((target.x == origin.x - 2) && (target.y == origin.y - 1 || target.y == origin.y + 1))
                        || ((target.x == origin.x + 2) && (target.y == origin.y - 1 || target.y == origin.y + 1))
                        || ((target.y == origin.y - 2) && (target.x == origin.x - 1 || target.x == origin.x + 1))
                        || ((target.y == origin.y + 2) && (target.x == origin.x - 1 || target.x == origin.x + 1))) {
                    return true;
                }
                return false;
            case 'p':
                // Taking pieces
                if (player == 'w') {
                    if (target.y == origin.y - 1 && (target.x == origin.x - 1 || target.x == origin.x + 1)) {
                        if (board[target.y][target.x].piece_ != ' ') {
                            return true;
                        }
                    }
                    if(origin.y == 3) {
                        if (target.y == 2 && (target.x == origin.x - 1 || target.x == origin.x + 1)) {
                            if (board[3][target.x].piece_ == 'p') {
                                if (board[3][target.x].piece_color_ != player) {
                                    if (board_states.get(move - 2)[1][target.x].piece_ == 'p') {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
                else if (player == 'b') {
                    if (target.y == origin.y + 1 && (target.x == origin.x - 1 || target.x == origin.x + 1)) {
                        if (board[target.y][target.x].piece_ != ' ') {
                            return true;
                        }
                    }
                    if(origin.y == 4) {
                        if (target.y == 5 && (target.x == origin.x - 1 || target.x == origin.x + 1)) {
                            if (board[4][target.x].piece_ == 'p') {
                                if (board[4][target.x].piece_color_ != player) {
                                    if (board_states.get(move - 2)[6][target.x].piece_ == 'p') {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
                // Movement
                if (target.x == origin.x) {
                    if(board[target.y][target.x].piece_ == ' ') {
                        if (board[origin.y][origin.x].piece_color_ == 'w') {
                            if (origin.y == 6 && target.y == 4) {
                                return true;
                            } else if (target.y == origin.y - 1) {
                                return true;
                            }
                        } else if (board[origin.y][origin.x].piece_color_ == 'b') {
                            if (origin.y == 1 && target.y == 3) {
                                return true;
                            } else if (target.y == origin.y + 1) {
                                return true;
                            }
                        }
                    }
                }
                return false;
        }
        return false;
    }

    boolean isPathClear(Point origin, Point target, Square[][] board) {
        Point next;
        switch(board[origin.y][origin.x].piece_) {
            case 'k':
                if (target.x == origin.x) {
                    if (target.y < origin.y) {
                        for (int i = origin.y - 1; i > target.y; i--) {
                            if (board[i][origin.x].piece_ != ' ') {
                                return false;
                            }
                        }
                    }
                    else if (target.y > origin.y) {
                        for (int i = origin.y + 1; i < target.y; i++) {
                            if (board[i][origin.x].piece_ != ' ') {
                                return false;
                            }
                        }
                    }
                }
                else if (target.y == origin.y) {
                    if (target.x < origin.x) {
                        for (int i = origin.x - 1; i > target.x; i--) {
                            if (board[origin.y][i].piece_ != ' ') {
                                return false;
                            }
                        }
                    }
                    else if (target.x > origin.x) {
                        for (int i = origin.x + 1; i < target.x; i++) {
                            if (board[origin.y][i].piece_ != ' ') {
                                return false;
                            }
                        }
                    }
                }
                return true;
            case 'q':
                next = new Point();
                if (target.x < origin.x && target.y < origin.y) {
                    next.x = origin.x - 1;
                    next.y = origin.y - 1;
                    while (next.x > target.x && next.y > target.y) {
                        if (board[next.y][next.x].piece_ != ' ') {
                            return false;
                        }
                        next.x--;
                        next.y--;
                    }
                }
                else if (target.x > origin.x && target.y < origin.y) {
                    next.x = origin.x + 1;
                    next.y = origin.y - 1;
                    while (next.x < target.x && next.y > target.y) {
                        if (board[next.y][next.x].piece_ != ' ') {
                            return false;
                        }
                        next.x++;
                        next.y--;
                    }
                }
                else if (target.x < origin.x && target.y > origin.y) {
                    next.x = origin.x - 1;
                    next.y = origin.y + 1;
                    while (next.x > target.x && next.y < target.y) {
                        if (board[next.y][next.x].piece_ != ' ') {
                            return false;
                        }
                        next.x--;
                        next.y++;
                    }
                }
                else if (target.x > origin.x && target.y > origin.y) {
                    next.x = origin.x + 1;
                    next.y = origin.y + 1;
                    while (next.x < target.x && next.y < target.y) {
                        if (board[next.y][next.x].piece_ != ' ') {
                            return false;
                        }
                        next.x++;
                        next.y++;
                    }
                }
                else if (target.x == origin.x) {
                    if (target.y < origin.y) {
                        for (int i = origin.y - 1; i > target.y; i--) {
                            if (board[i][origin.x].piece_ != ' ') {
                                return false;
                            }
                        }
                    }
                    else if (target.y > origin.y) {
                        for (int i = origin.y + 1; i < target.y; i++) {
                            if (board[i][origin.x].piece_ != ' ') {
                                return false;
                            }
                        }
                    }
                }
                else if (target.y == origin.y) {
                    if (target.x < origin.x) {
                        for (int i = origin.x - 1; i > target.x; i--) {
                            if (board[origin.y][i].piece_ != ' ') {
                                return false;
                            }
                        }
                    }
                    else if (target.x > origin.x) {
                        for (int i = origin.x + 1; i < target.x; i++) {
                            if (board[origin.y][i].piece_ != ' ') {
                                return false;
                            }
                        }
                    }
                }
                return true;
            case 'r':
                if (target.x == origin.x) {
                    if (target.y < origin.y) {
                        for (int i = origin.y - 1; i > target.y; i--) {
                            if (board[i][origin.x].piece_ != ' ') {
                                return false;
                            }
                        }
                    }
                    else if (target.y > origin.y) {
                        for (int i = origin.y + 1; i < target.y; i++) {
                            if (board[i][origin.x].piece_ != ' ') {
                                return false;
                            }
                        }
                    }
                }
                else if (target.y == origin.y) {
                    if (target.x < origin.x) {
                        for (int i = origin.x - 1; i > target.x; i--) {
                            if (board[origin.y][i].piece_ != ' ') {
                                return false;
                            }
                        }
                    }
                    else if (target.x > origin.x) {
                        for (int i = origin.x + 1; i < target.x; i++) {
                            if (board[origin.y][i].piece_ != ' ') {
                                return false;
                            }
                        }
                    }
                }
                return true;
            case 'b':
                next = new Point();
                if (target.x < origin.x && target.y < origin.y) {
                    next.x = origin.x - 1;
                    next.y = origin.y - 1;
                    while (next.x > target.x && next.y > target.y) {
                        if (board[next.y][next.x].piece_ != ' ') {
                            return false;
                        }
                        next.x--;
                        next.y--;
                    }
                }
                else if (target.x > origin.x && target.y < origin.y) {
                    next.x = origin.x + 1;
                    next.y = origin.y - 1;
                    while (next.x < target.x && next.y > target.y) {
                        if (board[next.y][next.x].piece_ != ' ') {
                            return false;
                        }
                        next.x++;
                        next.y--;
                    }
                }
                else if (target.x < origin.x && target.y > origin.y) {
                    next.x = origin.x - 1;
                    next.y = origin.y + 1;
                    while (next.x > target.x && next.y < target.y) {
                        if (board[next.y][next.x].piece_ != ' ') {
                            return false;
                        }
                        next.x--;
                        next.y++;
                    }
                }
                else if (target.x > origin.x && target.y > origin.y) {
                    next.x = origin.x + 1;
                    next.y = origin.y + 1;
                    while (next.x < target.x && next.y < target.y) {
                        if (board[next.y][next.x].piece_ != ' ') {
                            return false;
                        }
                        next.x++;
                        next.y++;
                    }
                }
                return true;
            case 'n':
                return true;
            case 'p':
                if (target.x == origin.x) {
                    if (target.y < origin.y) {
                        for (int i = origin.y - 1; i > target.y; i--) {
                            if (board[i][origin.x].piece_ != ' ') {
                                return false;
                            }
                        }
                    }
                    else if (target.y > origin.y) {
                        for (int i = origin.y + 1; i < target.y; i++) {
                            if (board[i][origin.x].piece_ != ' ') {
                                return false;
                            }
                        }
                    }
                }
                return true;
        }
        return false;
    }
}
