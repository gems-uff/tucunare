package model;

import javax.swing.table.AbstractTableModel;

public class CustomTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

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

	//Recupera os repositórios selecionados no formato: "juniorlimeira/bugpredict; Katello/katello;" 
	public String getSelectedRepositories(int [] selectedRows){
		String result ="";
		for (int i : selectedRows) {
			result += data[i][2]+"/"+data[i][1]+"; ";
		}

		return result;
	}

	public boolean isCellEditable(int row, int col) {
		return false;

	}
}