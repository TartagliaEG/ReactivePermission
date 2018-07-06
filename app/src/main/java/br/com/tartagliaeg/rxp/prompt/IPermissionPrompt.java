package br.com.tartagliaeg.rxp.prompt;

import br.com.tartagliaeg.rxp.domain.PermissionPack;
import br.com.tartagliaeg.rxp.domain.PermissionRequest;
import io.reactivex.Observable;

/**
 * Created by TartagliaEG on 2018/06/29.
 * ...
 */
public interface IPermissionPrompt {

  /**
   * Ask for the given permissions.
   *
   * @param request a list of permissions to ask for. {@link PermissionRequest}
   * @return an Observable holding an PermissionPack instance.
   */
  Observable<PermissionPack> promptPermissions(PermissionRequest request);

}
