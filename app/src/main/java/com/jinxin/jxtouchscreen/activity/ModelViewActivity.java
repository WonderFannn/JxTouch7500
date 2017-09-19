package com.jinxin.jxtouchscreen.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.adapter.ModelViewAdapter;
import com.jinxin.jxtouchscreen.base.BaseActivity;
import com.jinxin.jxtouchscreen.db.DBM;
import com.jinxin.jxtouchscreen.model.CustomerProduct;
import com.jinxin.jxtouchscreen.model.FunDetail;
import com.jinxin.jxtouchscreen.model.ProductFun;
import com.jinxin.jxtouchscreen.model.ProductFunVO;
import com.jinxin.jxtouchscreen.model.ProductPatternOperation;
import com.jinxin.jxtouchscreen.util.StringUtils;
import com.jinxin.jxtouchscreen.widget.SyncHorizontalScrollView;
import com.jinxin.jxtouchscreen.widget.VoiceService;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yh on 2017/4/11.
 */

public class ModelViewActivity  extends Activity implements View.OnClickListener{

    private List<List<ProductFunVO>> productFunVOLists;
    private List<FunDetail> funDetailList = null;
    private List<List<ProductFunVO>> productLists = null;
    private List<FunDetail> funList = null;
    private int patternId = -1;
    private RelativeLayout rl_tab;
    private LinearLayout rl_nav;
    private SyncHorizontalScrollView mHsv;
    private ImageView iv_nav_left;
    private ImageView iv_nav_right;
    private ImageView back_btn;
    private TextView title_name;
    private TextView fundetail_name;
    private ListView mList;
    private Context mContext;
    private int mWidth = 0;
    private  int mColumnSelectIndex = 0;
    private ModelViewAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        //停止悬浮窗服务
        stopService(new Intent(getApplicationContext(), VoiceService.class));
        initData();
        initView();
    }

    private void initView() {
        setContentView(R.layout.model_view_layout);
        rl_tab = (RelativeLayout) findViewById(R.id.rl_tab);
        rl_nav = (LinearLayout) findViewById(R.id.rl_nav);
        mHsv = (SyncHorizontalScrollView) findViewById(R.id.mHsv);
        iv_nav_left = (ImageView) findViewById(R.id.iv_nav_left);
        iv_nav_right = (ImageView) findViewById(R.id.iv_nav_right);
        back_btn = (ImageView) findViewById(R.id.back_btn);
        title_name = (TextView) findViewById(R.id.title_name);
        fundetail_name = (TextView) findViewById(R.id.fundetail_name);
        mList = (ListView) findViewById(R.id.fundetail_lv);
        back_btn.setOnClickListener(this);
        iv_nav_left.setOnClickListener(this);
        iv_nav_right.setOnClickListener(this);
        //根据数据动态的填充数据
        setView();
    }

    private void setView() {
        title_name.setText(this.getIntent().getStringExtra("title_name") + mContext.getString(R.string.color_light_5));
        mWidth = mHsv.getMeasuredWidth();
        mHsv.setSomeParam(rl_nav, iv_nav_left, iv_nav_right, this);
        float count = 0f;
        float size = 0f;
        for (int i = 0; i < funList.size(); i++) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.char_view_layout, null);
            TextView textView = (TextView) v.findViewById(R.id.text);
            textView.setText(funList.get(i).getFunName());
            TextPaint textPaint = textView.getPaint();
            float boundsWidth = textPaint.measureText(textView.getText().toString());
            size = boundsWidth + StringUtils.dip2px(mContext, 20);
            count += size;

        }
        int diswith = 0;
        if(count<mWidth){
            float disparity = mWidth - count;
            diswith = (int) (disparity/funList.size());
        }
        for (int i = 0; i < funList.size(); i++) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.char_view_layout,null);
            TextView textView = (TextView) v.findViewById(R.id.text);
            textView.setText(funList.get(i).getFunName());
            TextPaint textPaint = textView.getPaint();
            float boundsWidth = textPaint.measureText(textView.getText().toString());
            size = boundsWidth + StringUtils.dip2px(mContext, 20);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) (size + diswith), LinearLayout.LayoutParams.MATCH_PARENT);
            rl_nav.addView(v, i, params);
            if(mColumnSelectIndex == i){
                titleClick(mColumnSelectIndex);
            }

            final int mSelectIndex = i;
            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    titleClick(mSelectIndex);
                }
            });
        }
    }



    private void initData() {
        List<ProductPatternOperation> ppoList = null;
        // 取当前模式数据
        this.patternId = this.getIntent().getIntExtra("patternId", -1);
        if (this.patternId != -1) {
            ppoList =  DBM.getCurrentOrm().query(new QueryBuilder<>(ProductPatternOperation.class).where("patternId = ?", new String[]{Integer.toString(this.patternId)}));
        }
        this.funDetailList =  DBM.getCurrentOrm().query(new QueryBuilder<>(FunDetail.class).where("joinPattern = ?", new String[]{Integer.toString(1)}));

        // 填充每种类型列表数据
        this.productFunVOLists = new ArrayList<List<ProductFunVO>>();
        List<ProductFun> _pfList = new ArrayList<ProductFun>();
        for (FunDetail fd : this.funDetailList) {
            _pfList.clear();
            if (fd != null) {
                List<ProductFun> tempList = DBM.getCurrentOrm().query(new QueryBuilder<>(ProductFun.class).where("funType=? and enable=?", new String[]{fd.getFunType(),Integer.toString(1)}));
                for (ProductFun productFun : tempList) {
                    for (ProductPatternOperation _ppo : ppoList) {
                        if (productFun.getFunId() == _ppo.getFunId()) {
                            _pfList.add(productFun);
                        }
                    }
                }
                this.productFunVOLists.add(creatProductFunVOList(_pfList, ppoList, fd));
            }
        }

        this.productLists = new ArrayList<List<ProductFunVO>>();
        this.funList = new ArrayList<FunDetail>();

        if (funDetailList != null && productFunVOLists != null) {
            for (int i = 0; i < funDetailList.size(); i++) {
                List<ProductFunVO> tempList = productFunVOLists.get(i);
                if (tempList.size() > 0) {
                    this.funList.add(funDetailList.get(i));
                    this.productLists.add(tempList);
                }
            }
        }
        if(funList.size() == 0){
            setResult(RESULT_CANCELED);
            this.finish();
        }

    }

    /**
     * 构建设备显示列表
     *
     * @param pfList
     *            设备功能列表
     * @param ppoList
     *            模式控制列表
     * @return
     */
    private List<ProductFunVO> creatProductFunVOList(List<ProductFun> pfList,
                                                     List<ProductPatternOperation> ppoList, FunDetail fd) {
        List<ProductFunVO> _list = new ArrayList<ProductFunVO>();
        if (pfList == null)
            return _list;
        for (ProductFun pf : pfList) {
            if (pf != null) {
                ProductFunVO _pfVO = new ProductFunVO(pf);
                _list.add(_pfVO);
                if (ppoList != null) {
                    ProductPatternOperation _ppo = this
                            .isExistProductPatternOperation(ppoList,
                                    pf.getFunId());
                    if (_ppo != null) {
                        _pfVO.setProductPatternOperation(_ppo);
                        _pfVO.setSelected(true);
                        if (_ppo.getOperation().equals("open") || _ppo.getOperation().
                                equals("set") || _ppo.getOperation().equals("send")
                                || _ppo.getOperation().equals("play")
                                ||_ppo.getOperation().equals("hueandsat")
                                ||_ppo.getOperation().equals("on")
                                ||_ppo.getOperation().equals("up")
                                ||_ppo.getOperation().equals("double-on-off")
                                ||_ppo.getOperation().equals("five-on-off")
                                ||_ppo.getOperation().equals("six-on-off")
                                ||_ppo.getOperation().equals("three-on-off")
                                ||_ppo.getOperation().equals("autoMode")
                                ||_ppo.getOperation().equals("automode")) {
                            _pfVO.setOpen(true);
                        }else{
                            _pfVO.setOpen(false);
                        }

                    }
                }
            }
        }
        return _list;
    }

    /**
     * 判断当前funId的设备功能在该模式下是否已存在
     *
     * @param ppoList
     * @param funId
     * @return
     */
    private ProductPatternOperation isExistProductPatternOperation(
            List<ProductPatternOperation> ppoList, int funId) {
        if (ppoList == null || funId == -1)
            return null;
        for (ProductPatternOperation ppo : ppoList) {
            if (ppo != null) {
                if (ppo.getFunId() == funId) {
                    return ppo;
                }
            }
        }
        return null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_btn:
                this.setResult(RESULT_OK);
                finish();
                break;
            case R.id.iv_nav_left:
                mHsv.arrowScroll(View.FOCUS_LEFT);
                break;
            case R.id.iv_nav_right:
                mHsv.arrowScroll(View.FOCUS_RIGHT);
                break;
            default:
                break;
        }
    }

    private void titleClick(int index) {
        fundetail_name.setText(funList.get(index).getFunName());
        mColumnSelectIndex = index;
        // 除了当前relativeLayout，其他relativeLayout设置为不选中
        for (int i = 0; i < rl_nav.getChildCount(); i++) {
            View view = rl_nav.getChildAt(i);
            if (i != index) {
                view.setSelected(false);
            } else {
                view.setSelected(true);
            }
        }
        mAdapter = new ModelViewAdapter(mContext,mColumnSelectIndex,productLists);
        mList.setAdapter(mAdapter);
    }
}


