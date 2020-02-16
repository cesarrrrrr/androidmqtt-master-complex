package smartonet.com.myapplication;

/**
 * Info：
 */
public interface MqttListener {
    void onConnected();//连接成功
    void onFail();//连接失败
    void onLost();//丢失连接
    void onReceive(String topic, String message);//接收到消息
    void onSendSucc();//消息发送成功
}
