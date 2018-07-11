package br.com.tartagliaeg.rxp;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
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

  private ShowRationaleStrategy mPermissionRationaleStrategy = ShowRationaleStrategy.BEFORE_ANY_PERMISSION;

  public static ReactivePermissionConfiguration getInstance() {
    return sInstance = (sInstance != null ? sInstance : new ReactivePermissionConfiguration());
  }

  private ReactivePermissionConfiguration() {
  }


  /* ------ NOTIFICATION SECTION ------ */

  @NonNull
  public ReactivePermissionConfiguration notificationTitle(@StringRes int notificationTitle) {
    mNotificationTitle = notificationTitle;
    return this;
  }

  @NonNull
  public ReactivePermissionConfiguration notificationContent(@StringRes int notificationContent) {
    mNotificationContent = notificationContent;
    return this;
  }

  @NonNull
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

  @NonNull
  public ReactivePermissionConfiguration neverAskAgainHeader(@PluralsRes int neverAskAgainHeader) {
    mNeverAskAgainHeader = neverAskAgainHeader;
    return this;
  }

  @NonNull
  public ReactivePermissionConfiguration neverAskAgainSettingsButton(@StringRes int neverAskAgainSettingsButton) {
    mNeverAskAgainSettingsButton = neverAskAgainSettingsButton;
    return this;
  }

  @NonNull
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

  @NonNull
  public ReactivePermissionConfiguration permissionRationaleStrategy(@NonNull ShowRationaleStrategy strategy) {
    mPermissionRationaleStrategy = strategy;
    return this;
  }

  @NonNull
  public ReactivePermissionConfiguration permissionRationaleHeader(@StringRes int permissionRationaleHeader) {
    mPermissionRationaleHeader = permissionRationaleHeader;
    return this;
  }

  @NonNull
  public ReactivePermissionConfiguration permissionRationaleConfirmButton(@StringRes int permissionRationaleContinueButton) {
    mPermissionRationaleConfirmButton = permissionRationaleContinueButton;
    return this;
  }

  @NonNull
  public ReactivePermissionConfiguration permissionRationaleCancelButton(@PluralsRes int permissionRationaleCancelButton) {
    mPermissionRationaleCancelButton = permissionRationaleCancelButton;
    return this;
  }

  @NonNull
  public ShowRationaleStrategy getPermissionRationaleStrategy() {
    return mPermissionRationaleStrategy;
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

  /**
   * Strategy taken to show the rationale dialog
   */
  public enum ShowRationaleStrategy {
    /**
     * The dialog will be ever shown.
     */
    BEFORE_ANY_PERMISSION,
    /**
     * The rationale dialog will be shown only if one of the asked permissions is set as never_ask_again
     */
    BEFORE_PERMISSIONS_SET_AS_NEVER_ASK_AGAIN,
    /**
     * The dialog won't be shown at all.
     */
    NEVER
  }

}
