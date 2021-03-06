package org.jarucas.breakapp.barcode;

/**
 * Created by Javier on 06/09/2018.
 */

import android.content.Context;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

/**
 * Factory for creating a tracker and associated graphic to be associated with a new barcode.  The
 * multi-processor uses this factory to create barcode trackers as needed -- one for each barcode.
 */
public class BarcodeTrackerFactory implements MultiProcessor.Factory<Barcode> {
    private Context mContext;

    public BarcodeTrackerFactory(final Context context) {
        mContext = context;
    }

    @Override
    public Tracker<Barcode> create(final Barcode barcode) {
        return new BarcodeTracker(mContext);
    }
}
