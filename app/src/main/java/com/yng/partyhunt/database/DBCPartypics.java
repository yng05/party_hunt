package com.yng.partyhunt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by yng1905 on 6/11/14.
 */
public class DBCPartypics {
    public String tableName = "TBL_PARTYPICS";
    private Long idNr,partyId,picId;

    private DatabaseHelper dbh;

    public DBCPartypics(Context ctx)
    {
        dbh = new DatabaseHelper(ctx);
    }
    public void setIdNr(Long idNr) {
        this.idNr = idNr;
    }

    public Long getIdNr() {
        return idNr;
    }

    public void setPartyId(Long partyId) {
        this.partyId = partyId;
    }

    public Long getPartyId() {
        return partyId;
    }

    public void setPicId(Long picId) {
        this.picId = picId;
    }

    public Long getPicId() {
        return picId;
    }



    public ContentValues GetContentValues() {

        ContentValues vals = new ContentValues();

        vals.put("idNr", idNr);
        vals.put("partyId", partyId);
        vals.put("picId", picId);
        return vals;

    }

    public Long Insert()
    {
        Long id = dbh.Insert(tableName, GetContentValues());
        return id;
    }

    public Cursor Select(DBCPartypics Group,String selectQuery)
    {
        return dbh.Select(selectQuery);
    }

    public int Update(Long id)
    {
        return dbh.Update(tableName,GetContentValues(),"idNr = ?",new String[] { String.valueOf(String.valueOf(id)) });
    }


    public void Delete(Long id)
    {
        dbh.Delete(tableName,"idNr = ?",new String[] { String.valueOf(String.valueOf(id)) });
    }

    public void DeleteWithPartyId(String id)
    {
        dbh.Delete(tableName,"partyId = ?",new String[] { id });
    }
}


