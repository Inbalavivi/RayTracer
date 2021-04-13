package RayTracing;

public class Ray {
    Vector p0;  //basePoint = camPosition;
	Vector direction;  	//directionVector;

	public Ray(Vector camPos, Vector v ) {
		this.p0 = camPos;
		this.direction = v;
	}
} 

