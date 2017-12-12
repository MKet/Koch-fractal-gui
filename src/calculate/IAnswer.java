package calculate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public interface IAnswer extends Serializable {
    int getLevel();
    List<Edge> getEdges();
}