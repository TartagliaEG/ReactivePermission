package br.com.tartagliaeg.rxp.prompt.notification;

import android.content.Context;

/**
 * Created by TartagliaEG on 2018/06/29.
 * ...
 */

public interface IPermissionPromptNotification {

  interface Sender {
    void sendNotification(Context context, String channelId, int notificationId, PermissionPromptNotificationActivityArgs args);
  }

  interface ChannelRegistration {
    void registerNotificationChannelIfNeeded(Context context, String channelId);
  }
}
