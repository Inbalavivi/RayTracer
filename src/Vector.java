public class Vector {
	double x;
	double y;
	double z;

	public Vector(double xVal, double yVal, double zVal) {
		this.x=xVal;
		this.y=yVal;
		this.z=zVal;
	}

	public void normalize() {
		double n = Math.sqrt(this.dotProduct(this));
		this.x /= n;
		this.y /= n;
		this.z /= n;
	}

	public Vector add(Vector otherVec) {
		double x=this.x +otherVec.x;
		double y=this.y +otherVec.y;
		double z=this.z +otherVec.z;
		Vector vec = new Vector(x,y,z);
		return vec;
	}

	public Vector scalarMult(double scalar) {
		double x= scalar*this.x;
		double y= scalar*this.y;
		double z= scalar*this.z;
		Vector vec = new Vector(x,y,z);
		return vec;
	}

	public double dotProduct(Vector otherVec) {
		double scalar = otherVec.x*this.x + otherVec.y*this.y+ otherVec.z*this.z;
		return scalar;
	}

	public Vector vecsMult(Vector otherVec) { // elementwize
		double x= otherVec.x*this.x;
		double y= otherVec.y*this.y;
		double z= otherVec.z*this.z;
		Vector vec = new Vector(x,y,z);
		return vec;
	}

	public Vector crossProduct(Vector otherVec ) {
		double x = (this.y * otherVec.z) - (this.z * otherVec.y);
		double y = (this.z * otherVec.x) - (this.x * otherVec.z);
		double z = (this.x * otherVec.y) - (this.y * otherVec.x);
		Vector vec = new Vector(x,y,z);
		return vec;
	}

	public void checkBound(){
		if (this.x > 1) {
			this.x = 1;
		}
		if (this.y > 1) {
			this.y = 1;
		}
		if (this.z > 1) {
			this.z = 1;
		}
	}

}