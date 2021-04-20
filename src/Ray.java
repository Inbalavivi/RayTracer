public class Ray {
	Vector p0;  //basePoint = camPosition;
	Vector v;  	//directionVector;

	public Ray(Vector camPos, Vector v ) {
		this.p0 = camPos;
		this.v = v;
	}
}
