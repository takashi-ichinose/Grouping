package com.example.grouping;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

public class MainActivity extends Activity {
	private ListView listView;
	private ArrayList<String> nameList = new ArrayList<String>();
	private ArrayList<String> attendList = new ArrayList<String>();
	private SharedPreferences pref;
	private SharedPreferences.Editor editor;
	private int memberCount = 4;
	private int range = -1;
	private TextView ruleView;
	private TextView rangeView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button groupingButton = (Button) findViewById(R.id.groupingButton);
		Button settingButton = (Button) findViewById(R.id.settingButton);
		Button deleteButton = (Button) findViewById(R.id.deleteButton);
		ruleView = (TextView) findViewById(R.id.ruleView);
		rangeView = (TextView) findViewById(R.id.rangeView);
		listView = (ListView) findViewById(R.id.listView);
		// 保存領域を取得。（今回はグループ人数、誤差範囲、チェックボックスのチェック情報を保存する）
		pref = getSharedPreferences("groupingData", Activity.MODE_PRIVATE);

		String[] nameArray = new String[] { "安達誠寛", "一瀬孝", "今井俊介", "岩崎大輔",
				"岩崎拓也", "岩塚美由紀", "大司まり", "大津良馬", "嘉村翼", "神田圭司", "楠元信吾", "桑原玲",
				"小西未央子", "佐藤章", "柴田久美子", "大力新太郎", "高倉健治", "壇義弘", "能島章典",
				"野田幸代", "松島あゆみ", "松本真由美", "山口徹", "山本康平" };

		for (int i = 0; i < nameArray.length; i++) {
			nameList.add(nameArray[i]);
		}
		//　チェックボックス付のリストビューと紐づけるAdapter。
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice, nameList);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setAdapter(adapter);

		// Preferencesに値が保存されていた場合はその情報を読み込んで表示。
		if (pref != null) {
			// グループ分けの人数（初期値:4）
			memberCount = pref.getInt("rule", 4);
			ruleView.setText(memberCount + "人グループ");
			// グループ生成時の誤差範囲（初期値:-1）
			range = pref.getInt("range", -1);
			rangeView.setText(range + "");
			// 出席者情報（前回のチェックボックスのチェック情報を読み込み、予めチェックを入れる）
			for (int i = 0; i < nameList.size(); i++) {
				if (pref.getBoolean(nameList.get(i), false)) {
					listView.setItemChecked(i, true);
				}
			}
		}
		
		// グループ生成ボタン
		groupingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 戻るボタンでSecoundActivityから戻った場合の値の重複を避ける為の分岐
				if (attendList.isEmpty() == false) {
					attendList.clear();
				}
				// チェックボックスのチェック情報を取得
				SparseBooleanArray checked = listView.getCheckedItemPositions();
				for (int i = 0; i < checked.size(); i++) {
					// getCheckedItemPositions()では1度でもチェックしていればtrueを返すので、実際の状況に合わせて補正する。
					if (checked.valueAt(i)) {
						attendList.add(nameList.get(checked.keyAt(i)));
					}
				}
				Intent intent = new Intent(MainActivity.this,
						SecondActivity.class);
				// 出席者情報を格納するListをput
				intent.putStringArrayListExtra("attendList", attendList);
				// グループ分けの人数
				intent.putExtra("menberCount", memberCount);
				// グループ分けの誤差範囲
				intent.putExtra("range", range);
				startActivity(intent);
			}
		});
		
		// グループ分け条件の設定
		settingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 今回はグループ人数の設定はNumberPicker（APIレベル11）で行う。
				final NumberPicker picker = new NumberPicker(MainActivity.this);
				// NumberPickerの最大値
				picker.setMaxValue(nameList.size() / 2);
				// NumberPickerの最小値
				picker.setMinValue(2);
				// NumberPickerの初期値
				picker.setValue(memberCount);
				// NumberPickerの入力時にキーボードを表示させないようにFOCUSを拒否。
				picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
				// 条件設定用のダイアログ
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this)
						.setTitle("グループの人数と人数の誤差範囲を選択して下さい。")
						.setMessage("グループ人数")
						// 生成したNumberPickerを表示
						.setView(picker)
						// ボタンで誤差範囲を設定
						.setPositiveButton("+1以下",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										range = 1;
										setTexts(range, picker);
									}
								})
						.setNeutralButton("-1以下",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										range = -1;
										setTexts(range, picker);
									}
								}).setNegativeButton("キャンセル", null);
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		});
		// 保存している情報のクリア
		deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				editor = pref.edit();
				editor.clear();
				editor.commit();
				for (int i = 0; i < nameList.size(); i++) {
					listView.setItemChecked(i, false);
				}
			}
		});
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// onPause()が呼ばれた時にPreferencesに全ての情報を保存する。
		SparseBooleanArray checked = listView.getCheckedItemPositions();
		editor = pref.edit();
		editor.clear();
		for (int i = 0; i < checked.size(); i++) {
			if (checked.valueAt(i)) {
				editor.putBoolean(nameList.get(checked.keyAt(i)), true);
			}
		}
		editor.putInt("rule", memberCount);
		editor.putInt("range", range);
		editor.commit();
	}
	
	// グループ分けの条件に合わせて表示を変更するメソッド
	private void setTexts(int range, NumberPicker picker) {
		if (range < 0) {
			rangeView.setText("" + range);
		} else {
			rangeView.setText("+" + range);
		}
		memberCount = picker.getValue();
		ruleView.setText(memberCount + "人グループ");
	}
}
