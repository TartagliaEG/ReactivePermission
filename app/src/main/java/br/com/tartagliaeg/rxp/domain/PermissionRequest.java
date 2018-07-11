package br.com.tartagliaeg.rxp.domain;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.tartagliaeg.rxp.ReactivePermissionConfiguration;

/**
 * Created by TartagliaEG on 2018/04/03.
 * ...
 */
public class PermissionRequest implements Parcelable {
  private final Map<String, PermissionMeta> mPermissions;
  private final ReactivePermissionConfiguration mConfig = ReactivePermissionConfiguration.getInstance();

  private PermissionRequest(String permission, @StringRes int promptMessage) {
    mPermissions = new HashMap<>();
    mPermissions.put(permission, new PermissionMeta(permission, promptMessage));
  }

  private PermissionRequest(Map<String, PermissionMeta> permissions) {
    mPermissions = permissions;
  }

  private PermissionRequest() {
    mPermissions = new HashMap<>();
  }

  public static PermissionRequest newRequestFrom(PermissionRequest... requests) {
    Map<String, PermissionMeta> permissions = new HashMap<>();

    for (PermissionRequest request : requests)
      permissions.putAll(request.mPermissions);

    return new PermissionRequest(permissions);
  }

  public static PermissionRequest newEmptyRequest() {
    return new PermissionRequest();
  }

  public static PermissionRequest newRequestWith(String permission, @StringRes int promptMessage) {
    return new PermissionRequest(permission, promptMessage);
  }

  public PermissionRequest newRequestMerging(PermissionRequest request) {
    Map<String, PermissionMeta> permissions = new HashMap<>();
    permissions.putAll(mPermissions);
    permissions.putAll(request.mPermissions);
    return new PermissionRequest(permissions);
  }

  @SuppressWarnings("unused")
  public PermissionRequest newRequestMerging(String permission, @StringRes int promptMessage) {
    Map<String, PermissionMeta> permissions = new HashMap<>();
    permissions.put(permission, new PermissionMeta(permission, promptMessage));
    permissions.putAll(mPermissions);
    return new PermissionRequest(permissions);
  }

  @SuppressWarnings("unused")
  public PermissionRequest newRequestExcluding(PermissionRequest request) {
    Map<String, PermissionMeta> permissions = new HashMap<>();

    for (String perm : mPermissions.keySet())
      if (!request.mPermissions.containsKey(perm))
        permissions.put(perm, mPermissions.get(perm));

    return new PermissionRequest(permissions);
  }

  public PermissionRequest newRequestExcluding(String... permissions) {
    Map<String, PermissionMeta> permissionMap = new HashMap<>(mPermissions);

    for (String permission : permissions)
      permissionMap.remove(permission);

    return new PermissionRequest(permissionMap);
  }

  public String[] getPermissionNames() {
    return mPermissions.keySet().toArray(new String[mPermissions.size()]);
  }

  public List<PermissionMeta> getPermissionMetas() {
    return new ArrayList<>(mPermissions.values());
  }

  public int size() {
    return mPermissions.size();
  }

  @SuppressWarnings("unused")
  @StringRes
  public int getPromptMessage(String permission) {
    if (!mPermissions.containsKey(permission))
      throw new RuntimeException("No message associated with the newRequestMerging: " + permission);

    return mPermissions.get(permission).mPromptMessage;
  }

  /**
   * Check whether this request should show the rationale dialog. This method uses the configured
   * {@link br.com.tartagliaeg.rxp.ReactivePermissionConfiguration.ShowRationaleStrategy} on {@link ReactivePermissionConfiguration} class.
   *
   * @param fragment Any attached fragment.
   * @return a boolean stating if the rationale dialog should be shown
   */
  public boolean shouldShowRationale(@NonNull Fragment fragment) {
    return shouldShowRationale(null, fragment);
  }

  /**
   * Check whether this request should show the rationale dialog. This method uses the configured
   * {@link br.com.tartagliaeg.rxp.ReactivePermissionConfiguration.ShowRationaleStrategy} on {@link ReactivePermissionConfiguration} class.
   *
   * @param activity Any activity.
   * @return a boolean stating if the rationale dialog should be shown
   */
  @SuppressWarnings("unused")
  public boolean shouldShowRationale(@NonNull Activity activity) {
    return shouldShowRationale(activity, null);
  }

  private boolean shouldShowRationale(@Nullable Activity activity, @Nullable Fragment fragment) {
    if (fragment == null && activity == null)
      throw new IllegalArgumentException("Invalid arguments. You must provide either a valid activity or fragment");

    Context context = activity == null ? fragment.getContext() : activity;

    if (context == null)
      throw new IllegalStateException("Failed to extract a valid context from the given fragment");


    switch (mConfig.getPermissionRationaleStrategy()) {
      case NEVER:
        return false;
      case BEFORE_ANY_PERMISSION:
        return true;
      default:
        return activity == null
          ? asPack(context).isAnyNeverAskAgain(fragment)
          : asPack(context).isAnyNeverAskAgain(activity);
    }
  }

  public PermissionPack asPack(Context context) {
    return PermissionPack.from(context, getPermissionNames());
  }


  /* PARCELABLE */

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.mPermissions.size());
    for (Map.Entry<String, PermissionMeta> entry : this.mPermissions.entrySet()) {
      dest.writeString(entry.getKey());
      dest.writeInt(entry.getValue().mPromptMessage);
    }
  }

  protected PermissionRequest(Parcel in) {
    int mPermissionsSize = in.readInt();
    this.mPermissions = new HashMap<>(mPermissionsSize);
    for (int i = 0; i < mPermissionsSize; i++) {
      String key = in.readString();
      int value = in.readInt();
      this.mPermissions.put(key, new PermissionMeta(key, value));
    }
  }

  public static final Creator<PermissionRequest> CREATOR = new Creator<PermissionRequest>() {
    @Override
    public PermissionRequest createFromParcel(Parcel source) {
      return new PermissionRequest(source);
    }

    @Override
    public PermissionRequest[] newArray(int size) {
      return new PermissionRequest[size];
    }
  };

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public boolean isEmpty() {
    return mPermissions.isEmpty();
  }

  public boolean contains(String permission) {
    return mPermissions.containsKey(permission);
  }

  @SuppressWarnings("WeakerAccess")
  public static class PermissionMeta {
    public final String mName;
    public final int mPromptMessage;

    public PermissionMeta(String name, int promptMessage) {
      mName = name;
      mPromptMessage = promptMessage;
    }

    public Drawable getDefaultPermissionIcon(Context mContext) {
      try {
        PermissionInfo permInfo = mContext.getPackageManager().getPermissionInfo(mName, PackageManager.GET_META_DATA);
        PermissionGroupInfo permGroupInfo = mContext.getPackageManager().getPermissionGroupInfo(permInfo.group, PackageManager.GET_META_DATA);
        return mContext.getPackageManager().getResourcesForApplication("android").getDrawable(permGroupInfo.icon);

      } catch (PackageManager.NameNotFoundException e) {
        throw new RuntimeException(e);
      }
    }

  }
}
