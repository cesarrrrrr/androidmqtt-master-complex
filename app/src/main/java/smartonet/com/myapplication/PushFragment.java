package smartonet.com.myapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class PushFragment extends Fragment {
    @BindView(R.id.topic)
    EditText topic;
    @BindView(R.id.messgae)
    EditText messgae;
    @BindView(R.id.push)
    Button push;
    Unbinder unbinder;
    private Gson gson=new Gson();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_push, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (!EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().register(this);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.push)
    public void onClick() {
        if (TextUtils.isEmpty(topic.getText().toString())) {
            Toast.makeText(getActivity(), "Topic Can not be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(messgae.getText().toString())) {
            Toast.makeText(getActivity(), "Message Can not be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if ( MqttService.getMqttConfig().isConnect()){
            Constants.MQTT_TOPIC=topic.getText().toString();
            MqttMessage mqttMessage=new MqttMessage();
            mqttMessage.setUserid("test");
            mqttMessage.setMessage(messgae.getText().toString());
            mqttMessage.setUsername(Constants.MQTT_USERNAME);
            mqttMessage.setSendTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
            MqttService.getMqttConfig().pubMsg(Constants.MQTT_TOPIC,gson.toJson(mqttMessage),0);

        }else {
            Toast.makeText(getActivity(), "MQTT not connected!", Toast.LENGTH_SHORT).show();

        }


    }
    @Subscribe
    public void onEventMainThread(EventBusBean event) {

        if (event.getTag().equals("1")) {
            Toast.makeText(getContext(), "Message sent successfully!", Toast.LENGTH_SHORT).show();
            messgae.setText("");
            messgae.clearFocus();

        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))//加上判断
            EventBus.getDefault().unregister(this);
    }




}
