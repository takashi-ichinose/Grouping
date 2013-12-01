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
		// �ۑ��̈���擾�B�i����̓O���[�v�l���A�덷�͈́A�`�F�b�N�{�b�N�X�̃`�F�b�N����ۑ�����j
		pref = getSharedPreferences("groupingData", Activity.MODE_PRIVATE);

		String[] nameArray = new String[] { "���B����", "�ꐣ�F", "����r��", "�����",
				"�����", "��˔��R�I", "��i�܂�", "��×ǔn", "�Ñ���", "�_�c�\�i", "�팳�M��", "�K����",
				"���������q", "������", "�ēc�v���q", "��͐V���Y", "���q����", "�d�`�O", "�\���͓T",
				"��c�K��", "���������", "���{�^�R��", "�R���O", "�R�{�N��" };

		for (int i = 0; i < nameArray.length; i++) {
			nameList.add(nameArray[i]);
		}
		//�@�`�F�b�N�{�b�N�X�t�̃��X�g�r���[�ƕR�Â���Adapter�B
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice, nameList);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setAdapter(adapter);

		// Preferences�ɒl���ۑ�����Ă����ꍇ�͂��̏���ǂݍ���ŕ\���B
		if (pref != null) {
			// �O���[�v�����̐l���i�����l:4�j
			memberCount = pref.getInt("rule", 4);
			ruleView.setText(memberCount + "�l�O���[�v");
			// �O���[�v�������̌덷�͈́i�����l:-1�j
			range = pref.getInt("range", -1);
			rangeView.setText(range + "");
			// �o�Ȏҏ��i�O��̃`�F�b�N�{�b�N�X�̃`�F�b�N����ǂݍ��݁A�\�߃`�F�b�N������j
			for (int i = 0; i < nameList.size(); i++) {
				if (pref.getBoolean(nameList.get(i), false)) {
					listView.setItemChecked(i, true);
				}
			}
		}
		
		// �O���[�v�����{�^��
		groupingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// �߂�{�^����SecoundActivity����߂����ꍇ�̒l�̏d���������ׂ̕���
				if (attendList.isEmpty() == false) {
					attendList.clear();
				}
				// �`�F�b�N�{�b�N�X�̃`�F�b�N�����擾
				SparseBooleanArray checked = listView.getCheckedItemPositions();
				for (int i = 0; i < checked.size(); i++) {
					// getCheckedItemPositions()�ł�1�x�ł��`�F�b�N���Ă����true��Ԃ��̂ŁA���ۂ̏󋵂ɍ��킹�ĕ␳����B
					if (checked.valueAt(i)) {
						attendList.add(nameList.get(checked.keyAt(i)));
					}
				}
				Intent intent = new Intent(MainActivity.this,
						SecondActivity.class);
				// �o�Ȏҏ����i�[����List��put
				intent.putStringArrayListExtra("attendList", attendList);
				// �O���[�v�����̐l��
				intent.putExtra("menberCount", memberCount);
				// �O���[�v�����̌덷�͈�
				intent.putExtra("range", range);
				startActivity(intent);
			}
		});
		
		// �O���[�v���������̐ݒ�
		settingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ����̓O���[�v�l���̐ݒ��NumberPicker�iAPI���x��11�j�ōs���B
				final NumberPicker picker = new NumberPicker(MainActivity.this);
				// NumberPicker�̍ő�l
				picker.setMaxValue(nameList.size() / 2);
				// NumberPicker�̍ŏ��l
				picker.setMinValue(2);
				// NumberPicker�̏����l
				picker.setValue(memberCount);
				// NumberPicker�̓��͎��ɃL�[�{�[�h��\�������Ȃ��悤��FOCUS�����ہB
				picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
				// �����ݒ�p�̃_�C�A���O
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this)
						.setTitle("�O���[�v�̐l���Ɛl���̌덷�͈͂�I�����ĉ������B")
						.setMessage("�O���[�v�l��")
						// ��������NumberPicker��\��
						.setView(picker)
						// �{�^���Ō덷�͈͂�ݒ�
						.setPositiveButton("+1�ȉ�",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										range = 1;
										setTexts(range, picker);
									}
								})
						.setNeutralButton("-1�ȉ�",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										range = -1;
										setTexts(range, picker);
									}
								}).setNegativeButton("�L�����Z��", null);
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		});
		// �ۑ����Ă�����̃N���A
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
		// onPause()���Ă΂ꂽ����Preferences�ɑS�Ă̏���ۑ�����B
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
	
	// �O���[�v�����̏����ɍ��킹�ĕ\����ύX���郁�\�b�h
	private void setTexts(int range, NumberPicker picker) {
		if (range < 0) {
			rangeView.setText("" + range);
		} else {
			rangeView.setText("+" + range);
		}
		memberCount = picker.getValue();
		ruleView.setText(memberCount + "�l�O���[�v");
	}
}
