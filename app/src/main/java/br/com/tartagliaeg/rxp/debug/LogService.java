package br.com.tartagliaeg.rxp.debug;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import br.com.tartagliaeg.rxp.Constants;

public abstract class LogService extends Service {
  private static final String TAG = Constants.TAG + LogService.class.getSimpleName();

  @Override
  public void onCreate() {
    Log.i(TAG, "onCreate <<");
    super.onCreate();
    Log.i(TAG, "onCreate >>");
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.i(TAG, "onStartCommand <<");
    int x = super.onStartCommand(intent, flags, startId);
    Log.i(TAG, "onStartCommand >>");

    return x;
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    Log.i(TAG, "onBind <<");
    Log.i(TAG, "onBind >>");
    return null;
  }

  @Override
  public boolean onUnbind(Intent intent) {
    Log.i(TAG, "onUnbind <<");
    boolean x = super.onUnbind(intent);
    Log.i(TAG, "onUnbind >>");
    return x;
  }

  @Override
  public void onDestroy() {
    Log.i(TAG, "onDestroy <<");
    super.onDestroy();
    Log.i(TAG, "onDestroy >>");
  }
}
