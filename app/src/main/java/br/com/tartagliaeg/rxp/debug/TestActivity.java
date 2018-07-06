package br.com.tartagliaeg.rxp.debug;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import br.com.tartagliaeg.rxp.R;
import br.com.tartagliaeg.rxp.domain.PermissionPack;
import br.com.tartagliaeg.rxp.domain.PermissionRequest;
import br.com.tartagliaeg.rxp.domain.PermissionRequests;
import br.com.tartagliaeg.rxp.prompt.IPermissionPromptContinuable;
import br.com.tartagliaeg.rxp.prompt.fragment.PermissionPromptFragment;
import br.com.tartagliaeg.rxp.utils.SimpleObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Used to test the library.
 */
public class TestActivity extends LogActivity {
  private static final String TAG = TestActivity.class.getName();
  private static final int PERM_LOCATION = 100105;
  private IPermissionPromptContinuable mPerms;
  private CompositeDisposable mDisposables = new CompositeDisposable();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

//    final PermissionPromptNotification mPerms = new PermissionPromptNotification(this);
    mPerms = PermissionPromptFragment.createOrRetrievePermissionPrompt(getSupportFragmentManager());

    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        mPerms
          .promptPermissions(PERM_LOCATION, PermissionRequest.newRequestFrom(
            PermissionRequests.CAMERA,
            PermissionRequests.ACCESS_FINE_LOCATION
          ))
          .subscribe(handleLocationPermission());

//        mPerms
//          .promptPermissions(1111, PermissionRequests.CAMERA)
//          .subscribe(handleLocationPermission());

      }
    });
  }

  @Override
  protected void onStart() {
    super.onStart();

    mPerms
      .continuePromptPermissions(
        PERM_LOCATION,
        PermissionRequests.ACCESS_FINE_LOCATION
      )
      .subscribe(handleLocationPermission());

    mPerms
      .continuePromptPermissions(
        1111,
        PermissionRequests.CAMERA
      )
      .subscribe(handleLocationPermission());


  }

  @Override
  protected void onStop() {
    super.onStop();
    mDisposables.clear();
  }


  private SimpleObserver<PermissionPack> handleLocationPermission() {
    return new SimpleObserver<PermissionPack>() {
      @Override
      public void onSubscribe(Disposable d) {
        super.onSubscribe(d);
        mDisposables.add(d);
      }

      @Override
      public void onNext(PermissionPack pack) {
        Log.d(getClass().getName(), pack.toString());
      }
    };
  }
}
