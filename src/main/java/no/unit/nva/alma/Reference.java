package no.unit.nva.alma;

import com.google.gson.annotations.SerializedName;

public class Reference {

    @SerializedName("title")
    private String title;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }
}
