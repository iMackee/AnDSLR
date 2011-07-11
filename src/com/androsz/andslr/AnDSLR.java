package com.androsz.andslr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.SensorManager;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.mtp.MtpConstants;
import android.mtp.MtpDevice;
import android.mtp.MtpDeviceInfo;
import android.mtp.MtpStorageInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import com.androsz.andslr.R;

public class AnDSLR extends Activity implements Runnable {

    private UsbManager mUsbManager;
    private UsbDevice mDevice;
    private UsbDeviceConnection mConnection;
    private UsbEndpoint mEndpointIntr;
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		thumbnailBitmap = LibRaw.unpackThumbnailBitmapToFit("/sdcard/raw.CR2", (short)512, (short)512);
		ImageView iv = new ImageView(this);
		//iv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		iv.setImageBitmap(this.thumbnailBitmap);
		mUsbManager = (UsbManager)getSystemService(Context.USB_SERVICE);
		
		setContentView(iv);
	}

	Bitmap thumbnailBitmap;

	public void onDestroy() {
		thumbnailBitmap.recycle();
		System.gc();
		super.onDestroy();
	}
	
	@Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();
        String action = intent.getAction();
        Log.d(AnDSLR.class.getSimpleName(), action);
        UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            setDevice(device);
        } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            if (mDevice != null && mDevice.equals(device)) {
                setDevice(null);
            }
        }	
    }
	
	private void setDevice(UsbDevice device) {
		Log.d("bbbbbbbbbb", device.toString());
		Toast.makeText(this, device.toString(), Toast.LENGTH_LONG).show();
        if (device.getInterfaceCount() != 1) {
            return;
        }
        UsbInterface intf = device.getInterface(0);
        // device should have one endpoint
        if (intf.getEndpointCount() != 1) {
            return;
        }
        // endpoint should be of type interrupt
        UsbEndpoint ep = intf.getEndpoint(0);
        if (ep.getType() != UsbConstants.USB_ENDPOINT_XFER_BULK) {
            return;
        }
        mDevice = device;
        mEndpointIntr = ep;
        if (device != null) {
            UsbDeviceConnection connection = mUsbManager.openDevice(device);
            if (connection != null && connection.claimInterface(intf, true)) {
                mConnection = connection;
                //Thread thread = new Thread(this);
                //thread.start();

            } else {
                mConnection = null;
            }
         }
    }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
}
