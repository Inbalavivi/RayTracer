package RayTracing;

public class Vector {
    double x;
    double y;
    double z;

	public Vector(double xVal, double yVal, double zVal) {
        this.x=xVal;
        this.y=yVal;
        this.z=zVal;
	}

	public double dotProduct(Vector otherVec) {
		double scalar = otherVec.x*this.x + otherVec.y*this.y+ otherVec.z*this.z;
		return scalar;
	}

	public Vector crossProduct(Vector otherVec ) {
		double x = (this.y * otherVec.z) - (this.z * b.y);
		double y = (this.z * otherVec.x) - (this.x * b.z);
		double z = (this.x * otherVec.y) - (this.y * b.x);
		Vector vec = new Vector(x,y,z);
		return vec;
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

    public Vector vecsMult(Vector otherVec) {
       
	}
    

	public void normalize() {
		double n = Math.sqrt(this.dotProduct(this));
		this.x /= n;
		this.y /= n;
		this.z /= n;
	}

}
