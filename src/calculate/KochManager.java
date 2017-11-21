package calculate;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import jsf31kochfractalfx.JSF31KochFractalFX;
import timeutil.TimeStamp;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class KochManager {
    private JSF31KochFractalFX application;
    private ArrayList<Edge> edges = new ArrayList<>();
    private TimeStamp stamp;
    private Thread EdgeProccesingThread;

    public KochManager(JSF31KochFractalFX application) {
        this.application = application;
    }

    public void changeLevel(int nxt) {
        if (EdgeProccesingThread != null && EdgeProccesingThread.isAlive())
            EdgeProccesingThread.interrupt();


        EdgeProccesingThread = new Thread(() -> {
            edges.clear();

            stamp = new TimeStamp();
            stamp.setBegin("Start read");
            File file = new File("edges/Text/"+nxt);

            FileInputStream fileStream = null;
            BufferedInputStream buffer;
            ObjectInputStream objectStream = null;
            try {

                fileStream = new FileInputStream(file);
                buffer = new BufferedInputStream(fileStream);

                Scanner scanner = new Scanner(buffer);

                String line;
                while (scanner.hasNext()) {
                    line = scanner.next();

                    String[] split = line.split(",");

                    double X1 = Double.parseDouble(split[0]);
                    double Y1 = Double.parseDouble(split[1]);
                    double X2 = Double.parseDouble(split[2]);
                    double Y2 = Double.parseDouble(split[3]);
                    double red = Double.parseDouble(split[4]);
                    double blue = Double.parseDouble(split[5]);
                    double green = Double.parseDouble(split[6]);

                    edges.add(new Edge(X1, Y1, X2, Y2, Color.color(red, green, blue)));
                }

            }
            catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fileStream != null)
                        fileStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            Platform.runLater(() -> {
                this.stamp.setEnd("reading complete");
                application.setTextCalc(this.stamp.toString());
                application.setTextNrEdges("" + edges.size());

                drawEdges();
            });
        });
        EdgeProccesingThread.start();

    }

    public void drawEdges() {
        TimeStamp stamp = new TimeStamp();
        stamp.setBegin("Start draw");

        application.clearKochPanel();
        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            application.drawEdge(edge);
        }

        stamp.setEnd("drawing complete");
        application.setTextDraw(stamp.toString());

    }

}
