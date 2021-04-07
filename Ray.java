import jdk.internal.misc.VM;
import jdk.javadoc.internal.tool.resources.version;

public class Ray {
    vector p0;
    double t;
	vector v;
	public Ray(vector camPos, double t, vector v ) {
		this.p0 = camPos;
        this.t=t;
		this.v = v;
	}
} 

