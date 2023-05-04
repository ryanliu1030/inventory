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

public class sell extends Thread {
	public String user;
    private JFrame frame;
    private JTextField idTxtField;
    private JTextField soldTxtField;

    @Override
    public void run() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                	sell window = new sell(user);
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
    public sell(String user) {
    	this.user = user;
        initialize();
    }

    //initializing the contents of the frame
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 250, 180);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel itemidLbl = new JLabel("ITEM ID");
        itemidLbl.setForeground(Color.BLACK);
        itemidLbl.setFont(new Font("Times New Roman", Font.BOLD, 15));
        itemidLbl.setBounds(10, 11, 125, 20);
        frame.getContentPane().add(itemidLbl);

        idTxtField = new JTextField();
        idTxtField.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        idTxtField.setBounds(10, 31, 210, 20);
        frame.getContentPane().add(idTxtField);
        idTxtField.setColumns(10);

        JLabel soldLbl = new JLabel("AMOUNT SOLD");
        soldLbl.setForeground(Color.BLACK);
        soldLbl.setFont(new Font("Times New Roman", Font.BOLD, 15));
        soldLbl.setBounds(10, 51, 125, 20);
        frame.getContentPane().add(soldLbl);

        JButton updateBtn = new JButton("Update");
        updateBtn.setForeground(SystemColor.textHighlight);
        updateBtn.setFont(new Font("Times New Roman", Font.BOLD, 18));
        updateBtn.setBounds(10, 102, 100, 30);
        // When pressing update button, attempts to connect with usrTextField and pwdField then clear fields after.
        updateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connect();
                clearFields();
            }
        });
        frame.getContentPane().add(updateBtn);
        
        JButton backBtn = new JButton("Back");
        backBtn.setForeground(Color.RED);
        backBtn.setFont(new Font("Times New Roman", Font.BOLD, 18));
        backBtn.setBounds(120, 102, 100, 30);
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
        
        
        soldTxtField = new JTextField();
        soldTxtField.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        soldTxtField.setColumns(10);
        soldTxtField.setBounds(10, 71, 210, 20);
        frame.getContentPane().add(soldTxtField);
    }

    public void clearFields() {
        idTxtField.setText("");
        soldTxtField.setText("");
    }

    public void connect() {
        Connection conn = null;
        try {
            //testing connection
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Inventory", "postgres", "password");

            Statement statement;
            statement = conn.createStatement();
            ResultSet resultSet;

            resultSet = statement.executeQuery(
                    "SELECT ITEM_ID, ITEM_NAME, ITEM_PRICE, ITEM_STOCK, ITEM_SOLD FROM INVENTORY WHERE ITEM_ID = " + idTxtField.getText()
            );

            int sold = Integer.parseInt(soldTxtField.getText());

            try {
                while (resultSet.next()) {
                    int id = resultSet.getInt("item_id");
                    int stock = resultSet.getInt("item_stock");
                    int soldold = resultSet.getInt("item_sold");
                    // If id exists, check for stock v sell for validity and sell.
                    if (Integer.parseInt(idTxtField.getText()) == id) {
                        if(sold <= stock) {
                        	statement.executeUpdate(
                                    "UPDATE INVENTORY SET item_stock='" + (stock - sold) + "' WHERE item_id = " + idTxtField.getText()
                            );
                        	statement.executeUpdate(
                                    "UPDATE INVENTORY SET item_sold='" + (soldold + sold) + "' WHERE item_id = " + idTxtField.getText()
                            );
                        	resultSet.close();
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
                        // Else, fail.
                        else {
                        	JOptionPane.showMessageDialog(null, "Update failed, please try again.", "Update Failed", JOptionPane.INFORMATION_MESSAGE);
                            sell sell = new sell(user);
                            sell.start();
                            return;
                        }	
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }     
            resultSet.close();
            statement.close();
            conn.close();
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
}
