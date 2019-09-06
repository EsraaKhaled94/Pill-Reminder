package com.esraakhaled.apps.pillreminder.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "medicine")
public class Medicine implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    private long id;
    @SerializedName("name")
    private String name;
    @SerializedName("lastTakenTime")
    private long lastTakenTime;
    @SerializedName("isFinished")
    private boolean isFinished;
    @SerializedName("type")
    private String type;
    @SerializedName("timesPerDay")
    private int timesPerDay;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLastTakenTime() {
        return lastTakenTime;
    }

    public void setLastTakenTime(long lastTakenTime) {
        this.lastTakenTime = lastTakenTime;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTimesPerDay() {
        return timesPerDay;
    }

    public void setTimesPerDay(int timesPerDay) {
        this.timesPerDay = timesPerDay;
    }

    public Medicine() {
    }

    protected Medicine(Parcel in) {
        id = in.readLong();
        name = in.readString();
        lastTakenTime = in.readLong();
        isFinished = in.readByte() != 0x00;
        type = in.readString();
        timesPerDay = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeLong(lastTakenTime);
        dest.writeByte((byte) (isFinished ? 0x01 : 0x00));
        dest.writeString(type);
        dest.writeInt(timesPerDay);
    }

    @SuppressWarnings("unused")
    public static final Creator<Medicine> CREATOR = new Creator<Medicine>() {
        @Override
        public Medicine createFromParcel(Parcel in) {
            return new Medicine(in);
        }

        @Override
        public Medicine[] newArray(int size) {
            return new Medicine[size];
        }
    };
}