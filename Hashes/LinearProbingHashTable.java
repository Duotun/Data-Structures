package projects.phonebook.hashes;
import projects.phonebook.utils.KVPair;
import projects.phonebook.utils.PrimeGenerator;

/**
 * <p>{@link LinearProbingHashTable} is an Openly Addressed {@link HashTable} implemented with <b>Linear Probing</b> as its
 * collision resolution strategy: every key collision is resolved by moving one address over. It is
 * the most famous collision resolution strategy, praised for its simplicity, theoretical properties
 * and cache locality. It <b>does</b>, however, suffer from the &quot; clustering &quot; problem:
 * collision resolutions tend to cluster collision chains locally, making it hard for new keys to be
 * inserted without collisions. {@link QuadraticProbingHashTable} is a {@link HashTable} that
 * tries to avoid this problem, albeit sacrificing cache locality.</p>
 *
 * @author Moweizi Xia
 *
 * @see HashTable
 * @see SeparateChainingHashTable
 * @see QuadraticProbingHashTable
 * @see CollisionResolver
 */
public class LinearProbingHashTable implements HashTable{

    private KVPair[] table;
    private PrimeGenerator primeGenerator;
    private int count = 0;

    private int hash(String key){
        return (key.hashCode() & 0x7fffffff) % table.length;
    }

    private void enlarge(){
      KVPair[] temp = new KVPair[primeGenerator.getNextPrime()];
      int index;
      
      for (int i = 0; i < table.length; i++) {
    	  if (table[i] != null) {
    		String key = table[i].getKey();
    	  	index = hashNew(key,temp.length);
    	  
    	  	while(temp[index] != null) {
  				if (index == temp.length - 1) index = 0;
  				else index++;
    	  	}
    	  	temp[index] = table[i];
    	  }
      }
      table = temp;
    }

    private void shrink(){
        KVPair[] temp = new KVPair[primeGenerator.getPreviousPrime()];
        int index;
        
        for (int i = 0; i < table.length; i++) {
        	if (table[i] != null) {
        		String key = table[i].getKey();
        		index = hashNew(key,temp.length);
        	
        		while(temp[index] != null) {
        			if (index == temp.length - 1) index = 0;
        			else index++;
        		}
        		temp[index] = table[i];
        	}
        }
        table = temp;
    }

    private int hashNew(String key, int length){
        return (key.hashCode() & 0x7fffffff) % length;
    }

    /**
     *  Default constructor. Initializes the internal storage with a size equal to the default of {@link PrimeGenerator}.
     *  This constructor is <b>given to you: DO NOT EDIT IT.</b>
     */
    public LinearProbingHashTable(){
        primeGenerator = new PrimeGenerator();
        table = new KVPair[primeGenerator.getCurrPrime()];
        count = 0;
    }

    /**
     * Inserts the pair &lt;key, value&gt; into <tt>this</tt>. The container should <b>not</b> allow for <tt>null</tt>
     * keys and values, and we <b>will</b> test if you are throwing a {@link IllegalArgumentException} from your code
     * if this method is given <tt>null</tt> arguments! It is important that we establish that no <tt>null</tt> entries
     * can exist in our database because the semantics of {@link #get(String)} and {@link #remove(String)} are that they
     * return <tt>null</tt> if, and only if, their <tt>key</tt> parameter is null. This method is expected to run in <em>amortized
     * constant time</em>.
     *
     * Instances of {@link LinearProbingHashTable} will follow the writeup's guidelines about how to internally resize
     * the hash table when the capacity exceeds 50&#37;
     * @param key The record's key.
     * @param value The record's value.
     * @throws IllegalArgumentException if either argument is null.
     */
    @Override
    public void put(String key, String value) {
        if (key == null || value == null) throw new IllegalArgumentException();
        
        float capacity = (count * 100.0f) / table.length;
        
        if (capacity >= 50.0) enlarge();
        
        int index = hash(key);

        while (table[index] != null) {
			if (index == table.length - 1) index = 0;
			else index++;
        }
        //System.out.println(key + " " + index);
        table[index] = new KVPair(key, value);
        count++;

    }
    
    public void reput(String key, String value) {
        
        int index = hash(key);

        while(table[index] != null) {
			if (index == table.length - 1) index = 0;
			else index++;
        }
        table[index] = new KVPair(key, value);
    }

    @Override
    public String get(String key) {
        if (key == null || !containsKey(key)) return null;
        
        int index = hash(key);
        
        while(table[index]!= null) {
        	if (table[index].getKey().equals(key)) return table[index].getValue();
        	else {
    			if (index == table.length - 1) index = 0;
    			else index++;
        	}
        }
        return null;
    }


    /**
     * <b>Return</b> and <b>remove</b> the value associated with <tt>key</tt> in the {@link HashTable}. If <tt>key</tt> does not exist in the database
     * or if <tt>key = null</tt>, this method returns <tt>null</tt>. This method is expected to run in <em>amortized constant time</em>.
     *
     * Instances of {@link LinearProbingHashTable} will follow the writeup's guidelines about how to internally resize
     * the hash table when the capacity drops below 50&#37;
     * @param key The key to search for.
     * @return The associated value if <tt>key</tt> is non-<tt>null</tt> <b>and</b> exists in our database, <tt>null</tt>
     * otherwise.
     */
    @Override
    public String remove(String key) {
    	if (key == null || !containsKey(key)) return null;
    	
    	int index = hash(key);
    	boolean resize = false;
    	float capacity = (count * 100.0f) / table.length;
    	
    	if (capacity <= 50.0) resize = true;
    	
    	while (!table[index].getKey().equals(key)) {
    		if (index == table.length - 1) index = 0;
    		else index++;
    	}
    	
    	String target = table[index].getValue();
    	table[index] = null;

    	if (index == table.length - 1) index = 0;
    	else index++;
    	
    	while (table[index] != null) {
    		String temp = table[index].getKey();
    		String temp2 = table[index].getValue();
    		table[index] = null;
    		this.reput(temp, temp2);
    		if (index == table.length - 1) index = 0;
    		else index++;
    	}
    	
    	if (resize) shrink();
    	count--;
    	
    	return target;
    }

    public void print() {
    	for (int i = 0; i < table.length; i++) {
    		if (table[i] == null) System.out.print("null ");
    		else System.out.print(table[i].getKey() + " ");
    	}
    	System.out.print("\n");
    }
    
    @Override
    public boolean containsKey(String key) {
    	if (key == null) throw new IllegalArgumentException();
    	
    	int index = hash(key);
    	
    	while (table[index] != null) {
    		if (table[index].getKey().equals(key)) return true;
    		else {
    			if (index == table.length - 1) index = 0;
    			else index++;
    		}
    	}
    	return false;
    }  

    @Override
    public boolean containsValue(String value) {
    	if (value == null) throw new IllegalArgumentException();
    	
    	for (int i = 0; i < table.length; i++) {
    		if (table[i].getValue().equals(value)) return true;
    	}
    	return false;
    }

    @Override
    public int size() {
        return count;

    }

    @Override
    public int capacity() {
        return table.length;

    }
}
