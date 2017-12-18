package calculate;

import server.Protocol;

import java.util.*;
import java.util.concurrent.*;

public class EdgeGenerator implements Callable<List<Edge>>, Observer {

    private final Protocol protocol;
    private KochFractal fractal;
    private EdgeType type;
    private List<Edge> edges;


    public EdgeGenerator(int level, EdgeType type, Protocol protocol) {
        fractal = new KochFractal(level);
        fractal.addObserver(this);
        edges = new LinkedList<>();
        this.type = type;
        this.protocol = protocol;
    }

    @Override
    public List<Edge> call() {
        switch (type) {
            case Left:
                fractal.generateLeftEdge();
                break;
            case Right:
                fractal.generateRightEdge();
                break;
            case Bottom:
                fractal.generateBottomEdge();
        }
        protocol.complete(edges);
        return edges;
    }

    @Override
    public void update(Observable o, Object arg) {
        Edge edge = (Edge)arg;
        edges.add(edge);
        protocol.progress(edge);
    }
}
