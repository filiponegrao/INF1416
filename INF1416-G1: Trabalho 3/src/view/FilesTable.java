package view;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class FilesTable extends JScrollPane {
	
	private JTable table;
	
	public FilesTable (int x, int y, int w, int h, JTable table) {

//		super(data, columnNames);
		this.setBounds(x, y, w, h);
        
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(x, y, w, h);
		table.setFillsViewportHeight(true);

		this.add(table.getTableHeader());
	}
	
	public static FilesTable createTablePanel(
			int x,
			int y,
			int w,
			int h,
			String[] columnNames,
			String[][] data) {
		
		DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
		
		JTable table = new JTable();
		table.setModel(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        FilesTable panel = new FilesTable(x, y, w, h, table);
        return panel;
	}
	
	public boolean isCellEditable(int nRow, int nCol) {
		return false;
	}

}
