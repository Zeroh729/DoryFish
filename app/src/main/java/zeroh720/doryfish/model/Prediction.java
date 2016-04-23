package zeroh720.doryfish.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Prediction implements Parcelable{
    private String id;
    private String status;
    private String time;
    private String locationId;
    private String locationName;

    public Prediction(String id, String status, String time, String locationId, String locationName) {
        this.id = id;
        this.status = status;
        this.time = time;
        this.locationId = locationId;
        this.locationName = locationName;
    }

    protected Prediction(Parcel in) {
        id = in.readString();
        status = in.readString();
        time = in.readString();
        locationId = in.readString();
        locationName = in.readString();
    }

    public static final Creator<Prediction> CREATOR = new Creator<Prediction>() {
        @Override
        public Prediction createFromParcel(Parcel in) {
            return new Prediction(in);
        }

        @Override
        public Prediction[] newArray(int size) {
            return new Prediction[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(status);
        dest.writeString(time);
        dest.writeString(locationId);
        dest.writeString(locationName);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}
