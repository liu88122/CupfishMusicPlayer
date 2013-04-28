package com.cupfish.music.common;

import java.util.List;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.cupfish.music.R;
import com.cupfish.music.bean.Song;

public class BaseApp extends Application {

	public List<Song> playlist;
	private OnClickListener mOnClickListener;

	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	
	

	public AlertDialog showDialog(Context context, String title, String message, boolean buttonShow, OnClickListener l){
		
		mOnClickListener = l;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		final AlertDialog dialog = builder.create();
		dialog.show();
		
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		
		Window window = dialog.getWindow();
		
		View view = View.inflate(context, R.layout.dialog_panel, null);
		TextView mTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
		TextView mMessage = (TextView) view.findViewById(R.id.tv_dialog_message);
		Button mConfirm = (Button) view.findViewById(R.id.bt_confirm);
		Button mCancel = (Button) view.findViewById(R.id.bt_cancel);
		View mDivider1 = view.findViewById(R.id.divider1);
		View mDivider2 = view.findViewById(R.id.divider2);
		
		if(!TextUtils.isEmpty(title)){
			mTitle.setText(title);
		}
		if(!TextUtils.isEmpty(message)){
			mMessage.setText(message);
		}
		if(!buttonShow){
			mConfirm.setVisibility(View.GONE);
			mCancel.setVisibility(View.GONE);
			mDivider1.setVisibility(View.GONE);
			mDivider2.setVisibility(View.GONE);
		}
		mConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(mOnClickListener!=null){
					mOnClickListener.onConfirmClick();
				}
			}
		});
		mCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(mOnClickListener!=null){
					mOnClickListener.onCancelClick();
				}
			}
		});
		
		window.setLayout(width * 5 / 6, WindowManager.LayoutParams.WRAP_CONTENT);
		window.setContentView(view);
		return dialog;
	}
	
	public interface OnClickListener{
		public void onConfirmClick();
		public void onCancelClick();
	}
}
