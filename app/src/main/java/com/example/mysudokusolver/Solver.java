package com.example.mysudokusolver;

import java.util.ArrayList;

public class Solver {
    int[][] board;
    ArrayList<ArrayList<Object>> emptyBoxIndex;
    int selected_row;
    int selected_col;

    public Solver() {
        selected_row = -1;
        selected_col = -1;

        board = new int[9][9];

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                board[r][c] = 0;
            }
        }
        emptyBoxIndex = new ArrayList<>();
    }

    private void getEmptyBoxIndexes() {
        emptyBoxIndex.clear();
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (this.board[r][c] == 0) {
                    this.emptyBoxIndex.add(new ArrayList<>());
                    this.emptyBoxIndex.get(this.emptyBoxIndex.size() - 1).add(r);
                    this.emptyBoxIndex.get(this.emptyBoxIndex.size() - 1).add(c);
                }
            }
        }
    }

    public void setNumberPosition(int num) {
        if (this.selected_row != -1 && this.selected_col != -1) {
            if (this.board[this.selected_row][this.selected_col] == num) {
                this.board[this.selected_row][this.selected_col] = 0;
            } else {
                this.board[this.selected_row][this.selected_col] = num;
            }
        }
    }

    public int[][] getBoard() {
        return this.board;
    }

    public int getSelectedRow() {
        return selected_row;
    }

    public ArrayList<ArrayList<Object>> getEmptyBoxIndex() {
        return emptyBoxIndex;
    }

    public void setSelectedRow(int selected_row) {
        this.selected_row = selected_row;
    }

    public int getSelectedCol() {
        return selected_col;
    }

    public void setSelectedCol(int selected_col) {
        this.selected_col = selected_col;
    }

    public boolean solveBoard() {
        getEmptyBoxIndexes();
        return solvePartialSudoku(0);
    }

    private boolean solvePartialSudoku(int index) {
        if (index == emptyBoxIndex.size()) {
            return true; // Solved the entire board
        }

        int row = (int) emptyBoxIndex.get(index).get(0);
        int col = (int) emptyBoxIndex.get(index).get(1);

        for (int num = 1; num <= 9; num++) {
            if (isValid(row, col, num)) {
                board[row][col] = num;
                if (solvePartialSudoku(index + 1)) {
                    return true;
                }
                board[row][col] = 0;
            }
        }

        return false;
    }

    private boolean isValid(int row, int col, int num) {
        // Check row
        for (int c = 0; c < 9; c++) {
            if (board[row][c] == num) {
                return false;
            }
        }


        for (int r = 0; r < 9; r++) {
            if (board[r][col] == num) {
                return false;
            }
        }


        int subgridRowStart = (row / 3) * 3;
        int subgridColStart = (col / 3) * 3;
        for (int r = subgridRowStart; r < subgridRowStart + 3; r++) {
            for (int c = subgridColStart; c < subgridColStart + 3; c++) {
                if (board[r][c] == num) {
                    return false;
                }
            }
        }

        return true;
    }
}

