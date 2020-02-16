package smartonet.com.myapplication;

/**
 * Created by
 */

public class EventBusBean {
    private String tag;
    private String message;

    public EventBusBean(String tag, String message) {
        this.tag = tag;
        this.message = message;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
