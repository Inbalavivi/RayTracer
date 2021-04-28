public class Ray {
	Vector p0;
	Vector v;
	//Ray: P = P0 + tV
	public Ray(Vector camPos, Vector v ) {
		this.p0 = camPos;
		v.normalize();
		this.v = v;
	}
}
