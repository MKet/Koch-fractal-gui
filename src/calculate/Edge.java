package calculate;

import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.Serializable;

/**
 *
 * @author Peter Boots
 */
public class Edge implements Serializable{
    public double X1, Y1, X2, Y2;
    public Color color;

    public Edge(double X1, double Y1, double X2, double Y2, Color color) {
        this.X1 = X1;
        this.Y1 = Y1;
        this.X2 = X2;
        this.Y2 = Y2;
        this.color = color;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeDouble(X1);
        out.writeDouble(X2);
        out.writeDouble(Y1);
        out.writeDouble(Y2);
        out.writeDouble(color.getRed());
        out.writeDouble(color.getBlue());
        out.writeDouble(color.getGreen());
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        X1 = in.readDouble();
        X2 = in.readDouble();
        Y1 = in.readDouble();
        Y2 = in.readDouble();

        double red = in.readDouble();
        double blue = in.readDouble();
        double green = in.readDouble();

        color = Color.color(red, green, blue);
    }

    @Override
    public String toString() {
        return        X1 +
                "," + Y1 +
                "," + X2 +
                "," + Y2 +
                "," + color.getRed() +
                ',' + color.getBlue()+
                ',' + color.getGreen();
    }
}
