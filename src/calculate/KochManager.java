package calculate;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import jsf31kochfractalfx.JSF31KochFractalFX;
import timeutil.TimeStamp;

import java.io.*;
import java.nio.DoubleBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Scanner;

public class KochManager {

    private static final int EDGE_SIZE = 7*8;

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

            try {
                File file = new File("../"+nxt);

                stamp = new TimeStamp();
                stamp.setBegin("Start read");

                System.out.println("start read");
                RandomAccessFile randomAccessFile = new RandomAccessFile(file , "rw");

                System.out.println("random access file made");
                FileChannel fc = randomAccessFile.getChannel();

                KochFractal koch = new KochFractal(nxt);

                MappedByteBuffer map = fc.map(FileChannel.MapMode.READ_ONLY, 0, EDGE_SIZE*koch.getNrOfEdges()+4);

                try {
                    for (int i = 0; i < koch.getNrOfEdges(); i++) {
                        while (map.getInt() < i+1) {
                            map.position(0);
                            Thread.sleep(10);
                        }
                        map.position(i*EDGE_SIZE+4);
                        FileLock lock = fc.lock(map.position(), EDGE_SIZE, false);
                        double X1 = map.getDouble();
                        double Y1 = map.getDouble();
                        double X2 = map.getDouble();
                        double Y2 = map.getDouble();
                        double red = map.getDouble();
                        double blue = map.getDouble();
                        double green = map.getDouble();
                        lock.release();

                        Edge edge = new Edge(X1, Y1, X2, Y2, new Color(red, green, blue, 1));

                        edges.add(edge);
                    }
                } finally {
                    try {
                        randomAccessFile.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                Platform.runLater(() -> {
                    System.out.println("reading complete");
                    this.stamp.setEnd("reading complete");
                    application.setTextCalc(this.stamp.toString());
                    application.setTextNrEdges("" + edges.size());

                    drawEdges();
                });
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) { }
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
