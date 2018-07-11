package br.com.tartagliaeg.rxp.debug;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import br.com.tartagliaeg.rxp.domain.PermissionPack;
import br.com.tartagliaeg.rxp.domain.PermissionRequest;
import br.com.tartagliaeg.rxp.domain.PermissionRequests;
import br.com.tartagliaeg.rxp.prompt.notification.PermissionPromptNotification;
import br.com.tartagliaeg.rxp.utils.SimpleObserver;

public class TestService extends LogService {
  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return super.onBind(intent);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    super.onStartCommand(intent, flags, startId);

    PermissionPromptNotification mPerms = new PermissionPromptNotification(this);

    mPerms.promptPermissions(PermissionRequest.newRequestFrom(
      PermissionRequests.CAMERA,
      PermissionRequests.ACCESS_FINE_LOCATION
    )).subscribe(new SimpleObserver<PermissionPack>() {
      @Override
      public void onNext(PermissionPack pack) {
        Log.d(getClass().getSimpleName(), pack.toString());
      }

      @Override
      public void onComplete() {
        super.onComplete();
        Log.d(getClass().getSimpleName(), "onComplete called. Finishing...");
        stopSelf();
      }
    });

    return START_STICKY_COMPATIBILITY;
  }
}
