package com.example.grouping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.widget.ListView;
import android.widget.Toast;

public class SecondActivity extends Activity {
	private int count;
	private int number = 1;
	private int remainder;
	private int groupCount;
	private int memberCount;
	private List<String> attendList = new ArrayList<String>();
	private List<List<String>> groupList = new ArrayList<List<String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);

		ListView listView = (ListView) findViewById(R.id.listView1);
		// 出席者情報
		attendList = getIntent().getStringArrayListExtra("attendList");
		// グループの人数
		memberCount = getIntent().getIntExtra("menberCount", 0);
		// グループ分けの誤差範囲
		int range = getIntent().getIntExtra("range", -1);
		// 分岐に使う変数
		int checkCount = memberCount;
		// リストの中身をシャッフルする。
		Collections.shuffle(attendList);
		// 出席者数をメンバー数で割った余り
		remainder = attendList.size() % memberCount;
		// グループの数
		groupCount = attendList.size() / memberCount;

		// グループ分け
		try {
			// 誤差範囲+1 or 余りなしの時の処理
			if (range == 1 || remainder == 0) {
				createGroup();
				// 誤差範囲-1の時の処理
			} else {
				// メンバー数を-1してグループ数を+1する。
				groupCount += 1;
				memberCount -= 1;
				createGroup();
			}
		} catch (RuntimeException e) {
			// 出席者数が設定してあるグループ人数以下の時にグループ人数を変更（トースト表示用）
			if (attendList.size() <= memberCount) {
				memberCount = attendList.size();
			// 誤差範囲+1 or try節でmemberCountを-1した時
			} else if (range == 1 || memberCount != checkCount) {
				// 設定条件に出来るだけ近づける為の分岐
				memberCount = attendList.size() % memberCount > attendList
						.size() % (memberCount + 1) ? memberCount
						: memberCount + 1;
			} else {
				memberCount = attendList.size() % memberCount > attendList
						.size() % (memberCount - 1) ? memberCount
						: memberCount - 1;
			}
			// 設定条件でのグループ分けが出来ないので、通知する。
			Toast.makeText(
					SecondActivity.this,
					"指定された条件でのグループ分けができませんでしたので\n" + memberCount
							+ "人毎にグループ分けします。", Toast.LENGTH_SHORT).show();
			// try節で代入した値で誤差が出ないように初期化
			List<String> memberList = new ArrayList<String>();
			groupList = new ArrayList<List<String>>();
			count = 0;
			// 各リストの0番目にはグループ名を入れる。
			memberList.add("group" + number);
			// 拡張for分で出席者数分ループさせる。
			for (String name : attendList) {
				count++;
				memberList.add(name);
				// グループ人数とcountが等しくなったらgroupListに追加して初期化。
				if (count == memberCount) {
					number++;
					groupList.add(memberList);
					memberList = new ArrayList<String>();
					memberList.add("group" + number);
					count = 0;
				}
			}
			//memberListが保持している値がグループ名のみの時にListを削除
			if (memberList.size() <= 1) {
				memberList.clear();
			} else {
				// 端数分をgroupListに追加
				groupList.add(memberList);
			}
		}
		// カスタマイズしたadapterでlistViewと紐づける。
		GroupAdapter adapter = new GroupAdapter(this, R.layout.list_item,
				groupList);
		listView.setAdapter(adapter);
	}

	// グループ分けメソッド
	private void createGroup() {
		for (int i = 0; i < groupCount; i++) {
			List<String> memberList = new ArrayList<String>();
			memberList.add("group" + (i + 1));
			for (int j = 0; j < memberCount; j++) {
				memberList.add(attendList.get(count));
				count++;
			}
			groupList.add(memberList);
		}
		if (remainder != 0 && count != attendList.size()) {
			int difference = count;
			for (int i = 0; i < attendList.size() - difference; i++) {
				groupList.get(i).add(attendList.get(count));
				count++;
			}
		}
	}
}
