package com.android.zjctools.utils;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.android.zjctools.interface_function.ZCallback;
import com.android.zjctools.interface_function.ZFunctionManager;
import com.android.zjctools.interface_function.ZFunctionOnlyParam;
import com.android.zjcutils.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZFile {


    /**
     * 借用牛逼哄哄的 lzan13 代码
     */

    /**
     * 判断sdcard是否被挂载
     */
    public static boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断目录是否存在
     *
     * @param path 目录路径
     */
    public static boolean isDirExists(String path) {
        if (ZStr.isEmpty(path)) {
            return false;
        }
        File dir = new File(path);
        return dir.exists();
    }

    /**
     * 判断文件是否存在
     *
     * @param path 文件路径
     */
    public static boolean isFileExists(String path) {
        if (ZStr.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        return file.exists();
    }

    /**
     * 创建目录，多层目录会递归创建
     */
    public static boolean createDirectory(String path) {
        if (ZStr.isEmpty(path)) {
            return false;
        }
        File dir = new File(path);
        if (!isDirExists(path)) {
            return dir.mkdirs();
        }
        return true;
    }

    /**
     * 创建新文件
     */
    public static File createFile(String filepath) {
        boolean isSuccess;
        if (ZStr.isEmpty(filepath)) {
            return null;
        }
        File file = new File(filepath);
        // 判断文件上层目录是否存在，不存在则首先创建目录
        if (!isDirExists(file.getParent())) {
            createDirectory(file.getParent());
        }
        if (!file.isFile()) {
            try {
                isSuccess = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            isSuccess = true;
        }
        if (isSuccess) {
            return file;
        }
        return null;
    }

    /**
     * 创建新文件，外部传入前缀和后缀
     */
    public static File createFile(String path, String prefix, String suffix) {
        if (!createDirectory(path)) {
            return null;
        }
        String filename = prefix + ZDate.filenameDateTime() + suffix;
        return createFile(path + filename);
    }

    /**
     * 压缩文件
     *
     * @param srcPath  源文件路径
     * @param destPath 压缩问及那路径
     * @return 压缩结果
     */
    public static boolean zipFile(String srcPath, String destPath) {
        if (ZStr.isEmpty(srcPath) || ZStr.isEmpty(destPath)) {
            return false;
        }
        try {
            File srcFile = new File(srcPath);
            File zipFile = new File(destPath);
            // 输入流读取数据
            FileInputStream input = new FileInputStream(srcFile);
            // 输出流输出数据，输出Zip
            ZipOutputStream output = new ZipOutputStream(new FileOutputStream(zipFile));
            // 用于缓存数据
            int temp;
            // 用于生成说明，会位于打开Zip后，右边的区域
            output.setComment(srcFile.getName());
            // 从输入流中获取的数据不能直接写入 Zip 文件，而是需要在 Zip 文件中新建一个 ZipEntry，然后将数据写入新建的文件
            output.putNextEntry(new ZipEntry(srcFile.getName()));
            while ((temp = input.read()) != -1) {
                output.write(temp);
            }
            // 关闭流
            input.close();
            output.close();

            return true;
        } catch (IOException e) {
            ZLog.e("压缩文件失败 %s", e.getMessage());
        }
        return false;
    }

    /**
     * 复制文件
     *
     * @param srcPath  源文件地址
     * @param destPath 目标文件地址
     * @return 返回复制结果
     */
    public static File copyFile(String srcPath, String destPath) {
        if (ZStr.isEmpty(srcPath)) {
            ZLog.e("源文件不存在，无法完成复制");
            return null;
        }
        File srcFile = new File(srcPath);
        if (!srcFile.exists()) {
            ZLog.e("源文件不存在，无法完成复制");
            return null;
        }
        if (ZStr.isEmpty(destPath)) {
            ZLog.e("目标路径不能为 null");
            return null;
        }
        File destFile = new File(destPath);
        ZLog.i(destFile.getParent());
        if (!isDirExists(destFile.getParent())) {
            createDirectory(destFile.getParent());
        }
        try {
            InputStream inputStream = new FileInputStream(srcFile);
            FileOutputStream outputStream = new FileOutputStream(destPath);
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buff)) != -1) {
                outputStream.write(buff, 0, len);
            }
            inputStream.close();
            outputStream.close();
            return destFile;
        } catch (FileNotFoundException e) {
            ZLog.e("拷贝文件出错：" + e);
        } catch (IOException e) {
            ZLog.e("拷贝文件出错：" + e);
        }
        return null;
    }


    /**
     * 从网络下载图片
     * @param pdfUrl  文件的网络地址
     * @param savePath 要保存到文件夹,会在根目录创建改文件夹
     * @param fileType  文件后缀，如 img,pdf
     */
    public static  void downloadFileByNetWork(final  String pdfUrl, String savePath, String fileType, ZCallback <File>zCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL(pdfUrl);
                    HttpURLConnection urlConn = (HttpURLConnection) url
                            .openConnection();
                    BufferedInputStream bis = new BufferedInputStream(urlConn
                            .getInputStream());
                    if(TextUtils.isEmpty(fileType)){//如 .jpg  .pdf
                        zCallback.onError(-1,"未设置文件类型");
                        return;
                    }
                    //创建文件目录
                    String pathDirectory;
                    if(!TextUtils.isEmpty(savePath)){
                        pathDirectory=getSDCard() + savePath;
                    }else{
                        pathDirectory=getSDCard() + ZStr.byRes(R.string.tool_name);
                    }
                    createDirectory(pathDirectory);
                    //设置文件存放路径
                    String path=pathDirectory+"/" + System.currentTimeMillis() + fileType;
                    FileOutputStream fos = new FileOutputStream(path);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    byte[] buf = new byte[3 * 1024];
                    int len =0;
                    long total = urlConn.getContentLength();
                    long sum = 0;
                    while ((len = bis.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                       int  progress = (int) (sum * 1.0f / total * 100);
                        zCallback.onProgress(progress,"下载进度");
                    }
                    bos.flush();
                    bis.close();
                    fos.close();
                    bos.close();
                    zCallback.onSuccess(new File(path));
                } catch (Exception e) {
                    e.printStackTrace();
                    zCallback.onError(-1,e.getMessage());
                }
            }
        }).start();

    }


    /**
     * 读取文件到 Bitmap
     */
    public static Bitmap fileToBitmap(String filepath) {
        if (ZStr.isEmpty(filepath)) {
            return null;
        }
        File file = new File(filepath);
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(filepath);
            return bitmap;
        }
        return null;
    }

    /**
     * 读取文件到drawable
     *
     * @param filepath 文件路径
     * @return 返回Drawable资源
     */
    public static Drawable fileToDrawable(String filepath) {
        if (ZStr.isEmpty(filepath)) {
            return null;
        }
        File file = new File(filepath);
        if (file.exists()) {
            Drawable drawable = Drawable.createFromPath(filepath);
            return drawable;
        }
        return null;
    }

    /**
     * 格式化文件字节大小
     */
    public static String formatSize(long size) {
        BigDecimal result;
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }
        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            result = new BigDecimal(Double.toString(kiloByte));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            result = new BigDecimal(Double.toString(megaByte));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }
        double teraByte = gigaByte / 1024;
        if (teraByte < 1) {
            result = new BigDecimal(Double.toString(gigaByte));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        result = new BigDecimal(Double.toString(teraByte));
        return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    /**
     * 递归实现遍历文件夹大小
     *
     * @param fileDir 要计算的文件夹
     */
    public static long getFolderSize(File fileDir) {
        long size = 0;
        if (!fileDir.exists()) {
            return size;
        }
        File[] fileList = fileDir.listFiles();
        for (File file : fileList) {
            if (file.isDirectory()) {
                size += getFolderSize(file);
            } else {
                size += file.length();
            }
        }
        return size;
    }

    /**
     * 删除文件
     */
    public static boolean deleteFile(String filepath) {
        if (ZStr.isEmpty(filepath)) {
            return false;
        }
        File file = new File(filepath);
        if (file.exists() && file.isFile()) {
            return file.delete();
        } else {
            return false;
        }
    }

    /**
     * 删除文件集合
     *
     * @param paths 文件路径集合
     */
    public static void deleteFiles(List<String> paths) {
        for (String path : paths) {
            deleteFile(path);
        }
    }

    /**
     * 递归删除文件夹内的文件
     *
     * @param path       需要操作的路径
     * @param deleteThis 删除自己
     */
    public static void deleteFolder(String path, boolean deleteThis) {
        if (ZStr.isEmpty(path)) {
            return;
        }
        File fileSrc = new File(path);
        // 文件/目录存在（包括文件及文件夹）
        if (fileSrc.exists()) {
            if (fileSrc.isFile()) {
                fileSrc.delete();
            } else if (fileSrc.isDirectory()) {
                //接收文件夹目录下所有的文件实例
                File[] listFiles = fileSrc.listFiles();
                //文件夹为空 递归出口
                if (listFiles == null) {
                    return;
                }
                for (File file : listFiles) {
                    deleteFolder(file.getAbsolutePath(), true);
                }
                if (deleteThis) {
                    // 递归跳出来的时候删除空文件夹
                    fileSrc.delete();
                }
            }
        }
    }

    /**
     * 根据文件路径解析文件名，不包含扩展类型
     */
    public static String parseFilename(String path) {
        String result = null;
        if (path != null && path.length() > 0) {
            int index = path.lastIndexOf("/");
            String fileName = path.substring(index + 1);
            result = fileName.substring(0, fileName.lastIndexOf("."));
        }
        return result;
    }

    /**
     * 获取文件扩展名，
     *
     * @param path 可以是路径，可以是文件名
     */
    public static String parseSuffix(String path) {
        String result = null;
        if (path != null && path.length() > 0) {
            int index = path.lastIndexOf("/");
            String filename;
            if (index == -1) {
                filename = path;
            } else {
                filename = path.substring(index + 1);
            }
            result = filename.substring(filename.lastIndexOf("."));
        }
        return result;
    }

    /**
     * 获取Android系统的一些默认路径
     * 不常用：
     * Environment.getDataDirectory().getPath()             : /data
     * Environment.getDownloadCacheDirectory().getPath()    : /cache
     * Environment.getRootDirectory().getPath()             : /system
     *
     * 常用：
     * Environment.getExternalStorageDirectory().getPath()  : /mnt/sdcard (storage/emulated/0)
     * Context.getCacheDir().getPath()                      : /data/data/packagename/cache
     * Context.getExternalCacheDir().getPath()              : /mnt/sdcard/Android/data/packagename/cache
     * Context.getFilesDir().getPath()                      : /data/data/packagename/files
     * Context.getObbDir().getPath()                        : /mnt/sdcard/Android/obb/packagename
     * Context.getPackageName()                             : packagename
     * Context.getPackageCodePath()                         : /data/app/packagename-1.apk
     * Context.getPackageResourcePath()                     : /data/app/packagename-1.apk
     */
    /**
     * Root 目录，一般不常用
     *
     * String rootCache = Environment.getDownloadCacheDirectory().getPath();
     * String rootData = Environment.getDataDirectory().getPath();
     * String rootSystem = Environment.getRootDirectory().getPath();
     *
     * SDCard 目录
     * Environment.getExternalStorageDirectory().getPath();
     * 当前 app 在 root 下的缓存目录
     * zjcTools.getContext().getCacheDir().getPath();
     * 当前 app 在 SDCard 下的缓存目录
     * zjcTools.getContext().getExternalCacheDir().getPath();
     * 当前 app 在 root 下的 files 目录
     * zjcTools.getContext().getFilesDir().getPath();
     * zjcTools.getContext().getFilesDir().getPath();
     * 当前 app 在 SDCard 下的 obb 目录，一般是apk包过大要分出资源包，游戏用的比较多
     * zjcTools.getContext().getObbDir().getPath();
     * 获取当前 app 包名
     * zjcTools.getContext().getPackageName();
     * 获取当前 app 代码路径
     * zjcTools.getContext().getPackageCodePath();
     * 获取当前 app 资源路径
     * zjcTools.getContext().getPackageResourcePath();
     *
     * 获取常用目录的方法，参数是需要获取的目录类型，可以是download，camera
     * Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
     * return null;
     */

    /**
     * 获取 /sdcard (/storage/emulated/0) 目录
     *
     * @return 返回得到的路径
     */
    public static String getSDCard() {
        return Environment.getExternalStorageDirectory().getPath() + "/";
    }

    /**
     * 获取 /data/data/packagename/cache 目录
     *
     * @return 返回得到的路径
     */
    public static String getCacheFromData() {
        return ZTools.getContext().getCacheDir().getPath() + "/";
    }

    /**
     * 获取 /sdcard/Android/data/packagename/cache 目录
     *
     * @return 返回得到的路径
     */
    public static String getCacheFromSDCard() {
        return ZTools.getContext().getExternalCacheDir().getPath() + "/";
    }

    /**
     * 获取/data/data/packagename/files 目录
     *
     * @return 返回得到的路径
     */
    public static String getFilesFromData() {
        return ZTools.getContext().getFilesDir().getPath() + "/";
    }

    /**
     * 获取 /sdcard/Android/data/packagename/files 目录
     *
     * @return 返回得到的路径
     */
    public static String getFilesFromSDCard() {
        return ZTools.getContext().getExternalFilesDir("").getAbsolutePath() + "/";
    }

    /**
     * 获取 /sdcard/Android/obb/packagename 目录
     *
     * @return 返回得到的路径
     */
    public static String getOBB() {
        return ZTools.getContext().getObbDir().getAbsolutePath() + "/";
    }

    /**
     * 获取设备默认的相册目录
     *
     * @return 返回得到的路径
     */
    public static String getDCIM() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/";
    }

    /**
     * 获取设备默认的下载目录
     *
     * @return 返回得到的路径
     */
    public static String getDownload() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/";
    }

    /**
     * 获取设备默认的音乐目录
     *
     * @return 返回得到的路径
     */
    public static String getMusic() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/";
    }

    /**
     * 获取设备默认的电影目录
     *
     * @return 返回得到的路径
     */
    public static String getMovies() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath() + "/";
    }

    /**
     * 获取设备默认的图片目录
     */
    public static String getPictures() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/";
    }

    /**
     * 获取 packagename 目录
     *
     * @return 返回得到的路径
     */
    public static String getPackageName() {
        return ZTools.getContext().getPackageName();
    }

    /**
     * 获取 /data/app/packagename-1.apk 目录
     *
     * @return 返回得到的路径
     */
    public static String getPackageCode() {
        return ZTools.getContext().getPackageCodePath();
    }

    /**
     * 获取 /data/app/packagename-1.apk 目录
     *
     * @return 返回得到的路径
     */
    public static String getPackageResource() {
        return ZTools.getContext().getPackageResourcePath();
    }

    /**
     * 根据 Uri 获取文件的真实路径，这个是网上的方法，用的还是比较多的，可以参考，
     * 不过在选择google相册的图片的时候，如果本地不存在图片会出现问题
     *
     * @param uri 包含文件信息的 Uri
     * @return 返回文件真实路径
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Uri uri) {

        // 判断当前系统 API 4.4（19）及以上
        boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(ZTools.getContext(), uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                // DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(ZTools.getContext(), contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                // MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(ZTools.getContext(), contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // MediaStore (and general)
            // Return the remote address
            // 这里先判断是否是通过 Google 相册 选择的图片，同时这个图片不存在于本地
            if (isGooglePhotosUri(uri)) {
                //                return null;
                return uri.getLastPathSegment();
            }
            return getDataColumn(ZTools.getContext(), uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // File
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * 这里我修改了下，我在最新的5.1上选择的一个在 Google相册里的一张图片时，这个 uri.getAuthority() 的值有所改变
     * com.google.android.apps.photos.contentprovider，之前的结尾是content，我测试的为contentprovider
     *
     * @param uri 需要判断的 Uri
     * @return 判断这个 Uri 是否是通过 Google 相册 选择的
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
