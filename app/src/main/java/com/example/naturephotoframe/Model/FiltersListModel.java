package com.example.naturephotoframe.Model;

import android.net.Uri;

public class FiltersListModel {
    String mFilterText;
    Uri mThumbnail;

    public FiltersListModel(String mFilterText, Uri mThumbnai) {
        this.mFilterText = mFilterText;
        this.mThumbnail = mThumbnai;
    }

    public String getmFilterText() {
        return mFilterText;
    }

    public void setmFilterText(String mFilterText) {
        this.mFilterText = mFilterText;
    }

    public Uri getmThumbnail() {
        return mThumbnail;
    }

    public void setmThumbnail(Uri mThumbnai) {
        this.mThumbnail = mThumbnai;
    }
}
