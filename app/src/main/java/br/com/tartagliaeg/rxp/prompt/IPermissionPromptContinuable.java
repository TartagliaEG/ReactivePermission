package br.com.tartagliaeg.rxp.prompt;

import br.com.tartagliaeg.rxp.domain.PermissionPack;
import br.com.tartagliaeg.rxp.domain.PermissionRequest;
import io.reactivex.Observable;

/**
 * Created by tartagle on 2018/07/02.
 * <p>
 * Core ReactPermission's interface. It exposes two methods: {@link IPermissionPromptContinuable#promptPermissions(int, PermissionRequest)} and {@link IPermissionPromptContinuable#continuePromptPermissions(int, PermissionRequest)}.
 * The first one is used to prompt permissions right before you using them. The last one is used to start observing again a permission
 * request previously made (it is useful to handle configuration changes).
 * <p>
 *
 * <p>Usage:</p>
 * <pre>
 * class MyActivity extends AppCompatActivity {
 *   private static final int PRM_TOUCH_PROFILE_PHOTO = 1;
 *   private IPermissionPromptContinuable mPerms;
 *
 *   public void onCreate(Bundle savedInstanceState) {
 *     // [...] //
 *     mPerms = PermissionPromptFragment.createOrRetrievePermissionPrompt(getSupportFragmentManager());
 *
 *     findViewById(R.id.profile_photo).setOnClickListener(new View.OnClickListener() {
 *       public void onClick(View view) {
 *         mPerms
 *           .promptPermissions(PRM_TOUCH_PROFILE_PHOTO, PermissionRequests.CAMERA)
 *           .subscribe(touchPhotoObserver());
 *       }
 *     });
 *   }
 *
 *   protected void onStart() {
 *     super.onStart();
 *
 *     mPerms
 *       .continuePromptPermissions(PRM_TOUCH_PROFILE_PHOTO, PermissionRequests.CAMERA)
 *       .subscribe(touchPhotoObserver());
 *   }
 * }
 * </pre>
 */
public interface IPermissionPromptContinuable {
  /**
   * Ask for the given permissions.
   * If all the permissions are already granted, it returns immediately without prompting the user.
   * If there're a permission request ongoing with the same permission set, it reuse the same request.
   *
   * @param promptId an int used to identify this request uniquely.
   * @param request  a list of permissions to ask for. {@link PermissionRequest}
   * @return an Observable holding a PermissionPack instance.
   */
  Observable<PermissionPack> promptPermissions(int promptId, PermissionRequest request);


  /**
   * Start observing again to a previous permission request.
   * Once that the configuration change event destroys the referenced objects, you must subscribe again to all pending
   * permission request. This method uses the promptId to identify an existing request and, if any is found, returns the
   * respective observable. If no request is found for the given id, it returns a finished observable, so it's safe to
   * call this method during the creation lifecycle.
   *
   * @param promptId an int used to identify a pending permission request
   * @param request  The list of permissions passed to promptPermissions
   * @return an Observable holding a PermissionPack instance.
   */
  Observable<PermissionPack> continuePromptPermissions(int promptId, PermissionRequest request);

}
