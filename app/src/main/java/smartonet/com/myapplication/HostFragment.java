package smartonet.com.myapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HostFragment extends Fragment {
    @BindView(R.id.id)
    EditText id;
    @BindView(R.id.server)
    EditText server;
    @BindView(R.id.post)
    EditText post;
    @BindView(R.id.swtch)
    Switch swtch;
    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.timeout)
    EditText timeout;
    @BindView(R.id.keepalive)
    EditText keepalive;
    @BindView(R.id.connect)
    Button connect;
    @BindView(R.id.disconnect)
    Button disconnect;
    Unbinder unbinder;
    private Boolean session=false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_host, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (!EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().register(this);
        }
        swtch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //Todo
                    session=true;
                }else {
                    //Todo
                    session=false;
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.connect, R.id.disconnect})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.connect:
                //连接Mqtt服务端
                if (TextUtils.isEmpty(id.getText().toString())) {
                    Toast.makeText(getActivity(), "Client ID Can not be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(server.getText().toString())) {
                    Toast.makeText(getActivity(), "Server Can not be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(post.getText().toString())) {
                    Toast.makeText(getActivity(), "Port Can not be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(username.getText().toString())) {
                    Toast.makeText(getActivity(), "Username Can not be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password.getText().toString())) {
                    Toast.makeText(getActivity(), "Password Can not be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
               Constants.MQTT_IP=server.getText().toString();
                Constants.MQTT_PORT=post.getText().toString();
                Constants.MQTT_USERNAME=username.getText().toString();
                Constants.MQTT_PASSWORD=password.getText().toString();
                Constants.MQTT_CLIENTID=id.getText().toString();
                Constants.MQTT_TIMEOUT=timeout.getText().toString();
                Constants.MQTT_KEEPALIVE=keepalive.getText().toString();
                Constants.MQTT_SeESSION=session;

                Intent intent=new Intent(getActivity(), MqttService.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    getActivity().startForegroundService(intent);
                }else {
                    getActivity().startService(intent);
                }
                break;
            case R.id.disconnect:

                if (MqttService.getMqttConfig().isConnect()){
                    MqttService.getMqttConfig().disConnectMqtt();
                    Toast.makeText(getActivity(), "MQTT Disconnect successfully!", Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(getActivity(), "MQTT not connected!", Toast.LENGTH_SHORT).show();

                }

                break;
        }
    }
    @Subscribe
    public void onEventMainThread(EventBusBean event) {

        if (event.getTag().equals("3")) {
            Toast.makeText(getContext(), "Mqtt connection succeeded!", Toast.LENGTH_SHORT).show();

        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))//加上判断
            EventBus.getDefault().unregister(this);
    }

}
