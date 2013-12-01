package com.example.grouping;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GroupAdapter extends ArrayAdapter<List<String>> {
	private int resource;
	private List<List<String>> objects;

	public GroupAdapter(Context context, int resource,
			List<List<String>> objects) {
		super(context, resource, objects);
		this.resource = resource;
		this.objects = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = View.inflate(getContext(), this.resource, null);
		LinearLayout layout = (LinearLayout) convertView
				.findViewById(R.id.itemLayout);
		TextView groupName = (TextView) convertView
				.findViewById(R.id.groupName);
		// 子要素のListの0番目にはグループ名を格納しているので、その名前をセットする。
		groupName.setText(objects.get(position).get(0).toString());
		// 子要素のListの要素数に応じてtextViewを生成し、それぞれにsetTextする。
		TextView[] memberNames = new TextView[objects.get(position).size()];
		for (int i = 1; i < memberNames.length; i++) {
			memberNames[i] = new TextView(getContext());
			layout.addView(memberNames[i]);
			memberNames[i].setText(objects.get(position).get(i).toString());
		}
		return convertView;
	}

}
