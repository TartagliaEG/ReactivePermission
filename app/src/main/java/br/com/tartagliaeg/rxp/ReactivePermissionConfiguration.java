package br.com.tartagliaeg.rxp;

import android.support.annotation.DrawableRes;
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;

/**
 * Created by TartagliaEG on 2018/07/03.
 * ...
 */
public class ReactivePermissionConfiguration {
  private static ReactivePermissionConfiguration sInstance;

  @StringRes
  private int mNotificationTitle = R.string.notification_title;
  @StringRes
  private int mNotificationContent = R.string.notification_content;
  @DrawableRes
  private int mNotificationIcon = R.drawable.ic_notification_icon;

  @PluralsRes
  private int mNeverAskAgainHeader = R.plurals.never_ask_again_header;
  @StringRes
  private int mNeverAskAgainSettingsButton = R.string.never_ask_again_settings_button;
  @StringRes
  private int mNeverAskAgainCancelButton = R.string.never_ask_again_cancel_button;

  @PluralsRes
  private int mPermissionRationaleHeader = R.plurals.permission_rationale_header;
  @StringRes
  private int mPermissionRationaleConfirmButton = R.string.permission_rationale_confirm_button;
  @StringRes
  private int mPermissionRationaleCancelButton = R.string.permission_rationale_cancel_button;


  public static ReactivePermissionConfiguration getInstance() {
    return sInstance = (sInstance != null ? sInstance : new ReactivePermissionConfiguration());
  }

  private ReactivePermissionConfiguration() {
  }


  /* ------ NOTIFICATION SECTION ------ */

  public ReactivePermissionConfiguration notificationTitle(@StringRes int notificationTitle) {
    mNotificationTitle = notificationTitle;
    return this;
  }

  public ReactivePermissionConfiguration notificationContent(@StringRes int notificationContent) {
    mNotificationContent = notificationContent;
    return this;
  }

  public ReactivePermissionConfiguration notificationIcon(@DrawableRes int notificationIcon) {
    mNotificationIcon = notificationIcon;
    return this;
  }

  @StringRes
  public int getNotificationTitle() {
    return mNotificationTitle;
  }

  @StringRes
  public int getNotificationContent() {
    return mNotificationContent;
  }

  @DrawableRes
  public int getNotificationIcon() {
    return mNotificationIcon;
  }



  /* ------ NEVER ASK AGAIN SECTION ------ */

  public ReactivePermissionConfiguration neverAskAgainHeader(@PluralsRes int neverAskAgainHeader) {
    mNeverAskAgainHeader = neverAskAgainHeader;
    return this;
  }

  public ReactivePermissionConfiguration neverAskAgainSettingsButton(@StringRes int neverAskAgainSettingsButton) {
    mNeverAskAgainSettingsButton = neverAskAgainSettingsButton;
    return this;
  }

  public ReactivePermissionConfiguration neverAskAgainCancelButton(@StringRes int neverAskAgainCancelButton) {
    mNeverAskAgainCancelButton = neverAskAgainCancelButton;
    return this;
  }

  @PluralsRes
  public int getNeverAskAgainHeader() {
    return mNeverAskAgainHeader;
  }

  @StringRes
  public int getNeverAskAgainSettingsButton() {
    return mNeverAskAgainSettingsButton;
  }

  @StringRes
  public int getNeverAskAgainCancelButton() {
    return mNeverAskAgainCancelButton;
  }



  /* ------ PERMISSION RATIONALE SECTION ------ */

  public ReactivePermissionConfiguration permissionRationaleHeader(@StringRes int permissionRationaleHeader) {
    mPermissionRationaleHeader = permissionRationaleHeader;
    return this;
  }

  public ReactivePermissionConfiguration permissionRationaleConfirmButton(@StringRes int permissionRationaleContinueButton) {
    mPermissionRationaleConfirmButton = permissionRationaleContinueButton;
    return this;
  }

  public ReactivePermissionConfiguration permissionRationaleCancelButton(@PluralsRes int permissionRationaleCancelButton) {
    mPermissionRationaleCancelButton = permissionRationaleCancelButton;
    return this;
  }

  @PluralsRes
  public int getPermissionRationaleHeader() {
    return mPermissionRationaleHeader;
  }

  @StringRes
  public int getPermissionRationaleConfirmButton() {
    return mPermissionRationaleConfirmButton;
  }

  @StringRes
  public int getPermissionRationaleCancelButton() {
    return mPermissionRationaleCancelButton;
  }

}
