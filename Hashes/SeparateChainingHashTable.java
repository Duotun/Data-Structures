package projects.phonebook.hashes;

import java.util.Iterator;

import projects.phonebook.utils.*;

/**<p>{@link SeparateChainingHashTable} is a {@link HashTable} that implements <b>Separate Chaining</b>
 * as its collision resolution strategy, i.e the collision chains are implemented as actual
 * Linked Lists. These Linked Lists are <b>not assumed ordered</b>. It is the easiest and most &quot; natural &quot; way to
 * implement a hash table and is useful for estimating hash function quality. In practice, it would
 * <b>not</b> be the best way to implement a hash table, because of the wasted space for the heads of the lists.
 * Open Addressing methods, like those implemented in {@link LinearProbingHashTable} and {@link QuadraticProbingHashTable}
 * are more desirable in practice, since they use the original space of the table for the collision chains themselves.</p>
 *
 * @author Moweizi Xia
 * @see HashTable
 * @see SeparateChainingHashTable
 * @see LinearProbingHashTable
 * @see CollisionResolver
 */
public class SeparateChainingHashTable implements HashTable{

    private KVPairList[] table;
    private int count;
    private PrimeGenerator primeGenerator;

    private int hash(String key){
        return (key.hashCode() & 0x7fffffff) % table.length;
    }
    
    private int hashNew(String key, int length){
        return (key.hashCode() & 0x7fffffff) % length;
    }

    /**
     *  Default constructor. Initializes the internal storage with a size equal to the default of {@link PrimeGenerator}.
     *  This constructor is <b>GIVEN TO YOU: DO NOT EDIT IT!</b>
     */
    public SeparateChainingHashTable(){
        primeGenerator = new PrimeGenerator();
        table = new KVPairList[primeGenerator.getCurrPrime()];
        for(int i = 0; i < table.length; i++){
            table[i] = new KVPairList();
        }
        count = 0;
    }

    @Override
    public void put(String key, String value) {
    	if (key == null || value == null) throw new IllegalArgumentException();
    	
    	int index = hash(key);
    	
    	table[index].addFront(key, value);
    	count++;
    }

    @Override
    public String get(String key) {
    	if (key == null) return null;
    	
        int index = hash(key);
        return table[index].getValue(key);
    }

    @Override
    public String remove(String key) {
        if (key == null || !containsKey(key)) return null;
        
        int index = hash(key);
        String target = table[index].getValue(key);
        table[index].removeByKey(key);
        
        count--;
        return target;
    }

    @Override
    public boolean containsKey(String key) {
        int index = hash(key);
        return table[index].containsKey(key);
    }

    @Override
    public boolean containsValue(String value) {
    	boolean contains = false;
        for (int i = 0; i < table.length; i++) {
        	contains = table[i].containsValue(value);
        	if (contains) return true;
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
    /**
     * Enlarges this hash table. At the very minimum, this method should increase the <b>capacity</b> of the hash table and ensure
     * that the new size is prime. The class {@link PrimeGenerator} implements the enlargement heuristic that
     * we have talked about in class and can be used as a black box if you wish.
     * @see PrimeGenerator#getNextPrime()
     */
    public void enlarge() {
        KVPairList[] temp = new KVPairList[primeGenerator.getNextPrime()];
        int index;
        
        for(int j = 0; j < temp.length; j++){
        	temp[j] = new KVPairList();
        }
        
        for (int i = 0; i < table.length; i++) {
        	if (table[i] != null) {
        		Iterator<KVPair> itr = table[i].iterator();
        		while (itr.hasNext()) {
        			KVPair curr = itr.next();
        	    	index = hashNew(curr.getKey(), temp.length);
        	    	
        	    	temp[index].addFront(curr.getKey(), curr.getValue());
        		}
        	}
        }
        table = temp;

    }

    /**
     * Shrinks this hash table. At the very minimum, this method should decrease the size of the hash table and ensure
     * that the new size is prime. The class {@link PrimeGenerator} implements the shrinking heuristic that
     * we have talked about in class and can be used as a black box if you wish.
     *
     * @see PrimeGenerator#getPreviousPrime()
     */
    public void shrink(){
        KVPairList[] temp = new KVPairList[primeGenerator.getPreviousPrime()];
        int index;
        
        for(int j = 0; j < temp.length; j++){
        	temp[j] = new KVPairList();
        }
        
        for (int i = 0; i < table.length; i++) {
        	if (table[i] != null) {
        		Iterator<KVPair> itr = table[i].iterator();
        		while (itr.hasNext()) {
        			KVPair curr = itr.next();
        			index = hashNew(curr.getKey(), temp.length);
        			
        			temp[index].addFront(curr.getKey(), curr.getValue());
        		}
        	}
        }
        table = temp;
    }
}
