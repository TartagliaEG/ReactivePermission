package br.com.tartagliaeg.rxp.prompt.notification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import br.com.tartagliaeg.rxp.R;
import br.com.tartagliaeg.rxp.debug.LogTag;
import br.com.tartagliaeg.rxp.domain.Permission;
import br.com.tartagliaeg.rxp.domain.PermissionFragment;
import br.com.tartagliaeg.rxp.domain.PermissionPack;
import br.com.tartagliaeg.rxp.debug.LogActivity;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

/**
 * Created by TartagliaEG on 2018/06/29.
 * ...
 */
public class PermissionPromptNotificationActivity extends LogActivity {
  private static final String TAG = LogTag.TAG + PermissionPromptNotificationActivity.class.getName();
  private static final String EXT_ARGS = TAG + ".args";

  private PermissionFragment mPermissionPrompt;
  private PermissionPromptNotificationActivityArgs mArgs;

  private Disposable mPermissionDisposable;

  static Intent newIntent(@NonNull Context context, PermissionPromptNotificationActivityArgs args) {

    Intent intent = new Intent(context, PermissionPromptNotificationActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.putExtra(EXT_ARGS, args);

    return intent;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.empty_frame);

    mArgs = getIntent().getParcelableExtra(EXT_ARGS);
    mArgs.mChannel.notifyChannelConnected(this);

    mPermissionPrompt = PermissionFragment.retrieveOrCreatePermissionPrompt(getSupportFragmentManager());
  }

  @Override
  protected void onStart() {
    super.onStart();
    startPermissionFlow();
  }

  @Override
  protected void onStop() {
    super.onStop();
    mPermissionDisposable.dispose();
  }

  private void startPermissionFlow() {
    mPermissionPrompt
      .promptPermissions(mArgs.mRequest)
      .toList()
      .subscribe(new SingleObserver<List<Permission>>() {
        @Override
        public void onSubscribe(Disposable disposable) {
          mPermissionDisposable = disposable;
        }

        @Override
        public void onSuccess(List<Permission> permissions) {
          Context context = PermissionPromptNotificationActivity.this;
          mArgs.mChannel.sendResponse(context, new PermissionPack(permissions).getPermissionNames());
          finish();
        }

        @Override
        public void onError(Throwable e) {
          e.printStackTrace();
          finish();
          throw new RuntimeException(e);
        }
      });
  }

}
