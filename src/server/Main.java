package server;

import calculate.*;
import timeutil.TimeStamp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    private List<Edge> edges;
    private ObjectOutputStream outStream;

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        while(true) {
            Main main = new Main();
            main.calculateFractal();
            System.out.println("Successful");
        }
    }

    public void calculateFractal() throws IOException, ExecutionException, InterruptedException {

        ServerSocket serverSocket = new ServerSocket(1337);

        System.out.println("waiting for client connection");
        ExecutorService pool = Executors.newCachedThreadPool();

        while (true) {
            Socket socket = serverSocket.accept();
            pool.submit(() -> {
                try {
                    DoFractalTask(socket);
                } catch (IOException | ExecutionException | InterruptedException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        }

    }

    public void DoFractalTask(Socket incoming) throws IOException, ExecutionException, InterruptedException, ClassNotFoundException {
        ObjectInputStream inStream = new ObjectInputStream(incoming.getInputStream());
        outStream = new ObjectOutputStream(incoming.getOutputStream());

        System.out.println("Scanning for level");
        Request request = (Request)inStream.readObject();

        System.out.println("Level " + request.getLevel() + " chosen.");

        calculateFractal(request.getLevel());
    }

    public void calculateFractals() throws IOException, ExecutionException, InterruptedException {
        for (int i = 1; i < 10; i++)
            calculateFractal(i);
    }

    public void calculateFractal(int level) throws IOException, ExecutionException, InterruptedException {
        KochFractal fractal = new KochFractal();
        TimeStamp time = new TimeStamp();
        time.setBegin("calculation start");

        EdgeGenerator leftEdgeGenerator = new EdgeGenerator(level, EdgeType.Left);
        EdgeGenerator rightEdgeGenerator = new EdgeGenerator(level, EdgeType.Right);
        EdgeGenerator bottomEdgeGenerator = new EdgeGenerator(level, EdgeType.Bottom);

        ExecutorService pool = Executors.newFixedThreadPool(3);
        Future<List<Edge>> leftFuture = pool.submit(leftEdgeGenerator);
        Future<List<Edge>> rightFuture = pool.submit(rightEdgeGenerator);
        Future<List<Edge>> bottomFuture = pool.submit(bottomEdgeGenerator);

        edges = new LinkedList<>();
        edges.addAll(leftFuture.get());
        edges.addAll(rightFuture.get());
        edges.addAll(bottomFuture.get());

        Answer answ = new Answer(level, edges);

        outStream.writeObject(answ);

        time.setEnd("calculation end");
        System.out.println(time.toString());
    }
}
