package com.cupfish.music.ui.anim;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * @author xindrace
 */
public class RotateAnimation extends Animation {

    /** 值为true时可明确查看动画的旋转方向。 */
    public static final boolean DEBUG = false;
    /** 沿Y轴正方向看，数值减1时动画逆时针旋转。 */
    public static final boolean ROTATE_DECREASE = true;
    /** 沿Y轴正方向看，数值减1时动画顺时针旋转。 */
    public static final boolean ROTATE_INCREASE = false;
    /** Z轴上最大深度。 */
    public static final float DEPTH_Z = 310.0f;
    /** 动画显示时长。 */
    public static final long DURATION = 800l;
    /** 图片翻转类型。 */
    private final boolean type;
    private final float centerX;
    private final float centerY;
    private Camera camera;

    public RotateAnimation(float cX, float cY, boolean type) {
        centerX = cX;
        centerY = cY;
        this.type = type;
        // 设置动画时长
        setDuration(DURATION);
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        // 在构造函数之后、applyTransformation()之前调用本方法。
        super.initialize(width, height, parentWidth, parentHeight);
        camera = new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        // interpolatedTime:动画进度值，范围为0～1，0.5为正好翻转一半
        if (listener != null) {
            listener.interpolatedTime(interpolatedTime);
        }

        float from = 0.0f, to = 0.0f;
        if (type == ROTATE_DECREASE) {
            from = 0.0f;
            to = 180.0f;
        } else if (type == ROTATE_INCREASE) {
            from = 360.0f;
            to = 180.0f;
        }

        // 旋转的角度
        float degree = from + (to - from) * interpolatedTime;
        boolean overHalf = (interpolatedTime > 0.5f);
        if (overHalf) {
            // 翻转过半的情况下，为保证数字仍为可读的文字而非镜面效果的文字，需翻转180度。
            degree = degree - 180;
        }

        // 旋转深度
        float depth = (0.5f - Math.abs(interpolatedTime - 0.5f)) * DEPTH_Z;

        final Matrix matrix = transformation.getMatrix();
        camera.save();
        // 深度——》相当于与屏幕的距离
        camera.translate(0.0f, 0.0f, depth);
        // 以x轴旋转
        // camera.rotateX(degree);
        // 以y轴旋转
        camera.rotateY(degree);
        camera.getMatrix(matrix);
        camera.restore();

        if (DEBUG) {
            if (overHalf) {
                matrix.preTranslate(-centerX * 2, -centerY);
                matrix.postTranslate(centerX * 2, centerY);
            }
        } else {
            // 确保图片的翻转过程一直处于组件的中心点位置
            /*
             * preTranslate是指在setScale前平移,postTranslate是指在setScale后平移,它们参数是平移的距离, 而不是平移目的地的坐标!
             * 由于缩放是以(0,0)为中心的,所以为了把界面的中心与(0,0)对齐,就要preTranslate(-centerX, -centerY),setScale完成后,
             * 调用postTranslate(centerX, centerY),再把图片移回来,这样看到的动画效果就是activity的界面图片从中心不停的缩放了 注:centerX和centerY是界面中心的坐标
             */
            matrix.preTranslate(-centerX, -centerY);
            matrix.postTranslate(centerX, centerY);
        }
    }

    /** 用于监听动画进度。当值过半时需更新的内容。 */
    private InterpolatedTimeListener listener;

    public void setInterpolatedTimeListener(InterpolatedTimeListener listener) {
        this.listener = listener;
    }

    /** 动画进度监听器。 */
    public static interface InterpolatedTimeListener {
        public void interpolatedTime(float interpolatedTime);
    }

}