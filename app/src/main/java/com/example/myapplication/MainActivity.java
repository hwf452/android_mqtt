package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

public class MainActivity extends AppCompatActivity {
    MqttAndroidClient mClient;
    TextView tv;
    ProcessView pv1;
    ProcessView1 pv2;
    ImageButton ib;
    boolean led_flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pv1 = (ProcessView)findViewById(R.id.pv1);
        pv2 = (ProcessView1)findViewById(R.id.pv2);
        tv = (TextView)findViewById(R.id.tv);
        ib = (ImageButton)findViewById(R.id.on);
        try {
            initClient();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public  void Click(View view) throws MqttException {
        switch (view.getId()){
            case R.id.on://此处是对布局中设置的id直接进行判断，
                // 不需要对控件进行获取（findviewByID）
                if(!led_flag){
                    mClient.publish("LED_STATUS","1".getBytes(),0,false);
                    led_flag = true;
                }else if (led_flag){
                    mClient.publish("LED_STATUS","0".getBytes(),0,false);
                    led_flag = false;
                }

                break;
        }
    }




    private void initClient() throws MqttException {

        //连接参数设置
        // 获取默认的临时文件路径
        String tmpDir = System.getProperty("java.io.tmpdir");

        /*
         * MqttDefaultFilePersistence：
         * 将数据包保存到持久化文件中，
         * 在数据发送过程中无论程序是否奔溃、 网络好坏
         * 只要发送的数据包客户端没有收到，
         * 这个数据包会一直保存在文件中，
         * 直到发送成功为止。
         */
        // Mqtt的默认文件持久化
        MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);
        String serverURI = "tcp://118.31.68.250:61613";//183.230.40.39:6002
        String clientId = "device1";//587670943
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(false);
        options.setUserName("admin");//324919
        options.setPassword("password".toCharArray());//vPdKsMQ=axTfZovQlkuTAm4Vl1c=
        options.setAutomaticReconnect(true);
        mClient = new MqttAndroidClient(getApplicationContext(), serverURI, clientId,dataStore);

        //连接回调
        mClient.setCallback(new MqttCallbackExtended() {

            //连接服务器成功时触发，可在这时订阅想要订阅的topic
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.e("connectComplete","connectComplete");
                try {
                    mClient.subscribe("temp",2);
                    mClient.subscribe("LED_STATUS_control",2);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }

            //连接丢失时触发
            @Override
            public void connectionLost(Throwable cause) {
                Log.e("connectionLost","connectionLost");
            }

            //接收到订阅的消息，可以进行一些逻辑业务处理
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.e("messageArrived",message.toString());
                String s = message.toString();
                Log.v("messageArrived",s);
                switch (topic){
                    case "temp":
                        float temp = Integer.parseInt(s.substring(0,2));
                        pv1.setProgress(temp);
                        float humi = Integer.parseInt(s.substring(2,4));
                        pv2.setProgress(humi);break;

                    case "LED_STATUS_control":
                        if(s.equals("1")) {
                            ib.setImageDrawable(ib.getResources().getDrawable(R.drawable.ic_led_on));
                        } else {
                            ib.setImageDrawable(ib.getResources().getDrawable(R.drawable.ic_led_off));
                        }break;
                }
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.e("deliveryComplete","deliveryComplete");
            }
        });
        mClient.connect(options);//开始连接

    }
}
