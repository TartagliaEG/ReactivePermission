package br.com.tartagliaeg.rxp.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;

public class Intents {
  private static Intents sInstance;

  @NonNull
  public static Intents getsInstance() {
    return sInstance = sInstance == null ? new Intents() : sInstance;
  }

  private Intents() {
  }

  public Intent createAppSettingsIntent(@NonNull Context context) {
    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    Uri uri = Uri.fromParts("package", context.getPackageName(), null);
    intent.setData(uri);
    return intent;
  }
}
