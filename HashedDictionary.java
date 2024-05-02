package project11hashing;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import project10dictionary.DictionaryInterface;
//https://courses.cs.washington.edu/courses/cse373/19su/projects/2/
/**
   A class that implements the ADT dictionary by using hashing and
   linear probing to resolve collisions.
   The dictionary is unsorted and has distinct search keys.
   Search keys and associated values are not null.
 
   Notes: Uses probe for add, but locate for remove and getValue.
   Uses linear probing, but includes code for quadratic probing.
   Has a display method for illustration and testing.

   @author Frank M. Carrano
   @author Timothy M. Henry
   @version 5.0
 */
public class HashedDictionary<K, V> implements DictionaryInterface<K, V>
{
   // The dictionary:
	private int numberOfEntries;
	private static final int DEFAULT_CAPACITY = 5; // Must be prime
	private static final int MAX_CAPACITY = 10000;
   
   // The hash table:
	private Entry<K, V>[] hashTable;
   private int tableSize;                         // Must be prime
   private static final int MAX_SIZE = 2 * MAX_CAPACITY;
   private boolean integrityOK = false;
	private static final double MAX_LOAD_FACTOR = 0.5; // Fraction of hash table
                                                      // that can be filled
	protected final Entry<K, V> AVAILABLE = new Entry<>(null, null);
   
	public HashedDictionary()
	{
		this(DEFAULT_CAPACITY); // Call next constructor
	} // end default constructor
   
	public HashedDictionary(int initialCapacity)
	{
      initialCapacity = checkCapacity(initialCapacity);
		numberOfEntries = 0;    // Dictionary is empty
      
      // Set up hash table:
		// Initial size of hash table is same as initialCapacity if it is prime;
		// otherwise increase it until it is prime size
		int tableSize = getNextPrime(initialCapacity);
      checkSize(tableSize);   // Check that size is not too large
		
		// The cast is safe because the new array contains null entries
      @SuppressWarnings("unchecked")
      Entry<K, V>[] temp = (Entry<K, V>[])new Entry[tableSize];
      hashTable = temp;
      integrityOK = true;
	} // end constructor

/* Implementations of methods in DictionaryInterface are here.
   . . .*/

  
   @Override
   public V add(K key, V value) 
   {
      checkIntegrity();
      if ((key == null) || (value == null))
          throw new IllegalArgumentException("Cannot add null to this dictionary.");
      int index = getHashIndex(key);
      Entry<K, V> newEntry = new Entry<>(key, value);
      // Check if the key already exists
      if (hashTable[index] == null || hashTable[index] == AVAILABLE) 
      {
          hashTable[index] = newEntry;
          numberOfEntries++;
          ensureCapacity(); // Ensure enough room for next add
          return null;
      } else 
      {
          // Replace existing value
          V oldValue = hashTable[index].getValue();
          hashTable[index].setValue(value);
          return oldValue;
      }
   }

   @Override
   public V remove(K key) 
   {
      checkIntegrity();
      int index = getHashIndex(key);
      if (hashTable[index] == null)
          return null; // Key not found
      // Key found
      V removedValue = hashTable[index].getValue();
      hashTable[index] = AVAILABLE; // Do not entirely remove, mark as AVAILABLE for probing
      numberOfEntries--;
      return removedValue;
   }

   @Override
   public V getValue(K key)
   {
      checkIntegrity();
      V result = null;
      int index = getHashIndex(key);
      if ((hashTable[index] != null) && hashTable[index]!=AVAILABLE)
      {
         result = hashTable[index].getValue(); //key found,get value
      }
      //else the key isnt found;return null;

      return result;
   } // end getValue


   

   @Override
   public boolean contains(K key) {
      checkIntegrity();
      return getValue(key) != null;
   }

   @Override
   public Iterator<K> getKeyIterator() {
      return new KeyIterator();
   }

   @Override
   public Iterator<V> getValueIterator() {
      return new ValueIterator();
   }

   @Override
   public boolean isEmpty() {
       return numberOfEntries == 0;
   }

   @Override
   public int getSize() {
      return numberOfEntries;
   }

   @Override
   public void clear() {
      Arrays.fill(hashTable, null);
      numberOfEntries = 0;
   }


   /*
   Implementations of private methods are here.
   . . . */

   private int getHashIndex(K key)
   {
      int hashIndex = key.hashCode() % hashTable.length;

      if (hashIndex < 0)
         hashIndex = hashIndex + hashTable.length;
      hashIndex = probe(hashIndex, key);

      return hashIndex;
   } // end getHashIndex
   
   private void enlargeHashTable()
   {
      Entry<K,V>[] oldTable = hashTable;
      int oldSize = hashTable.length;
      int newSize = getNextPrime(oldSize+oldSize);
      checkSize(newSize);

      // The cast is safe because the new array contains null entries
      @SuppressWarnings("unchecked")
      Entry<K, V>[] temp = (Entry<K, V>[])new Entry[newSize];
      hashTable = temp;
      numberOfEntries = 0;
      // Reset number of dictionary entries, since
      // it will be incremented by add during rehash

      // Rehash dictionary entries from old array to the new and bigger array;
      // skip elements that contain null or AVAILABLE
      for (int index = 0; index < oldSize; index++)
      {
         if ( (oldTable[index] != null) && oldTable[index] != AVAILABLE )
         {
            add(oldTable[index].getKey(), oldTable[index].getValue());
         }
      }// end for
   }// end enlargeHashTable



