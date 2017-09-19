package com.jinxin.jxtouchscreen.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.activity.ModelViewActivity;
import com.jinxin.jxtouchscreen.adapter.ScenceGridViewAdapter;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.base.BaseFragment;
import com.jinxin.jxtouchscreen.cmd.CmdBuilder;
import com.jinxin.jxtouchscreen.cmd.Command;
import com.jinxin.jxtouchscreen.cmd.OnlineCmdSenderLong;
import com.jinxin.jxtouchscreen.cmd.OnlineMulitGatewayCmdSender;
import com.jinxin.jxtouchscreen.cmd.RemoteJsonResultInfo;
import com.jinxin.jxtouchscreen.cmd.Task;
import com.jinxin.jxtouchscreen.cmd.TaskListener;
import com.jinxin.jxtouchscreen.constant.NetConstant;
import com.jinxin.jxtouchscreen.db.DBM;
import com.jinxin.jxtouchscreen.event.AreaEvent;
import com.jinxin.jxtouchscreen.event.PosEvent;
import com.jinxin.jxtouchscreen.event.RefreshDataEvent;
import com.jinxin.jxtouchscreen.event.UpdateFinishEvent;
import com.jinxin.jxtouchscreen.model.CustomerPattern;
import com.jinxin.jxtouchscreen.model.FunDetail;
import com.jinxin.jxtouchscreen.model.Music;
import com.jinxin.jxtouchscreen.model.ProductPatternOperation;
import com.jinxin.jxtouchscreen.util.ToastUtil;
import com.litesuits.orm.db.assit.QueryBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.sql.Ref;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Create By Ly At 2016/12/15
 */
public class SenceControlFragment extends BaseFragment {

	public static final int PAGE_SIZE = 6;

	@Bind(R.id.spacegridv)
	GridView gvScence;

	/**
	 * 当前页面索引
	 */
	private int curPage;

	/**
	 * 所有模式列表
	 */
	private List<CustomerPattern> customerPatternListAll;
	/**
	 * 当前页面所需模式列表
	 */
	private List<CustomerPattern> customerPatternList;
	private ScenceGridViewAdapter scenceGridViewAdapter;

	@Override
	public View inflate(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.fragment_sence_control, container, false);
	}

	@Override
	public void initData(Bundle savedInstanceState) {
		EventBus.getDefault().register(this);

		//初次进入此页面
		customerPatternListAll = (List<CustomerPattern>) getArguments().getSerializable("customer_pattern_list_all");

		updateCustomerPattern();

		gvScence.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				scenceControl(customerPatternList.get(position).getPatternId());
			}
		});
		//长按弹出模式下需要控制的设备
		gvScence.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
				//跳转进入新的页面
				Intent intent = new Intent(getContext(), ModelViewActivity.class);
				intent.putExtra("patternId", customerPatternList.get(position).getPatternId());
				intent.putExtra("title_name",customerPatternList.get(position).getPaternName());
				startActivityForResult(intent,100);
				return true;
			}
		});

	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onUpdateData(UpdateFinishEvent event){
		customerPatternListAll = DBM.getCurrentOrm().query(CustomerPattern.class);
		updateCustomerPattern();
	}


	/**
	 * 分配当前页需要显示的模式数据
	 *
	 * @param page 当前页数
	 */
	private void assignCurrentPageScence(int page) {
		int size = customerPatternListAll.size();
		//页数不可超出总页数
		if (page > size / PAGE_SIZE) {
			curPage = size / PAGE_SIZE;
			return;
		}
		if (page == size / PAGE_SIZE)
			curPage = size / PAGE_SIZE;
		//页数不可小于0
		if (page < 0) {
			curPage = 0;
			return;
		}
		//根据当前页数分配数据
		if (PAGE_SIZE * (page + 1) < size)
			customerPatternList = customerPatternListAll.subList(PAGE_SIZE * page, PAGE_SIZE * (page + 1));
		else
			customerPatternList = customerPatternListAll.subList(PAGE_SIZE * page, size);
		//更新适配器数据
		scenceGridViewAdapter.replace(customerPatternList);
	}

	/**
	 * 更新数据与视图
	 */
	private void updateCustomerPattern() {
		if (scenceGridViewAdapter == null) {
			scenceGridViewAdapter = new ScenceGridViewAdapter(getActivity(), R.layout.gridview_item, customerPatternListAll);
			gvScence.setAdapter(scenceGridViewAdapter);
		} else {
			scenceGridViewAdapter.replace(customerPatternListAll);
		}
		assignCurrentPageScence(curPage);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onUpdateCustomerPattern(AreaEvent event) {
		if (event.getGroudId() != 0) {
			customerPatternListAll = DBM.getCurrentOrm().query(new QueryBuilder<>(CustomerPattern.class).where("patternGroupId = ?", new String[]{String.valueOf(event.getGroudId())}));
		} else {
			customerPatternListAll = DBM.getCurrentOrm().query(CustomerPattern.class);
		}
		updateCustomerPattern();
	}

	@OnClick({R.id.rightbtn, R.id.leftbtn})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.rightbtn:
				assignCurrentPageScence(++curPage);
				break;
			case R.id.leftbtn:
				assignCurrentPageScence(--curPage);
				break;
		}
	}

	/**
	 * 生成模式命令
	 *
	 * @param patternId 模式id
	 */
	private void scenceControl(int patternId) {

		TaskListener<Task> listener = new TaskListener<Task>() {

			@Override
			public void onSuccess(Task task, Object[] arg) {
				ToastUtil.showShort(BaseApp.getContext(), R.string.operate_success);
			}

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
			public void onAnyFail(Task task, Object[] arg) {
				ToastUtil.showShort(BaseApp.getContext(), "部分设备操作失败");
			}
		};
		List<Command> listCmd = CmdBuilder.build().generateModeCommand(patternId);

		OnlineMulitGatewayCmdSender onlineMulitGatewayCmdSender = new OnlineMulitGatewayCmdSender(getActivity(), listCmd);
		onlineMulitGatewayCmdSender.addListener(listener);
		onlineMulitGatewayCmdSender.send();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		EventBus.getDefault().unregister(this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case 100:
				if (resultCode == getActivity().RESULT_CANCELED) {
					Toast.makeText(getContext(), getContext().getString(R.string.model_null), Toast.LENGTH_SHORT).show();
				}else{

				}
				break;

			default:
				break;
		}
	}
}
