public class Ray {
	Vector p0;
	Vector v;

	public Ray(Vector camPos, Vector v ) {
		this.p0 = camPos;
		v.normalize();
		this.v = v;
	}
}
