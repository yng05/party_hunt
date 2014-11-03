package com.yng.partyhunt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by yng1905 on 6/11/14.
 */
public class DBCParties {
    public String tableName = "TBL_PARTIES";
    private Long idNr;
    private String serverId, name, beginDate, endDate, startTime, endTime,
                   attendersCount, longitude, latitude,
                   description,mayor;

    private DatabaseHelper dbh;

    public DBCParties(Context ctx)
    {
        dbh = new DatabaseHelper(ctx);
    }
    public void setIdNr(Long idNr) {
        this.idNr = idNr;
    }

    public Long getIdNr() {
        return idNr;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getServerId() {
        return serverId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setAttendersCount(String attendersCount) {
        this.attendersCount = attendersCount;
    }

    public String getAttendersCount() {
        return attendersCount;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setMayor(String mayor) {
        this.mayor = mayor;
    }

    public String getMayor() {
        return mayor;
    }

    public ContentValues GetContentValues() {

        ContentValues vals = new ContentValues();

        vals.put("idNr", idNr);
        vals.put("serverId", serverId);
        vals.put("name", name);
        vals.put("beginDate", beginDate);
        vals.put("endDate", endDate);
        vals.put("startTime", startTime);
        vals.put("endTime", endTime);
        vals.put("attendersCount", attendersCount);
        vals.put("longitude", longitude);
        vals.put("latitude", latitude);
        vals.put("description", description);
        vals.put("mayor", mayor);
        return vals;

    }

    public Long Insert()
    {
        Long id = dbh.Insert(tableName, GetContentValues());
        return id;
    }

    public Cursor Select(DBCParties Group,String selectQuery)
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
}
