import org.postgresql.util.PSQLException;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.SystemColor;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPasswordField;
import java.awt.Color;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class login extends Thread {

	private JFrame frame;
	private JTextField usrTextField;
	private final JPasswordField pwdField = new JPasswordField();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					login window = new login();
					window.frame.setLocationRelativeTo(null);
					window.frame.setResizable(false);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	@Override
	public void run() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					login window = new login();
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
	public login() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 320, 160);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel usrnameLbl = new JLabel("USERNAME");
		usrnameLbl.setForeground(Color.BLACK);
		usrnameLbl.setFont(new Font("Times New Roman", Font.BOLD, 18));
		usrnameLbl.setBounds(10, 10, 125, 20);
		frame.getContentPane().add(usrnameLbl);

		JLabel pwdLbl = new JLabel("PASSWORD");
		pwdLbl.setForeground(Color.BLACK);
		pwdLbl.setFont(new Font("Times New Roman", Font.BOLD, 18));
		pwdLbl.setBounds(10, 65, 125, 20);
		frame.getContentPane().add(pwdLbl);

		usrTextField = new JTextField();
		usrTextField.setFont(new Font("Times New Roman", Font.PLAIN, 18));
		usrTextField.setBounds(10, 35, 175, 20);
		frame.getContentPane().add(usrTextField);
		usrTextField.setColumns(10);

		JButton loginBtn = new JButton("LOGIN");
		loginBtn.setForeground(SystemColor.textHighlight);
		loginBtn.setFont(new Font("Times New Roman", Font.BOLD, 18));
		loginBtn.setBounds(194, 80, 100, 30);
		// When pressing login button, attempts to connect with usrTextField and pwdField then clear fields after.
		loginBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connect();
				clearFields();
			}
		});
		frame.getContentPane().add(loginBtn);

		// Pressing enter while in the password field will do the same action as pressing login button, for convenience.
		pwdField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					connect();
					clearFields();
				}
			}
		});
		pwdField.setFont(new Font("Times New Roman", Font.PLAIN, 18));
		pwdField.setBounds(10, 90, 175, 20);
		frame.getContentPane().add(pwdField);
	}

	public void clearFields() {
		usrTextField.setText("");
		pwdField.setText("");
	}

	public void connect() {
		Connection conn = null;
		try {
        	/* Sein's default code
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/DBNAME",
                "DBUSER", "DBPASS");
            */

			//testing connection
			Class.forName("org.postgresql.Driver");

			conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Inventory", "postgres", "password");


			/*
			 * Connection is simple and can be altered, right now it is set so it connects to the MySQL host machine within
			 * the same network. I.E. Laptop running MySQL server, another computer running this program can connect to that
			 * laptop to access the database if it is within the local area network. If there is an option to change this so
			 * it can connect further without port restrictions, this should be changed.
			 *
			 * localhost should be the ip address of the host computer.
			 *
			 * DBNAME is the name of the database accessed.
			 * DBUSER is the login username for that database.
			 * DBPASS is the login password for that database.
			 */

			Statement statement;
			statement = conn.createStatement();
			ResultSet resultSet;

			// If the input of username contains illegal characters (* and ;), removes characters and doesn't test check, prevent injection.
			if(usrTextField.getText().contains("*") || usrTextField.getText().contains(";")) {
				JOptionPane.showMessageDialog(null, "Invalid characters in username, try again.", "Invalid Characters", JOptionPane.INFORMATION_MESSAGE);
				clearFields();
				return;
			}

			// USERS is the filler name for the user data. Should the name change, change this statement query.
			// Finds username from user field in database.

			resultSet = statement.executeQuery(
					"SELECT USERID, PWD, USERTYPE FROM LOGIN WHERE USERID = " + usrTextField.getText()
			);

			// Check user and pass combination, get results.
			String[] results = checkUser(resultSet);

			// If login was successful, close login window and open corresponding user window.
			if(results[0].equals("TRUE")) {
				if(results[1].equals("CLERK")) {
					clerkUser clerkUser = new clerkUser(results[2]);
					clerkUser.user = results[2];
					clerkUser.start();
					frame.dispose();
				}
				else if(results[1].equals("MANAGER")) {
					managerUser managerUser = new managerUser(results[2]);
					managerUser.user = results[2];
					managerUser.start();
					frame.dispose();
				}
			}
			// Else, return error message.
			else {
				JOptionPane.showMessageDialog(null, "Login failed, please try again.", "Login Failed", JOptionPane.INFORMATION_MESSAGE);
			}

			resultSet.close();
			statement.close();
			conn.close();
		}
		catch(IllegalArgumentException e){
			e.printStackTrace();
		}
		catch (Exception e) {
			System.out.println(e);
		}

	}

	// Checks user and pass from fields on DB user information.
	public String[] checkUser(ResultSet resultSet) {
		// Result set contains whether user and pass were successful and the type of user (Student, Teacher, Admin)
		String[] results = new String[3];
		try {
			while (resultSet.next()) {
				// Again, change COLUMN QUERY if columns are no longer named the same respectively.
				String username = resultSet.getString("userid");
				String password = resultSet.getString("pwd");
				// If user exists, check password.
				if(username.equals(usrTextField.getText())) {
					// If user and password matches, update resultSet.
					String pwd = new String(pwdField.getPassword());
					if(password.equals(pwd)) {
						results[0] = "TRUE";
						// USERTYPE is COLUMN name for user type (Student, Teacher, Admin), change if name changes.
						results[1] = resultSet.getString("usertype");
						// Get USERNAME
						results[2] = resultSet.getString("userid");
					}
				}
			}
		}
		catch (Exception e) {
			System.out.println(e);
		}

		// If user and pass was not found, login was unsuccessful.
		if(results[0] == null)
			results[0] = "FALSE";

		return results;
	}
}
