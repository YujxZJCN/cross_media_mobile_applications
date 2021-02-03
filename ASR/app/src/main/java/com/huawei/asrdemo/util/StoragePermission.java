package com.huawei.asrdemo.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by c00304202 on 2016/04/21.
 */
public class StoragePermission {
	private static final int REQUEST_PERMISSIONS = 1;

	private static String[] PERMISSIONS_STORAGE_AND_AUDIO = {
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.RECORD_AUDIO,
			Manifest.permission.MODIFY_AUDIO_SETTINGS
	};

	private static String[] PERMISSIONS_ALL = {
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.RECORD_AUDIO,
			Manifest.permission.MODIFY_AUDIO_SETTINGS,
			Manifest.permission.READ_CONTACTS,
			Manifest.permission.WRITE_CONTACTS
	};

	private static String[] PERMISSIONS_READ_STORAGE = {
			Manifest.permission.READ_EXTERNAL_STORAGE
	};

	private static String[] PERMISSIONS_WRITE_STORAGE = {
			Manifest.permission.WRITE_EXTERNAL_STORAGE
	};

	private static String[] PERMISSIONS_AUDIO = {
			Manifest.permission.RECORD_AUDIO
	};

	public static void getExStorageReadPermission(Activity activity) {
		int permission_read = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
		int permission_write = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		int permission_record = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
		int permission_set_audio = ContextCompat.checkSelfPermission(activity, Manifest.permission.MODIFY_AUDIO_SETTINGS);
		if (permission_read != PackageManager.PERMISSION_GRANTED
				|| permission_write != PackageManager.PERMISSION_GRANTED
				|| permission_record != PackageManager.PERMISSION_GRANTED
				|| permission_set_audio != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(
					activity,
					PERMISSIONS_STORAGE_AND_AUDIO,
					REQUEST_PERMISSIONS
			);
		}
	}

	public static void getAllPermission(Activity activity) {
		int permission_read = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
		int permission_write = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		int permission_record = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
		int permission_set_audio = ContextCompat.checkSelfPermission(activity, Manifest.permission.MODIFY_AUDIO_SETTINGS);
		int permission_read_contacts = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS);
		int permission_write_contacts = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CONTACTS);

		if (permission_read != PackageManager.PERMISSION_GRANTED
				|| permission_write != PackageManager.PERMISSION_GRANTED
				|| permission_record != PackageManager.PERMISSION_GRANTED
				|| permission_set_audio != PackageManager.PERMISSION_GRANTED
				|| permission_read_contacts != PackageManager.PERMISSION_GRANTED
				|| permission_write_contacts != PackageManager.PERMISSION_GRANTED
				) {
			ActivityCompat.requestPermissions(
					activity,
					PERMISSIONS_ALL,
					REQUEST_PERMISSIONS
			);
		}
	}

}
