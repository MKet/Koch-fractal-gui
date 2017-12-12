package calculate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Answer implements Serializable, IAnswer {
    private int level;
    private List<Edge> edges;

    public Answer(int level, List<Edge> edges) {
        this.level = level;
        this.edges = edges;
    }

    public int getLevel() {
        return level;
    }

    public List<Edge> getEdges() {
        return edges;
    }
}