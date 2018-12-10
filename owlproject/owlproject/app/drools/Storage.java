package drools;

import java.util.ArrayList;


public class Storage {

    public static ArrayList<String> existing_entities = new ArrayList<String>();




    public void setExisting_banks(String uniqueID) {
        existing_entities.add(uniqueID);
        System.out.println("Here adding bank");
        existing_entities.size();
    }

    public String getExisting_entities(String uniqueID)
    {
        String value = "Does not exist";
        System.out.println(existing_entities.size());
        Boolean b = existing_entities.contains(uniqueID);
        System.out.println(b);
        if(existing_entities.contains(uniqueID))
        {
            System.out.println("I am here");
            value = "Exists";
        }
        return value;

    }

    public void reset_list()
    {
        existing_entities.clear();
    }

}
