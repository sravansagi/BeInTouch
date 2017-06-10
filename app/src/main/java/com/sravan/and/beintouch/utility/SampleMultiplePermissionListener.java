/*
 * Copyright (C) 2015 Karumi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sravan.and.beintouch.utility;

import android.Manifest;

import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.sravan.and.beintouch.ui.MainActivity;

import java.util.List;


/**
 * This class is part of dexter library which is used to handle the runtime permissions for devices above android M
 */
public class SampleMultiplePermissionListener implements MultiplePermissionsListener {

  private final MainActivity activity;

  public SampleMultiplePermissionListener(MainActivity activity) {
    this.activity = activity;
  }

  @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
    for (PermissionGrantedResponse response : report.getGrantedPermissionResponses()) {
      activity.showPermissionGranted(response.getPermissionName());
    }

    for (PermissionDeniedResponse response : report.getDeniedPermissionResponses()) {
      activity.showPermissionDenied(response.getPermissionName(), response.isPermanentlyDenied());
    }
  }

  @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions,
      PermissionToken token) {
    int permissionIdentifier = 0;
    for (PermissionRequest permission:permissions) {
      if (Manifest.permission.READ_CALL_LOG.equalsIgnoreCase(permission.getName())){
        permissionIdentifier++;
      } else if(Manifest.permission.READ_CONTACTS.equalsIgnoreCase(permission.getName())){
        permissionIdentifier = permissionIdentifier+2;
      }
    }
    activity.showPermissionRationale(token, permissionIdentifier);
  }
}
