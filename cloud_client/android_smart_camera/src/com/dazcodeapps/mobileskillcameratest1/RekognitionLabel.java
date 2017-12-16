package com.dazcodeapps.mobileskillcameratest1;

import android.os.Parcel;
import android.os.Parcelable;


public class RekognitionLabel implements Parcelable {

    String labelName;
    String labelConfidence;

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(labelName);
        out.writeString(labelConfidence);
    }

    public static final Parcelable.Creator<RekognitionLabel> CREATOR
            = new Parcelable.Creator<RekognitionLabel>() {
        public RekognitionLabel createFromParcel(Parcel in) {
            return new RekognitionLabel(in);
        }

        public RekognitionLabel[] newArray(int size) {
            return new RekognitionLabel[size];
        }
    };

    public RekognitionLabel(Parcel in) {
        this.labelName = in.readString();
        this.labelConfidence = in.readString();
    }

    public RekognitionLabel() {
    }


    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getLabelConfidence() {
        return labelConfidence;
    }

    public void setLabelConfidence(String labelConfidence) {
        this.labelConfidence = labelConfidence;
    }
}