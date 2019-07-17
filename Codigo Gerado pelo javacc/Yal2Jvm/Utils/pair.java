package Yal2Jvm.Utils;

public class pair<T1,T2>{

    private T1 first;
    private T2 second;

    /**
     * pair class
     * @param first first element
     * @param second second element
     */
    public pair(T1 first,T2 second)
    {
        this.first = first;
        this.second = second;
    }

    /**
     * get first
     * @return first
     */
    public T1 getFirst()
    {
        return this.first;
    }

    /**
     * get second
     * @return the second
     */
    public T2 getSecond()
    {
        return this.second;
    }

    /**
     * set the first
     * @param first the first
     */
    public void setFirst(T1 first)
    {
        this.first = first;
    }

    /**
     * set the second
     * @param second the second
     */
    public void setSecond(T2 second)
    {
        this.second = second;
    }

    /**
     * get string representation
     * @return string representation
     */
    @Override
    public String toString()
    {
        String ret = "{";
        ret+=this.first+","+this.second+"}";
        return ret;
    }

}
