package model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class CustomTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private boolean DEBUG = false;

	private String[] columnNames = { "","Name","Owner","Total PR"};
	private Object[][] data;

	public CustomTableModel(Object[][] data){
		this.data = data;
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return data.length;
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Object getValueAt(int row, int col) {
		return data[row][col];
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	public void setValueAt(Object value, int row, int col) {
		if (DEBUG) {
			System.out.println("Setting value at " + row + "," + col
					+ " to " + value
					+ " (an instance of "
					+ value.getClass() + ")");
		}

		data[row][col] = value;
		// Normally, one should call fireTableCellUpdated() when 
		// a value is changed.  However, doing so in this demo
		// causes a problem with TableSorter.  The tableChanged()
		// call on TableSorter that results from calling
		// fireTableCellUpdated() causes the indices to be regenerated
		// when they shouldn't be.  Ideally, TableSorter should be
		// given a more intelligent tableChanged() implementation,
		// and then the following line can be uncommented.
		// fireTableCellUpdated(row, col);

		if (DEBUG) {
			printDebugData();
		}

	}

	private void printDebugData() {
		int numRows = getRowCount();
		int numCols = getColumnCount();

		for (int i=0; i < numRows; i++) {
			System.out.print("    row " + i + ":");
			for (int j=0; j < numCols; j++) {
				System.out.print("  " + data[i][j]);
			}
			System.out.println();
		}
		System.out.println("--------------------------");
	}
	public boolean isCellEditable(int row, int col) {
		return false;

	}

	//Recupera os repositórios selecionados no formato: "juniorlimeira/bugpredict; Katello/katello;" 
	public List<String> getSelectedRepositories(int [] selectedRows){
		List<String> result = new ArrayList<String>();
		for (int i : selectedRows) {
			result.add(data[i][2]+"/"+data[i][1]);
		}
		return result;
	}
}