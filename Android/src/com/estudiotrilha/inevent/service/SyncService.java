package com.estudiotrilha.inevent.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.estudiotrilha.android.utils.JsonUtils;
import com.estudiotrilha.inevent.InEvent;
import com.estudiotrilha.inevent.R;
import com.estudiotrilha.inevent.Utils;
import com.estudiotrilha.inevent.content.Activity;
import com.estudiotrilha.inevent.content.ActivityMember;
import com.estudiotrilha.inevent.content.ApiRequest;
import com.estudiotrilha.inevent.content.Event;
import com.estudiotrilha.inevent.content.EventMember;
import com.estudiotrilha.inevent.content.LoginManager;
import com.estudiotrilha.inevent.content.Member;
import com.estudiotrilha.inevent.content.SyncBroadcastManager;
import com.estudiotrilha.inevent.provider.InEventProvider;


public class SyncService extends IntentService implements ApiRequest.ResponseHandler
{
    public static final String SERVICE_NAME = InEvent.NAME + "." + SyncService.class.getSimpleName();

    private static final String EXTRA_EVENT_ID    = "extra.EVENT_ID";
    private static final String EXTRA_ACTIVITY_ID = "extra.ACTIVITY_ID";

    public static void syncEvents(Context c)
    {
        Intent service = new Intent(c, SyncService.class);
        service.setData(Event.CONTENT_URI);
        c.startService(service);
    }
    public static void syncEventAttenders(Context c, long eventId)
    {
        Intent service = new Intent(c, SyncService.class);
        service.setData(EventMember.CONTENT_URI);
        service.putExtra(EXTRA_EVENT_ID, eventId);
        c.startService(service);
    }
    public static void syncEventActivities(Context c, long eventId)
    {
        Intent service = new Intent(c, SyncService.class);
        service.setData(Activity.ACTIVITY_CONTENT_URI);
        service.putExtra(EXTRA_EVENT_ID, eventId);
        c.startService(service);
    }
    public static void syncEventSchedule(Context c, long eventId)
    {
        Intent service = new Intent(c, SyncService.class);
        service.setData(Activity.SCHEDULE_CONTENT_URI);
        service.putExtra(EXTRA_EVENT_ID, eventId);
        c.startService(service);
    }
    public static void syncEventActivityAttenders(Context c, long eventId, long activityId)
    {
        Intent service = new Intent(c, SyncService.class);
        service.setData(ActivityMember.CONTENT_URI);
        service.putExtra(EXTRA_EVENT_ID, eventId);
        service.putExtra(EXTRA_ACTIVITY_ID, activityId);
        c.startService(service);
    }


    private Intent mIntent;


