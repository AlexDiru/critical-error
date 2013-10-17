package com.alexdiru.criticalerror;

import android.os.Parcel;
import android.os.Parcelable;

//Speed improvement, rewriting point class so it's parcelable and doesn't have to be serialized

public class ParcelablePoint implements Parcelable {
	public int x;
	public int y;
	
	public ParcelablePoint(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(x);
		dest.writeInt(y);
	}
}
