package drools;


import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import  java.lang.*;
import java.io.*;
import java.util.Date;

public class Bank {

    Double amount = 0.0;

    private static  Connection connect() {
        Connection conn = null;
        try {
            // db parameters

            String url = "jdbc:sqlite:sample.db";

            conn = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {

            System.out.println(e.getMessage());
        }

        return conn;
    }

    public double fetchAmount(String bank_id)
    {
        String sql = "SELECT amount from Bank WHERE name = ? ";

        Double amt = 0.0;
        Connection conn = connect();
        try
        {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,bank_id);
            try {
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {

                    amt = rs.getDouble("amount");
                }
            }catch (SQLException e)
            {
                e.printStackTrace();
            }

        }catch(SQLException e)
        {
            e.printStackTrace();
        }
        return amt;
    }

    public void createBank(String nationality,String bank_ID)
    {
        String sql = "INSERT INTO Bank(name,nationality,status,amount,rejections) VALUES(?,?,?,?,?);";

        String status = "not blacklisted";
        Double amount = 0.0;
        int rej = 0;

        Connection conn = connect();
        try  {

            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, bank_ID);
            pstmt.setString(2, nationality);
            pstmt.setString(3, status);
            pstmt.setDouble(4,amount);
            pstmt.setInt(5,rej);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }

    }


    public static void updateStatus(String bank_id)
    {
        String sql = "UPDATE BANK SET status = ? where name = ?;";
        String status = "blacklisted";

        Connection conn = connect();
        try  {

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setString(2,bank_id);
            pstmt.executeUpdate();
            //return 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            //return -1;
        }finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }


    public void updateAmount(String bank_id,String increment)
    {
        String sql = "UPDATE BANK SET amount = ? where name = ?;";
        Double base = this.fetchAmount(bank_id);
        Double new_bal = base +Double.parseDouble(increment);

        Connection conn = connect();
        try  {

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDouble(1, new_bal);
            pstmt.setString(2,bank_id);
            pstmt.executeUpdate();
            //return 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            //return -1;
        }finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void deleteBank()
    {
        String sql = "DELETE FROM Bank;";
        Connection conn = connect();
        try
        {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
        }catch(SQLException e)
        {
                e.printStackTrace();
        }


    }

    public void deleteTransactions() {
        String sql = "DELETE FROM Transactions;";
        Connection conn = connect();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRejections() {
        String sql = "DELETE FROM Rejections;";
        Connection conn = connect();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public String findBankStatus(String Bank_id)
    {
        String sql = "SELECT status from Bank WHERE name = ?;";

        System.out.println(Bank_id);

        Boolean stat = false;
        String temp = null;
        Connection conn = connect();
        try
        {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,Bank_id);
            try
            {
                ResultSet rs = pstmt.executeQuery();
                while(rs.next()) {

                    try {
                        temp = rs.getString("status");

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

            }catch (SQLException e)
            {
                e.printStackTrace();
            }
        } catch(SQLException e)
        {
            e.printStackTrace();
        }
        System.out.println(temp);
        return temp;
    }


    public int countRejections(String BankID)
    {
        String sql =" SELECT count(*) from Rejections where bank = ?;";
        int count = 0;
        Connection conn = connect();
        try
        {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,BankID);
            try
            {
                ResultSet rs = pstmt.executeQuery();
                while(rs.next()) {

                    try {
                            count = rs.getInt("count(*)");

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }

            }catch (SQLException e)
            {
                e.printStackTrace();
            }
        } catch(SQLException e)
        {
            e.printStackTrace();
        }

        return count;

    }


    public  void getRejectionLog() throws IOException{
            String sql = "SELECT * from Rejections;";
            File new_file = new File("rejection_log.txt");
            FileWriter fwriter = new FileWriter(new_file,true);
            PrintWriter printer = new PrintWriter(fwriter);
            Connection conn = connect();
            String transactionID,bank,sender,receiver,cat,rule;
            Double amt;
            Date ts;

        try
        {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            try
            {
                ResultSet rs = pstmt.executeQuery();
                while(rs.next()) {

                    try {
                        transactionID = rs.getString("txn");
                        bank = rs.getString("bank");
                        sender = rs.getString("senderID");
                        receiver = rs.getString("receiverID");
                        amt = rs.getDouble("amount");
                        cat = rs.getString("category");
                        rule = rs.getString("rule");
                        ts = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("last_updated"))); //Throws exception


                        // System.out.println(ts);
                        printer.append('\n');

                        printer.append("Transaction ID:"+transactionID+",BankID: "+bank+",SenderID: "+sender+",ReceiverID"+receiver+",Amount: "+amt+",Cateoory: "+cat+", Timestamp:"+ts.toString()+", Rule:"+rule+'\n');



                    } catch (SQLException | ParseException e) {
                        e.printStackTrace();
                    }

                }

            }catch (SQLException e)
            {
                e.printStackTrace();
            }
        } catch(SQLException e)
        {
            e.printStackTrace();
        }

        printer.close();



    }

    /**
     * Acceptance Log
     */
    public  void getAcceptance() throws IOException {
        String sql = "SELECT * from Transactions;";
        File new_file = new File("acceptance_log.txt");
        FileWriter fwriter = new FileWriter(new_file,true);
        PrintWriter printer = new PrintWriter(fwriter);
        Connection conn = connect();
        String transactionID,bank,sender,receiver,cat;
        Double amt;
        Date ts;

        try
        {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            try
            {
                ResultSet rs = pstmt.executeQuery();
                while(rs.next()) {

                    try {
                        transactionID = rs.getString("txn");
                        bank = rs.getString("bank");
                        sender = rs.getString("senderID");
                        receiver = rs.getString("receiverID");
                        amt = rs.getDouble("amount");
                        cat = rs.getString("category");
                        ts = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("last_updated"))); //Throws exception


                        printer.append("Transaction ID:"+transactionID+",BankID: "+bank+",SenderID: "+sender+",ReceiverID"+receiver+",Amount: "+amt+",Cateoory: "+cat+", Timestamp: "+ts);



                    } catch (SQLException | ParseException e) {
                        e.printStackTrace();
                    }

                }

            }catch (SQLException e)
            {
                e.printStackTrace();
            }
        } catch(SQLException e)
        {
            e.printStackTrace();
        }

        printer.close();



    }




}
