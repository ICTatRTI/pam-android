package org.rti.rcd.researchstack.researchnet.body;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by apreston on 4/12/17.
 */

public class SubmissionBody {

    public SubmissionBody() {
        this.response = new HashMap<>();
    }

    @SerializedName("time_start")
    private String timeStart;

    @SerializedName("time_complete")
    private String timeComplete;

    @SerializedName("device_id")
    private String deviceId;

    @SerializedName("lat")
    private String latitude;

    @SerializedName("long")
    private String longitude;

    private Map<String,String> response;

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeComplete() {
        return timeComplete;
    }

    public void setTimeComplete(String timeComplete) {
        this.timeComplete = timeComplete;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void addResponse(String key, String value){
        response.put(key,value);
    }
}
