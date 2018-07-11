package br.com.tartagliaeg.rxp.debug;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by tartagle on 03/07/2018.
 *
 * Class used to log the Fragment lifecycle. Used for debug purpose.
 */

public class LogFragment extends Fragment {
  private String TAG = LogTag.TAG + this.getClass().getSimpleName();

  @Override
  public void onAttach(Context context) {
    Log.d(TAG, "onAttach: >>");
    super.onAttach(context);
    Log.d(TAG, "onAttach: <<");
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    Log.d(TAG, "onCreate: >>");
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate: <<");
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    Log.d(TAG, "onSaveInstanceState: >>");
    super.onSaveInstanceState(outState);
    Log.d(TAG, "onSaveInstanceState: <<");
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    Log.d(TAG, "onRequestPermissionsResult: >>");
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    Log.d(TAG, "onRequestPermissionsResult: <<");
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    Log.d(TAG, "onActivityCreated: >>");
    super.onActivityCreated(savedInstanceState);
    Log.d(TAG, "onActivityCreated: <<");
  }

  @Override
  public void onStart() {
    Log.d(TAG, "onStart: >>");
    super.onStart();
    Log.d(TAG, "onStart: <<");
  }

  @Override
  public void onResume() {
    Log.d(TAG, "onResume: >>");
    super.onResume();
    Log.d(TAG, "onResume: <<");
  }

  @Override
  public void onPause() {
    Log.d(TAG, "onPause: >>");
    super.onPause();
    Log.d(TAG, "onPause: <<");
  }

  @Override
  public void onStop() {
    Log.d(TAG, "onStop: >>");
    super.onStop();
    Log.d(TAG, "onStop: <<");
  }

  @Override
  public void onDetach() {
    Log.d(TAG, "onDetach: >>");
    super.onDetach();
    Log.d(TAG, "onDetach: <<");
  }

  @Override
  public void onDestroy() {
    Log.d(TAG, "onDestroy: >>");
    super.onDestroy();
    Log.d(TAG, "onDestroy: <<");
  }

}
