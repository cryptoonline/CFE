/**
 * Created by naveed on 1/15/14.
 */
public class Pair<L,R> {
    private L l;
    private R r;

    public Pair(){}

    public Pair(L l, R r){
        this.l = l;
        this.r = r;
    }

    public L getL(){ return l;}
    public R getR(){ return r;}
    public void set(L l, R r){ this.l = l; this.r = r;}
    public void setL(L l){ this.l = l;}
    public void setR(R r){ this.r = r;}
}
