package RayTracing;

public class Ray {
    Vector p0;
    double t;
	Vector v;

	public Ray(Vector camPos, double t, Vector v ) {
		this.p0 = camPos;
        this.t=t;
		this.v = v;
	}
} 

