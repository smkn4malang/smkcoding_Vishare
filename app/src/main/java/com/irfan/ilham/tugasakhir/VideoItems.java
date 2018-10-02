package com.irfan.ilham.tugasakhir;

public class VideoItems {

    public String getNamaVideo() {
        return namaVideo;
    }

    public void setNamaVideo(String namaVideo) {
        this.namaVideo = namaVideo;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    private String namaVideo;
    private String videoUrl;
    private int tonton;
    private float rating;

    public int getTonton() {
        return tonton;
    }

    public void setTonton(int tonton) {
        this.tonton = tonton;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    private String UID;
}
