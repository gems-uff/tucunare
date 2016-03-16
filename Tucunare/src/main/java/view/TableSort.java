package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
	private JScrollPane scrollPane;

	public TableSort(Object[][] data) {
		setTitle("Seleção de repositórios");
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

		table = new JTable();
		table.setModel(new MyTableModel(data));

		table.setFillsViewportHeight(true);
		table.setAutoCreateRowSorter(true);


		scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(450, 150));
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
		private String[] columnNames = {"", "Number","Owner","Name","Total PR"};
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
			if (col == 0)
				return true;
			else
				return false;
		}

		public void setValueAt(Object value, int row, int col) {
			data[row][col] = value;
		}

		public void loadData(){
			Object[][] object = this.data;
			for (Object[] objects : object) {
				if ((Boolean) objects[0])
					selectedRepositories.add((String)objects[2]+"/"+(String)objects[3]);

			}
		}
	}

	public static void main(String[] args) {
		Object[][] data = { 
				{new Boolean(false),8, "akkaE", "5joao",50},
				{new Boolean(false),9, "akkaF", "6joao",60},
				{new Boolean(false),10, "akkaG", "7joao",70},
				{new Boolean(false),11, "akkaH", "8joao",80},
				{new Boolean(false),12, "akkaI", "9joao",90},
				{new Boolean(false),13, "akkaJ", "10joao",10},
				{new Boolean(false),14, "akkaK", "11joao",20},
				{new Boolean(false),15, "akkaL", "12joao",20},
				{new Boolean(false),16, "akkaM", "13joao",30}};

		TableSort tsd = new TableSort(data);
		tsd.setModal(true);
		tsd.pack();
		tsd.setLocationRelativeTo(null);
		tsd.loadScrollPane();
		
		tsd.setVisible(true);

	}

	private void loadScrollPane() {
		table.setPreferredSize(new Dimension(150, 600));		
	}

	public void actionPerformed(ActionEvent e) {
		selectedRepositories = new ArrayList<String>();
		
		if (e.getSource() == btnSelecionar){			
			MyTableModel mtm = (MyTableModel) table.getModel();
			mtm.loadData();
			setVisible(false);	
		}else
			setVisible(false);

	}

	public List<String> getSelectedRepositories(){
		return selectedRepositories;
	}

}