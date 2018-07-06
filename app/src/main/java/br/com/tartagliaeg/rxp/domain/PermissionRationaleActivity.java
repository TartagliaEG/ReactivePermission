package br.com.tartagliaeg.rxp.domain;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import br.com.tartagliaeg.rxp.R;
import br.com.tartagliaeg.rxp.ReactivePermissionConfiguration;

/**
 * Created by TartagliaEG on 2018/07/04.
 * ...
 */
public class PermissionRationaleActivity extends AppCompatActivity {
  private static final String TAG = PermissionRationaleActivity.class.getName();
  private static final String EXT_PERMISSION_REQUEST = TAG + ".permission_request";
  private static final String EXT_RST_TYPE = TAG + ".result_type";

  public static final int RST_CANCELED = 1;
  public static final int RST_CONFIRMED = 2;
  public static final int RST_UNNECESSARY = 3;

  private PermissionRequest mRequest;
  private AppCompatButton mBtnCancel;
  private AppCompatButton mBtnSettings;
  private ListView mPermissionList;
  private AppCompatTextView mHeader;

  public static int extractResponse(Intent intent) {
    return RST_CANCELED;
  }

  public static Intent newIntent(Context context, PermissionRequest request) {
    Intent intent = new Intent(context, PermissionRationaleActivity.class);
    intent.putExtra(EXT_PERMISSION_REQUEST, request);
    return intent;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.permission_prompt_rationale);

    DisplayMetrics metrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(metrics);
    getWindow().setLayout(metrics.widthPixels, metrics.heightPixels);

    mRequest = getIntent().getParcelableExtra(EXT_PERMISSION_REQUEST);

    mBtnCancel = findViewById(R.id.apnaa_cancel_button);
    mBtnCancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent();
        intent.putExtra(EXT_RST_TYPE, RST_CANCELED);
        setResult(RESULT_CANCELED, intent);
        finish();
      }
    });

    mBtnSettings = findViewById(R.id.apnaa_settings_button);
    mBtnSettings.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent();
        intent.putExtra(EXT_RST_TYPE, RST_CONFIRMED);
        setResult(RESULT_OK, intent);
        finish();
      }
    });

    mPermissionList = findViewById(R.id.apnaa_permissions_list);
    mPermissionList.setAdapter(new Adapter(this, mRequest));

    mHeader = findViewById(R.id.apnaa_permissions_header);
  }


  @Override
  protected void onResume() {
    super.onResume();
    PermissionPack pack = PermissionPack.from(this, mRequest.getPermissionNames());

    if (pack.isAllGranted()) {
      Intent intent = new Intent();
      intent.putExtra(EXT_RST_TYPE, RST_UNNECESSARY);
      setResult(Activity.RESULT_OK, intent);
      finish();
      return;
    }

    boolean isAnyNeverAskAgain = pack.isAnyNeverAskAgain(this);
    ReactivePermissionConfiguration config = ReactivePermissionConfiguration.getInstance();

    String headerText = this.getResources().getQuantityString(
      isAnyNeverAskAgain
        ? config.getNeverAskAgainHeader()
        : config.getPermissionRationaleHeader(),
      mRequest.size()
    );

    mHeader.setText(headerText);

    mBtnCancel.setText(isAnyNeverAskAgain ? config.getNeverAskAgainCancelButton() : config.getPermissionRationaleCancelButton());
    mBtnSettings.setText(isAnyNeverAskAgain ? config.getNeverAskAgainSettingsButton() : config.getPermissionRationaleConfirmButton());
  }

  @Override
  public void onBackPressed() {
    // Disable back button
  }

  private static class Adapter extends ArrayAdapter<PermissionRequest.PermissionMeta> {
    private final List<PermissionRequest.PermissionMeta> mPermissionMetas;
    private final Context mContext;

    public Adapter(Context context, PermissionRequest request) {
      super(context, R.layout.permission_prompt_rationale_list_item);
      mPermissionMetas = request.getPermissionMetas();
      mContext = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
      view = view == null ? LayoutInflater.from(mContext).inflate(R.layout.permission_prompt_rationale_list_item, parent, false) : view;
      ViewHolder holder = view.getTag() == null ? new ViewHolder(mContext, view) : (ViewHolder) view.getTag();
      holder.bind(mPermissionMetas.get(position));
      return view;
    }

    @Override
    public int getCount() {
      return mPermissionMetas.size();
    }
  }

  private static class ViewHolder {
    private final AppCompatImageView mIcon;
    private final AppCompatTextView mPrompt;
    private final Context mContext;

    ViewHolder(Context context, View container) {
      mIcon = container.findViewById(R.id.capi_permission_icon);
      mPrompt = container.findViewById(R.id.capi_prompt_message);
      mContext = context;
    }

    void bind(PermissionRequest.PermissionMeta meta) {
      mPrompt.setText(meta.mPromptMessage);
      Drawable d = meta.getDefaultPermissionIcon(mContext);
      d.setColorFilter(ContextCompat.getColor(mContext, R.color.app_accent), PorterDuff.Mode.SRC_IN);
      mIcon.setImageDrawable(d);
    }
  }
}
