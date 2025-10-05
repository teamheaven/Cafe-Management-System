import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class MenuManagementForm extends JFrame {
    JTable menuTable;
    JButton selectButton;
    DefaultTableModel model;
    String userName;
    ArrayList<Object[]> selectedItems;

    public MenuManagementForm(String userName, ArrayList<Object[]> selectedItems) {
        this.userName = userName;
        this.selectedItems = selectedItems;

        setTitle("Menu - User: " + userName);
        setSize(700, 500);
        setLayout(new BorderLayout());

        String[] columns = {"Item ID", "Name", "Price", "Stock"};
        model = new DefaultTableModel(columns, 0);
        menuTable = new JTable(model);
        menuTable.setRowHeight(25);
        JScrollPane scroll = new JScrollPane(menuTable);
        add(scroll, BorderLayout.CENTER);

        selectButton = new JButton("Select Items");
        selectButton.setBackground(new Color(34,139,34));
        selectButton.setForeground(Color.WHITE);
        add(selectButton, BorderLayout.SOUTH);

        loadMenuItems();

        selectButton.addActionListener(_ -> {
            int[] rows = menuTable.getSelectedRows();
            if(rows.length==0) {
                JOptionPane.showMessageDialog(this,"Select at least one item!");
                return;
            }
            for(int r: rows){
                Object[] row = {
                    model.getValueAt(r,0),
                    model.getValueAt(r,1),
                    model.getValueAt(r,2),
                    model.getValueAt(r,3)
                };
                selectedItems.add(row);
            }
            JOptionPane.showMessageDialog(this,"Items selected! Go to Order.");
            dispose();
        });

        setVisible(true);
    }

    private void loadMenuItems() {
        try(Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM menu_items";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while(rs.next()){
                Object[] row = {
                    rs.getInt("item_id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("stock_qty")
                };
                model.addRow(row);
            }
        } catch(SQLException e){ e.printStackTrace();}
    }
}
