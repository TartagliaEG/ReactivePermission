package br.com.tartagliaeg.rxp.prompt.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import java.util.List;
import java.util.UUID;

import br.com.tartagliaeg.rxp.ReactivePermissionConfiguration;
import br.com.tartagliaeg.rxp.debug.LogTag;
import br.com.tartagliaeg.rxp.domain.Permission;
import br.com.tartagliaeg.rxp.domain.PermissionPack;
import br.com.tartagliaeg.rxp.domain.PermissionRequest;
import br.com.tartagliaeg.rxp.prompt.IPermissionPrompt;
import br.com.tartagliaeg.rxp.prompt.notification.broadcast.IPermissionPromptBroadcast;
import br.com.tartagliaeg.rxp.prompt.notification.broadcast.PermissionPromptBroadcast;
import br.com.tartagliaeg.rxp.utils.Pointer;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;


/**
 * Created by TartagliaEG on 2018/06/29.
 * ...
 * TODO: Handle situations where more than one notification is being shown
 */
public class PermissionPromptNotification implements IPermissionPrompt {
  private static final String TAG = LogTag.TAG + PermissionPromptNotification.class.getSimpleName();
  private static final String NOTIFICATION_CHANNEL = TAG + ".notification_channel";
  private static final int NOTIFICATION_ID = 100101;

  private final Context mContext;
  private final IPermissionPromptNotification.Sender mNotificationSender;
  private final IPermissionPromptNotification.ChannelRegistration mNotificationChannelRegistration;

  public PermissionPromptNotification(
    @NonNull Context context,
    @NonNull IPermissionPromptNotification.Sender notificationFactory,
    @NonNull IPermissionPromptNotification.ChannelRegistration channelRegistration) {

    mContext = context;
    mNotificationSender = notificationFactory;
    mNotificationChannelRegistration = channelRegistration;
  }

  public PermissionPromptNotification(@NonNull Context context) {
    this(context,
      new IPermissionPromptNotification.Sender() {
        @Override
        public void sendNotification(Context context, String channelId, int notificationId, PermissionPromptNotificationActivityArgs args) {
          Intent intent = PermissionPromptNotificationActivity.newIntent(context, args);
          PendingIntent pi = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_ONE_SHOT);

          ReactivePermissionConfiguration config = ReactivePermissionConfiguration.getInstance();

          Notification notification = new NotificationCompat.Builder(context, channelId)
            .setContentIntent(pi)
            .setSmallIcon(config.getNotificationIcon())
            .setContentText(context.getResources().getString(config.getNotificationContent()))
            .setContentTitle(context.getResources().getString(config.getNotificationTitle()))
            .setAutoCancel(true)
            .build();

          NotificationManager nm = getNotificationManager(context);

          nm.notify(notificationId, notification);
        }
      },
      new IPermissionPromptNotification.ChannelRegistration() {
        @Override
        public void registerNotificationChannelIfNeeded(Context context, String channelId) {
          if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return;

          NotificationManager nm = getNotificationManager(context);

          for (NotificationChannel c : nm.getNotificationChannels())
            if (c.getId().equals(channelId)) return;

          NotificationChannel channel = new NotificationChannel(channelId, "Permissions", NotificationManager.IMPORTANCE_DEFAULT);
          nm.createNotificationChannel(channel);
        }
      });
  }

  @Override
  public Observable<PermissionPack> promptPermissions(final PermissionRequest request) {
    PermissionPack pack = new PermissionPack(mContext, request.getPermissionNames());

    if (pack.isAllGranted())
      return Observable.just(pack);

    return buildPermissionPromptNotificationStream(request)
      .filter(new Predicate<Permission>() {
        @Override
        public boolean test(Permission permission) throws Exception {
          return request.contains(permission.getName());
        }
      })
      .toList()
      .map(new Function<List<Permission>, PermissionPack>() {
        @Override
        public PermissionPack apply(List<Permission> permissions) throws Exception {
          return PermissionPack.from(permissions);
        }
      })
      .toObservable();
  }


  private Observable<Permission> buildPermissionPromptNotificationStream(final PermissionRequest request) {
    final String uuidFilter = UUID.randomUUID().toString();
    final IPermissionPromptBroadcast.Registration registration = PermissionPromptBroadcast.createBroadcastRegistration(uuidFilter);
    final PublishSubject<Permission> publisher = PublishSubject.create();
    final Pointer<Boolean> isRegistered = new Pointer<>(false);

    return publisher.doOnSubscribe(new Consumer<Disposable>() {
      @Override
      public void accept(Disposable disposable) throws Exception {
        if (isRegistered.value)
          return;

        // Register the notification channel for Android O
        mNotificationChannelRegistration.registerNotificationChannelIfNeeded(mContext, NOTIFICATION_CHANNEL);

        // Register to the broadcast so we get notified when the response is sent
        registration.register(mContext, new IPermissionPromptBroadcast.OnChannelConnected() {
          @Override
          public void connected(Context context) {
            getNotificationManager(context).cancel(NOTIFICATION_ID);
          }
        }, new IPermissionPromptBroadcast.OnResult() {
          @Override
          public void onResult(Context context, PermissionPack pack) {
            for (Permission permission : pack.getPackFilteredByPermissions(request.getPermissionNames()).iterable())
              publisher.onNext(permission);
            publisher.onComplete();
          }
        });

        // Display the notification
        mNotificationSender.sendNotification(
          mContext,
          NOTIFICATION_CHANNEL,
          NOTIFICATION_ID,
          new PermissionPromptNotificationActivityArgs(
            PermissionPromptBroadcast.createBroadcastChannel(uuidFilter),
            request
          )
        );

        isRegistered.value = true;

      }
    }).doOnError(new Consumer<Throwable>() {
      @Override
      public void accept(Throwable throwable) throws Exception {
        registration.unregister(mContext);
      }

    })
      .doOnComplete(new Action() {
        @Override
        public void run() throws Exception {
          registration.unregister(mContext);
        }

      })
      .doOnDispose(new Action() {
        @Override
        public void run() throws Exception {
          registration.unregister(mContext);
        }
      });
  }

  @NonNull
  private static NotificationManager getNotificationManager(Context context) {
    NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    assert nm != null;
    return nm;
  }

}
