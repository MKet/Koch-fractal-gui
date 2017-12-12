package calculate;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import jsf31kochfractalfx.JSF31KochFractalFX;
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
import java.util.Scanner;

public class KochManager {

    private static final int EDGE_SIZE = 7*Double.BYTES;


    private JSF31KochFractalFX application;
    private List<Edge> edges = new ArrayList<>();
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

            try (
                    Socket socket = new Socket("localhost", 1337);
                    DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
                    ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream())
                ) {

                stamp = new TimeStamp();
                stamp.setBegin("Start read");

                outStream.writeInt(nxt);

                IAnswer answer = (IAnswer) inStream.readObject();

                edges = answer.getEdges();

                Platform.runLater(() -> {
                    System.out.println("reading complete");
                    this.stamp.setEnd("reading complete");
                    application.setTextCalc(this.stamp.toString());
                    application.setTextNrEdges("" + edges.size());

                    drawEdges();
                });
            } catch (IOException | ClassNotFoundException e) {
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
