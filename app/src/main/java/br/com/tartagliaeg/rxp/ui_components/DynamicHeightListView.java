package br.com.tartagliaeg.rxp.ui_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import br.com.tartagliaeg.rxp.R;

/**
 * Created by TartagliaEG on 2018/07/05.
 * ...
 */
public class DynamicHeightListView extends ListView {
  private final int mMaxHeight;


  public DynamicHeightListView(Context context, AttributeSet attrs) {
    super(context, attrs);

    TypedArray typedArr = context
      .getTheme()
      .obtainStyledAttributes(attrs, R.styleable.DynamicHeightListView, 0, 0);

    try {
      mMaxHeight = (int) typedArr.getDimension(R.styleable.DynamicHeightListView_maxHeight, -1);
    } finally {
      typedArr.recycle();
    }

  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int height = 0;

    if (getAdapter() == null || getAdapter().isEmpty() || mMaxHeight < 0)
      super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    for (int i = 0; i < getAdapter().getCount(); i++) {
      View view = getAdapter().getView(i, null, this);
      view.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
      height += view.getMeasuredHeight();

      if (height > mMaxHeight) {
        height = mMaxHeight;
        break;
      }
    }

    heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }
}
