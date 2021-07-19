
public class Variable <T> {
	
	 private T value;
	 public String varName;

	    public Variable(String varName,T value) {
	    	this.varName=varName;
	        this.value = value;
	        
	    }

	    public T get() {
	        return value;
	    }

	    public void set(T anotherValue) {
	        value = anotherValue;
	    }

	    @Override
	    public String toString() {
	        return value.toString();
	    }

	    @Override
	    public boolean equals(Object obj) {
	        return value.equals(obj);
	    }

	    @Override
	    public int hashCode() {
	        return value.hashCode();
	    }

}
