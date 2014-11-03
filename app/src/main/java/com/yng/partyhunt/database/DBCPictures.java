package com.yng.partyhunt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by yng1905 on 6/11/14.
 */
public class DBCPictures {
    public String tableName = "TBL_PICTURES";
    private Long idNr;
    private String URI;

    private DatabaseHelper dbh;

    public DBCPictures(Context ctx)
    {
        dbh = new DatabaseHelper(ctx);
    }
    public void setIdNr(Long idNr) {
        this.idNr = idNr;
    }

    public Long getIdNr() {
        return idNr;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public String getURI() {
        return URI;
    }

    public ContentValues GetContentValues() {

        ContentValues vals = new ContentValues();

        vals.put("idNr", idNr);
        vals.put("URI", URI);
        return vals;

    }

    public Long Insert()
    {
        Long id = dbh.Insert(tableName, GetContentValues());
        return id;
    }

    public Cursor Select(DBCPictures Group,String selectQuery)
    {
        return dbh.Select(selectQuery);
    }

    public int Update(Long id)
    {
        return dbh.Update(tableName,GetContentValues(),"idNr = ?",new String[] { String.valueOf(String.valueOf(id)) });
    }


    public void Delete(Long id)
    {
        dbh.Delete(tableName,"picId = ?",new String[] { String.valueOf(String.valueOf(id)) });
    }
}

