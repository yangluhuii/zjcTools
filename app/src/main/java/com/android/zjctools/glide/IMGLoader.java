package com.android.zjctools.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.android.zjctools.R;
import com.android.zjctools.app.GlideApp;
import com.android.zjctools.pick.ILoaderListener;
import com.android.zjctools.utils.ZDimen;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;



/**
 * Create by lzan13 on 2019/5/22 13:24
 *
 * 图片加载简单封装
 */
public class IMGLoader {

    /**
     * 加载封面
     *
     * @param context   上下文
     * @param cover     图片地址
     * @param imageView 目标 view
     */
    public static void loadCover(Context context, String cover, ImageView imageView) {
        ILoaderListener.Options options = new ILoaderListener.Options(cover);
        load(context, options, imageView);
    }

    /**
     * 加载相册封面
     *
     * @param context   上下文
     * @param cover     图片地址
     * @param imageView 目标 view
     */
    public static void loadAlbumCover(Context context, String cover, ImageView imageView) {
        ILoaderListener.Options options = new ILoaderListener.Options(cover);
        options.isRadius = true;
        options.radiusSize = ZDimen.dp2px(8);
        load(context, options, imageView, R.drawable.zjc_picture_default);
    }

    /**
     * 加载圆形图，一般是头像
     *
     * @param context   上下文
     * @param avatar    图片地址
     * @param imageView 目标 view
     */
    public static void loadCircleAvatar(Context context, String avatar, ImageView imageView) {
        ILoaderListener.Options options = new ILoaderListener.Options(avatar);
        options.isCircle = true;
        load(context, options, imageView, R.drawable.zjc_picture_default);
    }

    /**
     * 加载圆角图
     *
     * @param context   上下文
     * @param avatar    头像地址
     * @param imageView 目标 view
     */
    public static void loadRadiusAvatar(Context context, String avatar, ImageView imageView) {
        ILoaderListener.Options options = new ILoaderListener.Options(avatar);
        options.isRadius = true;
        options.radiusSize =ZDimen.dp2px(6);
        load(context, options, imageView, R.drawable.zjc_picture_default);
    }

    /**
     * 加载图片
     *
     * @param context   上下文
     * @param options   加载图片配置
     * @param imageView 目标 view
     */
    public static void load(Context context, ILoaderListener.Options options, ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions();
        if (options.isCircle) {
            requestOptions.circleCrop();
        } else if (options.isRadius) {
            requestOptions.transform(new MultiTransformation<>(new CenterCrop(), new RoundedCorners(options.radiusSize)));
        }
        if (options.isBlur) {
            requestOptions.transform(new BlurTransformation());
        }
        GlideApp.with(context).load(options.url).apply(requestOptions).thumbnail(placeholder(context, options)).into(imageView);
    }

    /**
     * 加载图片
     *
     * @param context   上下文
     * @param options   加载图片配置
     * @param imageView 目标 view
     */
    public static void load(Context context, ILoaderListener.Options options, ImageView imageView, int resId) {
        RequestOptions requestOptions = new RequestOptions();
        if (options.isCircle) {
            requestOptions.circleCrop();
        } else if (options.isRadius) {
            requestOptions.transform(new MultiTransformation<>(new CenterCrop(), new RoundedCorners(options.radiusSize)));
        }
        if (options.isBlur) {
            requestOptions.transform(new BlurTransformation());
        }
        GlideApp.with(context).load(options.url).apply(requestOptions).thumbnail(placeholder(context, options, resId)).into(imageView);
    }

    /**
     * 统一处理占位图
     *
     * @param context 上下文对象
     * @param options 加载配置
     * @return
     */
    private static RequestBuilder<Drawable> placeholder(Context context, ILoaderListener.Options options) {
        int resId = R.drawable.zjc_picture_default;
        return placeholder(context, options, resId);
    }

    /**
     * 处理占位图
     *
     * @param context 上下文对象
     * @param options 加载配置
     * @param resId   默认资源图
     * @return
     */
    private static RequestBuilder<Drawable> placeholder(Context context, ILoaderListener.Options options, int resId) {
        RequestOptions requestOptions = new RequestOptions();
        if (options.isCircle) {
            requestOptions.circleCrop();
        } else if (options.isRadius) {
            requestOptions.transform(new MultiTransformation<>(new CenterCrop(), new RoundedCorners(options.radiusSize)));
        }
        if (options.isBlur) {
            requestOptions.transform(new BlurTransformation());
        }

        return GlideApp.with(context).load(resId).apply(requestOptions);
    }

}
