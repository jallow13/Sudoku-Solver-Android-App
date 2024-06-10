package com.example.mysudokusolver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class SudokuBoard extends View {
    private final int letterColor;
    private final int letterColorSolve;
    private int cellSize;
    private final int boardColor;
    private final int cellFillColor;
    private final int cellsHighlightColor;
    private final Paint boardColorPaint = new Paint();
    private final Paint cellFillColorPaint = new Paint();
    private final Paint cellsHighlightColorPaint = new Paint();

    private final Paint letterColorPaint = new Paint();
    private final Rect letterPaintBounds = new Rect();
    private final Solver solver = new Solver();

    public SudokuBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SudokuBoard, 0, 0);

        try {
            boardColor = typedArray.getInteger(R.styleable.SudokuBoard_boardColor, 0);
            cellFillColor = typedArray.getInteger(R.styleable.SudokuBoard_cellFillColor, 0);
            cellsHighlightColor = typedArray.getInteger(R.styleable.SudokuBoard_cellsHighlightColor, 0);

            letterColor = typedArray.getInteger(R.styleable.SudokuBoard_letterColor, 0);
            letterColorSolve = typedArray.getInteger(R.styleable.SudokuBoard_letterColorSolve, 0);
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int width, int height) {
        super.onMeasure(width, height);
        int dimension = Math.min(this.getMeasuredWidth(), this.getMeasuredHeight());
        cellSize = dimension / 9;
        setMeasuredDimension(dimension, dimension);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        boardColorPaint.setStyle(Paint.Style.STROKE);
        boardColorPaint.setStrokeWidth(16);
        boardColorPaint.setColor(boardColor);
        boardColorPaint.setAntiAlias(true);

        cellFillColorPaint.setStyle(Paint.Style.FILL);
        cellFillColorPaint.setAntiAlias(true);
        cellFillColorPaint.setColor(cellFillColor);

        cellsHighlightColorPaint.setStyle(Paint.Style.FILL);
        cellsHighlightColorPaint.setAntiAlias(true);
        cellsHighlightColorPaint.setColor(cellsHighlightColor);

        letterColorPaint.setStyle(Paint.Style.FILL);
        letterColorPaint.setAntiAlias(true);
        letterColorPaint.setColor(letterColor);

        colorCell(canvas, solver.getSelectedRow(), solver.getSelectedCol());
        canvas.drawRect(0, 0, getWidth(), getHeight(), boardColorPaint);
        drawBoard(canvas);
        drawNumbers(canvas);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean isValid;

        float x = event.getX();
        float y = event.getY();

        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            solver.setSelectedRow((int) Math.ceil(y / cellSize) - 1);
            solver.setSelectedCol((int) Math.ceil(x / cellSize) - 1);
            isValid = true;
        } else {
            isValid = false;
        }

        return isValid;
    }

    private void drawNumbers(Canvas canvas) {
        letterColorPaint.setTextSize(cellSize);

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (solver.getBoard()[r][c] != 0) {
                    String text = Integer.toString(solver.getBoard()[r][c]);
                    float width, height;

                    letterColorPaint.getTextBounds(text, 0, text.length(), letterPaintBounds);
                    width = letterColorPaint.measureText(text);
                    height = letterPaintBounds.height();

                    canvas.drawText(text, c * cellSize + ((cellSize - width) / 2),
                            (r * cellSize + cellSize) - ((cellSize - height) / 2),
                            letterColorPaint);
                }
            }
        }

        letterColorPaint.setColor(letterColorSolve);
        for (ArrayList<Object> letter : solver.getEmptyBoxIndex()) {
            int r = (int) letter.get(0);
            int c = (int) letter.get(1);

            String text = Integer.toString(solver.getBoard()[r][c]);
            float width, height;

            letterColorPaint.getTextBounds(text, 0, text.length(), letterPaintBounds);
            width = letterColorPaint.measureText(text);
            height = letterPaintBounds.height();

            canvas.drawText(text, c * cellSize + ((cellSize - width) / 2),
                    (r * cellSize + cellSize) - ((cellSize - height) / 2),
                    letterColorPaint);
        }
    }

    private void colorCell(Canvas canvas, int r, int c) {
        if (solver.getSelectedCol() != -1 && solver.getSelectedRow() != -1) {
            canvas.drawRect(c * cellSize, 0, (c + 1) * cellSize, cellSize * 9, cellsHighlightColorPaint);
            canvas.drawRect(0, r * cellSize, cellSize * 9, (r + 1) * cellSize, cellsHighlightColorPaint);

            canvas.drawRect(c * cellSize, r * cellSize, (c + 1) * cellSize, (r + 1) * cellSize, cellsHighlightColorPaint);
        }
        invalidate();
    }

    private void drawThinLine() {
        boardColorPaint.setStyle(Paint.Style.STROKE);
        boardColorPaint.setStrokeWidth(4);
        boardColorPaint.setColor(boardColor);
    }

    private void drawThickLine() {
        boardColorPaint.setStyle(Paint.Style.STROKE);
        boardColorPaint.setStrokeWidth(10);
        boardColorPaint.setColor(boardColor);
    }

    private void drawBoard(Canvas canvas) {
        for (int c = 0; c < 10; c++) {
            if (c % 3 == 0) {
                drawThickLine();
            } else {
                drawThinLine();
            }
            canvas.drawLine(cellSize * c, 0, cellSize * c, getWidth(), boardColorPaint);
        }
        for (int r = 0; r < 10; r++) {
            if (r % 3 == 0) {
                drawThickLine();
            } else {
                drawThinLine();
            }
            canvas.drawLine(0, cellSize * r, getWidth(), cellSize * r, boardColorPaint);
        }
    }

    public Solver getSolver() {
        return this.solver;
    }
}
