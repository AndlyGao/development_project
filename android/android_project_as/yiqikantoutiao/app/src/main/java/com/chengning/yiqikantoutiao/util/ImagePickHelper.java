package com.chengning.yiqikantoutiao.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.chengning.common.base.util.PermissionManager;
import com.chengning.yiqikantoutiao.R;

import java.io.File;
import java.io.FileNotFoundException;

public class ImagePickHelper {
	public static final int PHOTO_REQUEST_TAKEPHOTO = 3111;// 拍照
	public static final int PHOTO_REQUEST_GALLERY = 3112;// 从相册中选择
	public static final int PHOTO_REQUEST_CROP = 3113;// 裁剪

	private static final int REQUEST_CODE_PERMISSION_CAMERA = 8;
    private static final int REQUEST_CODE_PERMISSION_ABULM = 9;

	private static final String IMAGE_FILE_LOCATION = "file:///sdcard/temp.jpg";//temp file
    Uri imageUri = Uri.parse(IMAGE_FILE_LOCATION);//The Uri to store the big bitmap

	private Activity activity;
	private String fileName;

	public ImagePickHelper(final Activity activity, final String fileName){
		this.activity = activity;
		this.fileName = fileName;
		deleteFile();
	}

	public ImagePickHelper() {
	}

	public void showPickDialog(final PermissionManager permissionManager){
		String[] items = new String[] {activity.getResources().getString(R.string.myProfile_edit_activity_takephoto),
				activity.getResources().getString(R.string.myProfile_edit_activity_localphoto)};
		DialogInterface.OnClickListener click = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which){
					//拍照
					case 0:
						handleCameraClick(activity, fileName, permissionManager);
						break;
					//从本地选择图片
					case 1:
						handleAbulmClick(activity, permissionManager);
						break;
					default :
						break;

				}
			}
		};
		new AlertDialog.Builder(activity).setItems(items,
				click).show();
	}

	/**
	 * 处理相册点击
	 * @param activity
	 */
	public void handleAbulmClick(final Activity activity, final PermissionManager permissionManager) {
		PermissionManager.PermisstionCallback callback = new PermissionManager.PermisstionCallback() {
			@Override
			public void success() {
				Intent intentFromGallery = new Intent(
						Intent.ACTION_PICK,
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				activity.startActivityForResult(intentFromGallery,
						PHOTO_REQUEST_GALLERY);
			}

			@Override
			public void failure() {
				return;
			}
		};
		permissionManager.init(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_CODE_PERMISSION_ABULM, callback);

	}

	/**
	 * 处理点击相机
	 * @param activity
	 */
	public void handleCameraClick(final Activity activity, final String fileName, final PermissionManager permissionManager) {
		PermissionManager.PermisstionCallback callback = new PermissionManager.PermisstionCallback() {
			@Override
			public void success() {
				Intent intentFromCapture = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);
				intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri
						.fromFile(Common.creatFile(JUrl.FilePathTemp,fileName)));
				activity.startActivityForResult(intentFromCapture,
						PHOTO_REQUEST_TAKEPHOTO);
			}

			@Override
			public void failure() {
				return;
			}
		};
		permissionManager.init(activity, Manifest.permission.CAMERA, REQUEST_CODE_PERMISSION_CAMERA, callback);

	}

	private void deleteFile() {
		File file = Common.creatFile(JUrl.FilePathTemp,fileName);
		if(file.exists() && file.isFile()){
			file.delete();
		}
	}

	public boolean handleActivityResult(int requestCode, int resultCode, Intent data){
		switch (requestCode) {
		case PHOTO_REQUEST_TAKEPHOTO:
			File file = Common.creatFile(JUrl.FilePathTemp,fileName);
			try {
				if(file.exists() && file.isFile()){
					startPhotoCrop(Uri.fromFile(file), 200);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case PHOTO_REQUEST_GALLERY:
			if (data != null && data.getData() != null) {
				startPhotoCrop(data.getData(), 200);
			}
			break;
		case PHOTO_REQUEST_CROP:
//		        if (data != null && data.getExtras() != null) {
//		            Bitmap face = data.getExtras().getParcelable("data");
//		            if(face != null){
//						ImageUtils.compressBmpFromBmp(face, Common.creatFile(JUrl.FilePathTemp,fileName));
//						return true;
//		            }
//		        }
			if (imageUri != null) {
				Bitmap bitmap = decodeUriAsBitmap(imageUri);// decode bitmap
				if (bitmap != null) {
					ImageUtils.compressBmpFromBmp(bitmap,
							Common.creatFile(JUrl.FilePathTemp, fileName));
					return true;
				}
			}
			break;
		}
		return false;
	}
	private void startPhotoCrop(Uri uri, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");

		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		intent.putExtra("return-data", false);
		intent.putExtra("scale", true);//解决部分手机裁剪出现黑边问题
		intent.putExtra("scaleUpIfNeeded", true);//解决部分手机裁剪出现黑边问题
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		activity.startActivityForResult(intent, PHOTO_REQUEST_CROP);
	}
	private Bitmap decodeUriAsBitmap(Uri uri){
		 Bitmap bitmap = null;
		 try {
		  	bitmap = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(uri));
		 } catch (FileNotFoundException e) {
		  	e.printStackTrace();
		  	return null;
		 }
		 return bitmap;
	}
}
