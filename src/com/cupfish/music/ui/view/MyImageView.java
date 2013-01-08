package com.cupfish.music.ui.view;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class MyImageView extends ImageView {

	private static final String TAG = "MyImageView";
	private boolean onAnimation = true;
	private int rotateDegree = 10;
	private boolean isFirst = true;
	private float minScale = 0.95f;
	private int mWidth;
	private int mHeight;
	private boolean isFinish = true;
	private boolean isActionMove = false;
	private boolean isScale = false;
	private Camera camera;

	private boolean XbigY = false;
	private float rotateX = 0;
	private float rotateY = 0;

	private OnViewClick onClick = null;

	public MyImageView(Context context) {
		super(context);
		camera = new Camera();
	}

	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		camera = new Camera();
	}

	public MyImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		camera = new Camera();
	}

	public void setAnimationOn(boolean onAnimation) {
		this.onAnimation = onAnimation;
	}

	public void setOnClickIntent(OnViewClick onClick) {
		this.onClick = onClick;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (isFirst) {
			isFirst = false;
			init();
		}
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG));
	}

	private void init() {
		mWidth = getWidth() - getPaddingLeft() - getPaddingRight();
		mHeight = getHeight() - getPaddingTop() - getPaddingBottom();
		Drawable drawable = getDrawable();
		BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
		bitmapDrawable.setAntiAlias(true);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		if (!onAnimation) {
			Log.i(TAG, "onAnimation");
			return true;
		}

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			float x = event.getX();
			float y = event.getY();
			rotateX = mWidth / 2 - x;
			rotateY = mHeight / 2 - y;
			XbigY = Math.abs(rotateX) > Math.abs(rotateY) ? true : false;
			isScale = x > mWidth / 3 && x < mWidth * 2 / 3 && y > mHeight / 3
					&& y < mHeight * 2 / 3;
			isActionMove = false;
			if (isScale) {
				handler.sendEmptyMessage(1);
			} else {
				rotateHandler.sendEmptyMessage(1);
			}
			Log.i(TAG, "MotionEvent.ACTION_DOWN");
			break;
		case MotionEvent.ACTION_MOVE:
			float x2 = event.getX();
			float y2 = event.getY();
			if (x2 > mWidth || x2 > mHeight || x2 < 0 || y2 < 0) {
				isActionMove = true;
			} else {
				isActionMove = false;
			}
			break;
		case MotionEvent.ACTION_UP:
			if (isScale) {
				handler.sendEmptyMessage(6);
			} else {
				rotateHandler.sendEmptyMessage(6);
			}
			break;
		}

		return true;
	}

	private Handler rotateHandler = new Handler() {
		private Matrix matrix = new Matrix();
		private float count = 0;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			matrix.set(getImageMatrix());
			switch (msg.what) {
			case 1:
				count = 0;
				beginRotate(matrix, (XbigY ? count : 0), (XbigY ? 0 : count));
				rotateHandler.sendEmptyMessage(2);
				break;
			case 2:
				beginRotate(matrix, (XbigY ? count : 0), (XbigY ? 0 : count));
				if (count < getRotateDegree()) {
					rotateHandler.sendEmptyMessage(2);
				} else {
					isFinish = true;
				}
				count++;
				count++;
				break;
			case 3:
				beginRotate(matrix, (XbigY ? count : 0), (XbigY ? 0 : count));
				if (count > 0) {
					rotateHandler.sendEmptyMessage(3);
				} else {
					isFinish = true;
					if (!isActionMove && onClick != null) {
						onClick.onClick();
					}
				}
				count--;
				count--;
				break;
			case 6:
				count = getRotateDegree();
				beginRotate(matrix, (XbigY ? count : 0), (XbigY ? 0 : count));
				rotateHandler.sendEmptyMessage(3);
				break;
			} 
		}
	};

	private Handler handler = new Handler() {
		private Matrix matrix = new Matrix();
		private float s;
		int count = 0;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			matrix = getImageMatrix();
			switch (msg.what) {
			case 1:
				Log.i(TAG, "ScaleHandler -------------->1");
				if (!isFinish) {
					return;
				} else {
					Log.i(TAG, "ScaleHandler -------------->isFinish==false");
					isFinish = false;
					count = 0;
					s = (float) Math.sqrt(Math.sqrt(minScale));
					beginScale(matrix, s);
					handler.sendEmptyMessage(2);
				}
				break;
			case 2:
				beginScale(matrix, s);
				if (count < 4) {
					handler.sendEmptyMessage(2);
				} else {
					isFinish = true;
					if (!isActionMove && onClick != null) {
						onClick.onClick();
					}
				}
				count++;
				break;
			case 6:
				if (!isFinish) {
					handler.sendEmptyMessage(6);
				} else {
					isFinish = false;
					count = 0;
					s = (float) Math.sqrt(Math.sqrt(1.0f / minScale));
					beginScale(matrix, s);
					handler.sendEmptyMessage(2);
				}
				break;
			}
		}

	};

	private synchronized void beginRotate(Matrix matrix, float rX,
			float rY) {
		int scaleX = (int) (mWidth * 0.5f);
		int scaleY = (int) (mHeight * 0.5f);
		camera.save();
		camera.rotateX(rotateY > 0 ? rY : -rY);
		camera.rotateY(rotateX < 0 ? rX : -rX);
		camera.getMatrix(matrix);
		camera.restore();

		// 控制中心点，怎么控制呢
		if (rotateX > 0 && rX != 0) {
			matrix.preTranslate(-mWidth, -scaleY);
			matrix.postTranslate(mWidth, scaleY);
		} else if (rotateY > 0 && rY != 0) {
			matrix.preTranslate(-scaleX, -mHeight);
			matrix.postTranslate(scaleX, mHeight);
		} else if (rotateX < 0 && rX != 0) {
			matrix.preTranslate(-0, -scaleY);
			matrix.postTranslate(0, scaleY);
		} else if (rotateY < 0 && rY != 0) {
			matrix.preTranslate(-scaleX, -0);
			matrix.postTranslate(scaleX, 0);
		}
		setImageMatrix(matrix);
	}

	private synchronized void beginScale(Matrix matrix, float scale) {
		int scaleX = (int) (mWidth * 0.5f);
		int scaleY = (int) (mHeight * 0.5f);
		matrix.postScale(scale, scale, scaleX, scaleY);
		setImageMatrix(matrix);
	}

	public int getRotateDegree() {
		return rotateDegree;
	}

	public void setRotateDegree(int rotateDegree) {
		this.rotateDegree = rotateDegree;
	}

	public float getMinScale() {
		return minScale;
	}

	public void setMinScale(float minScale) {
		this.minScale = minScale;
	}

	public interface OnViewClick {
		public void onClick();
	}

}
