package smartonet.com.myapplication;

import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Collections;

public class MqttActivity extends AppCompatActivity implements MqttListener{
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String[] title = {"HISTORY", "PUBLISH", "SUBSCRIBE"};
    private Gson gson=new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt);
        MqttService.addMqttListener(this);
        if (!EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().register(this);
        }
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.fragment_pager);
        initPager();

    }

    private void initPager() {
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment fragment = new Fragment();
                if (fragment != null) {
                    switch (position) {
                        case 0:
                            fragment = new HostFragment();
                            break;
                        case 1:
                            fragment = new PushFragment();
                            break;
                        case 2:
                            fragment = new SubscFragment();
                            break;
                    }
                }
                return fragment;
            }

            @Override
            public int getCount() {
                return 3;
            }
        });

        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0);
        tabLayout.getTabAt(0).setCustomView(getTabView(0));
        tabLayout.getTabAt(1).setCustomView(getTabView(1));
        tabLayout.getTabAt(2).setCustomView(getTabView(2));

        initTab();
    }
    private void initTab() {
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                TextView textView = view.findViewById(R.id.txt_tab_task);

                textView.setTextColor(Color.parseColor("#ed8200"));
                if (textView.getText().toString().equals(title[0])) {

                    viewPager.setCurrentItem(0);
                } else if (textView.getText().toString().equals(title[1])) {

                    viewPager.setCurrentItem(1);
                } else if (textView.getText().toString().equals(title[2])) {

                    viewPager.setCurrentItem(2);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                TextView textView = view.findViewById(R.id.txt_tab_task);

                textView.setTextColor(Color.parseColor("#999999"));
                if (textView.getText().toString().equals(title[0])) {

                    viewPager.setCurrentItem(0);
                } else if (textView.getText().toString().equals(title[1])) {

                    viewPager.setCurrentItem(1);
                } else if (textView.getText().toString().equals(title[2])) {

                    viewPager.setCurrentItem(2);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    private View getTabView(int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_task_item, null);
        TextView textView = view.findViewById(R.id.txt_tab_task);

        textView.setText(title[position]);


        if (position == 0) {
            textView.setTextColor(Color.parseColor("#ed8200"));

        }
        return view;
    }
    @Subscribe
    public void onEventMainThread(EventBusBean event) {

        if (event.getTag().equals("1")) {

        }
    }
    @Override
    public void onConnected() {
        EventBus.getDefault().post(
                new EventBusBean("3",""));
        Log.e("Mqtt","连接成功");
    }

    @Override
    public void onFail() {

    }

    @Override
    public void onLost() {

    }

    @Override
    public void onReceive(String topic, String message) {
        if(topic.equals(Constants.MQTT_TOPIC)){
            MqttMessage mqttMessage=gson.fromJson(message,MqttMessage.class);
            EventBus.getDefault().post(
                    new EventBusBean("2",mqttMessage.getSendTime()+"--"+mqttMessage.getMessage()));


        }
    }

    @Override
    public void onSendSucc() {
        EventBus.getDefault().post(
                new EventBusBean("1",""));
        Log.e("Mqtt","消息发送成功");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))//加上判断
            EventBus.getDefault().unregister(this);
    }
}
