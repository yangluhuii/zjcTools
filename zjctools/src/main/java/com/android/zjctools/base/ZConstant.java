package com.android.zjctools.base;

public class ZConstant {

    /**
     * 借用牛逼哄哄的 lzan13 代码
     */

    /**
     * 权限传参的 key
     */
    public static final String ZJC_KEY_PERMISSION_ENABLE_DIALOG = "zjc_key_permission_enable_dialog";
    public static final String ZJC_KEY_PERMISSION_TITLE = "zjc_key_permission_title";
    public static final String ZJC_KEY_PERMISSION_MSG = "zjc_key_permission_msg";
    public static final String ZJC_KEY_PERMISSION_LIST = "zjc_key_permission_list";

    /**
     * 图片选择器相关静态变量
     */
    // 选择器请求入口
    public static final int ZJC_PICK_REQUEST_CODE = 10000;
    // 请求拍照
    public static final int ZJC_PICK_REQUEST_CODE_TAKE = 10001;
    // 剪切照片
    public static final int ZJC_PICK_REQUEST_CODE_CROP = 10002;
    // 预览图片
    public static final int ZJC_PICK_REQUEST_CODE_PREVIEW = 10003;
    // 选择图片返回
    public static final int ZJC_PICK_RESULT_CODE_BACK = 20000;
    // 选择完成返回起始页
    public static final int ZJC_PICK_RESULT_CODE_PICTURES = 20001;
    // 是否选中原图
    public static final String ZJC_KEY_PICK_IS_ORIGIN = "zjc_key_pick_is_origin";
    // 选择器传参
    public static final String ZJC_KEY_PICK_PICTURES = "zjc_key_pick_pictures";
    //public static final String ZJC_KEY_PICK_TAKE_PICTURE = "ZJC_key_pick_take_picture";
    // 选择结果
    public static final String KEY_PICK_RESULT_PICTURES = "key_pick_result_pictures";
    // 当前选择位置
    public static final String KEY_PICK_CURRENT_SELECTED_POSITION = "key_pick_current_selected_position";
    // 是否预览全部
    public static final String KEY_PICK_PREVIEW_ALL = "key_pick_preview_all";

}