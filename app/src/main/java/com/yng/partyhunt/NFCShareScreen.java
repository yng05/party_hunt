package com.yng.partyhunt;


import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.app.partyhunt.R;
import com.yng.partyhunt.database.DBCParties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yng1905 on 6/29/14.
 */
public class NFCShareScreen extends ActionBarActivity {
    private static final String TAG = "PartyHuntNFCSharing";
    private boolean mResumed = false;
    private boolean mWriteMode = false;
    NfcAdapter mNfcAdapter;
    DBCParties dbParties;
    //EditText mNote;

    PendingIntent mNfcPendingIntent;
    IntentFilter[] mWriteTagFilters;
    IntentFilter[] mNdefExchangeFilters;

    Button btnShare;
    String tableParties;


    ArrayList<HashMap<String, String>> mylist =
            new ArrayList<HashMap<String, String>>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        dbParties = new DBCParties(this);
        tableParties = getListJson();
        setContentView(R.layout.activity_nfcshare_screen);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        mNfcPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, ((Object) this).getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // Intent filters for reading a note from a tag or exchanging over p2p.
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefDetected.addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) { }
        mNdefExchangeFilters = new IntentFilter[] { ndefDetected };

        // Intent filters for writing to a tag
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mWriteTagFilters = new IntentFilter[] { tagDetected };


    }

    @Override
    protected void onResume() {
        super.onResume();
        mResumed = true;
        // Sticky notes received from Android
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            NdefMessage[] messages = getNdefMessages(getIntent());
            byte[] payload = messages[0].getRecords()[0].getPayload();
            setNoteBody(new String(payload));
            setIntent(new Intent()); // Consume this intent.
        }
        enableNdefExchangeMode();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mResumed = false;
        mNfcAdapter.disableForegroundNdefPush(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // NDEF exchange mode
        if (!mWriteMode && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            NdefMessage[] msgs = getNdefMessages(intent);
            promptForContent(msgs[0]);
        }

        // Tag writing mode
        if (mWriteMode && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            writeTag(getNoteAsNdef(), detectedTag);
        }
    }

    private TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

        }

        @Override
        public void afterTextChanged(Editable arg0) {
            if (mResumed) {
                mNfcAdapter.enableForegroundNdefPush(NFCShareScreen.this, getNoteAsNdef());
            }
        }
    };



    private void promptForContent(final NdefMessage msg) {
        new AlertDialog.Builder(this).setTitle("Are you sure to update your list?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        String body = new String(msg.getRecords()[0].getPayload());
                        setNoteBody(body);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                }).show();
    }

    private void setNoteBody(String body) {
        Toast.makeText(this,"Parties are saving...",Toast.LENGTH_LONG).show();
        saveParties(body);
    }

    private NdefMessage getNoteAsNdef() {
        byte[] textBytes = tableParties.getBytes();//mNote.getText().toString().getBytes();
        NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "text/plain".getBytes(),
                new byte[] {}, textBytes);
        return new NdefMessage(new NdefRecord[] {
                textRecord
        });
    }

    NdefMessage[] getNdefMessages(Intent intent) {
        // Parse the intent
        NdefMessage[] msgs = null;
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[] {};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {
                        record
                });
                msgs = new NdefMessage[] {
                        msg
                };
            }
        } else {
            Log.d(TAG, "Unknown intent.");
            finish();
        }
        return msgs;
    }

    private void enableNdefExchangeMode() {
        mNfcAdapter.enableForegroundNdefPush(NFCShareScreen.this, getNoteAsNdef());
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mNdefExchangeFilters, null);
    }

    private void disableNdefExchangeMode() {
        mNfcAdapter.disableForegroundNdefPush(this);
        mNfcAdapter.disableForegroundDispatch(this);
    }

    private void enableTagWriteMode() {
        mWriteMode = true;
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mWriteTagFilters = new IntentFilter[] {
                tagDetected
        };
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mWriteTagFilters, null);
    }

    private void disableTagWriteMode() {
        mWriteMode = false;
        mNfcAdapter.disableForegroundDispatch(this);
    }

    boolean writeTag(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;

        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();

                if (!ndef.isWritable()) {
                    toast("Tag is read-only.");
                    return false;
                }
                if (ndef.getMaxSize() < size) {
                    toast("Tag capacity is " + ndef.getMaxSize() + " bytes, message is " + size
                            + " bytes.");
                    return false;
                }

                ndef.writeNdefMessage(message);
                toast("Wrote message to pre-formatted tag.");
                return true;
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        toast("Formatted tag and wrote message");
                        return true;
                    } catch (IOException e) {
                        toast("Failed to format tag.");
                        return false;
                    }
                } else {
                    toast("Tag doesn't support NDEF.");
                    return false;
                }
            }
        } catch (Exception e) {
            toast("Failed to write tag");
        }

        return false;
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private String getListJson()
    {
        Cursor cParties = dbParties.Select(dbParties,"Select * FROM " + dbParties.tableName);

        JSONArray resultSet 	= new JSONArray();

        cParties.moveToFirst();
        while (cParties.isAfterLast() == false) {

            int totalColumn = cParties.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for( int i=0 ;  i< totalColumn ; i++ )
            {
                if( cParties.getColumnName(i) != null )
                {

                    try
                    {

                        if( cParties.getString(i) != null )
                        {
                            Log.d("TAG_NAME", cParties.getString(i) );
                            rowObject.put(cParties.getColumnName(i) ,  cParties.getString(i) );
                        }
                        else
                        {
                            rowObject.put( cParties.getColumnName(i) ,  "" );
                        }
                    }
                    catch( Exception e )
                    {
                        Log.d("TAG_NAME", e.getMessage()  );
                    }
                }

            }

            resultSet.put(rowObject);
            cParties.moveToNext();
        }

        cParties.close();
        Log.d("TAG_NAME", resultSet.toString());
        return resultSet.toString();

    }

    public void saveParties(String responseBody)
    {
        mylist.clear();

        try {

            JSONArray jArrayPhotos = new JSONArray(responseBody);


            for (int j = 0; j < jArrayPhotos.length(); j++) {
                HashMap<String, String> mapP = new HashMap<String, String>();
                JSONObject eParties = jArrayPhotos.getJSONObject(j);
                mapP.put("idNr", eParties.getString("idNr"));
                mapP.put("serverId", eParties.getString("serverId"));
                mapP.put("name", eParties.getString("name"));
                mapP.put("beginDate", eParties.getString("beginDate"));
                mapP.put("endDate", eParties.getString("endDate"));
                mapP.put("startTime", eParties.getString("startTime"));
                mapP.put("endTime", eParties.getString("endTime"));
                mapP.put("attendersCount", eParties.getString("attendersCount"));
                mapP.put("longitude", eParties.getString("longitude"));
                mapP.put("latitude", eParties.getString("latitude"));
                mapP.put("description", eParties.getString("description"));
                mapP.put("mayor", eParties.getString("mayor"));

                mylist.add(mapP);
            }
            for(int i = 0; i< mylist.size();i++){
                Cursor partyResult = dbParties.Select(dbParties, "SELECT Name FROM " + dbParties.tableName + " WHERE serverId = " + mylist.get(i).get("serverId").toString());                 if(partyResult.getCount() > 0)
                    continue;

                dbParties.setServerId(mylist.get(i).get("serverId").toString());
                dbParties.setName(mylist.get(i).get("name").toString());
                dbParties.setBeginDate(mylist.get(i).get("beginDate").toString());
                dbParties.setEndDate(mylist.get(i).get("endDate").toString());
                dbParties.setStartTime(mylist.get(i).get("startTime").toString());
                dbParties.setEndTime(mylist.get(i).get("endTime").toString());
                dbParties.setDescription(mylist.get(i).get("description").toString());
                dbParties.setMayor(mylist.get(i).get("mayor").toString());
                dbParties.setLatitude(mylist.get(i).get("latitude").toString());
                dbParties.setLongitude(mylist.get(i).get("longitude").toString());
                dbParties.setAttendersCount(mylist.get(i).get("attendersCount"));

                dbParties.Insert();
            }


        } catch (JSONException e) {
            Log.d("Error: ",e.toString());
        }
    }

}
