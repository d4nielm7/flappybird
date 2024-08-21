import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        int boardWidth = 360;
        int boardHeight = 640;

        JFrame frame = new JFrame("FlappyBird");
        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);//place the window at center of screen
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        flappybird fb = new flappybird();
        frame.add(fb);
        frame.pack();
        fb.requestFocus();
        frame.setVisible(true);
    }
}