package project11hashing;

import java.util.Iterator;

import project10dictionary.DictionaryInterface;

public class DictionaryDemo 
{
    public static void main(String[] args)
    {
        //DictionaryInterface <String, String> addressBook = new LinkedDictionary<>();
        HashedDictionary <String, String> addressBook = new HashedDictionary<>();

        addressBook.add("555-1264", "150 Main Street");
        addressBook.add("555-8132", "75 Center Court");
        addressBook.add("555-4294", "205 Ocean Road");
        addressBook.add("555-2072", "82 Campus Way");

        System.out.println("Initial phone book entries:");
        displayAll(addressBook);

        // get a value from key
        getavalue(addressBook,"555-2072");

        //removing an entry and print
        removebykey(addressBook, "555-8132");
        removebykey(addressBook, "555-4294");

        //checksif key exists
        checkifkeyexists(addressBook,"555-1264");
        checkifkeyexists(addressBook,"000-0000");
        
        // Clear the dictionary
        addressBook.clear();
        System.out.println("\nDictionary cleared");
        
    }

    //helper
    private static void displayAll(DictionaryInterface<String, String> dictionary) 
    {
        Iterator<String> keyIterator = dictionary.getKeyIterator();
        Iterator<String> valueIterator = dictionary.getValueIterator();
        while (keyIterator.hasNext() && valueIterator.hasNext()) 
        {
            System.out.println("Key: " + keyIterator.next() + ", Value: " + valueIterator.next());
        }
    }
    
    private static void removebykey (DictionaryInterface<String, String> dictionary, String key)
    {
        System.out.println("\nRemoving " + key +":");
        dictionary.remove(key);
        displayAll(dictionary);
    }
    private static void getavalue (DictionaryInterface<String, String> dictionary, String key)
    {
        String value = dictionary.getValue(key);
        System.out.println("\nthe key:  ["+ key + "] gives us the value: [" + value + " ]");
    }
    private static void checkifkeyexists (DictionaryInterface<String, String> dictionary, String key)
    {
        System.out.println("\nDoes "+ key +" exist in the phone book? " + dictionary.contains(key));   
    }

    
}
