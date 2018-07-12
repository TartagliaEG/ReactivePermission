package br.com.tartagliaeg.rxp.debug;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import br.com.tartagliaeg.rxp.Constants;

/**
 * Created by TartagliaEG on 2018/07/02.
 *
 * Class used to log the lifecycle. Used for debug purpose.
 */
public abstract class LogActivity extends AppCompatActivity {
  private String TAG = Constants.TAG +  this.getClass().getSimpleName();

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    Log.d(TAG, "onCreate >>");
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate <<");
  }

  @Override
  protected void onStart() {
    Log.d(TAG, "onStart >>");
    super.onStart();
    Log.d(TAG, "onStart <<");
  }

  @Override
  protected void onResume() {
    Log.d(TAG, "onResume >>");
    super.onResume();
    Log.d(TAG, "onResume <<");
  }

  @Override
  protected void onPause() {
    Log.d(TAG, "onPause >>");
    super.onPause();
    Log.d(TAG, "onPause <<");
  }

  @Override
  protected void onStop() {
    Log.d(TAG, "onStop >>");
    super.onStop();
    Log.d(TAG, "onStop <<");
  }

  @Override
  protected void onDestroy() {
    Log.d(TAG, "onDestory >>");
    super.onDestroy();
    Log.d(TAG, "onDestroy <<");
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    Log.d(TAG, "onSaveInstanceState >>");
    super.onSaveInstanceState(outState);
    Log.d(TAG, "onSaveInstanceState <<");
  }
}
