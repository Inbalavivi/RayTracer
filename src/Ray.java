public class Ray {
	Vector p0;  //basePoint = camPosition;
	Vector v;  	//directionVector;
	//Ray: P = P0 + tV
	public Ray(Vector camPos, Vector v ) {
		this.p0 = camPos;
		v.normalize();
		this.v = v;
	}
}