   private int probe (int index, K key)
   {
      ensureCapacity();
      return this.linearProbe(index, key);
     //return this.linearProbe(index, key);
   }


   private int linearProbe(int index, K key)
   {
      boolean found = false;
      int availableStateIndex = -1; // Index of first element in available state
      while ( !found && (hashTable[index] != null) )
      {
         if (hashTable[index] != AVAILABLE)
         {
            if (key.equals(hashTable[index].getKey()))
               found = true; // Key found
            else             // Follow probe sequence
               index = (index + 1) % hashTable.length; // Linear probing
         }
         else // Element in available state; skip it, but mark the first one encountered
         {
            // Save index of first element in available state
            if (availableStateIndex == -1)
               availableStateIndex = index;
            index = (index + 1) % hashTable.length;   // Linear probing
         } // end if
      } // end while
      // Assertion: Either key or null is found at hashTable[index]

      if (found || (availableStateIndex == -1) )
         return index;               // Index of either key or null
      else
         return availableStateIndex; // Index of an available element
   } // end linearProbe


   private void checkIntegrity()
    {
        if (!integrityOK)
            throw new SecurityException("hashTable object is corrupt.");
    } // end checkIntegrity

     private void ensureCapacity() //fix later
    {
      if (numberOfEntries >= hashTable.length - 1) // If array is full, double its size
      {
            // int newLength = 2 * hashTable.length;
            // checkCapacity(newLength);
            // hashTable = Arrays.copyOf(hashTable, newLength);
            enlargeHashTable();
      } // end if
    } // end ensureCapacity

    private int checkCapacity(int capacity) 
    {
        if (capacity > MAX_CAPACITY) 
        {
            throw new IllegalStateException
            ("Attempt to create a hashTable whose " + "capacity exeeds allowed " + "maximum of " + MAX_CAPACITY);
        }
        return capacity;
    }

    private void checkSize(int tableSize)
    {
        if (tableSize > MAX_SIZE) 
       {
           throw new IllegalStateException("HashTable exceeds maximum size.");
       } // end if
    } // end checkSize
 
    // Updated method to find next prime number
    private int getNextPrime(int initialCapacity)
    {
        int nextPrime = initialCapacity;
        boolean found = false;
        // Loop continuously until isPrime returns true
        while (!found)
       {
           nextPrime++;
           if (isPrime(nextPrime)) 
           {
               found = true;
           }
       }
       return nextPrime;
    }
 
    // Helper method to check if an integer is prime
    private boolean isPrime(int number)
    {
        for (int divisor = 2; divisor <= number / 2; divisor++) 
        {
            if (number % divisor == 0) 
            { // If true, number is not prime
                return false;
            }
        }
        return true; // Number is prime
    }
 
    private class KeyIterator implements Iterator<K>
   {
      private int currentIndex; // Current position in hash table
      private int numberLeft;   // Number of entries left in iteration
      
      private KeyIterator()
      {
         currentIndex = 0;
         numberLeft = numberOfEntries;
      } // end default constructor
      
      public boolean hasNext()
      {
         return numberLeft > 0;
      } // end hasNext
      
      public K next()
      {
         K result = null;
         
         if (hasNext())
         {
            // Skip table locations that do not contain a current entry
            while ( (hashTable[currentIndex] == null) || (hashTable[currentIndex] == AVAILABLE) )
            {
               currentIndex++;
            } // end while
            
            result = hashTable[currentIndex].getKey();
            numberLeft--;
            currentIndex++;
         }
         else
            throw new NoSuchElementException();
         
         return result;
      } // end next
      
      public void remove()
      {
         throw new UnsupportedOperationException();
      } // end remove
   } // end KeyIterator
   private class ValueIterator implements Iterator<V> 
   {
     private int currentIndex;  // Current position in the hashtable
 
     public ValueIterator() {
         currentIndex = 0;
     }
 
     @Override
     public boolean hasNext() {
         // Check if the current index is within the number of entries
         return currentIndex < numberOfEntries;
     }
 
     @Override
     public V next() {
         if (!hasNext()) {
             throw new NoSuchElementException("Illegal call to next(); iterator is after end of list.");
         }
         // Access the value at the current index and increment the index
         V value = hashTable[currentIndex].getValue();
         currentIndex++;
         return value;
     }
 
     @Override
     public void remove() {
         // Optionally, you can implement this method if needed
         throw new UnsupportedOperationException("Remove not supported by this iterator");
     }
   }

   @SuppressWarnings("hiding")
   protected class Entry<K, V>
   {
      /* See Listing 21-1 in Chapter 21.
      . . . */

      private K key;
      private V value;

      private Entry(K searchKey, V dataValue)
      {
         key = searchKey;
         value = dataValue;
      } // end constructor


      public K getKey() {
         return key;
      }

      // public void setKey(S key) {
      //     this.key = key;
      // }

      public V getValue() {
         return value;
      }

      public void setValue(V value) {
         this.value = value;
      }

    // The private constructor and private methods getKey, getValue, and setValue are here.
      // Their definitions are in Listing 21-1 of Chapter 21.

   } // end Entry
   
   public static void main(String[] args)
   {
      
   }

} // end HashedDictionary

