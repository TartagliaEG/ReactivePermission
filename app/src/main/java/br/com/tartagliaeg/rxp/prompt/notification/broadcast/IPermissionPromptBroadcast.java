package br.com.tartagliaeg.rxp.prompt.notification.broadcast;

import android.content.Context;
import android.os.Parcelable;

import br.com.tartagliaeg.rxp.domain.PermissionPack;

/**
 * Created by TartagliaEG on 2018/06/29.
 * ...
 */
public interface IPermissionPromptBroadcast {
  interface Registration {
    void register(Context context, OnChannelConnected connected, OnResult result);
    void unregister(Context context);
  }

  interface Channel extends Parcelable {
    void notifyChannelConnected(Context context);
    void sendResponse(Context context, String... permissions);
  }

  interface OnResult {
    void onResult(Context context, PermissionPack pack);
  }

  interface OnChannelConnected {
    void connected(Context context);
  }

}
