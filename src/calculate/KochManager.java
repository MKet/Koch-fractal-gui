package calculate;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import jsf31kochfractalfx.JSF31KochFractalFX;
import server.Protocol;
import timeutil.TimeStamp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.DoubleBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class KochManager {

    private static final int EDGE_SIZE = 7*Double.BYTES;

    private JSF31KochFractalFX application;
    private List<Edge> edges = new ArrayList<>();
    private TimeStamp stamp;
    private Thread EdgeProccesingThread;
    private ConcurrentLinkedQueue<Edge> queue;
    private AnimationTimer timer;

    public KochManager(JSF31KochFractalFX application) {
        this.application = application;
        queue = new ConcurrentLinkedQueue<>();
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Edge edge;
                while ((edge = queue.poll()) != null )
                    application.drawEdge(edge);
            }
        };
        timer.start();
    }

    public void changeLevel(int nxt) {
        if (EdgeProccesingThread != null && EdgeProccesingThread.isAlive())
            EdgeProccesingThread.interrupt();

        EdgeProccesingThread = new Thread(() -> {
            edges.clear();

            try (
                    Socket socket = new Socket("localhost", 1337);
                    ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream())
                ) {

                application.clearKochPanel();
                stamp = new TimeStamp();
                stamp.setBegin("Start read");

                outStream.writeObject(new Request(nxt, ResponseType.OneTime));

                Protocol protocol = new Protocol(outStream, inStream, nxt);

                /*IAnswer answer = protocol.readComplete();

                edges = answer.getEdges();*/

                protocol.readEdges((e) -> queue.add(e));

                Platform.runLater(() -> {
                    System.out.println("reading complete");
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
        for (Edge edge : edges) {
            application.drawEdge(edge);
        }

        stamp.setEnd("drawing complete");
        application.setTextDraw(stamp.toString());

    }

}
