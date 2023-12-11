package component;

import javax.swing.JComponent;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.List;
import java.awt.image.BufferedImage;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import obj.Car;
import obj.ParkingLot;

/**
 *
 * @author SasUke UtChiHa
 */
public class PanelGame extends JComponent {

    private Graphics2D g2d;
    private BufferedImage image;

    private int width;
    private int height;
    private Thread thread;
    private boolean start = true;
    private Image bgParking;
    private final int FPS = 60;
    private final int TARGET_TIME = 1000000000 / FPS;
    private Car[] cars;
    private final int N = 2;
    private int selectedCar;
    private ArrayList<Shape> stopShapes;
    private ArrayList<ParkingLot> parkingLots;
    private ArrayList<ParkingLot> availableLots;
    private ArrayList<ParkingLot> occupiedLots;
    private Random random = new Random();
    private Key key;

    public void start() {
        start = true;
        width = getWidth();
        height = getHeight();
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = (Graphics2D) image.createGraphics();
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.clearRect(0, 0, width, height);

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                gameLoop();
            }
        });

        initParkingLots();
        initStopSahpes();
        initCarsGame();
        initKeyboard();
        thread.start();
    }

    public void initParkingLots() {
        parkingLots = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Shape parkingArea;
            parkingArea = new Rectangle2D.Double(i * 80 + 100, 125, 80, 165);

            ParkingLot parkingLot = new ParkingLot(parkingArea);
            parkingLots.add(parkingLot);
        }

        availableLots = new ArrayList<>(parkingLots);
    }

    public void initStopSahpes() {
        stopShapes = new ArrayList<Shape>();
        stopShapes.add(new Rectangle2D.Double(0, 470, 625, 70));
        stopShapes.add(new Rectangle2D.Double(0, 0, 1000, 125));
        stopShapes.add(new Rectangle2D.Double(0, 0, 100, 300));
        stopShapes.add(new Rectangle2D.Double(900, 0, 100, 300));
    }

    public void initCarsGame() {
        cars = new Car[N];
        for (int i = 0; i < N; i++) {
            ParkingLot p = getRandomLot();
            cars[i] = new Car(p, parkingLots.indexOf(p) + 1);
            cars[i].changeLocation(i * 120, 582);
        }
        setSelectedCar(N - 1);
    }

    public ParkingLot getRandomLot() {
        if (availableLots.isEmpty()) {
            return null;
        }

        int randomIndex = random.nextInt(availableLots.size());

        ParkingLot randomParkingLot = availableLots.get(randomIndex);

        availableLots.remove(randomParkingLot);

        return randomParkingLot;
    }

    public void initKeyboard() {
        key = new Key();
        requestFocus();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        key.setKey_left(true);
                        break;
                    case KeyEvent.VK_UP:
                        key.setKey_up(true);
                        break;
                    case KeyEvent.VK_RIGHT:
                        key.setKey_right(true);
                        break;
                    case KeyEvent.VK_DOWN:
                        key.setKey_down(true);
                        break;
                    case KeyEvent.VK_SPACE:
                        cars[selectedCar].setSpeed(0);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        key.setKey_left(false);
                        break;
                    case KeyEvent.VK_UP:
                        key.setKey_up(false);
                        break;
                    case KeyEvent.VK_RIGHT:
                        key.setKey_right(false);
                        break;
                    case KeyEvent.VK_DOWN:
                        key.setKey_down(false);
                        break;
                    default:
                        break;
                }
            }

        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                float s = 0.5f;
                while (start) {
                    float angle = cars[selectedCar].getAngle();
                    if (key.isKey_left()) {
                        if (cars[selectedCar].getSpeed() > 0.5) {
                            angle = cars[selectedCar].isFront() ? angle - s : angle + s;
                        }
                    }

                    if (key.isKey_right()) {
                        if (cars[selectedCar].getSpeed() > 0.5) {
                            angle = cars[selectedCar].isFront() ? angle + s : angle - s;
                        }
                    }

                    if (key.isKey_down()) {
                        cars[selectedCar].moveBack();
                    } else {
                        cars[selectedCar].stopBack();
                    }
                    if (key.isKey_up()) {
                        cars[selectedCar].moveFront();
                    } else {
                        cars[selectedCar].stopFront();
                    }
                    cars[selectedCar].update();
                    if (carIsCrushed() || carIsCrushed2() || carTouchesCorner()) {
                        start = false;
                        JOptionPane.showMessageDialog(null, "Game Over!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    }

                    if (cars[selectedCar].itsPark()) {
                        if (selectedCar == 0) {
                            start = false;
                            JOptionPane.showMessageDialog(null, "Win!", "Win", JOptionPane.INFORMATION_MESSAGE);
                            break;
                        } else {
                            angle = 0;
                            setSelectedCar(selectedCar - 1);
                        }
                    } else {
                        System.out.println("0");
                    }
                    cars[selectedCar].changeAngle(angle);
                    sleep(5);
                }
                start();
            }
        }
        ).start();
    }

    public boolean carIsCrushed() {
        for (Shape shape : stopShapes) {
            if (cars[selectedCar].check(shape)) {
                return true;
            }
        }

        return false;
    }

    public boolean carIsCrushed2() {
        for (int i = 0; i < N; i++) {
            if (i == selectedCar) {
                continue;
            }

            if (cars[selectedCar].check(cars[i].getShape())) {
                return true;
            }
        }

        return false;
    }

    public boolean carTouchesCorner() {
        Shape s = new Rectangle2D.Double(0, 0, 985, 700);
        if (!s.getBounds2D().contains(cars[selectedCar].getShape().getBounds2D())) {
            return true;
        }

        return false;
    }


    public void gameLoop() {
        while (start) {
            long startTime = System.nanoTime();
            drawBackground();
            drawGame();
            render();
            long time = System.nanoTime() - startTime;
            if (time < TARGET_TIME) {
                sleep((TARGET_TIME - time) / 1000000);
            }
        }
    }

    private void sleep(long wait) {
        try {
            Thread.sleep(wait);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawBackground() {
        this.bgParking = new ImageIcon(getClass().getResource("/img/parking.png")).getImage();

        g2d.setColor(new Color(18, 19, 22));
        g2d.fillRect(0, 0, width, height);
        if (bgParking != null) {
            g2d.drawImage(bgParking, 0, 0, this);
        }
    }

    private void drawGame() {
        for (int i = 0; i < N; i++) {
            cars[i].draw(g2d);
        }

        g2d.setColor(new Color(0,0,0,0));
        for (Shape shape : stopShapes) {
            g2d.draw(shape);
        }

        for (ParkingLot lot : parkingLots) {
            g2d.draw(lot.getArea());
        }
    }

    private void render() {
        Graphics g = (Graphics) getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
    }

    public void setSelectedCar(int i) {
        selectedCar = i;
    }
}
