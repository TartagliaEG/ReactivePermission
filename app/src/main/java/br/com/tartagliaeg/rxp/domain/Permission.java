package br.com.tartagliaeg.rxp.domain;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import java.util.Arrays;

import br.com.tartagliaeg.rxp.Constants;

/**
 * Created by TartagliaEG on 2018/06/29.
 * ...
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Permission implements Comparable<Permission> {
  private static final String TAG = Constants.TAG + Permission.class.getSimpleName();

  private static final String SP_NAME = TAG + ".PERMISSIONS_SHARED_PREFERENCES";
  private static final String SP_PERMISSION_ASKED = TAG + ".PERMISSION_ASKED.";

  private final String mPermission;

  private final SharedPreferences mPreferences;
  private final Context mContext;

  public Permission(String name, Context context) {
    mPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    mContext = context.getApplicationContext();
    mPermission = name;

    try {
      if (!Arrays.asList(context
        .getPackageManager()
        .getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS)
        .requestedPermissions).contains(name))
        throw new RuntimeException(String.format("The permission '%s' is not declared on the manifest", name));

    } catch (PackageManager.NameNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String toString() {
    return "Permission { " +
      "newRequestMerging: " + mPermission +
      ", granted: " + isGranted() +
      ", asked: " + isAsked() +
      " }";
  }

  public String getName() {
    return mPermission;
  }

  @SuppressWarnings("SimplifiableConditionalExpression")
  public boolean isGranted() {
    return isRuntimePermissionSupported()
      ? mContext.checkSelfPermission(mPermission) == PackageManager.PERMISSION_GRANTED
      : true;
  }

  public boolean isDenied() {
    return !isGranted();
  }

  @SuppressLint("NewApi")
  public boolean isNeverAskAgain(Activity a) {
    return isRuntimePermissionSupported()
      && !isGranted()
      && isAsked()
      && !a.shouldShowRequestPermissionRationale(mPermission);
  }

  @SuppressLint("NewApi")
  public boolean isNeverAskAgain(Fragment f) {
    return isRuntimePermissionSupported()
      && !isGranted()
      && isAsked()
      && !f.shouldShowRequestPermissionRationale(mPermission);
  }


  @SuppressWarnings("SimplifiableConditionalExpression")
  public boolean isAsked() {
    return isRuntimePermissionSupported()
      ? mPreferences.getBoolean(SP_PERMISSION_ASKED + mPermission, false)
      : true;
  }

  void setAsAsked() {
    mPreferences.edit().putBoolean(SP_PERMISSION_ASKED + mPermission, true).apply();
  }

  private boolean isRuntimePermissionSupported() {
    return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Permission that = (Permission) o;
    return mPermission.equals(that.mPermission);
  }

  @Override
  public int hashCode() {
    return mPermission.hashCode();
  }

  @Override
  public int compareTo(@NonNull Permission o) {
    return mPermission.compareTo(o.mPermission);
  }
}
