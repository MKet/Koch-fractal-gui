package server;

import calculate.Edge;

public interface EdgeListener {
    void update(Edge e) throws InterruptedException;
}
