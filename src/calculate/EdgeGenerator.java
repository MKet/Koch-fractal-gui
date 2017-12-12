package calculate;

import java.util.*;
import java.util.concurrent.*;

public class EdgeGenerator implements Callable<List<Edge>>, Observer {

    private KochFractal fractal;
    private EdgeType type;
    private List<Edge> edges;

    public EdgeGenerator(int level, EdgeType type) {
        fractal = new KochFractal(level);
        fractal.addObserver(this);
        edges = new LinkedList<>();
        this.type = type;
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
        return edges;
    }

    @Override
    public void update(Observable o, Object arg) {
        edges.add((Edge)arg);
    }
}
