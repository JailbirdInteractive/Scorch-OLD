package com.jailbird.scorch;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import noman.googleplaces.Place;

import static com.jailbird.scorch.MainActivity.isNotified;
import static com.jailbird.scorch.MainActivity.myID;
import static com.jailbird.scorch.MainActivity.requesters;

public class NotificationService extends Service {
    DatabaseReference mDatabase;
String myId;
    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        myId=MainActivity.myID;
        mDatabase= FirebaseDatabase.getInstance().getReference();
        refreshComments();
        refreshRequests();
        refreshReplies();
        Log.e("Notifications", "Service started");

        super.onCreate();
    }
    private void refreshComments() {
        DatabaseReference ref = mDatabase.child("Users").child(myId).child("Invites");
//recyclerView.removeAllViews();


        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot!=null) {
                    Log.e("Post Changed", "Comment added" + dataSnapshot.getValue());
                    PlaceInvite placeInvite = dataSnapshot.getValue(PlaceInvite.class);
                    if(!MainActivity.placeInviteList.contains(placeInvite)) {
                       if (isNotified&&!placeInvite.isAccepted)
                        sendNotification(placeInvite);

                        if(!MainActivity.placeInviteList.contains(placeInvite))
MainActivity.placeInviteList.add(placeInvite);
                    }
                }
                //String key=postsnap.getKey();


                //Log.e("Post Changed", "Comment added" + postsnap.getValue());
            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot!=null) {
                    Log.e("Post Changed", "Comment added" + dataSnapshot.getValue());
                    //PlaceInvite placeInvite = dataSnapshot.getValue(PlaceInvite.class);
                   // sendNotification(placeInvite);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void refreshReplies() {
        final DatabaseReference ref = mDatabase.child("Users").child(myId).child("Invite Replies");
//recyclerView.removeAllViews();


        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot!=null) {
                        InviteReply reply = dataSnapshot.getValue(InviteReply.class);
                        if (isNotified && !reply.seen)
                            sendReplyNotification(reply);
                        reply.seen = true;
                        ref.child(reply.userId).setValue(reply);
                    }
                                //String key=postsnap.getKey();


                //Log.e("Reply", "Comment added" + dataSnapshot.getValue());
            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot!=null) {
                    InviteReply reply = dataSnapshot.getValue(InviteReply.class);
                    if (isNotified && !reply.seen)
                        sendReplyNotification(reply);
                    reply.seen = true;
                    ref.child(reply.userId).setValue(reply);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void refreshRequests() {
        DatabaseReference ref = mDatabase.child("Friend Requests").child(myId);
//recyclerView.removeAllViews();


        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot!=null) {
                    //Log.e("Post Changed", "Comment added" + dataSnapshot.getValue());
                    FriendRequest friendRequest = dataSnapshot.getValue(FriendRequest.class);
                    if(!MainActivity.friendRequests.contains(friendRequest)){

MainActivity.friendRequests.add(friendRequest);
                        getRequestUser(friendRequest.id);
                     if (isNotified)
                        sendRequestNotification();                }

                    }

            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot!=null) {
                    FriendRequest friendRequest = dataSnapshot.getValue(FriendRequest.class);
                    if(!MainActivity.friendRequests.contains(friendRequest)){

                        MainActivity.friendRequests.add(friendRequest);
                        getRequestUser(friendRequest.id);
                      if(isNotified)
                        sendRequestNotification();
                    }


                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null) {

                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void getRequestUser(final String id){
        DatabaseReference ref = mDatabase.child("Users");
        ref.orderByKey().equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    User user = dataSnapshot.child(id).child("User").getValue(User.class);
                    requesters.add(user);
                    Log.e("Request","User: "+dataSnapshot.getValue().toString());




                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }





    public void sendNotification(PlaceInvite placeInvite) {


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_fire_empty)
                        .setAutoCancel(true)
                        .setContentTitle(placeInvite.name+" wants to hang out")
                        .setContentText(placeInvite.name+ " invited you to hang out at "+placeInvite.place.getName());

        mBuilder.setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS| Notification.DEFAULT_VIBRATE);

        Intent targetIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);

        NotificationManager mNotificationManager =

                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);



                mNotificationManager.notify(777, mBuilder.build());
    }








    public void sendRequestNotification() {

//Get an instance of NotificationManager//

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_fire_empty)
                        .setAutoCancel(true)
                        .setContentTitle("New friend request")
                        .setContentText("You have a new friend request.");
        mBuilder.setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS| Notification.DEFAULT_VIBRATE);

        Intent targetIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);

        NotificationManager mNotificationManager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);



        mNotificationManager.notify(999, mBuilder.build());
    }








    public void sendReplyNotification(InviteReply reply) {


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_fire_empty)
                        .setAutoCancel(true)
                        .setContentTitle(reply.userName+" replied to your Invite")
                        .setContentText(""+reply.reply);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS| Notification.DEFAULT_VIBRATE);

        Intent targetIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);

        NotificationManager mNotificationManager =

                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        mNotificationManager.notify(888, mBuilder.build());
    }
}
