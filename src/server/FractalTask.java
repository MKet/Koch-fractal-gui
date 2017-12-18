package server;

import calculate.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import timeutil.TimeStamp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.*;

public class FractalTask implements Runnable, Observer {

    private List<Edge> edges;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;
    private Socket incoming;

    public FractalTask(Socket incoming) {
        this.incoming = incoming;
    }

    @Override
    public void run() {
        try {
            DoFractalTask();
        } catch (IOException | ExecutionException | InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void DoFractalTask() throws IOException, ExecutionException, InterruptedException, ClassNotFoundException {
        inStream = new ObjectInputStream(incoming.getInputStream());
        outStream = new ObjectOutputStream(incoming.getOutputStream());

        System.out.println("Scanning for level");
        Request request = (Request)inStream.readObject();

        System.out.println("Level " + request.getLevel() + " chosen.");

        calculateFractal(request.getLevel());
    }

    public void calculateFractal(int level) throws IOException, ExecutionException, InterruptedException {
        Protocol protocol = new Protocol(outStream, inStream, level);

        EdgeGenerator leftEdgeGenerator = new EdgeGenerator(level, EdgeType.Left, protocol);
        EdgeGenerator rightEdgeGenerator = new EdgeGenerator(level, EdgeType.Right, protocol);
        EdgeGenerator bottomEdgeGenerator = new EdgeGenerator(level, EdgeType.Bottom, protocol);

        ExecutorService pool = Executors.newFixedThreadPool(3);
        pool.submit(leftEdgeGenerator);
        pool.submit(rightEdgeGenerator);
        pool.submit(bottomEdgeGenerator);
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
