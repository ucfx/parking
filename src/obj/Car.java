/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package obj;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import javax.swing.ImageIcon;

/**
 *
 * @author SasUke UtChiHa
 */
public class Car {

    public static final double CAR_SIZE = 120;
    private double x;
    private double y;
    private float angle = 0f;
    private final float MAX_SPEED = 1f;
    private float speed = 0;
    private boolean front;
    private boolean back;
    private final Image carImage;
    private final Image carImageSpeed;
    private final Area carShape;
    private final ParkingLot parkArea;
    private final int parkIndex;

    public Car(ParkingLot parkArea, int index) {
        this.parkIndex = index;
        this.parkArea = parkArea;
        this.carImage = new ImageIcon(getClass().getResource("/img/car120.png")).getImage();
        this.carImageSpeed = new ImageIcon(getClass().getResource("/img/car120.png")).getImage();
        Path2D p = new Path2D.Double();
        p.moveTo(25, 40);
        p.lineTo(32, 20);
        p.lineTo(34, 18);
        p.lineTo(120, 18);
        p.lineTo(130, 22);
        p.lineTo(133, 34);
        p.lineTo(130, 55);
        p.lineTo(128, 60);
        p.lineTo(30, 60);
        carShape = new Area(p);
    }

    public void changeLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        if (front) {
            x += Math.cos(Math.toRadians(angle)) * speed;
            y += Math.sin(Math.toRadians(angle)) * speed;
        }
        if (back) {
            x -= Math.cos(Math.toRadians(angle)) * speed;
            y -= Math.sin(Math.toRadians(angle)) * speed;
        }

    }

    public void changeAngle(float angle) {
        if (angle < 0) {
            angle = 359;
        } else if (angle > 359) {
            angle = 0;
        }

        this.angle = angle;
    }

    public boolean check(Shape area) {
        Area carArea = getShape();

        return carArea.intersects(area.getBounds2D());
    }

    public boolean itsPark() {
        Area carArea = new Area(getShape());

        return parkArea.getArea().contains(carArea.getBounds2D());
    }

    public void draw(Graphics2D g) {
        AffineTransform oldTransform = g.getTransform();

        g.translate(x, y);

        AffineTransform trans = new AffineTransform();

        trans.rotate(Math.toRadians(angle - 90), CAR_SIZE / 2, CAR_SIZE / 3);

        g.drawImage(carImage, trans, null);
        double textX = CAR_SIZE / 2;
        double textY = CAR_SIZE / 3;
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString(Integer.toString(parkIndex), (float) textX, (float) textY);

        g.setTransform(oldTransform);

        Shape shape = getShape();
        g.setColor(new Color(0, 0, 0, 0));
        g.draw(shape);
    }

    public Area getShape() {
        AffineTransform a = new AffineTransform();
        a.translate(x, y);
        a.rotate(Math.toRadians(angle), CAR_SIZE / 2, CAR_SIZE / 3);
        return new Area(a.createTransformedShape(carShape));
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public float getAngle() {
        return angle;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public boolean isFront() {
        return front;
    }

    public void setFront(boolean front) {
        this.front = front;
    }

    public boolean isBack() {
        return back;
    }

    public void setBack(boolean back) {
        this.back = back;
    }

    public void moveFront() {
        front = true;
        back = false;
        if (speed > MAX_SPEED) {
            speed = MAX_SPEED;
        } else {
            speed += 0.1f;
        }
    }

    public void stopFront() {
        if (speed <= 0) {
            speed = 0;
            front = false;
        } else {
            speed -= 0.01f;
        }
    }

    public void moveBack() {
        back = true;
        front = false;
        if (speed > MAX_SPEED - MAX_SPEED / 4) {
            speed = MAX_SPEED - MAX_SPEED / 4;
        } else {
            speed += 0.08f;
        }
    }

    public void stopBack() {
        if (speed <= 0) {
            speed = 0;
            back = false;
        } else {
            speed -= 0.008f;
        }
    }

    public ParkingLot getParkArea() {
        return parkArea;
    }

    public int getParkIndex() {
        return parkIndex;
    }
}
