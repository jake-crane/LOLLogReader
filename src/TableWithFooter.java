import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class TableWithFooter extends JPanel {
	private static final long serialVersionUID = 2189075148394322272L;

	private JTable table = new JTable();
	private JTable footerTable = new JTable();
	private JScrollPane scrollPane = new JScrollPane(table);
	private JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

	public TableWithFooter() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(table);
		add(footerTable);
	}

	public TableWithFooter(Object[][] rowData, Object[][] footerData, Object[] columnNames) {
		table = new JTable(rowData, columnNames);
		scrollPane = new JScrollPane(table);
		footerTable = new JTable(footerData, columnNames);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(footerTable);
		add(table);
	}

	public void setModel(TableModel model, TableModel footerModel) {
		removeAll();

		table.setModel(model);
		scrollPane = new JScrollPane(table);

		footerTable.setModel(footerModel);
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
					footerTable.getColumnModel().getColumn(i).setMinWidth(width);
				}
			}
		});
		footerPanel.add(footerTable);
		((FlowLayout)footerPanel.getLayout()).setVgap(0);
		((FlowLayout)footerPanel.getLayout()).setHgap(1);
		add(scrollPane);
		add(footerPanel);

	}

	public void adjustColumns() {
		TableColumnModel tcm = table.getColumnModel();
		for (int i = 0; i < tcm.getColumnCount(); i++) {
			adjustColumn(i);
		}
	}

	public void adjustColumn(final int column) {
		TableColumn tableColumn = table.getColumnModel().getColumn(column);

		if (!tableColumn.getResizable()) {
			System.err.println("ubable to resize " + tableColumn);
			return;
		}

		int tableColumnHeaderWidth = getColumnHeaderWidth(column);
		int tableColumnDataWidth = getColumnDataWidth(table, column);
		int tablePreferredWidth = Math.max(tableColumnHeaderWidth, tableColumnDataWidth);

		int footerColumnDataWidth = getColumnDataWidth(footerTable, column);

		int preferredWidth = Math.max(tablePreferredWidth, footerColumnDataWidth);

		updateTableColumn(column, preferredWidth);
	}

	private void updateTableColumn(int column, int width) {
		final TableColumn tableColumn = table.getColumnModel().getColumn(column);
		final TableColumn footerColumn = footerTable.getColumnModel().getColumn(column);

		if (!tableColumn.getResizable() || !footerColumn.getResizable()) {
			System.err.println("ubable to resize " + tableColumn + " or " + footerColumn);
			return;
		}
		int spacing = 6;

		width += spacing;

		table.getTableHeader().setResizingColumn(tableColumn);
		tableColumn.setPreferredWidth(width);
		tableColumn.setMinWidth(width);
	}

	private int getColumnHeaderWidth(int column) {

		TableColumn tableColumn = table.getColumnModel().getColumn(column);
		Object value = tableColumn.getHeaderValue();
		TableCellRenderer renderer = tableColumn.getHeaderRenderer();

		if (renderer == null) {
			if (table.getTableHeader() != null) {
				renderer = table.getTableHeader().getDefaultRenderer();
			}
		}

		Component c = renderer.getTableCellRendererComponent(table, value, false, false, -1, column);
		return c.getPreferredSize().width;
	}

	private int getCellDataWidth(JTable table, int row, int column) {

		TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
		Component c = table.prepareRenderer(cellRenderer, row, column);
		int width = c.getPreferredSize().width + table.getIntercellSpacing().width;

		return width;
	}

	private int getColumnDataWidth(JTable table, int column) {

		int preferredWidth = 0;
		int maxWidth = table.getColumnModel().getColumn(column).getMaxWidth();

		for (int row = 0; row < table.getRowCount(); row++) {
			preferredWidth = Math.max(preferredWidth, getCellDataWidth(table, row, column));

			// We've exceeded the maximum width, no need to check other rows

			if (preferredWidth >= maxWidth)
				break;
		}

		return preferredWidth;
	}

	public void setAutoCreateRowSorter(boolean autoCreateRowSorter) {
		table.setAutoCreateRowSorter(autoCreateRowSorter);
	}

	public JTable getTable() {
		return table;
	}

	public JTable getFooterTable() {
		return footerTable;
	}

	public JPanel getFooterPanel() {
		return footerPanel;
	}

	public static Long getLongSumforColumn(int columnIndex, Object[][] data) {
		int total = 0;
		for (int i = 0; i < data.length; i++) {
			total += ((Long)data[i][columnIndex]).longValue();
		}
		return new Long(total);
	}

	public static Integer getIntegerSumforColumn(int columnIndex, Object[][] data) {
		int total = 0;
		for (int i = 0; i < data.length; i++) {
			total += ((Integer)data[i][columnIndex]).intValue();
		}
		return new Integer(total);
	}

	public static Double getDoubleSumforColumn(int columnIndex, Object[][] data) {
		int total = 0;
		for (int i = 0; i < data.length; i++) {
			total += ((Double)data[i][columnIndex]).doubleValue();
		}
		return new Double(total);
	}

	public static Float getFloatSumforColumn(int columnIndex, Object[][] data) {
		int total = 0;
		for (int i = 0; i < data.length; i++) {
			total += ((Float)data[i][columnIndex]).floatValue();
		}
		return new Float(total);
	}

}
