package calculate;

import javafx.application.Platform;
import jsf31kochfractalfx.JSF31KochFractalFX;
import timeutil.TimeStamp;

import java.io.*;
import java.util.ArrayList;

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
            File file = new File("edges/binary/"+nxt);

            FileInputStream fileStream = null;
            BufferedInputStream buffer;
            ObjectInputStream objectStream = null;
            try {
                fileStream = new FileInputStream(file);
                buffer = new BufferedInputStream(fileStream);
                objectStream = new ObjectInputStream(buffer);

                Edge edge = null;

                while (true) {
                    edge = (Edge) objectStream.readObject();
                    edges.add(edge);
                }

            }
            catch (EOFException e) {}
            catch (IOException | ClassNotFoundException e) {
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
