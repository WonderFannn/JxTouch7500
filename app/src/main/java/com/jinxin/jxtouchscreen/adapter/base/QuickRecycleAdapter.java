package com.jinxin.jxtouchscreen.adapter.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * adapter 快速使用基类
 *
 * @author XTER
 */
public abstract class QuickRecycleAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
	protected Context context;
	protected List<T> data;
	protected int res;

	public interface OnItemClickLitener {
		void onItemClick(View view, int position);

		void onItemLongClick(View view, int position);
	}

	OnItemClickLitener mOnItemClickLitener;

	public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
		this.mOnItemClickLitener = mOnItemClickLitener;
	}

	public QuickRecycleAdapter(Context context, int res, List<T> data) {
		this.context = context;
		this.data = data == null ? new ArrayList<T>() : data;
		this.res = res;
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ViewHolder(View.inflate(context, res, null));
	}

	public abstract void bindView(ViewHolder holder, int position);

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mOnItemClickLitener != null)
					mOnItemClickLitener.onItemClick(holder.itemView, holder.getLayoutPosition());
			}
		});
		holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if (mOnItemClickLitener != null)
					mOnItemClickLitener.onItemLongClick(holder.itemView, holder.getLayoutPosition());
				return false;
			}
		});
		bindView(holder, position);
	}

	public void addAll(List<T> elem) {
		data.addAll(elem);
		notifyDataSetChanged();
	}

	public void addToHead(T elem) {
		data.add(0, elem);
		notifyDataSetChanged();
	}

	public void remove(T elem) {
		data.remove(elem);
		notifyDataSetChanged();
	}

	public void remove(int index) {
		data.remove(index);
		notifyDataSetChanged();
	}

	public void replaceAll(List<T> elem) {
		if (elem == null) {
			data.clear();
			notifyDataSetChanged();
			return;
		}
		data.clear();
		data.addAll(elem);
		notifyDataSetChanged();
	}
}
