package projects.phonebook.hashes;

import projects.phonebook.utils.KVPair;
import projects.phonebook.utils.PrimeGenerator;

/**
 * <p>@link QuadraticProbingHashTable} is an Openly Addressed {@link HashTable} which uses <b>Quadratic
 * Probing</b> as its collision resolution strategy. Quadratic Probing differs from <b>Linear</b> Probing
 * in that collisions are resolved by taking &quot; jumps &quot; on the hash table, the length of which
 * determined by an increasing polynomial factor. For example, during a key insertion which generates
 * several collisions, the first collision will be resolved by moving 1^2 = 1 positions over from
 * the originally hashed address (like Linear Probing), the second one will be resolved by moving
 * 2^2 = 4 positions over from our hashed address, the third one by moving 3^2 = 9 positions over, etc.
 * </p>
 *
 * <p>By using this collision resolution technique, {@link QuadraticProbingHashTable} aims to get rid of the
 * &quot;key clustering &quot; problem that {@link LinearProbingHashTable} suffers from. Leaving more
 * space in between memory probes allows other keys to be inserted without many collisions. The tradeoff
 * is that, in doing so, {@link QuadraticProbingHashTable} sacrifices <em>cache locality</em>.</p>
 *
 * @author Moweizi Xia
 *
 * @see HashTable
 * @see SeparateChainingHashTable
 * @see LinearProbingHashTable
 * @see CollisionResolver
 */
public class QuadraticProbingHashTable implements HashTable{

    private KVPair[] table;
    private PrimeGenerator primeGenerator;
    private int count = 0;
    private int hash(String key){
        return (key.hashCode() & 0x7fffffff) % table.length;
    }

    private void enlarge(){
        KVPair[] temp = new KVPair[primeGenerator.getNextPrime()];
        int index;
        
        for (int k = 0; k < table.length; k++) {
        	if (table[k] != null) {
        		String key = table[k].getKey();
        		index = hashNew(key, temp.length);
        		int curr = index;
        		int i = 1;
        		
        		while (temp[curr] != null) {
                	int j = i * i;
                	curr = index + j;
                	curr = curr % temp.length;
                	i++;
        		}
        		temp[curr] = table[k];
        	}
        }
        table = temp;
    }

    private void shrink(){
        KVPair[] temp = new KVPair[primeGenerator.getPreviousPrime()];
        int index;
        
        for (int k = 0; k < table.length; k++) {
        	if (table[k] != null) {
        		String key = table[k].getKey();
        		index = hashNew(key,temp.length);
        		int curr = index;
        		int i = 1;
        		
        		while (temp[curr] != null) {
        			int j = i * i;
                	curr = index + j;
                	curr = curr % temp.length;
                	i++;
        		}
        		temp[curr] = table[k];
        	}
        }
        table = temp;
    }
    
    private int hashNew(String key, int length){
        return (key.hashCode() & 0x7fffffff) % length;
    }

    /**
     *  Default constructor. Initializes the internal storage with a size equal to the default of {@link PrimeGenerator}.
     *  This constructor is <b>GIVEN TO YOU: DO NOT EDIT!</b>
     */
    public QuadraticProbingHashTable(){
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
     * Instances of {@link QuadraticProbingHashTable} will follow the writeup's guidelines about how to internally resize
     * the hash table when the capacity drops below 50&#37;
     * @param key The record's key.
     * @throws IllegalArgumentException if either argument is null.
     */
    @Override
    public void put(String key, String value) {
        if (key == null || value == null) throw new IllegalArgumentException();
        
        float capacity = (count * 100.0f) / table.length;
        
        if (capacity >= 50.0) enlarge();
        
        int index = hash(key);
        int curr = index;
        int i = 1;
        
        while (table[curr] != null) {
        	int j = i * i;
        	curr = index + j;
        	curr = curr % table.length;
        	i++;
        }
        
        table[curr] = new KVPair(key, value);
        count++;
    }
    
    public void reput(String key, String value) {
        
        int index = hash(key);
        int curr = index;
        int i = 1;
        
        while (table[curr] != null) {
        	int j = i * i;
        	curr = index + j;
        	curr = curr % table.length;
        	i++;
        }
        table[curr] = new KVPair(key, value);
    }


    @Override
    public String get(String key) {
    	if (key == null || !containsKey(key)) return null;
    	
    	int index = hash(key);
        int curr = index;
        int i = 1;
    	
    	while (table[curr] != null) {
    		if (table[curr].getKey().equals(key)) return table[curr].getValue();
    		else {
            	int j = i * i;
            	curr = index + j;
            	curr = curr % table.length;
            	i++;
    		}
    	}
    	return null;
    }


    /**
     * <b>Return</b> and <b>remove</b> the value associated with <tt>key</tt> in the {@link HashTable}. If <tt>key</tt> does not exist in the database
     * or if <tt>key = null</tt>, this method returns <tt>null</tt>. This method is expected to run in <em>amortized constant time</em>.
     *
     * Instances of {@link QuadraticProbingHashTable} will follow the writeup's guidelines about how to internally resize
     * the hash table when the capacity drops below 50&#37;
     * @param key The key to search for.
     * @return The associated value if <tt>key</tt> is non-<tt>null</tt> <b>and</b> exists in our database, <tt>null</tt>
     * otherwise.
     */
    @Override
    public String remove(String key) {
    	if (key == null || !containsKey(key)) return null;
    	
    	int index = hash(key);
        int curr = index;
        int i = 1;
    	boolean resize = false;
    	float capacity = (count * 100.0f) / table.length;
    	
    	if (capacity <= 50.0) resize = true;
    	
    	while (!table[curr].getKey().equals(key)) {
        	int j = i * i;
        	curr = index + j;
        	curr = curr % table.length;
        	i++;
    	}
    	
    	String target = table[curr].getValue();
    	table[curr] = null;
    	
    	int j = i * i;
    	curr = index + j;
    	curr = curr % table.length;
    	i++;
    	
    	while (table[curr] != null) {
    		String temp = table[curr].getKey();
    		String temp2 = table[curr].getValue();
    		table[curr] = null;
    		this.reput(temp, temp2);
        	j = i * i;
        	curr = index + j;
        	curr = curr % table.length;
        	i++;
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
        int curr = index;
        int i = 1;
        
    	while (table[curr] != null) {
    		if (table[curr].getKey().equals(key)) return true;
    		else {
            	int j = i * i;
            	curr = index + j;
            	curr = curr % table.length;
            	i++;
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
