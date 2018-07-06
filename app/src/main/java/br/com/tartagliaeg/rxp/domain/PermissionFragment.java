package br.com.tartagliaeg.rxp.domain;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by TartagliaEG on 2018/06/29.
 * ...
 */
public class PermissionFragment extends Fragment {
  private static final int RC_ASK_FOR_PERMISSION = 27401;

  private static final String TAG = PermissionFragment.class.getName();
  private static final String FRAGMENT_TAG = "Fragment:" + TAG;
  private static final String SVD_IS_WAITING_DIALOG = TAG + ".waiting_dialog";

  private PublishSubject<Permission> mPermissionsPublisher = PublishSubject.create();
  private boolean mIsCreated = false;
  private boolean mIsWaitingPermissionDialogResponse;
  private Runnable mDelayedResponse;

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
  public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if (requestCode != RC_ASK_FOR_PERMISSION)
      return;

    sendPermissionResult(permissions);
  }

  private void sendPermissionResult(final String... permissions) {
    // The response flow is postponed because onActivityResult and onRequestPermissionResult methods
    // may be called between onCreate and onStart. When it happens, the library's user doesn't have
    // a good lifecycle callback to start observing the response again. If the user do so during the
    // onCreate callback, this fragment won't have restored it's state yet. Any callback called after
    // onCreate won't work too once the response would have already been sent. To workaround this
    // issue, we're enqueueing the response flow so it can be executed after the whole lifecycle.
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

  @Override
  public void onResume() {
    super.onResume();
    if (mDelayedResponse != null) {
      mDelayedResponse.run();
      mDelayedResponse = null;
    }
  }

  public Observable<Permission> promptPermissions(PermissionRequest request) {
    assertIsCreated();

    if (mIsWaitingPermissionDialogResponse)
      return mPermissionsPublisher;

    boolean isAllGranted = true;
    PermissionPack pack = new PermissionPack(getContext(), request.getPermissionNames());

    for (Permission permission : pack.iterable())
      isAllGranted = isAllGranted && permission.isGranted();

    if (isAllGranted)
      return Observable.fromIterable(pack.iterable());

    mIsWaitingPermissionDialogResponse = true;
//    requestPermissions(request.getPermissionNames(), RC_ASK_FOR_PERMISSION);
    startActivity(PermissionRationaleActivity.newIntent(getContext(), request));

    return mPermissionsPublisher;
  }

  private void assertIsCreated() {
    if (!mIsCreated)
      throw new IllegalStateException("Can't prompt permissions before onCreate was called.");
  }
}
