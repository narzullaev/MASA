package com.slumberjer.smarthealthband;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MainActivity extends AppCompatActivity {

    TextView txtTemp,txtHR,txtMove,txtBroker;
    String broker="";
    MemoryPersistence persistence = new MemoryPersistence();
    NotificationCompat.Builder notification;
    Vibrator vibrator;
    Uri soundUri;
    private  static final int unique_id = 45612;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtTemp = findViewById(R.id.textTemp);
        txtHR= findViewById(R.id.textHR);
        txtMove = findViewById(R.id.textMove);
        txtBroker = findViewById(R.id.textBroker);
        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);
        vibrator = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
        soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }

    public void subscribeMQTT(View v) {
        broker = "tcp://"+txtBroker.getText().toString()+":1883";
        if (broker.isEmpty()){
            Toast.makeText(this, "Set the broker ip address", Toast.LENGTH_SHORT).show();
            return;
        }

        class GetDataBroker extends AsyncTask<Void, Void, String>
                implements MqttCallback {

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    MqttClient clientsub = new MqttClient(broker,
                            "AndroidClient",persistence);
                    clientsub.setCallback(this);
                    clientsub.connect();
                    clientsub.subscribe("pubTopic/sensor/data");
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void connectionLost(Throwable cause) {
                Toast.makeText(MainActivity.this, "Broker connection lost!!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void messageArrived(String topic, final MqttMessage message) {
                String messageSplit = message.toString();
                final String [] messages = messageSplit.split(",");
                int fall = Integer.parseInt(messages[0]);
                final double heart_rate = Double.parseDouble(messages[1]);
                final double temperature = Double.parseDouble(messages[2]);

                try{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtTemp.setText(temperature+"°C");
                            txtHR.setText(heart_rate+"");
                           // series.appendData(new DataPoint(lastX++,
                            //        Double.parseDouble(message.toString()) ), true, 10);
                        }
                    });

                 if(fall==1){
                     txtMove.setText("Fall Detected!");
                     pushFallNotification();
                 }else{
                     txtMove.setText("Idle...");
                 }
                }catch (Exception e){ }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        }
        GetDataBroker getdatabroker = new GetDataBroker();
        getdatabroker.execute(); //run object asynctask
    }

    public void unsubscribeMQTT(View v) {
        broker = "tcp://"+txtBroker.getText().toString()+":1883";
        class TurnOffSub extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    MqttClient mqttclient = new MqttClient
                            (broker, "AndroidClient", persistence);
                    mqttclient.connect();
                    mqttclient.subscribe("pubTopic/sensor/data");
                    mqttclient.disconnect();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }
        TurnOffSub turnofsub = new TurnOffSub();
        turnofsub.execute();
        Toast.makeText(this, "Off Subcription", Toast.LENGTH_SHORT).show();
    }


    public void pushFallNotification(){
        notification.setSmallIcon(R.drawable.fall);
        notification.setSound(soundUri);
        notification.setTicker("Set the ticker");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("");
        notification.setContentText("Fall Detected");

        Intent inent =  new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, inent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        NotificationManager nm  = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(unique_id, notification.build());
        vibrator.vibrate(700);

    }

}
