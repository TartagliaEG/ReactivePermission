package br.com.tartagliaeg.rxp.domain;

import static android.Manifest.permission;
import static br.com.tartagliaeg.rxp.R.string.prompt_permission_access_coarse_location;
import static br.com.tartagliaeg.rxp.R.string.prompt_permission_access_fine_location;
import static br.com.tartagliaeg.rxp.R.string.prompt_permission_camera;
import static br.com.tartagliaeg.rxp.R.string.prompt_permission_read_external_storage;
import static br.com.tartagliaeg.rxp.R.string.prompt_permission_write_external_storage;

/**
 * Created by TartagliaEG on 2018/07/03.
 * ...
 */
public class PermissionRequests {
  public static final PermissionRequest CAMERA = PermissionRequest
    .newRequestWith(permission.CAMERA, prompt_permission_camera);
  public static final PermissionRequest ACCESS_FINE_LOCATION = PermissionRequest
    .newRequestWith(permission.ACCESS_FINE_LOCATION, prompt_permission_access_fine_location);
  public static final PermissionRequest ACCESS_COARSE_LOCATION = PermissionRequest
    .newRequestWith(permission.ACCESS_COARSE_LOCATION, prompt_permission_access_coarse_location);
  public static final PermissionRequest READ_EXTERNAL_STORAGE = PermissionRequest
    .newRequestWith(permission.READ_EXTERNAL_STORAGE, prompt_permission_read_external_storage);
  public static final PermissionRequest WRITE_EXTERNAL_STORAGE = PermissionRequest
    .newRequestWith(permission.WRITE_EXTERNAL_STORAGE, prompt_permission_write_external_storage);

  // TODO
  // public static final PermissionRequest READ_CONTACTS
  // public static final PermissionRequest WRITE_CONTACTS
  // public static final PermissionRequest GET_ACCOUNTS
  // public static final PermissionRequest RECORD_AUDIO
  // ...
}
