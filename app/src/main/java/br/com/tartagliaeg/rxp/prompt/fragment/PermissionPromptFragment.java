package br.com.tartagliaeg.rxp.prompt.fragment;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.tartagliaeg.rxp.domain.Permission;
import br.com.tartagliaeg.rxp.domain.PermissionFragment;
import br.com.tartagliaeg.rxp.domain.PermissionPack;
import br.com.tartagliaeg.rxp.domain.PermissionRequest;
import br.com.tartagliaeg.rxp.prompt.IPermissionPromptContinuable;
import br.com.tartagliaeg.rxp.utils.SimpleObserver;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by TartagliaEG on 2018/07/03.
 * ...
 */
public class PermissionPromptFragment extends PermissionFragment implements IPermissionPromptContinuable {
  private static final String TAG = PermissionPromptFragment.class.getName();
  private static final String FRAGMENT_TAG = PermissionPromptFragment.class.getName();
  private PublishSubject<Permission> mPermissionSubject;

  private static final String SVD_ASKED_PERMISSIONS = TAG + ".asked_permissions";
  private PermissionRequest mPendingPermissions;

  private static final String SVD_RESULTING_PERMISSIONS = TAG + ".resulting_permissions";
  private PermissionPack mResultingPermissions;

  private static final String SVD_PENDING_PROMPT_IDS = TAG + ".pending_prompt_ids";
  private Set<Integer> mPendingPromptIds = new HashSet<>();
  private Disposable mPermissionDisposable;


  @SuppressWarnings("unused")
  public static IPermissionPromptContinuable createOrRetrievePermissionPrompt(FragmentManager fm) {
    Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG);

    if (fragment == null) {
      fragment = newInstance();

      fm.beginTransaction()
        .add(fragment, FRAGMENT_TAG)
        .commit();
    }

    return (IPermissionPromptContinuable) fragment;
  }

  @SuppressWarnings("unused")
  public static IPermissionPromptContinuable createOrRetrievePermissionPrompt(FragmentManager fm, @IdRes int id) {
    Fragment fragment = fm.findFragmentById(id);

    if (fragment == null) {
      fragment = newInstance();
      fm.beginTransaction().add(id, fragment).commit();
    }

    return (IPermissionPromptContinuable) fragment;
  }


  public static Fragment newInstance() {
    return new PermissionPromptFragment();
  }


  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  private boolean isWaitingPermissionsDialog() {
    return mPermissionDisposable != null;
  }

  @Override
  public void onCreate(@Nullable Bundle bundle) {
    super.onCreate(bundle);

    //noinspection ConstantConditions
    mPendingPromptIds = bundle != null
      ? new HashSet<>(bundle.getIntegerArrayList(SVD_PENDING_PROMPT_IDS))
      : new HashSet<Integer>();

    mPermissionSubject = PublishSubject.create();
    mPendingPermissions = bundle != null ? (PermissionRequest) bundle.getParcelable(SVD_ASKED_PERMISSIONS) : PermissionRequest.newEmptyRequest();
    mResultingPermissions = PermissionPack.from(getContext(), bundle != null ? bundle.getStringArray(SVD_RESULTING_PERMISSIONS) : new String[]{});

    if (!mPendingPermissions.isEmpty())
      requestPermission(mPendingPermissions);

  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (mPermissionDisposable != null)
      mPermissionDisposable.dispose();
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle bundle) {
    super.onSaveInstanceState(bundle);
    bundle.putParcelable(SVD_ASKED_PERMISSIONS, mPendingPermissions);
    bundle.putStringArray(SVD_RESULTING_PERMISSIONS, mResultingPermissions.getPermissionNames());
    bundle.putIntegerArrayList(SVD_PENDING_PROMPT_IDS, new ArrayList<>(Arrays.asList(mPendingPromptIds.toArray(new Integer[mPendingPromptIds.size()]))));
  }


  @Override
  public Observable<PermissionPack> promptPermissions(int promptId, final PermissionRequest request) {
    PermissionPack pack = PermissionPack.from(getContext(), request.getPermissionNames());

    if (pack.isAllGranted())
      return Observable.just(pack);

    mPendingPromptIds.add(promptId);
    mPendingPermissions = mPendingPermissions.newRequestMerging(request);

    return mPermissionSubject.filter(new Predicate<Permission>() {
      @Override
      public boolean test(Permission permission) throws Exception {
        return Arrays.asList(request.getPermissionNames()).contains(permission.getName());

      }
    }).doOnSubscribe(new Consumer<Disposable>() {
      @Override
      public void accept(Disposable disposable) throws Exception {
        if (!isWaitingPermissionsDialog())
          requestPermission(request);

      }
    }).toList()
      .map(new Function<List<Permission>, PermissionPack>() {
        @Override
        public PermissionPack apply(List<Permission> permissions) throws Exception {
          return PermissionPack.from(permissions);
        }
      })
      .toObservable();
  }

  private void requestPermission(PermissionRequest request) {
    promptPermissions(request).subscribe(new SimpleObserver<Permission>() {
      @Override
      public void onSubscribe(Disposable d) {
        mPermissionDisposable = d;
      }

      @Override
      public void onNext(Permission permission) {
        mResultingPermissions = mResultingPermissions.newPackMergingPermissions(getContext(), permission.getName());
      }

      @Override
      public void onComplete() {
        mPendingPermissions = mPendingPermissions.newRequestExcluding(mResultingPermissions.getPermissionNames());

        if (!mPendingPermissions.isEmpty()) {
          requestPermission(mPendingPermissions);
          return;
        }

        for (Permission p : mResultingPermissions.iterable())
          mPermissionSubject.onNext(p);

        mPermissionSubject.onComplete();
        mPermissionDisposable = null;
        mPermissionSubject = PublishSubject.create();

        mPendingPermissions = PermissionRequest.newEmptyRequest();
        mResultingPermissions = PermissionPack.from();
        mPendingPromptIds.clear();
      }
    });
  }

  @Override
  public Observable<PermissionPack> continuePromptPermissions(int promptId, final PermissionRequest request) {
    if (!isWaitingPermissionsDialog() || !mPendingPromptIds.contains(promptId))
      return Observable.empty();

    return mPermissionSubject.filter(new Predicate<Permission>() {
      @Override
      public boolean test(Permission permission) throws Exception {
        return request.contains(permission.getName());
      }
    }).toList()
      .map(new Function<List<Permission>, PermissionPack>() {
        @Override
        public PermissionPack apply(List<Permission> permissions) throws Exception {
          return PermissionPack.from(permissions);
        }
      })
      .toObservable();
  }
}
