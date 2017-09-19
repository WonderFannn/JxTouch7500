package com.jinxin.jxtouchscreen.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.adapter.base.QuickAdapter;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.cmd.CmdBuilder;
import com.jinxin.jxtouchscreen.cmd.CmdType;
import com.jinxin.jxtouchscreen.cmd.OnlineCmdSenderLong;
import com.jinxin.jxtouchscreen.cmd.RemoteJsonResultInfo;
import com.jinxin.jxtouchscreen.cmd.Task;
import com.jinxin.jxtouchscreen.cmd.TaskListener;
import com.jinxin.jxtouchscreen.constant.NetConstant;
import com.jinxin.jxtouchscreen.constant.ProductConstants;
import com.jinxin.jxtouchscreen.constant.StaticConstant;
import com.jinxin.jxtouchscreen.model.FunDetail;
import com.jinxin.jxtouchscreen.model.ProductFun;
import com.jinxin.jxtouchscreen.util.ToastUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/26.
 */
public class ThreeSwitchGridViewAdapter extends QuickAdapter<Boolean> {

	/**
	 * 命令监听器
	 */
	private TaskListener<Task> listener;
	private Map<String, Object> params = null;
	public static String[] cmds = StaticConstant.TYPE_DST_MAP;
	private ProductFun productFun;
	private FunDetail funDetail;
	private Context mContext;
	private ImageView imageView;
	private TextView textView;
	private List<Boolean> list;
	private int drawableId;
	public static final String[] THREE_SWITCHES_NAMES = BaseApp.getContext().getResources()
			.getStringArray(R.array.three_switches);

	public ThreeSwitchGridViewAdapter(Context context, int res, List<Boolean> data, ProductFun productFun, FunDetail funDetail) {
		super(context, res, data);
		list = data;
		mContext = context;
		this.productFun = productFun;
		this.funDetail = funDetail;
		init(productFun);
	}

	private void init(ProductFun productFun) {
		if (productFun.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_SIX)) {
			drawableId = R.drawable.selector_three_switch_six;
		} else if (productFun.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_FIVE)) {
			drawableId = R.drawable.selector_three_switch_five;
		} else if (productFun.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_FOUR)) {
			drawableId = R.drawable.selector_three_switch_four;
		} else if (productFun.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_THREE)) {
			drawableId = R.drawable.selector_three_switch_three;
		}
		listener = new TaskListener<Task>() {


			@Override
			public void onFail(Task task, Object[] arg) {
				if (arg != null && arg.length > 0) {
					RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
					ToastUtil.showShort(BaseApp.getContext(), resultObj.validResultInfo);
				} else {
					ToastUtil.showShort(BaseApp.getContext(), R.string.operate_failed);
				}
			}

			@Override
			public void onSuccess(Task task, Object[] arg) {
//				RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
//				if ("-1".equals(resultObj.validResultInfo)) {
//					ToastUtil.showShort(BaseApp.getContext(), R.string.operate_failed);
//					return;
//				}
				ToastUtil.showShort(BaseApp.getContext(), R.string.operate_success);
				updateSwitchStatus();
			}

		};
	}

	protected void updateSwitchStatus() {
		for (int i = 0; i < cmds.length; i++) {
			if (params.get("dst").equals(cmds[i]))
				data.set(i, !data.get(i));
		}
		notifyDataSetChanged();
	}

	@Override
	public View getItemView(final int position, View convertView, ViewHolder holder) {
		params = new HashMap<String, Object>();
		imageView = holder.getView(R.id.three_switch_gv_imag);
		imageView.setBackgroundResource(drawableId);
		textView = holder.getView(R.id.three_switch_gv_iv);
		textView.setText(THREE_SWITCHES_NAMES[position]);
		final Boolean aBoolean = data.get(position);
		imageView.setSelected(aBoolean);
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switchControl(productFun, cmds[position], aBoolean);
			}
		});

		return convertView;
	}

	/**
	 * 开关控制
	 *
	 * @param productFun 产品
	 * @param cmdStr     命令
	 * @param open       状态
	 */
	protected void switchControl(ProductFun productFun, String cmdStr, boolean open) {
		params.put("src", "0x01");
		params.put("dst", cmdStr);

		String type = open ? "off" : "on";

		List<byte[]> cmdList = CmdBuilder.build().generateCmd(productFun, funDetail, params, type);
		OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(BaseApp.getContext(),
				NetConstant.hostForZigbee(), CmdType.CMD_ZG, cmdList, true, 0);
		onlineSender.addListener(listener);
		onlineSender.send();
	}

}
