package com.quiz;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class QuizListener implements ServletContextListener{
	/*
	 * we will create ans initatilize all contexts in this method 
	 * like the init should do
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		Connection con=null;
		try {
			PreparedStatement ps1=null;
			ResultSet rs=null;
			Class.forName("com.mysql.jdbc.Driver");
			con=DriverManager.getConnection("jdbc:mysql://localhost:3306/quizapp","root","root");
			ps1=con.prepareStatement("SELECT * FROM QUIZCONTACT");
			rs=ps1.executeQuery();
			if(rs.next()) {
				System.out.println("Table already exists");
			}
			else{
				System.out.println("Talble has to be created");
				//first lets create a sequence to keep track of the users who are logging in without collision
				PreparedStatement ps2=con.prepareStatement("CREATE SEQUENCE ONLINEQUIZ MINVALUE 1 MAXVALUE 9999999 INCREMENT BY 1 START WITH 1 NOCACHE NOORDER NOCYCLE");
				ps2.executeUpdate();
				//a table get filled with registeration of new user
				PreparedStatement ps3=con.prepareStatement("CREATE TABLE QUIZREGISTER(USERNAME VARCHAR(4000), USERPASS VARCHAR(4000), CATEGORY VARCHAR(4000), EMAIL VARCHAR(4000))");
				ps3.executeUpdate();
				//a table keep track of subjects of quizes
				PreparedStatement ps4=con.prepareStatement("CREATE TABLE QUIZINFO(SUBJECT VARCHAR(4000),QUIZNAME VARCHAR(4000))");
				ps4.executeUpdate();
				//a table to have or post queries on questions
				PreparedStatement ps5=con.prepareStatement("CREATE TABLE QUIZQUERY(NAME VARCHAR(4000), EMAIL VARCHAR(4000), PHONE VARCHAR(4000),QUESTION VARCHAR(4000))");
				ps5.executeUpdate();
				//a table for each question details in the quiz
				PreparedStatement ps6=con.prepareStatement("CREATE TABLE QUIZQUES(QUESTION VARCHAR(4000), OPTION1 VARCHAR(4000), OPTION2 VARCHAR(4000), OPTION3 VARCHAR(4000), OPTION4 VARCHAR(4000), ANSWER VARCHAR(4000), QUIZNAME VARCHAR(4000), QID VARCHAR(4000), DESCRIPTION VARCHAR(4000) CONSTRAINT PK PRIMARY KEY(QID) ENABLE)");
				ps6.executeUpdate();
				/*
				 * everytime you create a new quiz or quizquestion 
				 * lets create a trigger
				 */
				Statement st=con.createStatement();
				st.executeUpdate("CREATE TRIGGER BI_QUIZINFO before insert on QUIZINFO for each row begin select ONLINEQUIZ.newval into: NEW.QUIZNAME from dual;end");
				st.executeUpdate("CREATE TRIGGER BI_QUIZQUES before insert on QUIZQUES for each row begin select ONLINEQUIZ.newval into:NEW.QID from dual;end");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	/*
	 * we will ensure closing and destroying any remaining resources
	 * to avoid any unnecessary leaks and clean up mess u created
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("destroy remaining resources");
	}
}
