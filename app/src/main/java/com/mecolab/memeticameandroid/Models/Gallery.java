package com.mecolab.memeticameandroid.Models;

import android.graphics.Bitmap;
import android.net.Uri;

public class Gallery {
	private Bitmap image;
	private String title;


	public Uri getUri() {
		return uri;
	}

	private Uri uri;
	private String mime;
	public String getMime() {
		return mime;
	}

	public Gallery(Bitmap image, String title, String mime, Uri uri) {
		super();
		this.image = image;
		this.title = title;
		this.mime=mime;
		this.uri=uri;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}