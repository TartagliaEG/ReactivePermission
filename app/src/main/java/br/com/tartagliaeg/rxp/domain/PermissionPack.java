package br.com.tartagliaeg.rxp.domain;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by TartagliaEG on 2018/06/29.
 * ...
 */
public class PermissionPack {
  private final Map<String, Permission> mPermissions = new HashMap<>();

  public PermissionPack(Permission... permissions) {
    this(Arrays.asList(permissions));
  }

  public PermissionPack(Collection<Permission> permissions) {
    for (Permission p : permissions)
      mPermissions.put(p.getName(), p);
  }

  public PermissionPack(Context context, String... permissions) {
    this(context, Arrays.asList(permissions));
  }

  public PermissionPack(Context context, Collection<String> permissions) {
    for (String permissionName : permissions)
      mPermissions.put(permissionName, new Permission(permissionName, context));
  }

  public static PermissionPack from(Permission... permissions) {
    return new PermissionPack(permissions);
  }

  public static PermissionPack from(Collection<Permission> permissions) {
    return new PermissionPack(permissions);
  }

  public static PermissionPack from(Context context, String... permissions) {
    return new PermissionPack(context, permissions);
  }

  public static PermissionPack from(Context context, Collection<String> permissions) {
    return new PermissionPack(context, permissions);
  }

  public Permission get(String permissionName) {
    if (!mPermissions.containsKey(permissionName))
      throw new IllegalArgumentException("No newRequestMerging found with name: " + permissionName);
    return mPermissions.get(permissionName);
  }

  public Iterable<Permission> iterable() {
    return mPermissions.values();
  }

  void addPermission(Permission permission) {
    mPermissions.put(permission.getName(), permission);
  }

  void setAsAsked() {
    for (Permission permission : mPermissions.values())
      permission.setAsAsked();
  }

  public PermissionPack getPackFilteredByAsked() {
    PermissionPack pack = new PermissionPack();

    for (Permission p : mPermissions.values())
      if (p.isAsked()) pack.addPermission(p);

    return pack;
  }

  public PermissionPack getPackFilteredByGranted() {
    PermissionPack pack = new PermissionPack();

    for (Permission p : mPermissions.values())
      if (p.isGranted()) pack.addPermission(p);

    return pack;
  }

  public PermissionPack getPackFilteredByDenied() {
    PermissionPack pack = new PermissionPack();

    for (Permission p : mPermissions.values())
      if (!p.isGranted()) pack.addPermission(p);

    return pack;
  }

  public PermissionPack getPackFilteredByNeverAsked() {
    PermissionPack pack = new PermissionPack();

    for (Permission p : mPermissions.values())
      if (!p.isAsked()) pack.addPermission(p);

    return pack;
  }

  public PermissionPack getPackFilteredByPermissions(String... permissions) {
    PermissionPack pack = new PermissionPack();

    for (String permission : permissions)
      if (mPermissions.containsKey(permission))
        pack.addPermission(get(permission));

    return pack;
  }

  public PermissionPack getPackExcludingPermissions(String... permissions) {
    PermissionPack pack = new PermissionPack();
    List<String> toExclude = Arrays.asList(permissions);

    for (Permission permission : mPermissions.values())
      if (!toExclude.contains(permission.getName()))
        pack.addPermission(permission);

    return pack;
  }

  public PermissionPack newPackMergingPermissions(Context context, String... permissions) {
    PermissionPack pack = PermissionPack.from(context, permissions);

    for (Permission permission : mPermissions.values())
      pack.addPermission(permission);

    return pack;
  }

  public boolean isAnyNeverAskAgain(Activity activity) {
    for (Permission p : mPermissions.values())
      if (p.isNeverAskAgain(activity)) return true;

    return false;
  }

  public boolean isAnyNeverAskAgain(Fragment fragment) {
    for (Permission p : mPermissions.values())
      if (p.isNeverAskAgain(fragment)) return true;

    return false;
  }


  public boolean isAllGranted() {
    for (Permission p : mPermissions.values())
      if (!p.isGranted()) return false;

    return true;
  }

  public String[] getPermissionNames() {
    String[] names = new String[mPermissions.size()];
    Permission[] permissions = mPermissions.values().toArray(new Permission[mPermissions.size()]);

    for (int i = 0; i < mPermissions.size(); i++)
      names[i] = permissions[i].getName();

    return names;
  }

  public String generatePermissionsIdentifier() {
    StringBuilder identifier = new StringBuilder();
    List<Permission> permissions = new ArrayList<>(mPermissions.values());
    Collections.sort(permissions);

    for (Permission permission : permissions)
      identifier.append(permission.getName()).append(":");

    return identifier.toString();
  }

  public int size() {
    return mPermissions.size();
  }

  public boolean isEmpty() {
    return mPermissions.isEmpty();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PermissionPack that = (PermissionPack) o;

    return mPermissions.equals(that.mPermissions);
  }

  @Override
  public int hashCode() {
    return mPermissions.hashCode();
  }

  @Override
  public String toString() {
    return "PermissionPack{" +
      "mRequest=" + mPermissions +
      '}';
  }
}
