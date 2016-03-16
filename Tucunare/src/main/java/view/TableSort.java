package view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class TableSort extends JDialog implements ActionListener{

	private static final long serialVersionUID = 1L;
	private JButton btnSelecionar;
	private JButton btnCancelar;
	private JTable table;
	private List<String> selectedRepositories = new ArrayList<String>();;

	public TableSort(Object[][] data) {
		setTitle("Seleção de repositórios");
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		
		table = new JTable(new MyTableModel(data));
		table.setFillsViewportHeight(true);
		table.setAutoCreateRowSorter(true);

		JScrollPane scrollPane = new JScrollPane(table);

		add(scrollPane);
		btnSelecionar = new JButton("Selecionar");
		btnSelecionar.addActionListener(this);

		btnCancelar = new JButton("Cancelar");
		btnCancelar.addActionListener(this);

		JPanel panel = new JPanel();
		panel.add(btnSelecionar);
		panel.add(btnCancelar);
		add(panel, BorderLayout.SOUTH);
	}

	class MyTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;
		private String[] columnNames = { "Number","Owner","Name","Total PR"};
		private Object[][] data = {
				{new Integer(1), "Akka","maria", new Long(10)},
				{new Integer(2), "katello","jose", new Long(300)},
				{new Integer(3), "bugpredict","limeira", new Long(50)}
		};
		public MyTableModel (Object[][] data){
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

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		public boolean isCellEditable(int row, int col) {
			return false;
		}

		public void setValueAt(Object value, int row, int col) {
			data[row][col] = value;
		}
	}

	public static void main(String[] args) {
		Object[][] data = { 
				{1, "bugpredict", "Limeira",10},
				{2, "katello", "maria",200},
				{3, "akka", "joao",50}};

		TableSort tsd = new TableSort(data);
		tsd.setModal(true);
		tsd.pack();
		tsd.setVisible(true);
		
	}

	public void actionPerformed(ActionEvent e) {
		selectedRepositories = new ArrayList<String>();
		if (e.getSource() == btnSelecionar){			
			for (int i : table.getSelectedRows())
				selectedRepositories.add(table.getValueAt(i, 1)+"/"+table.getValueAt(i, 2));

			setVisible(false);	
		}else
			setVisible(false);

	}

	public List<String> getSelectedRepositories(){
		return selectedRepositories;
	}

}