package cs.sii.model;

import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class RWRandom  extends Random{

	public RWRandom() {}
	
	public RWRandom(Long seed) {super(seed);	}
	
	public long nextPosLong(long n){		
		  long bits, val;
		   do {
		      bits = (this.nextLong() << 1) >>> 1;
		      val = bits % n;
		   } while (bits-val+(n-1) < 0L);
		   return val;		
	}

}
 