    public SyncService()
    {
        super(SERVICE_NAME);
    }
    @Override
    protected void onHandleIntent(Intent intent)
    {
        mIntent = intent;

        if (!Utils.checkConnectivity(this))
        {
            // send a broadcast telling that there is no connection
            sendBroadcast(new Intent(InEvent.ACTION_TOAST_NOTIFICATION).putExtra(InEvent.EXTRA_TOAST_MESSAGE, R.string.error_connection_internetless));
            return;
        }

        Uri data = intent.getData();
        int code = InEventProvider.uriMatcher.match(data);

        Log.v(InEvent.NAME, "Downloading " + data);

        switch (code)
        {
        case InEventProvider.URI_EVENT:
        {
            SyncBroadcastManager.setSyncState(this, "Syncing Events");

            try
            {
                String tokenID = LoginManager.getInstance(this).getTokenId();
                HttpURLConnection connection = Event.Api.getEvents(tokenID);
                ApiRequest.getJsonFromConnection(code, connection, this, false);
            }
            catch (IOException e)
            {
                Log.e(InEvent.NAME, "Couldn't create connection for person.getEvents(tokenID)", e);
            }

            // stop the sync state
            SyncBroadcastManager.setSyncState(this, false);
            break;
        }

        case InEventProvider.URI_EVENT_ATTENDERS:
        {
            SyncBroadcastManager.setSyncState(this, "Syncing Event Attenders");

            try
            {
                String tokenID = LoginManager.getInstance(this).getTokenId();
                long eventID = intent.getLongExtra(EXTRA_EVENT_ID, -1);
                HttpURLConnection connection = Event.Api.getPeople(tokenID, eventID, Event.Api.PeopleSelection.ALL);
                ApiRequest.getJsonFromConnection(code, connection, this, false);
            }
            catch (IOException e)
            {
                Log.e(InEvent.NAME, "Couldn't create connection for event.getPeople(tokenID, eventID, selection)", e);
            }

            // stop the sync state
            SyncBroadcastManager.setSyncState(this, false);
            break;
        }

        case InEventProvider.URI_ACTIVITY:
        {
            SyncBroadcastManager.setSyncState(this, "Syncing Activities");

            try
            {
                long eventID = intent.getLongExtra(EXTRA_EVENT_ID, -1);
                HttpURLConnection connection = Event.Api.getActivities(eventID);
                ApiRequest.getJsonFromConnection(code, connection, this, false);
            }
            catch (IOException e)
            {
                Log.e(InEvent.NAME, "Couldn't create connection for event.getActivities(eventID)", e);
            }

            // stop the sync state
            SyncBroadcastManager.setSyncState(this, false);
            break;
        }

        case InEventProvider.URI_ACTIVITY_SCHEDULE:
        {
            SyncBroadcastManager.setSyncState(this, "Syncing Schedule");

            try
            {
                String tokenID = LoginManager.getInstance(this).getTokenId();
                long eventID = intent.getLongExtra(EXTRA_EVENT_ID, -1);
                HttpURLConnection connection = Event.Api.getSchedule(tokenID, eventID);
                ApiRequest.getJsonFromConnection(code, connection, this, false);
            }
            catch (IOException e)
            {
                Log.e(InEvent.NAME, "Couldn't create connection for event.getSchedule(tokenID, eventID)", e);
            }

            // stop the sync state
            SyncBroadcastManager.setSyncState(this, false);
            break;
        }

        case InEventProvider.URI_ACTIVITY_ATTENDERS:
        {
            SyncBroadcastManager.setSyncState(this, "Syncing Attenders");

            try
            {
                String tokenID = LoginManager.getInstance(this).getTokenId();
                long eventID = intent.getLongExtra(EXTRA_ACTIVITY_ID, -1);
                HttpURLConnection connection = Activity.Api.getPeople(tokenID, eventID, Activity.Api.PeopleSelection.ALL);
                ApiRequest.getJsonFromConnection(code, connection, this, false);
            }
            catch (IOException e)
            {
                Log.e(InEvent.NAME, "Couldn't create connection for event.getPeople(tokenID, activityID, selection)", e);
            }

            // stop the sync state
            SyncBroadcastManager.setSyncState(this, false);
            break;
        }

        default:
            break;
        }

        Log.v(InEvent.NAME, "Done with " + data);
    }


