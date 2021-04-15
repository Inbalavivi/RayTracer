public class Plane implements Primitive {
	Vector normal;
	double offset;
	int materialIndex;

	public Plane() {};

	void setNormal(Vector normal) {
		normal.normalize();
		this.normal=normal;
	}
	void setOffset(double offset) {
		this.offset=-1*offset;
	}
	void setMaterialIndex(int index) {
		this.materialIndex=index;
	}

	@Override
	public double intersecte(Ray ray)  {
		double t= (-1) * (ray.basePoint.dotProduct(this.normal)+ this.offset) / (ray.directionVector.dotProduct(this.normal));
		return t;
	}

	@Override
	public Vector findNormal(Vector intersectionPoint) {
		this.normal.normalize();
		return this.normal;
	}

	@Override
	public int getMterialIndex() {
		return this.materialIndex;
	}

	public double findOffset(Vector point) {
		return (this.normal.dotProduct(point));
	}


	public Vector findVecOnPlane(Vector point) {
		double z = -1* (this.normal.x*1+this.normal.y*1+this.offset)/this.normal.z;
		Vector z_vec = new Vector(1,1,z);
		Vector newVector = z_vec.add(point.scalarMult(-1));
		return newVector;
	}

}