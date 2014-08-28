
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableModel;

public class TableWithFooter extends JPanel {
	private static final long serialVersionUID = 2189075148394322272L;

	private JTable table = new JTable();
	private JTable footerTable;
	private JScrollPane scrollPane = new JScrollPane();
	private JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private TableColumnAdjuster tca = new TableColumnAdjuster(table);

	public TableWithFooter() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(table);
	}

	public TableWithFooter(Object[][] rowData, Object[][] footerData, Object[] columnNames) {
		table = new JTable(rowData, columnNames);
		tca = new TableColumnAdjuster(table);
		footerTable = new JTable(footerData, columnNames);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(footerTable);
		add(table);
		tca.adjustColumns();
	}

	public void setModel(TableModel model, TableModel footerModel) {
		removeAll();
		
		table.setModel(model);
		scrollPane = new JScrollPane(table);

		footerTable = new JTable(footerModel);
		footerTable.setPreferredScrollableViewportSize(new Dimension(footerTable.getPreferredScrollableViewportSize().width, footerTable.getRowHeight()));
		footerTable.setTableHeader(null);
		table.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
			@Override
			public void columnAdded(TableColumnModelEvent e) {}

			@Override
			public void columnRemoved(TableColumnModelEvent e) {}

			@Override
			public void columnMoved(TableColumnModelEvent e) {}

			@Override
			public void columnSelectionChanged(ListSelectionEvent e) {}

			@Override
			public void columnMarginChanged(ChangeEvent e) {
				for (int i = 0; i < table.getColumnCount(); i++) {
					int width = table.getColumnModel().getColumn(i).getWidth();
					footerTable.getColumnModel().getColumn(i).setPreferredWidth(width);
				}
			}
		});
		footerPanel.add(footerTable);
		((FlowLayout)footerPanel.getLayout()).setVgap(0);
		((FlowLayout)footerPanel.getLayout()).setHgap(1);
		add(scrollPane);
		add(footerPanel);
		tca.adjustColumns();
	}

	public void setAutoCreateRowSorter(boolean autoCreateRowSorter) {
		table.setAutoCreateRowSorter(autoCreateRowSorter);
	}
	
	public static Long getLongSumforColumn(int columnIndex, Object[][] data) {
		int total = 0;
		for (int i = 0; i < data.length; i++) {
			total+= ((Long)data[i][columnIndex]).longValue();
		}
		return new Long(total);
	}
	
	public static Integer getIntegerSumforColumn(int columnIndex, Object[][] data) {
		int total = 0;
		for (int i = 0; i < data.length; i++) {
			total+= ((Integer)data[i][columnIndex]).intValue();
		}
		return new Integer(total);
	}
	
	public static Double getDoubleSumforColumn(int columnIndex, Object[][] data) {
		int total = 0;
		for (int i = 0; i < data.length; i++) {
			total+= ((Double)data[i][columnIndex]).doubleValue();
		}
		return new Double(total);
	}
	
	public static Float getFloatSumforColumn(int columnIndex, Object[][] data) {
		int total = 0;
		for (int i = 0; i < data.length; i++) {
			total+= ((Float)data[i][columnIndex]).floatValue();
		}
		return new Float(total);
	}
	
	public JTable getTable() {
		return table;
	}
	
	public JTable getFooterTable() {
		return footerTable;
	}
	
}
