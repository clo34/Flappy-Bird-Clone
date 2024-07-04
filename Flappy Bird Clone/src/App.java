import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception{
        /*
         * Setting up the Window
         * */
        //Board width and height in pixels
        int boardWidth = 360;
        int boardHeight = 640;

        JFrame frame = new JFrame("Flappy Bird");
        //frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        // When user clicks on 'x' button on windows, it'll close the entire app
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        /*
         * Setting up the Panel so we can draw the game
         * */
        FlappyBird flappyBird = new FlappyBird();
        frame.add(flappyBird);
        //frame.pack excludes the title board in the 360x640 dimension of the panel
        frame.pack();
        flappyBird.requestFocus();
        frame.setVisible(true);
    }
}
