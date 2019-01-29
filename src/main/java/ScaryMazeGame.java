import javax.imageio.ImageIO;
import javax.swing.*;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class ScaryMazeGame extends JComponent implements MouseMotionListener,
        MouseListener {
    final int MAX_GIF_IMGS = 23;
    final int MAX_LEVELS = 3;
    final int LEVEL_TO_SCARE = 3;
    int cur_level;
    boolean is_first_black;
    ArrayList<BufferedImage> gif;
    ArrayList<BufferedImage> steps;
    ArrayList<BufferedImage> intros;
    ArrayList<BufferedImage> intros_masks;
    ArrayList<BufferedImage> white_screens;
    ArrayList<BufferedImage> black_screens;
    ArrayList<BufferedImage> screens_masks;
    BufferedImage gameOver;
    BufferedImage meir;
    BufferedImage currentLevel;
    AudioClip scream = JApplet.newAudioClip(getClass().getResource("scream.aiff"));
    int cur_gif;
    private Timer timerStep = new Timer(3000, new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            currentLevel = intros.get(cur_level);
            timerStep.stop();
            repaint();
        }
    });
    private Timer timerGIF = new Timer(140, new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            cur_gif = (cur_gif + 1) % MAX_GIF_IMGS;
            repaint();
        }
    });
    private Timer timerBlackBGMain = new Timer(2000, new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentLevel == intros.get(cur_level) || currentLevel == gameOver || currentLevel == steps.get(cur_level)) {
                is_first_black = true;
                timerBlackBGMain.stop();
                timerBlackBGMain.restart();
                timerBlackBGHelper.stop();
                timerBlackBGHelper.restart();
            }
            else {
                currentLevel = black_screens.get(cur_level);
                timerBlackBGMain.stop();
                timerBlackBGHelper.restart();
                timerBlackBGHelper.start();
            }
        }
    });
    private Timer timerBlackBGHelper = new Timer(200, new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentLevel == intros.get(cur_level) || currentLevel == gameOver || currentLevel == steps.get(cur_level)) {
                is_first_black = true;
                timerBlackBGMain.stop();
                timerBlackBGMain.restart();
                timerBlackBGHelper.stop();
                timerBlackBGHelper.restart();
            }
            if (currentLevel == black_screens.get(cur_level)) {
                currentLevel = white_screens.get(cur_level);
                if (!is_first_black) {
                    is_first_black = !is_first_black;
                    timerBlackBGHelper.stop();
                    timerBlackBGMain.restart();
                    timerBlackBGMain.start();
                }
            }
            else if (currentLevel == white_screens.get(cur_level)){
                currentLevel = black_screens.get(cur_level);
                is_first_black = !is_first_black;
            }
            else {
                timerBlackBGMain.stop();
                timerBlackBGMain.restart();
                timerBlackBGHelper.stop();
                timerBlackBGHelper.restart();
            }
        }
    });

    public ScaryMazeGame() throws IOException {
        gif = new ArrayList<BufferedImage>(MAX_GIF_IMGS);
        steps = new ArrayList<BufferedImage>(MAX_LEVELS);
        intros = new ArrayList<BufferedImage>(MAX_LEVELS);
        intros_masks = new ArrayList<BufferedImage>(MAX_LEVELS);
        white_screens = new ArrayList<BufferedImage>(MAX_LEVELS);
        black_screens = new ArrayList<BufferedImage>(MAX_LEVELS);
        screens_masks = new ArrayList<BufferedImage>(MAX_LEVELS);
        init_gif();
        init_arrays();
        cur_gif = 0;
        cur_level = 0;
        gameOver = ImageIO.read(getClass().getResource("Game Over.jpg"));
        meir = ImageIO.read(getClass().getResource("meir.jpg"));
        currentLevel = steps.get(cur_level);
        is_first_black = true;
        timerGIF.start();
        timerStep.start();

    }

    private void init_gif() throws IOException{
        int i = 0;
        while (i < MAX_GIF_IMGS){
            gif.add(ImageIO.read(getClass().getResource("gif/gif".concat(String.valueOf(i)).concat(".png"))));
            i++;
        }
    }

    private void init_arrays() throws IOException{
        int i = 1;
        while (i < MAX_LEVELS + 1){
            steps.add(ImageIO.read(getClass().getResource("step_".concat(String.valueOf(i)).concat(".png"))));
            intros.add(ImageIO.read(getClass().getResource("intro_".concat(String.valueOf(i)).concat(".png"))));
            intros_masks.add(ImageIO.read(getClass().getResource("intro_mask_".concat(String.valueOf(i)).concat(".png"))));
            white_screens.add(ImageIO.read(getClass().getResource("white_screen_".concat(String.valueOf(i)).concat(".png"))));
            black_screens.add(ImageIO.read(getClass().getResource("black_screen_".concat(String.valueOf(i)).concat(".png"))));
            screens_masks.add(ImageIO.read(getClass().getResource("screen_mask_".concat(String.valueOf(i)).concat(".png"))));
            i++;
        }
    }

    public static void main(String args[]) throws IOException {
        JFrame window = new JFrame("Scary Maze Typography");
        ScaryMazeGame game = new ScaryMazeGame();
        window.add(game);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setVisible(true);

        game.addMouseMotionListener(game);
        game.addMouseListener(game);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1280, 720);
    }

    @Override
    protected void paintComponent(Graphics g) {
//        g.setColor(Color.white);
        g.fillRect(0, 0, 1280, 720);
        g.drawImage(gif.get(cur_gif), 0,0,null);
        if (currentLevel == gameOver) {
            g.drawImage(currentLevel, 0, -150, null);
        }
        else {
            g.drawImage(currentLevel, 0, 0, null);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Do nothing
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // check the color of the pixel the the mouse is over, and
        // go to the next level, or show the game over screen

        int x = e.getX();
        int y = e.getY();
        int color = currentLevel.getRGB(x, y);

//        System.out.println(color);

        int black = -16777216;
        int white = -1;

        if (currentLevel == intros.get(cur_level) && intros_masks.get(cur_level).getRGB(x,y) == white) {
            currentLevel = white_screens.get(cur_level);
            timerBlackBGMain.restart();
            timerBlackBGMain.start();
        }

        if (currentLevel == white_screens.get(cur_level) || currentLevel == black_screens.get(cur_level)) {
            if (cur_level < LEVEL_TO_SCARE - 1 && color == black) {
                currentLevel = intros.get(cur_level);
                }
            else if (cur_level >= LEVEL_TO_SCARE - 1 && color == black) {
                showGameOver(x);
                }
            else if (screens_masks.get(cur_level).getRGB(x,y) == white){
                    cur_level++;
                    currentLevel = steps.get(cur_level);
                    timerStep.start();
                }
        }
        repaint();
    }

    private void showGameOver(int x) {
        scream.play();
        if (x < 400) {
            currentLevel = meir;
        }
        else {
            currentLevel = gameOver;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (currentLevel == gameOver) {
            cur_level = 0;
            currentLevel = steps.get(cur_level);
            timerStep.start();
        }
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
