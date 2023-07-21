package com.lencodigitexer.shedule_app;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class University implements Parcelable {
    private String name;
    private String description;

    public University(String name, String description) {
        this.name = name;
        this.description = description;
    }

    protected University(Parcel in) {
        name = in.readString();
        description = in.readString();
    }

    public static final Creator<University> CREATOR = new Creator<University>() {
        @Override
        public University createFromParcel(Parcel in) {
            return new University(in);
        }

        @Override
        public University[] newArray(int size) {
            return new University[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
    }
}
