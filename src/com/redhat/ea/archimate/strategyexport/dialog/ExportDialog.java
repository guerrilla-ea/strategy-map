/**
 * 
 *	Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 *	This file is part of strategy exporter.
 *	This program is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU Lesser General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *	
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU Lesser General Public License for more details.
 *
 *	You should have received a copy of the GNU Lesser General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.redhat.ea.archimate.strategyexport.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import com.redhat.ea.archimate.strategyexport.global.types.StateEnum;

public class ExportDialog extends Dialog {

	protected String[] statuses;
	private boolean _cancelled;
	private List _listSelection;
	
	private String prompt;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public ExportDialog(Shell parentShell, String prompt) {
		super(parentShell);
		setBlockOnOpen(true);
		_cancelled = true;
		this.prompt = prompt;
	}

	public boolean isCancelled() {
		return _cancelled;
	}
	
	public String[] getStatuses(){
		return this.statuses;
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(null);

		Label lblExportTheSelected = new Label(container, SWT.CENTER);
		lblExportTheSelected.setBounds(10, 10, 280, 43);
		//lblExportTheSelected
		//		.setText("Export the selected Roadmap to a .dot export file\n for visualization with the GraphViz program.");
		lblExportTheSelected.setText(this.prompt);

		ListViewer listViewer = new ListViewer(container, SWT.BORDER
				| SWT.V_SCROLL | SWT.MULTI);

		listViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				statuses = ((ListViewer) event.getSelectionProvider()).getList()
						.getSelection();
			}
		});
		
		List list = listViewer.getList();
		// list.setItems(new String[] {"Proposed", "Validated", "Approved",
		// "Implemented (completed)", "Mandatory (missing dependencies)"});

		for (String s : StateEnum.GetStateEnumList()) {
			list.add(s);
		}
		list.setBounds(52, 74, 208, 116);
		
		// do the selectAll after we've populated...
		list.selectAll();
		// bootstrap statuses, as if we don't click anything, never gets set
		statuses = list.getSelection();

		_listSelection = list;

		Label lblSelectedStatesWill = new Label(container, SWT.NONE);
		lblSelectedStatesWill.setBounds(52, 54, 193, 14);
		lblSelectedStatesWill.setText("Selected States will be included.");
		

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button_1 = createButton(parent, IDialogConstants.OK_ID,
				IDialogConstants.OK_LABEL, true);
		button_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (_listSelection.getSelectionCount() == 0) {
					MessageDialog.openError(getParentShell(),
							"Missing Status Selection",
							"Please select at least one status.");
				} else {
					close();
					_cancelled = false;
				}
			}
		});
		Button button = createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				_cancelled = true;
				close();
			}
		});
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(300, 300);
	}
}
