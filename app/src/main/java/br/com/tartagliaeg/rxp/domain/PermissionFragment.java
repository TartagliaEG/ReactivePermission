package br.com.tartagliaeg.rxp.domain;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import br.com.tartagliaeg.rxp.Constants;
import br.com.tartagliaeg.rxp.utils.Intents;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by TartagliaEG on 2018/06/29.
 * ...
 */
public class PermissionFragment extends Fragment {
  private static final int RC_ASK_FOR_PERMISSION = 27401;
  private static final int RC_RATIONALE_DIALOG = 27402;
  private static final int RC_OPEN_SETTINGS = 27403;

  private static final String TAG = Constants.TAG + PermissionFragment.class.getSimpleName();
  private static final String FRAGMENT_TAG = "Fragment:" + TAG;
  private static final String SVD_IS_WAITING_DIALOG = TAG + ".waiting_dialog";

  private Intents mIntents = Intents.getsInstance();
  private PublishSubject<Permission> mPermissionsPublisher = PublishSubject.create();
  private boolean mIsCreated = false;
  private boolean mIsWaitingPermissionDialogResponse;
  private PermissionRequest mPermissionRequest;

  public static PermissionFragment retrieveOrCreatePermissionPrompt(FragmentManager fm) {
    Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG);

    if (fragment == null) {
      fragment = newInstance();

      fm.beginTransaction()
        .add(fragment, FRAGMENT_TAG)
        .commit();
    }

    return (PermissionFragment) fragment;
  }

  public static Fragment newInstance() {
    return new PermissionFragment();
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mIsWaitingPermissionDialogResponse = savedInstanceState != null && savedInstanceState.getBoolean(SVD_IS_WAITING_DIALOG, false);
    mIsCreated = true;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mIsCreated = false;
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putBoolean(SVD_IS_WAITING_DIALOG, mIsWaitingPermissionDialogResponse);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (RC_OPEN_SETTINGS == requestCode) {
      handleSettingsResult();

    } else if (requestCode == RC_RATIONALE_DIALOG) {
      handleRationaleResult(data);

    }

  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if (requestCode == RC_ASK_FOR_PERMISSION)
      sendPermissionResult(permissions);
  }

  /**
   * Handle the App Settings response.
   * <p>
   * If all the permissions were granted, just send the response. Otherwise, opens the rationale
   * dialog again. Once that this method is handling the settings result, it assumes that the
   * rationale dialog were open before. So, there's no need to check the configured {@link br.com.tartagliaeg.rxp.ReactivePermissionConfiguration.ShowRationaleStrategy}
   */
  private void handleSettingsResult() {
    if (mPermissionRequest.asPack(getContext()).isAllGranted()) {
      sendPermissionResult(mPermissionRequest.getPermissionNames());
      return;
    }

    startActivityForResult(
      PermissionRationaleActivity.newIntent(getContext(), mPermissionRequest),
      RC_RATIONALE_DIALOG
    );
  }


  private void handleRationaleResult(Intent data) {
    int rationaleResponse = PermissionRationaleActivity.extractResponse(data);

    if (rationaleResponse == PermissionRationaleActivity.RST_CANCELED) {
      sendPermissionResult(mPermissionRequest.getPermissionNames());
      return;
    }

    if (rationaleResponse == PermissionRationaleActivity.RST_UNNECESSARY) {
      sendPermissionResult(mPermissionRequest.getPermissionNames());
      return;
    }

    if (rationaleResponse == PermissionRationaleActivity.RST_CONFIRMED) {
      new Handler().post(new Runnable() {
        @Override
        public void run() {
          if (mPermissionRequest.asPack(getContext()).isAnyNeverAskAgain(PermissionFragment.this)) {
            assert getContext() != null;
            startActivityForResult(mIntents.createAppSettingsIntent(getContext()), RC_OPEN_SETTINGS);
          } else {
            requestPermissions(mPermissionRequest.getPermissionNames(), RC_ASK_FOR_PERMISSION);
          }
        }
      });
      return;
    }

    throw new RuntimeException("Unexpected response code: " + rationaleResponse);
  }

  private void sendPermissionResult(final String... permissions) {
    assert getContext() != null;
    // The response flow is postponed because onActivityResult and onRequestPermissionResult methods
    // may be called between onCreate and onStart. When it happens, the library's user doesn't have
    // a good lifecycle callback to start observing the response again. If the user do so during the
    // onCreate callback, this fragment won't have its state yet. Any callback called after onCreate
    // won't work too once the response would have already been sent. To workaround this issue,
    // we're enqueueing the response flow so it can be executed after the whole lifecycle.
    new Handler().post(new Runnable() {
      @Override
      public void run() {
        for (String permissionName : permissions) {
          Permission permission = new Permission(permissionName, getContext());
          permission.setAsAsked();
          mPermissionsPublisher.onNext(permission);
        }

        mIsWaitingPermissionDialogResponse = false;

        PublishSubject<Permission> oldSubject = mPermissionsPublisher;
        mPermissionsPublisher = PublishSubject.create();
        oldSubject.onComplete();

      }
    });
  }

  public Observable<Permission> promptPermissions(PermissionRequest request) {
    assertIsCreated();

    PermissionPack pack = new PermissionPack(getContext(), request.getPermissionNames());
    mPermissionRequest = request;

    if (mIsWaitingPermissionDialogResponse)
      return mPermissionsPublisher;

    boolean isAllGranted = true;

    for (Permission permission : pack.iterable())
      isAllGranted = isAllGranted && permission.isGranted();

    if (isAllGranted)
      return Observable.fromIterable(pack.iterable());

    mIsWaitingPermissionDialogResponse = true;

    if (request.shouldShowRationale(this))
      startActivityForResult(
        PermissionRationaleActivity.newIntent(getContext(), request),
        RC_RATIONALE_DIALOG
      );
    else
      requestPermissions(request.getPermissionNames(), RC_ASK_FOR_PERMISSION);

    return mPermissionsPublisher;
  }

  private void assertIsCreated() {
    if (!mIsCreated)
      throw new IllegalStateException("Can't prompt permissions before onCreate was called.");
  }
}
