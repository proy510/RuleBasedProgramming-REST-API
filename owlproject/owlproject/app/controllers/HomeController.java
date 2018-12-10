package controllers;



import com.fasterxml.jackson.databind.node.ObjectNode;
import drools.Storage;
import play.libs.Json;
import play.mvc.*;
import openllet.jena.PelletReasonerFactory;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.apache.jena.reasoner.*;
import org.apache.jena.shared.JenaException;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.data.DynamicForm;
import play.data.FormFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import drools.Bank;
import drools.Transaction;
import drools.Storage;


import plugins.Drools;


import java.io.IOException;
import java.io.InputStream;

import static org.apache.jena.vocabulary.OWL.NS;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */

    @Inject
    Drools drools;

    public static OntModel ontReasoned = null;

    Storage search_storage = new Storage();
    Bank del_bank = new Bank();

 
    String source_file = "csc750.owl";
    String source_url = "http://www.semanticweb.org/proy4/ontologies/2018/9/csc750.owl"; // Remember that IRI from before?
    String NS = source_url + "#";

    static int count = 0;
    public Result index() {
        return ok(views.html.index.render());
    }


    public void reload()
    {

        // Read the ontology. No reasoner yet.
        OntModel baseOntology = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
        try
        {
            System.out.println(NS);
            System.out.println("I am trying to load the ontology.");
            InputStream in = FileManager.get().open(source_file);
            System.out.println("InputStream");
            System.out.println(in);
            try
            {
                baseOntology.read(in, null);
                search_storage.reset_list();
                del_bank.deleteBank();
                del_bank.deleteRejections();
                del_bank.deleteTransactions();


            }
            catch (Exception e)
            {
                System.out.println("I am having exception here.");
                e.printStackTrace();
            }
        }
        catch (JenaException je)
        {
            System.out.println("But I have entered here.");
            System.err.println("ERROR" + je.getMessage());
            je.printStackTrace();
            System.exit(0);
        }
        baseOntology.setNsPrefix( "csc750.owl", NS ); // Just for compact printing; doesn't really matter


        // This will create an ontology that has a reasoner attached.
        // This means that it will automatically infer classes an individual belongs to, according to restrictions, etc.
        ontReasoned = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC, baseOntology);
        System.out.println("Hello reload");
        //System.out.println(ontReasoned);
    }

    /**
     * REST API PART OF THE CODE
     */
    /**
     * This method should reset the ontology
     */
    public Result reset()
    {
        ObjectNode result = Json.newObject();
        String status = "success";
        try
        {
            if(ontReasoned == null)
            {
                reload();
            }
            else
            {
                ontReasoned = null;
                reload();
            }
        }catch(Exception e)
        {
            e.printStackTrace();
            status = "failure";
        }

        result.put("result:",status);
        return ok(result);
    }

    /**
     * This method should add bank .
     */

    public Result addBank(String nationality,String bankID)
    {
        ObjectNode result = Json.newObject();
        String s = "success";
        String status = "success";
        Individual det_unique = ontReasoned.getIndividual(NS +bankID);

        String det_unique2 = search_storage.getExisting_entities(bankID);
        System.out.println(det_unique2);
        if(det_unique == null && det_unique2.equalsIgnoreCase("Does not exist"))
        {
                System.out.println("I have entered the add Bank method.");
                Bank new_bank = new Bank();
                new_bank.createBank(nationality, bankID);
                search_storage.setExisting_banks(bankID);

                System.out.println("New bank added.");
        }
        else
        {
            status = "Individual already exists";
        }
        result.put("status:",status);
        return ok(result);

    }

    /**
     * This method should create an individual of class Merchant
     * @param uniqueID
     * @return result
     */
   public Result addMerchant(String uniqueID)
   {
       ObjectNode result = Json.newObject();
       String s = "success";
       String status = "success";
       Individual det_unique = ontReasoned.getIndividual(NS +uniqueID);
       if(det_unique == null)
       {
           try {
               System.out.println("I have entered the add merchant method.");
               //Getting the classes we need
               OntClass merchant = ontReasoned.getOntClass(NS + "Merchant");
               // Create the individuals we need.
               Individual merch = ontReasoned.createIndividual(NS +uniqueID, merchant);
               System.out.println("Merchant has been created:"+merch);
           }
           catch (JenaException je)
           {
               System.err.println("ERROR" + je.getMessage());
               je.printStackTrace();
               status = "failure";
           }
       }
       else
       {
           status = "individual already exists";
       }
       result.put("status:",status);
       return ok(result);
    }
    /**
     * This method should create an individual of the Consumer Class
     * @param uniqueID
     * @return result
     */
    public Result addConsumer(String uniqueID)
    {
        ObjectNode result = Json.newObject();
        System.out.println(uniqueID);
        String status = "success";
        Individual det_unique = ontReasoned.getIndividual(NS +uniqueID);
        if(det_unique == null)
        {
            try {
                System.out.println("I have entered the add consumer method.");
                //Getting the classes we need
                OntClass consumer = ontReasoned.getOntClass(NS + "Consumer");
                // Create the individuals we need.
                Individual cons = ontReasoned.createIndividual(NS +uniqueID, consumer);
                System.out.println("Consumer has been created:"+cons);
            }
            catch (JenaException je)
            {
                System.err.println("ERROR" + je.getMessage());
                je.printStackTrace();
                status = "failure";
            }
        }
        else
        {
                status = "individual already exists.";
        }
        result.put("status:",status);
        return ok(result);
    }

    /**
     * This method should create an individual of class Transaction
     */
    public String addTransaction(String senderID,String receiverID,String transactionID)
    {
        ObjectNode result = Json.newObject();
        String status = "success";
        Individual det_existing_sender = ontReasoned.getIndividual(NS +senderID);
        Individual det_existing_receiver = ontReasoned.getIndividual(NS +receiverID);
        Individual det_unique = ontReasoned.getIndividual(NS +transactionID);
        if((det_existing_sender !=null)&&(det_existing_receiver != null)&&(det_unique == null))
        {
            try
            {
                System.out.println("I have entered the add transaction method.");
                // First, get the classes we need
                OntClass transaction = ontReasoned.getOntClass(NS + "Transaction");
                // Get the properties we need
                OntProperty hasSender = ontReasoned.getObjectProperty(NS + "hasSender");
                OntProperty hasReceiver = ontReasoned.getObjectProperty(NS + "hasReceiver");
                // Create the individuals we need. We need a pizza and a topping.
                Individual trans1 = ontReasoned.createIndividual(NS +transactionID,transaction);
                //Transaction needs two individuals, that is two persons which should be already created in the ontology
                Individual sender = ontReasoned.getIndividual(NS+senderID);
                Individual receiver = ontReasoned.getIndividual(NS+receiverID);
                //Adding the properties hasSender and hasReceiver to the transaction
                trans1.addProperty(hasSender,sender);
                trans1.addProperty(hasReceiver,receiver);
                System.out.println("Transaction got created:"+trans1);
            }
            catch(JenaException je)
            {
                System.err.println("ERROR" + je.getMessage());
                je.printStackTrace();
                status = "failure";
            }
        }
        else if(det_unique != null)
            status = "transaction id already exists in the system.";
        if(det_existing_sender == null && det_unique == null)
                   status = "sender is not present in ontology. ";
        if(det_existing_receiver == null && det_unique == null)
                   status = "receiver is not present in ontology yet. ";
        if (det_existing_receiver == null && det_existing_sender == null && det_unique == null)
                   status = "sender and receiver not present in ontology. ";

       // result.put("status:",status);
        return status;
    }

    /**
     * This method should request to Add Transaction if everything is satisfied:
     */
    public Result requestToAddTransaction(String senderID,String receiverID,String bankID,String category,String amount,String transactionRequestID)
    {
        ObjectNode result = Json.newObject();
        String status = "failure";
        Transaction transaction_request = new Transaction(bankID,senderID,receiverID,category,transactionRequestID,amount);
        drools.kieSession.insert(transaction_request);
        drools.kieSession.fireAllRules();
        //drools.kieSession.fireUntilHalt();
        String res = transaction_request.getRuleNumber();
        System.out.println("Rule Number:"+res);
        if(res == null)
        {
            status = this.addTransaction(senderID,receiverID,transactionRequestID);
            Bank tempbank = new Bank();
            //Create transaction entry
            transaction_request.createTransaction();
            tempbank.updateAmount(bankID,amount);
            //Update bank
            result.put("status:",status);
        }
        else
        {
            res = transaction_request.getRuleNumber();
            transaction_request.createRejection();


            if(res.equalsIgnoreCase("1"))
            {
                //Update Bank status
                Bank bank_temp = new Bank();
                bank_temp.updateStatus(bankID);
            }
            else if(res.equalsIgnoreCase("3"))
            {
                System.out.println("Failed for not satisfying rule 3 for weapons");
            }
            else if(res.equalsIgnoreCase("4"))
            {
                System.out.println(" Failed for rule 4");
            }


            result.put("status:",status);
            result.put("reason Rule number:",res);
        }


        return  ok(result);
    }
    /**
     * This method should contact the reasoner to fetch whether an existing transaction is a Commercial Transaction
     */
    public Result isCommercial(String transactionID)
    {
        ObjectNode result = Json.newObject();
        String status = "false";
        Individual det_unique = ontReasoned.getIndividual(NS +transactionID);
        if(det_unique != null)
        {
            try{
                Individual transaction = ontReasoned.getIndividual(NS +transactionID);
                System.out.println("This is the individual fetched:"+transaction);
                OntClass commercialTransaction = ontReasoned.getOntClass(NS + "CommercialTransaction");
                System.out.println("Transaction is commercial:"+transaction.hasOntClass(commercialTransaction));
                status = "success";
                result.put("status:",status);
                result.put("result:",transaction.hasOntClass(commercialTransaction));
            }
            catch(JenaException je)
            {
                System.err.println("ERROR" + je.getMessage());
                je.printStackTrace();
                status = "failure";
                result.put("result:",status);
            }
        }
        else
        {
            status = "failure";
            String reason = "not a transaction";
            result.put("status:",status);
            result.put("result:",reason);
        }

        return ok(result);
    }

    /**
     * This method should contact the reasoner to fetch whether an existing transaction is a Personal Transaction.
     */
    public Result isPersonal(String transactionID)
    {
        ObjectNode result = Json.newObject();
        String status = "false";
        Individual det_unique = ontReasoned.getIndividual(NS +transactionID);

        if(det_unique != null)
        {
            try{
                Individual transaction = ontReasoned.getIndividual(NS +transactionID);
                System.out.println("This is the individual fetched:"+transaction);
                OntClass personalTransaction = ontReasoned.getOntClass(NS + "PersonalTransaction");
                System.out.println("Transaction is personal:"+transaction.hasOntClass(personalTransaction));
                status = "success";
                result.put("status:",status);
                result.put("result:",transaction.hasOntClass(personalTransaction));
            }
            catch(JenaException je)
            {
                System.err.println("ERROR" + je.getMessage());
                je.printStackTrace();
                status = "failure";
                result.put("result:",status);
            }
        }
        else
        {
            status = "failure";
            String reason = "not a transaction";
            result.put("status:",status);
            result.put("result:",reason);
        }


        return ok(result);
    }
    /**
     * This method should contact the reasoner to fetch whether an existing transaction is a Purchase Transaction
     */
    public Result isPurchase(String transactionID)
    {
        ObjectNode result = Json.newObject();
        String status = "false";
        Individual det_unique = ontReasoned.getIndividual(NS +transactionID);

        if(det_unique != null)
        {
            try{
                Individual transaction = ontReasoned.getIndividual(NS +transactionID);
                System.out.println("This is the individual fetched:"+transaction);
                OntClass purchaseTransaction = ontReasoned.getOntClass(NS + "PurchaseTransaction");
                System.out.println("Transaction is purchase:"+transaction.hasOntClass(purchaseTransaction));
                status = "success";
                result.put("status:",status);
                result.put("result:",transaction.hasOntClass(purchaseTransaction));
            }
            catch(JenaException je)
            {
                System.err.println("ERROR" + je.getMessage());
                je.printStackTrace();
                status = "failure";
                result.put("result:",status);
            }
        }
        else
        {
            status = "failure";
            String reason = "not a transaction";
            result.put("status:",status);
            result.put("result:",reason);
        }


        return ok(result);
    }
    /**
     * This method should contact the reasoner to fetch whether an existing transaction is a Refund Transaction
     */
    public Result isRefund(String transactionID)
    {
        ObjectNode result = Json.newObject();
        String status = "false";
        Individual det_unique = ontReasoned.getIndividual(NS +transactionID);

        if(det_unique != null)
        {
            try{
                Individual transaction = ontReasoned.getIndividual(NS +transactionID);
                System.out.println("This is the individual fetched:"+transaction);
                OntClass refundTransaction = ontReasoned.getOntClass(NS + "RefundTransaction");
                System.out.println("Transaction is refund:"+transaction.hasOntClass(refundTransaction));
                status = "success";
                result.put("status:",status);
                result.put("result:",transaction.hasOntClass(refundTransaction));
            }
            catch(JenaException je)
            {
                System.err.println("ERROR" + je.getMessage());
                je.printStackTrace();
                status = "failure";
                result.put("result:",status);
            }
        }
        else
        {
            status = "failure";
            String reason = "not a transaction";
            result.put("status:",status);
            result.put("result:",reason);
        }
        return ok(result);
    }


    /**
     * This is for bank blacklisted
     */
    public Result isBlacklisted(String bankID)
    {
        ObjectNode result = Json.newObject();
        Bank new_bank = new Bank();
        String temp = new_bank.findBankStatus(bankID);
        String status = null, reason =  null;
        if(temp.equalsIgnoreCase("blacklisted"))
        {
            status = "success";
            reason = "true";
            result.put("status:",status);
            result.put("result:",reason);
        }
        else
        {
            status = "failure";
            reason = "not a bank";

            result.put("status:",status);
            result.put("reason:",reason);
        }

        return ok(result);
    }
    /**
     * This method is for Bank ID Rejection count
     */
    public Result  getRejCount(String bankID)
    {
            ObjectNode result = Json.newObject();
            Bank nb1 = new Bank();
            int count = -1;
            String stat = null,status = null, reason = null;
            count = nb1.countRejections(bankID);
            if(count == 0)
            {
                stat = nb1.findBankStatus(bankID);
                if(stat == null)
                {
                    status = "failure";
                    reason = "not a bank";

                    result.put("status:",status);
                    result.put("reason:",reason);
                }
                else
                {
                    status = "success";
                    result.put("status:",status);
                    result.put("rejections:",count);
                }
            }
            else
            {
                status = "success";
                result.put("status:",status);
                result.put("rejections:",count);
            }

            return ok(result);
    }
    /**
     * This method should contact the reasoner to fetch whether an existing merchant is trusted or not.
     */
    public Result isTrusted(String merchantID)
    {
        ObjectNode result = Json.newObject();
        String status = "false";
        System.out.println("Home Controller:");
        System.out.println(ontReasoned);
        Individual det_unique = ontReasoned.getIndividual(NS +merchantID);

        if(det_unique != null)
        {
            try{
                Individual merchant = ontReasoned.getIndividual(NS +merchantID);
                System.out.println("This is the individual fetched:"+merchantID);
                OntClass trusted_merchant = ontReasoned.getOntClass(NS + "TrustedMerchant");
                OntClass consumer = ontReasoned.getOntClass(NS+"Consumer");
                if(merchant.hasOntClass(consumer) == true)
                {
                    System.out.println("Not a merchant");
                    status = "not a merchant";
                    String reason = "failure";
                    result.put("status",reason);
                    result.put("reason:",status);
                }
                else
                {
                    System.out.println("Merchant is trusted:"+merchant.hasOntClass(trusted_merchant));
                    status = "success";
                    result.put("status:",status);
                    result.put("result:",merchant.hasOntClass(trusted_merchant));
                }
            }
            catch(JenaException je)
            {
                System.err.println("ERROR" + je.getMessage());
                je.printStackTrace();
                status = "failure";
                result.put("result:",status);
            }

        }
        else
        {
            status = "person id does not exist in system.";
            result.put("result:",status);
        }
        System.out.println(result);
        return ok(result);
    }


    /**
     * For rules purpose isTrusted
     */

    public String toCheckTrusted(String merchantID)
    {
        ObjectNode result = Json.newObject();
        String status = "false";
        //System.out.println("Home Controller:");
        //System.out.println(ontReasoned);
        Individual det_unique = ontReasoned.getIndividual(NS +merchantID);

        if(det_unique != null)
        {
            try{
                Individual merchant = ontReasoned.getIndividual(NS +merchantID);
                System.out.println("This is the individual fetched:"+merchantID);
                OntClass trusted_merchant = ontReasoned.getOntClass(NS + "TrustedMerchant");
                OntClass consumer = ontReasoned.getOntClass(NS+"Consumer");
                if(merchant.hasOntClass(consumer) == true)
                {
                    System.out.println("Not a merchant");
                    status = "not a merchant";
                    result.put("reason:",status);
                }
                else
                {
                    Boolean temp = merchant.hasOntClass(trusted_merchant);
                    System.out.println("Merchant is trusted:"+merchant.hasOntClass(trusted_merchant));
                    result.put("result:",merchant.hasOntClass(trusted_merchant));
                    if(temp == false)
                        status = "failure";
                    else
                        status = "success";
                }
            }
            catch(JenaException je)
            {
                System.err.println("ERROR" + je.getMessage());
                je.printStackTrace();
                status = "failure";
                result.put("result:",status);
            }

        }
        else
        {
            status = "person id does not exist in system.";
            result.put("result:",status);
        }
        System.out.println(result);
        return status;
    }

    public Result getRejectionLog() throws IOException {
        ObjectNode result = Json.newObject();
        Bank newb2 = new Bank();
        newb2.getRejectionLog();

        String status = "rejection log generated";
        result.put("status:",status);
        return  ok(result);
    }

    public Result getAcceptanceLog() throws IOException {
        ObjectNode result = Json.newObject();
        Bank newb3 = new Bank();
        newb3.getAcceptance();

        String status = "acceptance log generated";
        result.put("status:",status);
        return  ok(result);
    }





}
