package com.yng.partyhunt;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.app.partyhunt.R;
import com.yng.partyhunt.database.DBCParties;

import java.util.ArrayList;

/**
 * Created by yng1905 on 6/24/14.
 */
public class ListScreen extends Fragment {

    ListView lvParties;
    DBCParties dbParties;

    private NfcAdapter nfcAdapter;

    ArrayList<ViewPartiesList> oParties;
    private AdapterParties adapterParties;
    private Button btnShare;

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_list_screen, container, false);
        lvParties = (ListView) rootView.findViewById(R.id.lvPartyList);
        dbParties = new DBCParties(getActivity());
        btnShare = (Button)rootView.findViewById(R.id.btnWriteTag);

        if(getActivity().getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC))
        {

            btnShare.setOnClickListener(btnWriteTagClick);
        }
        else
            btnShare.setVisibility(View.GONE);

        return rootView;
    }

    private View.OnClickListener btnWriteTagClick = new View.OnClickListener() {

        public void onClick(View v) {

            Intent myIntent = new Intent(getActivity(), NFCShareScreen.class);
            startActivity(myIntent);
        }
    };

    @Override
    public void onStart()
    {
        super.onStart();
        BindItems();
    }

    private void BindItems()
    {
        Cursor cParties = (Cursor) dbParties.Select(dbParties,"SELECT idNr,name,beginDate,endDate,startTime,endTime,attendersCount FROM " + dbParties.tableName);
        oParties = ViewPartiesList.LoadListFromCursor(cParties);
        adapterParties = new AdapterParties(getActivity(),R.layout.adapter_parties,oParties);
        if (lvParties.getAdapter() != null)
            lvParties.setAdapter(null);
        if (oParties.size() > 0)
            lvParties.setAdapter(adapterParties);

        lvParties.setClickable(true);
        lvParties.setOnItemClickListener(onClickLvParties);

    }

    private AdapterView.OnItemClickListener onClickLvParties = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {

            Intent myIntent = new Intent(getActivity(), PartyDetailScreen.class);
            myIntent.putExtra("PartyId",String.valueOf(oParties.get(position).idNr));
            myIntent.putExtra("ScreenMode","ListDetail");
            startActivity(myIntent);
        }
    };

    private static class ViewPartiesList {
             public Long idNr;
             public String name;
             public String beginDate;
             public String endDate;
             public String startTime;
             public String endTime;
             public String attendersCount;

             public static ViewPartiesList LoadFromCursor(Cursor oCursor) {
                 ViewPartiesList vpl = new ViewPartiesList();

                 if (oCursor == null || oCursor.getCount() < 0 || oCursor.getColumnCount() <= 0)
                     return null;

                 vpl.idNr = oCursor.getLong(0);
                 vpl.name = oCursor.getString(1);
                 vpl.beginDate = oCursor.getString(2);
                 vpl.endDate = oCursor.getString(3);
                 vpl.startTime = oCursor.getString(4);
                 vpl.endTime = oCursor.getString(5);
                 vpl.attendersCount = oCursor.getString(6);
                 return vpl;
             }

             public static ArrayList<ViewPartiesList> LoadListFromCursor(Cursor oCursor) {

                 ArrayList<ViewPartiesList> avpl = new ArrayList<ViewPartiesList>();
                 avpl.clear();

                 if (oCursor != null && oCursor.getCount() > 0) {
                     oCursor.moveToFirst();
                     do {
                         avpl.add(ViewPartiesList.LoadFromCursor(oCursor));
                     } while (oCursor.moveToNext());
                 }

                 return avpl;
             }
         }

    public class AdapterParties extends ArrayAdapter<ViewPartiesList> {

             private ArrayList<ViewPartiesList> mItems;
             TextView txtName;
             TextView txtCount;
             TextView txtTime;
             View v;

             private final Object mLock = new Object();

             @SuppressWarnings("unchecked")
             public AdapterParties(Context context, int pTextViewResourceId, ArrayList<ViewPartiesList> pItems) {
                 super(context, pTextViewResourceId, pItems);
                 this.mItems = pItems;

             }

             @Override
             public View getView(int position, View convertView, ViewGroup parent) {

                 View v = convertView;
                 int pos;

                 if (v == null) {
                     LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                     v = vi.inflate(R.layout.adapter_parties, null);
                 }


                 if (mItems != null) {
                     pos = position;
                     txtName = (TextView) v.findViewById(R.id.lblName);
                     txtCount = (TextView) v.findViewById(R.id.lblCount);
                     txtTime = (TextView) v.findViewById(R.id.lblTime);

                     if (txtName != null)
                         txtName.setText(mItems.get(position).name);
                     if (txtCount != null)
                         txtCount.setText("Check-in: " + mItems.get(position).attendersCount);
                     if (txtTime != null)
                         txtTime.setText("Day: " + mItems.get(position).beginDate
                                        +" Start : " + mItems.get(position).startTime
                                        +" End : " + mItems.get(position).endTime);
                 }

                 return v;
             }

         }



}
