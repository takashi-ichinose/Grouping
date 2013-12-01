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
		// �o�Ȏҏ��
		attendList = getIntent().getStringArrayListExtra("attendList");
		// �O���[�v�̐l��
		memberCount = getIntent().getIntExtra("menberCount", 0);
		// �O���[�v�����̌덷�͈�
		int range = getIntent().getIntExtra("range", -1);
		// ����Ɏg���ϐ�
		int checkCount = memberCount;
		// ���X�g�̒��g���V���b�t������B
		Collections.shuffle(attendList);
		// �o�ȎҐ��������o�[���Ŋ������]��
		remainder = attendList.size() % memberCount;
		// �O���[�v�̐�
		groupCount = attendList.size() / memberCount;

		// �O���[�v����
		try {
			// �덷�͈�+1 or �]��Ȃ��̎��̏���
			if (range == 1 || remainder == 0) {
				createGroup();
				// �덷�͈�-1�̎��̏���
			} else {
				// �����o�[����-1���ăO���[�v����+1����B
				groupCount += 1;
				memberCount -= 1;
				createGroup();
			}
		} catch (RuntimeException e) {
			// �o�ȎҐ����ݒ肵�Ă���O���[�v�l���ȉ��̎��ɃO���[�v�l����ύX�i�g�[�X�g�\���p�j
			if (attendList.size() <= memberCount) {
				memberCount = attendList.size();
			// �덷�͈�+1 or try�߂�memberCount��-1������
			} else if (range == 1 || memberCount != checkCount) {
				// �ݒ�����ɏo���邾���߂Â���ׂ̕���
				memberCount = attendList.size() % memberCount > attendList
						.size() % (memberCount + 1) ? memberCount
						: memberCount + 1;
			} else {
				memberCount = attendList.size() % memberCount > attendList
						.size() % (memberCount - 1) ? memberCount
						: memberCount - 1;
			}
			// �ݒ�����ł̃O���[�v�������o���Ȃ��̂ŁA�ʒm����B
			Toast.makeText(
					SecondActivity.this,
					"�w�肳�ꂽ�����ł̃O���[�v�������ł��܂���ł����̂�\n" + memberCount
							+ "�l���ɃO���[�v�������܂��B", Toast.LENGTH_SHORT).show();
			// try�߂ő�������l�Ō덷���o�Ȃ��悤�ɏ�����
			List<String> memberList = new ArrayList<String>();
			groupList = new ArrayList<List<String>>();
			count = 0;
			// �e���X�g��0�Ԗڂɂ̓O���[�v��������B
			memberList.add("group" + number);
			// �g��for���ŏo�ȎҐ������[�v������B
			for (String name : attendList) {
				count++;
				memberList.add(name);
				// �O���[�v�l����count���������Ȃ�����groupList�ɒǉ����ď������B
				if (count == memberCount) {
					number++;
					groupList.add(memberList);
					memberList = new ArrayList<String>();
					memberList.add("group" + number);
					count = 0;
				}
			}
			//memberList���ێ����Ă���l���O���[�v���݂̂̎���List���폜
			if (memberList.size() <= 1) {
				memberList.clear();
			} else {
				// �[������groupList�ɒǉ�
				groupList.add(memberList);
			}
		}
		// �J�X�^�}�C�Y����adapter��listView�ƕR�Â���B
		GroupAdapter adapter = new GroupAdapter(this, R.layout.list_item,
				groupList);
		listView.setAdapter(adapter);
	}

	// �O���[�v�������\�b�h
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