    @Override
    public void handleResponse(int requestCode, JSONObject json, int responseCode)
    {
        if (responseCode != HttpStatus.SC_OK || json == null)
        {
            // Error!
            int message = Utils.getBadResponseMessage(requestCode, responseCode);

            // Send the message to the user
            sendBroadcast(new Intent(InEvent.ACTION_TOAST_NOTIFICATION).putExtra(InEvent.EXTRA_TOAST_MESSAGE, message));

            return;
        }

        switch (requestCode)
        {
        case InEventProvider.URI_EVENT:
        {
            ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
            
            try
            {
                // Delete the previous events
                operations.add(
                        ContentProviderOperation
                            .newDelete(Event.CONTENT_URI)
                            .build()
                );

                LoginManager loginManager = LoginManager.getInstance(getBaseContext());
                boolean signedIn = loginManager.isSignedIn();
                String where = signedIn ? EventMember.Columns.MEMBER_ID_FULL+"="+loginManager.getMember().memberId : null;

                // and eventMembers
                operations.add(
                        ContentProviderOperation
                            .newDelete(EventMember.CONTENT_URI)
                            .withSelection(where, null)
                            .build()
                );

                // Get the new ones
                JSONArray eventArray = json.getJSONArray(JsonUtils.DATA);
                for (int i = 0; i < eventArray.length(); i++)
                {
                    // Parse the json
                    JSONObject jobj = eventArray.getJSONObject(i);
                    ContentValues values = Event.valuesFromJson(jobj);

                    // Add the insert operation
                    operations.add(
                            ContentProviderOperation
                                .newInsert(Event.CONTENT_URI)
                                .withValues(values)
                                .build()
                    );

                    if (signedIn && jobj.getInt(EventMember.Columns.APPROVED) == 1)
                    {
                        values = EventMember.valuesFromJson(jobj, jobj.getLong(JsonUtils.ID));
                        operations.add(
                                ContentProviderOperation
                                    .newInsert(EventMember.CONTENT_URI)
                                    .withValues(values)
                                    .build()
                        );
                    }
                }

                getContentResolver().applyBatch(InEventProvider.AUTHORITY, operations);
            }
            catch (JSONException e)
            {
                Log.w(InEvent.NAME, "Couldn't properly get the Events from the json = "+json, e);
            }
            catch (RemoteException e)
            {
                Log.e(InEvent.NAME, "...", e);
            }
            catch (OperationApplicationException e)
            {
                Log.e(InEvent.NAME, "Failed while adding Events to the database", e);
            }
            break;
        }

        case InEventProvider.URI_EVENT_ATTENDERS:
        {
            ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

            try
            {
                // Get some info
                long eventID = mIntent.getLongExtra(EXTRA_EVENT_ID, -1);

                // Delete the previous stored event members
                operations.add(
                        ContentProviderOperation
                            .newDelete(Member.CONTENT_URI)
                            .withSelection(Member.Columns._ID_FULL+" IN (" +
                            		"SELECT "+EventMember.Columns.MEMBER_ID_FULL+
                            		" FROM "+EventMember.TABLE_NAME+
                            		" WHERE "+EventMember.Columns.EVENT_ID_FULL+"="+eventID+")", null)
                            .build()
                );
                operations.add(
                        ContentProviderOperation
                            .newDelete(EventMember.CONTENT_URI)
                            .withSelection(EventMember.Columns.EVENT_ID_FULL+"="+eventID, null)
                            .build()
                );

                // Get the new ones
                JSONArray peopleArray = json.getJSONArray(JsonUtils.DATA);
                for (int i = 0; i < peopleArray.length(); i++)
                {
                    // Parse the json
                    JSONObject jobj = peopleArray.getJSONObject(i);
                    ContentValues values = EventMember.valuesFromJson(jobj, eventID);

                    // Add the insert operation
                    operations.add(
                            ContentProviderOperation
                                .newInsert(EventMember.CONTENT_URI)
                                .withValues(values)
                                .build()
                    );

                    // Parse the Member info
                    values = Member.valuesFromJson(jobj);

                    operations.add(
                            ContentProviderOperation
                                .newInsert(Member.CONTENT_URI)
                                .withValues(values)
                                .build()
                    );
                }

                getContentResolver().applyBatch(InEventProvider.AUTHORITY, operations);
            }
            catch (JSONException e)
            {
                Log.w(InEvent.NAME, "Couldn't properly get the Event Attenders from the json = "+json, e);
            }
            catch (RemoteException e)
            {
                Log.e(InEvent.NAME, "", e);
            }
            catch (OperationApplicationException e)
            {
                Log.e(InEvent.NAME, "Failed while adding Event Attenders to the database", e);
            }
            break;
        }

        case InEventProvider.URI_ACTIVITY:
        {
            ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

            try
            {
                // Get some info
                long eventID = mIntent.getLongExtra(EXTRA_EVENT_ID, -1);

                // Delete the previous stored activities
                operations.add(
                        ContentProviderOperation
                            .newDelete(Activity.ACTIVITY_CONTENT_URI)
                            .withSelection(Activity.Columns.EVENT_ID_FULL+"="+eventID, null)
                            .build()
                );

                // Get the new ones
                JSONArray dayArray = json.getJSONArray(JsonUtils.DATA);
                for (int i = 0; i < dayArray.length(); i++)
                {
                    JSONArray activityArray = dayArray.getJSONArray(i);
                    for (int j = 0; j < activityArray.length(); j++)
                    {
                        // Parse the json
                        ContentValues values = Activity.valuesFromJson(activityArray.getJSONObject(j), eventID);

                        // Add the insert operation
                        operations.add(
                                ContentProviderOperation
                                    .newInsert(Activity.ACTIVITY_CONTENT_URI)
                                    .withValues(values)
                                    .build()
                        );
                    }
                }

                getContentResolver().applyBatch(InEventProvider.AUTHORITY, operations);
            }
            catch (JSONException e)
            {
                Log.w(InEvent.NAME, "Couldn't properly get the Activities from the json = "+json, e);
            }
            catch (RemoteException e)
            {
                Log.e(InEvent.NAME, "", e);
            }
            catch (OperationApplicationException e)
            {
                Log.e(InEvent.NAME, "Failed while adding Activities to the database", e);
            }
            break;
        }

        case InEventProvider.URI_ACTIVITY_SCHEDULE:
        {
            ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

            try
            {
                // Get some info
                long eventID = mIntent.getLongExtra(EXTRA_EVENT_ID, -1);
                long memberID = LoginManager.getInstance(this).getMember().memberId;

                // Delete the previous stored activities
                operations.add(
                        ContentProviderOperation
                            .newDelete(Activity.SCHEDULE_CONTENT_URI)
                            .withSelection(
                                    ActivityMember.Columns.EVENT_ID_FULL  +"="+ eventID + " AND " +
                                    ActivityMember.Columns.MEMBER_ID_FULL +"="+ memberID 
                                    , null)
                            .build()
                );

                // Get the new ones
                JSONArray dayArray = json.getJSONArray(JsonUtils.DATA);
                for (int i = 0; i < dayArray.length(); i++)
                {
                    JSONArray activityArray = dayArray.getJSONArray(i);
                    for (int j = 0; j < activityArray.length(); j++)
                    {
                        // Parse the json
                        JSONObject jobj = activityArray.getJSONObject(j);

                        long activityID = jobj.getLong(JsonUtils.ID);
                        boolean approved = jobj.getInt(ActivityMember.Columns.APPROVED) == 1;

                        // Gather the values
                        ContentValues values = ActivityMember.newActivtyMember(eventID, activityID, memberID, approved);

                        // Add the insert operation
                        operations.add(
                                ContentProviderOperation
                                    .newInsert(Activity.SCHEDULE_CONTENT_URI)
                                    .withValues(values)
                                    .build()
                        );
                    }
                }

                getContentResolver().applyBatch(InEventProvider.AUTHORITY, operations);
            }
            catch (JSONException e)
            {
                Log.w(InEvent.NAME, "Couldn't properly get the Activities Schedule from the json = "+json, e);
            }
            catch (RemoteException e)
            {
                Log.e(InEvent.NAME, "", e);
            }
            catch (OperationApplicationException e)
            {
                Log.e(InEvent.NAME, "Failed while adding Activities Schedule to the database", e);
            }
            break;
        }

        case InEventProvider.URI_ACTIVITY_ATTENDERS:
        {
            ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

            try
            {
                // Get some info
                long activityID = mIntent.getLongExtra(EXTRA_ACTIVITY_ID, -1);
                long eventID = mIntent.getLongExtra(EXTRA_EVENT_ID, -1);

                // Delete the previous stored activity members
                operations.add(
                        ContentProviderOperation
                            .newDelete(ActivityMember.CONTENT_URI)
                            .withSelection(ActivityMember.Columns.ACTIVITY_ID_FULL+"="+activityID, null)
                            .build()
                );

                // Get the new ones
                JSONArray peopleArray = json.getJSONArray(JsonUtils.DATA);
                for (int i = 0; i < peopleArray.length(); i++)
                {
                    // Parse the json
                    JSONObject jobj = peopleArray.getJSONObject(i);

                    long memberID = jobj.getLong(Member.MEMBER_ID);
                    boolean approved = jobj.getInt(ActivityMember.Columns.APPROVED) == 1;
                    boolean present = jobj.getInt(ActivityMember.Columns.PRESENT) == 1;

                    // Parse the member
                    ContentValues values = ActivityMember.newActivtyMember(eventID, activityID, memberID, approved, present);

                    // Parse the link member-activity
                    // Add the insert operation
                    operations.add(
                            ContentProviderOperation
                                .newInsert(ActivityMember.CONTENT_URI)
                                .withValues(values)
                                .build()
                    );
                }

                getContentResolver().applyBatch(InEventProvider.AUTHORITY, operations);
            }
            catch (JSONException e)
            {
                Log.w(InEvent.NAME, "Couldn't properly get the Activities Attenders from the json = "+json, e);
            }
            catch (RemoteException e)
            {
                Log.e(InEvent.NAME, "", e);
            }
            catch (OperationApplicationException e)
            {
                Log.e(InEvent.NAME, "Failed while adding Activities Attenders to the database", e);
            }
            break;
        }

        default:
            break;
        }
    }
}