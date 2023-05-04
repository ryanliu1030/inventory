import java.awt.EventQueue;

import javax.swing.*;

import java.awt.Font;
import java.awt.SystemColor;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.awt.Color;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public class addremove extends Thread {
	public String user;
    private JFrame frame;
    private JTextField idTxtField;
    private JTextField nameTxtField;
    private JTextField costTxtField;
    private JTextField stockTxtField;
    private JTextField soldTxtField;

    @Override
    public void run() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                	addremove window = new addremove(user);
                    window.frame.setLocationRelativeTo(null);
                    window.frame.setResizable(false);
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //create application
    public addremove(String user) {
    	this.user = user;
        initialize();
    }

    //initializing the contents of the frame
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 355, 259);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel itemidLbl = new JLabel("ITEM ID");
        itemidLbl.setForeground(Color.BLACK);
        itemidLbl.setFont(new Font("Times New Roman", Font.BOLD, 15));
        itemidLbl.setBounds(10, 11, 125, 20);
        frame.getContentPane().add(itemidLbl);

        idTxtField = new JTextField();
        idTxtField.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        idTxtField.setBounds(10, 31, 155, 20);
        frame.getContentPane().add(idTxtField);
        idTxtField.setColumns(10);

        JLabel nameLbl = new JLabel("ITEM NAME");
        nameLbl.setForeground(Color.BLACK);
        nameLbl.setFont(new Font("Times New Roman", Font.BOLD, 15));
        nameLbl.setBounds(10, 51, 125, 20);
        frame.getContentPane().add(nameLbl);

        JButton addBtn = new JButton("Add");
        addBtn.setForeground(SystemColor.textHighlight);
        addBtn.setFont(new Font("Times New Roman", Font.BOLD, 18));
        addBtn.setBounds(10, 179, 100, 30);
        // When pressing update button, attempts to connect with usrTextField and pwdField then clear fields after.
        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addItem();
                clearFields();
            }
        });
        frame.getContentPane().add(addBtn);
        
        JButton backBtn = new JButton("Back");
        backBtn.setForeground(Color.RED);
        backBtn.setFont(new Font("Times New Roman", Font.BOLD, 18));
        backBtn.setBounds(230, 179, 100, 30);
        frame.getContentPane().add(backBtn);
        backBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	String type = checkUserType(Integer.parseInt(user));
            	if(type.equals("CLERK")) {
            		clerkUser clerkUser = new clerkUser(user);
            		clerkUser.start();
                    frame.dispose();
            	}
            	else if(type.equals("MANAGER")) {
            		managerUser managerUser = new managerUser(user);
            		managerUser.start();
                    frame.dispose();
            	}
            }
        });
        
        
        nameTxtField = new JTextField();
        nameTxtField.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        nameTxtField.setColumns(10);
        nameTxtField.setBounds(10, 71, 155, 20);
        frame.getContentPane().add(nameTxtField);
        
        JButton remBtn = new JButton("Remove");
        remBtn.setForeground(SystemColor.textHighlight);
        remBtn.setFont(new Font("Times New Roman", Font.BOLD, 18));
        remBtn.setBounds(120, 179, 100, 30);
        frame.getContentPane().add(remBtn);
        remBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                delete();
                clearFields();
            }
        });
        
        costTxtField = new JTextField();
        costTxtField.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        costTxtField.setColumns(10);
        costTxtField.setBounds(10, 112, 155, 20);
        frame.getContentPane().add(costTxtField);
        
        JLabel costLbl = new JLabel("ITEM COST");
        costLbl.setForeground(Color.BLACK);
        costLbl.setFont(new Font("Times New Roman", Font.BOLD, 15));
        costLbl.setBounds(10, 93, 125, 20);
        frame.getContentPane().add(costLbl);
        
        stockTxtField = new JTextField();
        stockTxtField.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        stockTxtField.setColumns(10);
        stockTxtField.setBounds(174, 31, 155, 20);
        frame.getContentPane().add(stockTxtField);
        
        soldTxtField = new JTextField();
        soldTxtField.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        soldTxtField.setColumns(10);
        soldTxtField.setBounds(175, 71, 155, 20);
        frame.getContentPane().add(soldTxtField);
        
        JLabel stockLbl = new JLabel("ITEM STOCK");
        stockLbl.setForeground(Color.BLACK);
        stockLbl.setFont(new Font("Times New Roman", Font.BOLD, 15));
        stockLbl.setBounds(174, 11, 125, 20);
        frame.getContentPane().add(stockLbl);
        
        JLabel soldLbl = new JLabel("ITEM SOLD");
        soldLbl.setForeground(Color.BLACK);
        soldLbl.setFont(new Font("Times New Roman", Font.BOLD, 15));
        soldLbl.setBounds(174, 51, 125, 20);
        frame.getContentPane().add(soldLbl);
    }

    public void clearFields() {
        idTxtField.setText("");
        nameTxtField.setText("");
        costTxtField.setText("");
        stockTxtField.setText("");
        soldTxtField.setText("");
    }

    public void addItem() {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Inventory", "postgres", "password");

            Statement statement;
            statement = conn.createStatement();

            statement.executeUpdate(
                    "insert into inventory (item_id, item_name, item_price, item_stock, item_sold) values ("
                            + idTxtField.getText() + ", '" + nameTxtField.getText() + "', '" + costTxtField.getText() + "', " 
                    		+ stockTxtField.getText() + ", " + soldTxtField.getText() + ")"
            );
        } catch (Exception e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(null, "Update failed, please try again.", "Update Failed", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
        }
    }
    
    public String checkUserType(int id) {
		Connection conn = null;
		String results = null;
		try {
			//testing connection
			Class.forName("org.postgresql.Driver");

			conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Inventory", "postgres", "password");

			Statement statement;
			statement = conn.createStatement();
			ResultSet resultSet;
			resultSet = statement.executeQuery(
					"SELECT USERTYPE FROM LOGIN WHERE USERID = " + id
			);
			resultSet.next();
			results = resultSet.getString(1);
			resultSet.close();
		}
		catch(Exception e) {}
		return results;
	}
    
    public void delete() {
    	Connection conn = null;
    	try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Inventory", "postgres", "password");

            Statement statement;
            statement = conn.createStatement();
            ResultSet resultSet;

            resultSet = statement.executeQuery("select exists (select * from INVENTORY where ITEM_ID = '" + idTxtField.getText() +"')");
            while(resultSet.next()) {
                if (!resultSet.getBoolean(1)) {
                    throw new SQLException();
                }
            }

            statement.executeUpdate("delete from inventory where item_id = " + idTxtField.getText());
            JOptionPane.showMessageDialog(null, "Delete Success", "Delete Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();

            statement.close();
            conn.close();
        }
        catch (SQLException e){
            JOptionPane.showMessageDialog(null, "Delete Failed", "Delete Failed", JOptionPane.INFORMATION_MESSAGE);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Delete failed", "Delete Failed", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
