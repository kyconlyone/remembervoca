package com.ihateflyingbugs.hsmd.manager;

public class ExamResult {
	int RowID;
	int SelectedNum;
	
	public ExamResult(int questionNum, int selectedNum) {
		super();
		RowID = questionNum;
		SelectedNum = selectedNum;
	}

	

	public int getRowID() {
		return RowID;
	}



	public void setRowID(int rowID) {
		RowID = rowID;
	}



	public int getSelectedNum() {
		return SelectedNum;
	}

	public void setSelectedNum(int selectedNum) {
		SelectedNum = selectedNum;
	}
}
