/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easeui.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.model.EaseImageCache;
import com.hyphenate.easeui.utils.EaseImageUtils;
import com.hyphenate.easeui.utils.EaseLoadLocalBigImgTask;
import com.hyphenate.easeui.widget.photoview.EasePhotoView;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.ImageUtils;
import com.stratagile.pnrouter.R;
import com.stratagile.pnrouter.base.BaseActivity;
import com.stratagile.pnrouter.utils.UIUtils;

import java.io.File;

/**
 * download and show original image
 * 
 */
public class EaseShowChooseImageActivity extends BaseActivity {
	private static final String TAG = "ShowBigImage"; 
	private ProgressDialog pd;
	private EasePhotoView image;
	private int default_res = R.drawable.image_defalut_bg;
	private String localFilePath;
	private Bitmap bitmap;
	private boolean isDownloaded;
	private CheckBox chooseCheckBox;
	private Button sureBtn;
	private boolean isCheck;
	private String path;
	private TextView statusBar;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setNeedFront(true);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ease_activity_show_choose_image);
		isCheck = false;
		image = (EasePhotoView) findViewById(R.id.image);
		chooseCheckBox = (CheckBox) findViewById(R.id.chooseCheckBox);
		sureBtn = (Button) findViewById(R.id.sureBtn);
		ProgressBar loadLocalPb = (ProgressBar) findViewById(R.id.pb_load_local);
		statusBar = findViewById(R.id.statusBar);

		findViewById(R.id.llSort).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);//设置状态栏白色字体
		}

		statusBar.setLayoutParams(new RelativeLayout.LayoutParams(UIUtils.getDisplayWidth(this), UIUtils.getStatusBarHeight(this)));
		default_res = getIntent().getIntExtra("default_image", R.drawable.ease_default_avatar);
		Uri uri = getIntent().getParcelableExtra("uri");
		localFilePath = getIntent().getExtras().getString("localUrl");
		path  = getIntent().getStringExtra("path");
		String msgId = getIntent().getExtras().getString("messageId");
		EMLog.d(TAG, "show big deleteMsgId:" + msgId );
		//show the image if it exist in local path
		if (uri != null && new File(uri.getPath()).exists()) {
			EMLog.d(TAG, "showbigimage file exists. directly show it");
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			// int screenWidth = metrics.widthPixels;
			// int screenHeight =metrics.heightPixels;
			Bitmap bigData = EaseImageUtils.getBitmap(new File(uri.getPath()));
			int degree = EaseImageUtils.readPictureDegree(uri.getPath());
			if(degree != 0)
			{
				Bitmap bmpOk = EaseImageUtils.rotateToDegrees(bigData, degree);
				image.setImageBitmap(bmpOk);
			}else{
				image.setImageBitmap(bigData);
			}
//			bitmap = EaseImageCache.getInstance().get(uri.getPath());
//			if (bitmap == null) {
//				EaseLoadLocalBigImgTask task = new EaseLoadLocalBigImgTask(this, uri.getPath(), image, loadLocalPb, UIUtils.getDisplayWidth(this),
//						ImageUtils.SCALE_IMAGE_HEIGHT);
//				if (android.os.Build.VERSION.SDK_INT > 10) {
//					task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//				} else {
//					task.execute();
//				}
//			} else {
//				Bitmap bigData = EaseImageUtils.getBitmap(new File(uri.getPath()));
//				int degree = EaseImageUtils.readPictureDegree(uri.getPath());
//				if(degree != 0)
//				{
//					Bitmap bmpOk = EaseImageUtils.rotateToDegrees(bigData, degree);
//					image.setImageBitmap(bmpOk);
//				}else{
//					image.setImageBitmap(bigData);
//				}
//
//			}
		} else if(msgId != null) {
		    downloadImage(msgId);
		}else {
			image.setImageResource(default_res);
		}

		/*image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("isCheck", isCheck);
				setResult(RESULT_OK,intent);
				finish();
			}
		});*/
		sureBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isCheck = chooseCheckBox.isChecked();
				Intent intent = new Intent();
				intent.putExtra("isCheck", isCheck);
				intent.putExtra("path", path);
				setResult(RESULT_OK,intent);
				finish();
			}
		});
	}
	
	/**
	 * download image
	 * 
	 * @param
	 */
	@SuppressLint("NewApi")
	private void downloadImage(final String msgId) {
        EMLog.e(TAG, "download with messageId: " + msgId);
		String str1 = getResources().getString(R.string.Download_the_pictures);
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setCanceledOnTouchOutside(false);
		pd.setMessage(str1);
		pd.show();
		File temp = new File(localFilePath);
		final String tempPath = temp.getParent() + "/temp_" + temp.getName();
		final EMCallBack callback = new EMCallBack() {
			public void onSuccess() {
			    EMLog.e(TAG, "onSuccess" );
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
                        new File(tempPath).renameTo(new File(localFilePath));

                        DisplayMetrics metrics = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(metrics);
						int screenWidth = metrics.widthPixels;
						int screenHeight = metrics.heightPixels;

						bitmap = ImageUtils.decodeScaleImage(localFilePath, screenWidth, screenHeight);
						if (bitmap == null) {
							image.setImageResource(default_res);
						} else {
							int degree = EaseImageUtils.readPictureDegree(localFilePath);
							if(degree !=0)
							{
								Bitmap bmpOk = EaseImageUtils.rotateToDegrees(bitmap, degree);
								image.setImageBitmap(bmpOk);
								EaseImageCache.getInstance().put(localFilePath, bmpOk);
							}else{
								image.setImageBitmap(bitmap);
								EaseImageCache.getInstance().put(localFilePath, bitmap);
							}

							isDownloaded = true;
						}
						if (isFinishing() || isDestroyed()) {
						    return;
						}
						if (pd != null) {
							pd.dismiss();
						}
					}
				});
			}

			public void onError(int error, String msg) {
				EMLog.e(TAG, "offline file transfer error:" + msg);
				File file = new File(tempPath);
				if (file.exists()&&file.isFile()) {
					file.delete();
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (EaseShowChooseImageActivity.this.isFinishing() || EaseShowChooseImageActivity.this.isDestroyed()) {
						    return;
						}
                        image.setImageResource(default_res);
                        pd.dismiss();
					}
				});
			}

			public void onProgress(final int progress, String status) {
				EMLog.d(TAG, "Progress: " + progress);
				final String str2 = getResources().getString(R.string.Download_the_pictures_new);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
                        if (EaseShowChooseImageActivity.this.isFinishing() || EaseShowChooseImageActivity.this.isDestroyed()) {
                            return;
                        }
						pd.setMessage(str2 + progress + "%");
					}
				});
			}
		};
		
		EMMessage msg = EMClient.getInstance().chatManager().getMessage(msgId);
		msg.setMessageStatusCallback(callback);

		EMLog.e(TAG, "downloadAttachement");
		EMClient.getInstance().chatManager().downloadAttachment(msg);
	}

	@Override
	public void onBackPressed() {
//		isCheck = chooseCheckBox.isChecked();
//		Intent intent = new Intent();
//		intent.putExtra("isCheck", isCheck);
//		intent.putExtra("path", path);
//		setResult(RESULT_OK,intent);
		finish();
	}

	@Override
	protected void initData() {

	}

	@Override
	protected void initView() {

	}

	@Override
	protected void setupActivityComponent() {

	}

	@Override
	public void onPointerCaptureChanged(boolean hasCapture) {

	}
}
