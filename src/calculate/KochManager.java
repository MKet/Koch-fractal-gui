package calculate;

import javafx.application.Platform;
import jsf31kochfractalfx.JSF31KochFractalFX;
import timeutil.TimeStamp;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class KochManager {
    private JSF31KochFractalFX application;
    private ArrayList<Edge> edges = new ArrayList<>();
    private TimeStamp stamp;
    private Thread EdgeProccesingThread;

    public KochManager(JSF31KochFractalFX application) {
        this.application = application;
    }

    public void changeLevel(int nxt) {
        if (EdgeProccesingThread != null && EdgeProccesingThread.isAlive())
            EdgeProccesingThread.interrupt();

        stamp = new TimeStamp();
        stamp.setBegin("Start read");
        EdgeProccesingThread = new Thread(() -> {
            edges.clear();



            Platform.runLater(() -> {
                this.stamp.setEnd("reading complete");
                application.setTextCalc(this.stamp.toString());
                application.setTextNrEdges("" + edges.size());

                drawEdges();
            });
        });
        EdgeProccesingThread.start();

    }

    public void drawEdges() {
        TimeStamp stamp = new TimeStamp();
        stamp.setBegin("Start draw");

        application.clearKochPanel();
        for(Edge edge : edges)
            application.drawEdge(edge);

        stamp.setEnd("drawing complete");
        application.setTextDraw(stamp.toString());

    }

}
