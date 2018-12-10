package drools;

import java.sql.*;
import java.util.*;
import  java.lang.*;

import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.HomeController;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.shared.JenaException;
import play.libs.Json;
import play.mvc.Result;

import static org.apache.jena.vocabulary.OWL.NS;

public class Transaction {

    public String bank_id;
    public String sender_id;
    public String receiver_id;
    public String category;
    public String transaction_id;
    public String amount;
    String rule_no = null;
    //private OntModel ontReasoned;

    public Transaction(String bank_id, String sender_id, String receiver_id, String category, String transaction_id,String amount)
    {
       this.bank_id = bank_id;
       this.sender_id = sender_id;
       this.receiver_id = receiver_id;
       this.category = category;
       this.transaction_id = transaction_id;
       this.amount = amount;
    }

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


    public void createTransaction()
    {
        String sql = "INSERT INTO TRANSACTIONS(txn,bank,senderID,receiverID,amount,category)" +
                " values(?,?,?,?,?,?);";

        Double amt = Double.parseDouble(this.amount);
        Connection conn = connect();
        try  {

            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, this.transaction_id);
            pstmt.setString(2, this.bank_id);
            pstmt.setString(3, this.sender_id);
            pstmt.setString(4,this.receiver_id);
            pstmt.setDouble(5,amt);
            pstmt.setString(6,this.category);
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


    public void createRejection()
    {


        String rule = this.getRuleNumber();
        String sql = "INSERT INTO REJECTIONS(txn,bank,senderID,receiverID,amount,category,rule)" +
                " values(?,?,?,?,?,?,?);";

        Double amt = Double.parseDouble(this.amount);
        Connection conn = connect();
        try  {
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, this.transaction_id);
            pstmt.setString(2, this.bank_id);
            pstmt.setString(3, this.sender_id);
            pstmt.setString(4,this.receiver_id);
            pstmt.setDouble(5,amt);
            pstmt.setString(6,this.category);
            pstmt.setString(7,rule);
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

    public Boolean checkAverageAmount(String Bank_id)
    {
        Boolean stat = false;
        Double amt = 0.0;

        Bank nb = new Bank();
        amt = nb.fetchAmount(Bank_id);

        int temp = this.fetchCount(Bank_id);
        double average = amt/temp;

        if(temp > 0)
        {
            if(Double.parseDouble(amount)> 10*average)
                stat = false;
            else
                stat = true;
        }

        if(temp == 0)
        {
            stat = true;
        }

        return stat;
    }


    public int fetchCount(String Bank_id)
    {

        String sql = "SELECT count(*) from Transactions WHERE bank =?;";
        int temp = -1;
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
                        temp = rs.getInt("count(*)");

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

        System.out.println("Count:"+temp);

        return temp;


    }


    public Boolean checkConsecutiveRejections(String Bank_id)
    {
        Boolean stat = false;

        String sql = "SELECT id,count(*) from Rejections where bank = ? group by id;";

        int id = 0, count1 = 0,id_prev = 0;
        Connection conn = connect();
        try
        {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,Bank_id);
            try
            {
                ResultSet rs = pstmt.executeQuery();
                while(rs.next()) {
                    stat = false;
                    try {


                        id = rs.getInt("id");

                        if(id_prev >0 && count1<3)
                        {
                            if(id == id_prev+1)
                            {
                                stat = true;
                                count1++;
                            }

                        }

                        id_prev = id;

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

        if(stat == true)
        {
            Bank nb = new Bank();
            nb.updateStatus(Bank_id);
        }


        return stat;

    }

    public Boolean findStatusOfBank(String Bank_id)
    {

        Bank nb = new Bank();
        String temp = nb.findBankStatus(Bank_id);
        Boolean stat = false;
       System.out.println(temp);
        if(temp.equalsIgnoreCase("blacklisted"))
            stat = true;
        return stat;
    }



    public Boolean checkPercentage(String Bank_id,String s_id,String r_id)
    {
        Boolean stat = false;
        String status1 = null, status2 = null;
        Double count;
        count = Double.valueOf(this.fetchCount(Bank_id));
        System.out.println("Denominator:"+count);
        double percen =  0.0;
        HomeController hnew = new HomeController();

        String sql = "SELECT senderID,receiverID from Transactions WHERE bank = ?;";

            System.out.println(Bank_id);
            String temp = null,sender = null,receiver = null;
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
                            sender = rs.getString("senderID");
                            receiver = rs.getString("receiverID");
                            status1 = hnew.toCheckTrusted(sender);
                            status2 = hnew.toCheckTrusted(receiver);
                            if(status1.equalsIgnoreCase("success") || status2.equalsIgnoreCase("success"))
                            {
                                percen++;
                            }

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
            Double result = 0.0;

            if(count!=0)
                result = count/percen;

            System.out.println("AM I NAN?");
            System.out.println(result);

            if(result <0.25)
            {
               status1 = hnew.toCheckTrusted(s_id);
               status2 = hnew.toCheckTrusted(r_id);

               if(status1.equalsIgnoreCase("success")||status2.equalsIgnoreCase("success"))
               {
                    percen++;
               }
               if(count!=0)
                result = count/percen;
               else
                   stat = true;

            }

        if(result >=0.25)
        {
            stat = true;
        }
        return stat;
    }

    /*
    * Weapon
     */

    public Boolean checkWeapon(String bank_id,String sender_id,String receiver_id)
    {
        String sql = "SELECT nationality from Bank WHERE name = ?;";


        Boolean fin = false;
        Boolean stat1= false,stat2 = false ,stat3 = false;
        String temp = null;
        Connection conn = connect();
        try
        {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,bank_id);
            try
            {
                ResultSet rs = pstmt.executeQuery();
                while(rs.next()) {

                    try {
                        temp = rs.getString("nationality");
                        System.out.println(temp);

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

        if(temp.equalsIgnoreCase("local"))
        {
            HomeController home = new HomeController();
            System.out.println(sender_id);
            System.out.println(receiver_id);

            String status = home.toCheckTrusted(sender_id);
            String status2 = home.toCheckTrusted((receiver_id));

            System.out.println("Hello status sender1:"+status);
            System.out.println("Hello status receiver1:"+status2);
            if(status.equalsIgnoreCase("success") && status2.equalsIgnoreCase("success"))
                fin = true;
            if(fin == false)
                System.out.println("Failure for non trusted.");

        }
        else
        {
            System.out.println("Failure for international");
            fin =  false;
        }

        return fin;
    }





    /*public Boolean checkWeaponTransacAcceptability(String category,String bank_id,String sender_id,String receiver_id)
    {

        System.out.println("I HAVE ENTERED HEREEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
        String sql = "SELECT nationality from Bank WHERE name = ?;";


        Boolean stat_fin = false;
        Boolean stat1= false,stat2 = false ,stat3 = false;
        String temp = null;
        Connection conn = connect();
        try
        {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,bank_id);
            try
            {
                ResultSet rs = pstmt.executeQuery();
                while(rs.next()) {

                    try {
                        temp = rs.getString("nationality");
                        System.out.println(temp);

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
        if(temp.equalsIgnoreCase("local"))
            stat1 = true;
        HomeController home = new HomeController();
        System.out.println(sender_id);
        System.out.println(receiver_id);

        String status = home.toCheckTrusted(sender_id);
         String status2 = home.toCheckTrusted((receiver_id));

         System.out.println("Hello status sender1:"+status);
        System.out.println("Hello status receiver1:"+status2);

       /*  if(status.equalsIgnoreCase("success")&& status2.equalsIgnoreCase("success"))
         {
             stat2 = true;
         }

       // String status = this.isTrusted()
        if(stat1 == true)
        {
            System.out.println("I am here");
            if(stat2 == true)
                stat_fin = true;
            else
                stat_fin = false;
        }

        if(category.equalsIgnoreCase("Weapons") && temp.equalsIgnoreCase("Local"))
        {
            if(stat2 == true)
                stat_fin = true;
        }
        else if(category.equalsIgnoreCase("Weapons") && temp.equalsIgnoreCase("international"))
        {
            stat_fin = false;
        }
        else  if(!category.equalsIgnoreCase("Weapons"))
        {
            stat_fin = false;
        }
        return stat_fin;


    }*/

    public Boolean compareAmount(String amount)
    {
        Double amt = Double.parseDouble(amount);
        Boolean stat1 = false;
        if(amt > 100000)
            stat1 = true;
        return stat1;
    }


    public Boolean checkAcceptabilityGreaterAmount(String sender_id,String receiver_id)
    {
        //Double amt = Double.parseDouble(amount);
        Boolean  stat2 = false;


        HomeController home1 = new HomeController();
        Boolean stat = false;
        String status1 = home1.toCheckTrusted(sender_id);
        String status2 = home1.toCheckTrusted(receiver_id);
        if(status1.equalsIgnoreCase("success") || status2.equalsIgnoreCase("success"))
        {
            stat2 = true;
        }

        return stat2;
    }


        public void sendRuleNumberForFailure(String rule)
    {

        System.out.println("RUle number received:"+rule);
        this.rule_no = rule;

    }

    public String getRuleNumber()
    {
        System.out.println("Sending:"+rule_no);
        return this.rule_no;
    }



}
