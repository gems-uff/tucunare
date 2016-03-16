package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import model.CustomTableModel;

public class ListRepositories extends JDialog implements ActionListener{
	private static final long serialVersionUID = 1L;
	private JTable table;
	private JButton btnCancelar;
	private JButton btnProcessar;
	private List<String> selectedRepositories = new ArrayList<String>();
	private boolean DEBUG = false;
	
	public static void main(String[] args) {
		Object[][] data = { 
				{"1", "bugpredict", "Limeira",10},
				{"2", "katello", "maria",200},
				{"3", "akka", "joao",50}};
		
		ListRepositories lr = new ListRepositories(data);
		lr.setVisible(true);
	}

	public ListRepositories(Object[][] data) {
		setTitle("Seleção de repositórios.");
		setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);

		CustomTableModel model = new CustomTableModel(data);
		table = new JTable(model);
		
		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.setFillsViewportHeight(true);
		table.setAutoCreateRowSorter(true);
	    
		JScrollPane scrollPane = new JScrollPane(table);

		getContentPane().add(scrollPane, BorderLayout.CENTER);

		btnProcessar = new JButton("Selecionar");
		btnProcessar.addActionListener(this);
		panel.add(btnProcessar);

		btnCancelar = new JButton("Cancelar");
		btnCancelar.addActionListener(this);
		panel.add(btnCancelar);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnCancelar)
			setVisible(true);
		else
			if (e.getSource() == btnProcessar){
				CustomTableModel ctm = (CustomTableModel) table.getModel(); 
				selectedRepositories = ctm.getSelectedRepositories(table.getSelectedRows());
				System.out.println(selectedRepositories);
				//setVisible(false);
			}
	}
	
	public List<String> getSelectedRepositories(){
		return selectedRepositories;
	}
	
	
}
