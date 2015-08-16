package br.com.vinniccius.contas.activity;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import br.com.vinniccius.contas.R;
import br.com.vinniccius.contas.bean.ItemList;

public class ItemListAdapter extends BaseAdapter {

	private List<ItemList> items;

	public ItemListAdapter(List<ItemList> items) {
		this.items = items;	
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ItemList item = items.get(position);
		
		view = inflateView(view, parent);		
		
		ImageView image = (ImageView)view.findViewById(R.id.item_icon);
		TextView title = (TextView)view.findViewById(R.id.item_title);
		TextView counter = (TextView)view.findViewById(R.id.item_counter);
		
		int iconId = item.getIcon();
		String titleValue = item.getTitle();
		Long counterValue = item.getCounter();
		
		image.setImageResource(iconId);
		title.setText(titleValue);
		if (counterValue > 0) { 
			counter.setText(counterValue.toString());
			counter.setBackgroundResource(R.drawable.calculator);
		}
		
		return view;
	}
	
	private View inflateView(View view, ViewGroup parent) {
		if (view == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			view = inflater.inflate(R.layout.item_list, parent, false);			
		}
		return view;
	}
	
}
