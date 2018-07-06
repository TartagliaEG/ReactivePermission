package br.com.tartagliaeg.rxp.utils;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by TartagliaEG on 2018/06/29.
 * ...
 */
public abstract class SimpleObserver<T> implements Observer<T> {
  @Override
  public void onSubscribe(@NonNull Disposable d) {

  }

  @Override
  public void onError(@NonNull Throwable e) {
    throw new RuntimeException(e);
  }

  @Override
  public void onComplete() {

  }
}
