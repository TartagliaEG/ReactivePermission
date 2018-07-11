package br.com.tartagliaeg.rxp.prompt.notification.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcel;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import br.com.tartagliaeg.rxp.debug.LogTag;
import br.com.tartagliaeg.rxp.domain.PermissionPack;

/**
 * Created by TartagliaEG on 2018/06/29.
 * ...
 */
public class PermissionPromptBroadcast implements IPermissionPromptBroadcast.Registration, IPermissionPromptBroadcast.Channel {
  private static final String TAG = LogTag.TAG + PermissionPromptBroadcast.class.getSimpleName();
  private static final String EXT_PERMISSIONS = TAG + ".PERMISSIONS";
  private static final String EXT_CONNECTED = TAG + ".CONNECTED";

  private final String mBroadcastResultFilter;
  private BroadcastReceiver mBroadcastResultListener;


  private PermissionPromptBroadcast(String filter) {
    mBroadcastResultFilter = TAG + ".broadcast_result:" + filter;
  }

  public static IPermissionPromptBroadcast.Registration createBroadcastRegistration(String filter) {
    return new PermissionPromptBroadcast(filter);
  }

  public static IPermissionPromptBroadcast.Channel createBroadcastChannel(String filter) {
    return new PermissionPromptBroadcast(filter);
  }

  @Override
  public void register(Context context, final IPermissionPromptBroadcast.OnChannelConnected connected, final IPermissionPromptBroadcast.OnResult receiver) {
    Log.d(TAG, "Register: " + mBroadcastResultFilter);
    mBroadcastResultListener = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(EXT_CONNECTED))
          connected.connected(context);
        else
          receiver.onResult(context, new PermissionPack(context, intent.getStringArrayExtra(EXT_PERMISSIONS)));
      }
    };

    LocalBroadcastManager
      .getInstance(context)
      .registerReceiver(mBroadcastResultListener, new IntentFilter(mBroadcastResultFilter));
  }

  @Override
  public void unregister(Context context) {
    Log.d(TAG, "Unregister: " + mBroadcastResultFilter);
    LocalBroadcastManager
      .getInstance(context)
      .unregisterReceiver(mBroadcastResultListener);

  }

  @Override
  public void sendResponse(Context context, String... permissions) {
    Log.d(TAG, "sendResponse: " + mBroadcastResultFilter);
    Intent intent = new Intent(mBroadcastResultFilter);
    intent.putExtra(EXT_PERMISSIONS, permissions);

    LocalBroadcastManager
      .getInstance(context)
      .sendBroadcast(intent);
  }

  @Override
  public void notifyChannelConnected(Context context) {
    Intent intent = new Intent(mBroadcastResultFilter);
    intent.putExtra(EXT_CONNECTED, true);

    LocalBroadcastManager
      .getInstance(context)
      .sendBroadcast(intent);
  }


  /* Parcelable */

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.mBroadcastResultFilter);
  }

  protected PermissionPromptBroadcast(Parcel in) {
    this.mBroadcastResultFilter = in.readString();
  }

  public static final Creator<PermissionPromptBroadcast> CREATOR = new Creator<PermissionPromptBroadcast>() {
    @Override
    public PermissionPromptBroadcast createFromParcel(Parcel source) {
      return new PermissionPromptBroadcast(source);
    }

    @Override
    public PermissionPromptBroadcast[] newArray(int size) {
      return new PermissionPromptBroadcast[size];
    }
  };
}
