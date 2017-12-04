package calculate;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import jsf31kochfractalfx.JSF31KochFractalFX;
import timeutil.TimeStamp;

import java.io.*;
import java.nio.DoubleBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
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
                Path path = FileSystems.getDefault().getPath(file.getAbsolutePath());
                path = path.getParent();
                boolean fileFound = false;
                if (!file.exists()) {
                    WatchService watchService = FileSystems.getDefault().newWatchService();
                    try {
                        WatchKey key = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
                        while (!fileFound) {
                            final WatchKey wk = watchService.take();
                            for (WatchEvent<?> event : wk.pollEvents()) {
                                final Path changed = (Path) event.context();
                                if (changed.endsWith(""+nxt)) {
                                    System.out.println("watched file changed");
                                    fileFound = true;
                                    break;
                                }
                            }
                            // reset the key
                            boolean valid = wk.reset();
                            if (!valid) {
                                System.out.println("Key has been unregistered");
                            }
                        }
                    } catch (InterruptedException e) {
                        return;
                    } finally {
                        watchService.close();
                    }
                }

                stamp = new TimeStamp();
                stamp.setBegin("Start read");

                System.out.println("start read");

                RandomAccessFile randomAccessFile = new RandomAccessFile(file , "r");

                System.out.println("random access file made");
                FileChannel fc = randomAccessFile.getChannel();

                KochFractal koch = new KochFractal(nxt);

                try {
                    MappedByteBuffer map = fc.map(FileChannel.MapMode.READ_ONLY, 0, EDGE_SIZE*koch.getNrOfEdges());
                    DoubleBuffer doubleBuffer = map.asDoubleBuffer();

                    for (int i = 0; i < koch.getNrOfEdges(); i++) {
                        double X1 = doubleBuffer.get();
                        double Y1 = doubleBuffer.get();
                        double X2 = doubleBuffer.get();
                        double Y2 = doubleBuffer.get();
                        double red = doubleBuffer.get();
                        double blue = doubleBuffer.get();
                        double green = doubleBuffer.get();

                        Edge edge = new Edge(X1, Y1, X2, Y2, new Color(red, green, blue, 1));

                        edges.add(edge);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        randomAccessFile.close();
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
            } catch (IOException e) {
                e.printStackTrace();
            }
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
