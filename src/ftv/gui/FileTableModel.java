package ftv.gui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import ftv.types.InvoiceFile;

public class FileTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String[] columnNames = { "Filename", "gesendet", "Status", "Response", "check" };

	private ArrayList<InvoiceFile> files;
	private String[] states;
	private MainApp ma;

	public FileTableModel(ArrayList<InvoiceFile> files, MainApp ma) {
		this.files = files;
		this.ma = ma;
		states = new String[files.size()];
		for (int i = 0; i < states.length; i++) {
			states[i] = "not sent";
		}
	}

	public FileTableModel() {
		super();
	}

	public ArrayList<InvoiceFile> getFiles() {
		return files;
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		if (files != null) {
			return files.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getValueAt(int i, int j) {
		// TODO Auto-generated method stub
		if (files == null) {
			return "";
		} else {
			switch (j) {
			case 0:
				return files.get(i).getName();
			
			case 1:
				if (files.get(i).isSent()) {
					return "ja";
				} else {
					return "nein";
				}
			case 2:
				return files.get(i).getStatus();
			case 3:
				return files.get(i).getResponse();
			case 4:
				return files.get(i).isSelected();

			default:
				return "1";
			}
		}
	}

	@Override
	public String getColumnName(int i) {
		return this.columnNames[i];
	}

	public void setState(int i, String str) {
		this.files.get(i).setStatus(str);
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
		if (col == 4) {
			return true;
		} else {
			return false;
		}
	}
	@Override
	public void setValueAt(Object value, int row, int col) {
	    if (col == 4) {
		this.files.get(row).setSelected((boolean) value);
	    fireTableCellUpdated(row, col);
	    ma.checkDismiss();
	    ma.checkSend();
	    }
	}
	
	@Override
	public Class<?> getColumnClass(int col) {
		if (col == 4) {
			return java.lang.Boolean.class;
		} else {
			return java.lang.Object.class;
		}
	}


}
