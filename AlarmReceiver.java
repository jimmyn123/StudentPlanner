package com.example.studentplanner.studentplanner;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String course = intent.getStringExtra("course");
        int alarmID = intent.getIntExtra("alarmID", -1);

        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.ic_calendar_today_white_18dp)
                .setVibrate(new long[] {0, 250, 250, 250, 250, 250, 250, 500})
                .setContentTitle("Upcoming due date")
                .setContentText("You have an upcoming due date for: " + course)
                .setPriority(Notification.PRIORITY_HIGH);

        Intent openIntent= new Intent(context, CoursesActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(openIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        nm.notify(alarmID, builder.build());
    }
}
