/**
 * The alarm receiver class. Creates a notification that tells which class is due soon.
 * @author Jimmy Nguyen
 * @version 3/8/2017
 */
package com.example.studentplanner.studentplanner;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AlarmReceiver extends BroadcastReceiver {

    /**
     * When a broadcast is received, creates a notification
     * @param context the context pass in
     * @param intent the intent received
     */
    @Override
    public void onReceive(Context context, Intent intent) {


        // Gets the extras
        String name = intent.getStringExtra("name");
        int alarmID = intent.getIntExtra("alarmID", -1);
        boolean courseActivity = intent.getBooleanExtra("course", true);

        // Builds a new notification
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.ic_calendar_today_white_18dp)
                .setContentTitle("Upcoming due date")
                .setPriority(Notification.PRIORITY_HIGH);

        String text = "You have an upcoming course: " + name;

        // Gets the shared preferences and sets vibrate if true
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        if(sp.getBoolean("Vibrate", true)){
            builder.setVibrate(new long[] {0, 250, 250, 250, 250, 250, 250, 500});
        }

        //Create a new intent
        Intent openIntent= new Intent(context, CoursesActivity.class);

        // If it is an assessment, then change the intent
        if(!courseActivity){
            openIntent = new Intent(context, AssessmentsActivity.class);
            text = "You have an upcoming assessment: " + name;
        }
        builder.setContentText(text);

        // Adds the intent and parent to the BackStack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(openIntent);
        // Get the PendingIntent and set
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        // Creates a new notification manager and sets notification
        NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(alarmID, builder.build());
    }
}
