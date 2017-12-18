package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
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
            pool.submit(new FractalTask(socket));
        }

    }




}
