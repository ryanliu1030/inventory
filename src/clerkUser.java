import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class clerkUser extends Thread {

    public String user;
    private JFrame frame;
    public static JTable table;

    /**
     * Launch the application.
     */
    @Override
    public void run() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                	clerkUser window = new clerkUser(user);
                    window.frame.setLocationRelativeTo(null);
                    window.frame.setResizable(false);
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public clerkUser(String user) {
        this.user = user;
    	initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        //student welcome label
		JLabel welcomeLbl = new JLabel("Hello Clerk ID#" + user);
		welcomeLbl.setBounds(10, 10, 200, 20);
		welcomeLbl.setForeground(Color.BLACK);
		welcomeLbl.setFont(new Font("Times New Roman", Font.BOLD, 18));
		frame.getContentPane().add(welcomeLbl);


		//view current grades
		JButton viewInventoryBtn = new JButton("View Inventory");
		viewInventoryBtn.setBounds(10, 34, 180, 30);
		viewInventoryBtn.setForeground(SystemColor.textHighlight);
		viewInventoryBtn.setFont(new Font("Times New Roman", Font.BOLD, 18));
		frame.getContentPane().add(viewInventoryBtn);
		viewInventoryBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearResults();
				viewInventory();
			}
		});

        //Logout button
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBounds(474, 323, 100, 30);
        logoutBtn.setForeground(Color.RED);
        logoutBtn.setFont(new Font("Times New Roman", Font.BOLD, 18));
        frame.getContentPane().add(logoutBtn);
        logoutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login login = new login();
                login.start();
                frame.dispose();
            }
        });
        
        // Sell button window
        JButton sellBtn = new JButton("Sell Item");
        sellBtn.setForeground(SystemColor.textHighlight);
        sellBtn.setFont(new Font("Times New Roman", Font.BOLD, 18));
        sellBtn.setBounds(200, 34, 180, 30);
        frame.getContentPane().add(sellBtn);  
        sellBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sell sell = new sell(user);
                sell.start();
                frame.dispose();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 75, 564, 240);
        frame.getContentPane().add(scrollPane);
        
        String header[] = {"Item ID", "Item Name", "Item Cost", "Item Stock", "Item Sold"};        
        DefaultTableModel tableModel = new DefaultTableModel(header, 30) {
			@Override
			public boolean isCellEditable(int row, int column) {
				switch (column) {
				default:
					return false;
				}
			}
        };
        table = new JTable(tableModel);
        scrollPane.setViewportView(table);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);
		table.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));
		table.getColumnModel().getColumn(0).setPreferredWidth(30);
		table.getColumnModel().getColumn(0).setMinWidth(30);
		table.getColumnModel().getColumn(1).setPreferredWidth(90);
		table.getColumnModel().getColumn(1).setMinWidth(90);
		table.getColumnModel().getColumn(2).setPreferredWidth(15);
		table.getColumnModel().getColumn(2).setMinWidth(15);
		table.getColumnModel().getColumn(3).setPreferredWidth(60);
		table.getColumnModel().getColumn(3).setMinWidth(60);
		table.getColumnModel().getColumn(4).setPreferredWidth(45);
		table.getColumnModel().getColumn(4).setMinWidth(45);
		table.setBorder(new LineBorder(Color.GRAY, 1, true));
		table.setFont(new Font("Tahoma", Font.BOLD, 14));
		table.setRowHeight(20);
        
        scrollPane.setViewportView(table);      
    }

	public static void clearResults() {
		for(int i = 0; i < 30; i++) {
			table.setValueAt(null, i, 0);
			table.setValueAt(null, i, 1);
			table.setValueAt(null, i, 2);
			table.setValueAt(null, i, 3);
			table.setValueAt(null, i, 4);
		}
	}
	
	public void viewInventory() {
		Connection conn = null;
		try {
			//testing connection
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Inventory", "postgres", "password");

			Statement statement;
			statement = conn.createStatement();

			ResultSet itemList = statement.executeQuery(
					"select * from inventory order by item_id asc"
			);
			try {
				int i = 0;
				while (itemList.next()) {
					table.setValueAt(itemList.getInt(1), i, 0);
					table.setValueAt(itemList.getString(2), i, 1);
					table.setValueAt(itemList.getString(3), i, 2);
					table.setValueAt(itemList.getInt(4), i, 3);
					table.setValueAt(itemList.getInt(5), i, 4);
					i++;
				}
			}
			catch (Exception e) {
				System.out.println(e);
			}
			itemList.close();
			statement.close();
			conn.close();
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}
}


