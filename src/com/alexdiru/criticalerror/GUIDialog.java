package com.alexdiru.criticalerror;

public interface GUIDialog {
	public void createAndShow();
	public void createAndShow(final String dialogTitle, final String dialogMessage);
	public void positiveAction();
	public void negativeAction();
	public void neutralAction();
}
