package server;

import calculate.Answer;
import calculate.Edge;
import calculate.KochFractal;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Protocol {

    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private int level;

    private List<Edge> edges;
    private int sidessComplete = 0;
    private int edgesComplete = 0;

    public Protocol(ObjectOutputStream outputStream, ObjectInputStream inputStream, int level) {
        this.outputStream = outputStream;
        this.inputStream = inputStream;
        this.level = level;
        edges = new ArrayList<>();
    }

    public synchronized void progress(Edge edge) {
        try {
            outputStream.writeObject(edge);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void complete(List<Edge> edges) {
        try {
            this.edges.addAll(edges);
            sidessComplete++;

            if (sidessComplete < 3)
                return;

            Answer answer = new Answer(level, this.edges);
            outputStream.writeObject(answer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readEdges(EdgeListener edgeListener) {
        Object o;
        KochFractal fractal = new KochFractal(level);
        try {
            while (edgesComplete <= fractal.getNrOfEdges()) {
                if ((o = inputStream.readObject()) instanceof Edge) {
                    edgesComplete++;
                    edgeListener.update((Edge) o);
                }
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public Answer readComplete() {
        try {
            Object o;
            while (!((o = inputStream.readObject()) instanceof Answer));

            return (Answer) o;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
