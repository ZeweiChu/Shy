package library;

import java.util.List;

//monoid defines join and empty operation, which can be passed to generic query for query usage
public interface Monoid<R> {
    R join(R x, R y);
    R empty();
    default R fold(List<R> lr){
    	R res = empty();
    	for (R r: lr){
    		res = join(res, r);
    	}
    	return res;
    }
}
