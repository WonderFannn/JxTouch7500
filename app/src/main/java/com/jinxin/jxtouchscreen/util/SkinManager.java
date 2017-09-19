//package com.jinxin.jxtouchscreen.util;
//
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.content.res.AssetManager;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.drawable.Drawable;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//
//import com.jinxin.jxtouchscreen.model.SkinItemVO;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//
///**
// *  皮肤管理类
// * Created by yh on 2017/3/28.
// */
//
//public class SkinManager {
//
//    private static SkinManager instance = null;
//    private Context mContext = null;//主程序context
//    private Context skinContext = null;// 皮肤包
//    private String currentSkinID = null;// 当前皮肤ID
//    private ArrayList<SkinItemVO> skinList = null;
//    public SkinManager (Context context){
//        this.mContext = context;
//        this.createPackageContext(context.getPackageName());
//    }
//    /**
//     * 初始化
//     * @param context
//     * @return
//     */
//    public static SkinManager instance(Context context) {
//        if (instance == null) {
//            instance = new SkinManager(context);
//        }
//        return instance;
//    }
//    /**
//     * 获取皮肤资源库
//     *
//     * @param packageName 皮肤包名
//     */
//    public void createPackageContext(String packagename) {
//        try {
//            setSkinContext(mContext.createPackageContext(packagename, Context.CONTEXT_INCLUDE_CODE|Context.CONTEXT_IGNORE_SECURITY));
//        } catch (PackageManager.NameNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            L.e("皮肤管理器：", "皮肤包异常或不存在，将使用原皮肤！");
//            setSkinContext(mContext);//context使用主程序的
//        }
//    }
//
//    /**
//     * 获取当前设定的皮肤(若未设置当前皮肤ID返回为空)
//     *
//     * @return
//     */
//    public SkinItemVO getCurrentItem() {
//        return this.getSkinIDItem(getCurrentSkinID());
//    }
//
//    /**
//     * 获得指定ID的皮肤（未取到为null）
//     *
//     * @param index
//     * @return
//     */
//    public SkinItemVO getSkinIDItem(String id) {
//        if (this.skinList != null && id != null && id.length() > 0) {
//            for (int i = 0; i < this.skinList.size(); i++) {
//                SkinItemVO _itemVO = this.skinList.get(i);
//                if (_itemVO != null) {
//                    if (id.trim().equals(_itemVO.getID())) {
//                        return _itemVO;
//                    }
//                }
//            }
//        }
//        return null;
//    }
//
//    /**
//     * 获得指定索引的皮肤（未取到为null）
//     *
//     * @param index
//     * @return
//     */
//    public SkinItemVO getIndexItem(int index) {
//        if (this.skinList != null && index >= 0 && this.skinList.size() > index) {
//            return this.skinList.get(index);
//        }
//        return null;
//    }
//
//    /**
//     * 添加皮肤
//     *
//     * @param skinItemVO
//     */
//    public void addSkin(SkinItemVO skinItemVO) {
//        if (this.skinList == null) {
//            this.skinList = new ArrayList<SkinItemVO>();
//        }
//        if (skinItemVO == null)
//            return;
//        this.skinList.add(skinItemVO);
//    }
//
//    /**
//     * 设置皮肤列表
//     *
//     * @param skinList
//     */
//    public void setSkinList(ArrayList<SkinItemVO> skinList) {
//        this.skinList = skinList;
//    }
//
//    /**
//     * 清空皮肤列表
//     */
//    public void clearSkinList() {
//        if (this.skinList != null) {
//            this.skinList.clear();
//        }
//    }
//
//
//
//    /**
//     * 获得当前选中皮肤ID（未取到为null）
//     *
//     * @return
//     */
//    public String getCurrentSkinID() {
//        return currentSkinID;
//    }
//
//    /**
//     * 记录当前选中皮肤ID
//     *
//     * @param currentSkinID
//     */
//    public void setCurrentSkinID(String currentSkinID) {
//        this.currentSkinID = currentSkinID;
//    }
//
//    /**
//     * 获得当前皮肤资源库（未取到为null）
//     *
//     * @return
//     */
//    public Context getSkinContext() {
//        return skinContext;
//    }
//
//    /**
//     * 设置当前皮肤资源库
//     *
//     * @param skinContext
//     */
//    private void setSkinContext(Context skinContext) {
//        this.skinContext = skinContext;
//    }
//
//    /**
//     * 取皮肤包asset（未取到为null）
//     *
//     * @return eg:
//     */
//    public AssetManager getAssets() {
//        if (this.skinContext == null)
//            return null;
//        return this.skinContext.getAssets();
//    }
//    /**
//     * 取皮肤包asset下images包图片（未取到为null）
//     *
//     * @return eg:
//     */
//    public Bitmap getAssetsBitmap(String path) {
//        if (this.skinContext == null)
//            return null;
//        InputStream is = null;
//        Bitmap bitmap = null;
//        try {
//            is = this.getAssets().open(path);
//            bitmap = BitmapFactory.decodeStream(is);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        } finally{
//            FileUtil.closeInputStream(is);
//        }
//        return bitmap;
//    }
//    /**
//     * 取皮肤包String（未取到为null）
//     *
//     * @param id
//     * @return eg:((TextView) findViewById(R.id.testb_string)).setText(return);
//     */
//    public String string(int id) {
//        if (this.skinContext == null)
//            return null;
//        return this.skinContext.getResources().getString(id);
//    }
//
//    /**
//     * 取皮肤包Drawable（未取到为null）
//     *
//     * @param id
//     * @return eg:((ImageView) findViewById(R.id.testb_drawable)).setImageDrawable(return);
//     */
//    public Drawable drawable(int id) {
//        if (this.skinContext == null)
//            return null;
//        return this.skinContext.getResources().getDrawable(id);
//    }
//
//    /**
//     * 取皮肤包Color（未取到为-1）
//     *
//     * @param id
//     * @return eg:((TextView) findViewById(R.id.testb_color)).setBackgroundColor(return);
//     */
//    public int color(int id) {
//        if (this.skinContext == null)
//            return -1;
//        return this.skinContext.getResources().getColor(id);
//    }
//    /**
//     * 取皮肤包dimens（未取到为-1）
//     *
//     * @param id
//     * @return
//     * eg:((TextView) findViewById(R.id.testb_color)).setTextSize(return);
//     */
//    public float dimens(int id) {
//        if (this.skinContext == null)
//            return -1;
//        return this.skinContext.getResources().getDimension(id);
//    }
//
//    /**
//     * 取皮肤包布局文件（未取到为null）
//     *
//     * @param id
//     * @return
//     * eg:((LinearLayout) findViewById(R.id.testb_layout)).addView(return);
//     */
//    public View view(int id) {
//        if (this.skinContext == null)
//            return null;
//        return ((LayoutInflater) this.skinContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(id, null);
//    }
//    /**
//     * 取皮肤包布局文件（未取到为null）
//     *
//     * @param id
//     * @param view
//     * @return
//     * eg:((LinearLayout) findViewById(R.id.testb_layout)).addView(return);
//     */
//    public View view(int id, ViewGroup view) {
//        if (this.skinContext == null)
//            return null;
//        return ((LayoutInflater) this.skinContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
//                .inflate(id, view,false);
//    }
//
//    /**
//     * 取皮肤包动画文件（未取到为null）
//     *
//     * @param id
//     * @return
//     * eg:((LinearLayout) findViewById(R.id.testb_layout)).startAnimation(return);
//     */
//    public Animation animation(int id) {
//        if (this.skinContext == null)
//            return null;
//        return AnimationUtils.loadAnimation(skinContext, id);
//    }
//    /**
//     * 取StringArray
//     * @param id
//     * @return
//     */
//    public String[] stringArray(int id){
//        if (this.skinContext == null)
//            return null;
//        return this.skinContext.getResources().getStringArray(id);
//    }
//
//}
