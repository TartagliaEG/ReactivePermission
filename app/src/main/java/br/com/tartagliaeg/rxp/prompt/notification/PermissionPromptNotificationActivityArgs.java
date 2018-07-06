package br.com.tartagliaeg.rxp.prompt.notification;

import android.os.Parcel;
import android.os.Parcelable;

import br.com.tartagliaeg.rxp.prompt.notification.broadcast.IPermissionPromptBroadcast;
import br.com.tartagliaeg.rxp.domain.PermissionRequest;

/**
 * Created by TartagliaEG on 2018/06/30.
 * ...
 */
public class PermissionPromptNotificationActivityArgs implements Parcelable {
  final PermissionRequest mRequest;
  final IPermissionPromptBroadcast.Channel mChannel;

  public PermissionPromptNotificationActivityArgs(IPermissionPromptBroadcast.Channel channel, PermissionRequest request) {
    mRequest = request;
    mChannel = channel;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelable(this.mRequest, flags);
    dest.writeParcelable(this.mChannel, flags);
  }

  @SuppressWarnings("WeakerAccess")
  protected PermissionPromptNotificationActivityArgs(Parcel in) {
    this.mRequest = in.readParcelable(PermissionRequest.class.getClassLoader());
    this.mChannel = in.readParcelable(IPermissionPromptBroadcast.Channel.class.getClassLoader());
  }

  public static final Parcelable.Creator<PermissionPromptNotificationActivityArgs> CREATOR = new Parcelable.Creator<PermissionPromptNotificationActivityArgs>() {
    @Override
    public PermissionPromptNotificationActivityArgs createFromParcel(Parcel source) {
      return new PermissionPromptNotificationActivityArgs(source);
    }

    @Override
    public PermissionPromptNotificationActivityArgs[] newArray(int size) {
      return new PermissionPromptNotificationActivityArgs[size];
    }
  };
}
