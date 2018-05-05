package zhenkit.masaproject;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

import static android.content.Context.NOTIFICATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class Third extends Fragment {
    private TextView mFall;
    View button;
    private static final String CHANNEL_ID = "zhenkit.masaproject";
    private static final int NOTIFICATION_ID = 001;

    public Third() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_third, container, false);
        mFall =(TextView) v.findViewById(R.id.tv_fall);
        DatabaseReference mDatabaseGig;
        // get the gig database
        mDatabaseGig = FirebaseDatabase.getInstance().getReference("fall_condition");

        mDatabaseGig.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mFall.setText((String ) dataSnapshot.getValue());
                String value = (String) dataSnapshot.getValue();
                if (value.equalsIgnoreCase("1")){
                    sendNotification();
                    Toast.makeText(getActivity(), "Fall Detected", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        button = v.findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Toast.makeText(getActivity(), "it works", Toast.LENGTH_SHORT).show();
                sendNotification();
            }
        });

        return v;
    }
    public void sendNotification() {
        createNotificationChannel();
  NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID);
    builder.setSmallIcon(R.mipmap.ic_launcher_round);
    builder.setContentTitle("Fall Detected");
    builder.setContentText("You may need to check up on the person");
    builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getContext());
        notificationManagerCompat.notify(NOTIFICATION_ID,builder.build());


        Vibrator vibrator = (Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 1 seconds
        vibrator.vibrate(1000);
    }
    public void createNotificationChannel(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence name = "Personal Notifications";
            String description = "Include all personal notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,name,importance);

            notificationChannel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);

        }
    }
}
