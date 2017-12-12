package calculate;

import java.io.Serializable;

public class Request implements Serializable {
    private int level;
    private ResponseType responseType;

    public Request(int level, ResponseType responseType) {
        this.level = level;
        this.responseType = responseType;
    }

    public int getLevel() {
        return level;
    }

    public ResponseType getResponseType() {
        return responseType;
    }
}
