public class Plane implements Surface {
	Vector normal;
	double offset;
	int materialIndex;

	public Plane(Vector normal,double offset , int matindex) {
		normal.normalize();
		this.normal=normal;
		this.offset=-1*offset;
		this.materialIndex=matindex;
	}

	public double intersect(Ray ray)  {
		double t= (-1) * (ray.p0.dotProduct(this.normal)+ this.offset) / (ray.v.dotProduct(this.normal));
		return t;
	}

	public Vector getNormal(Vector intersectionPoint) {
		this.normal.normalize();
		return this.normal;
	}

	public int getMaterialIndex() {
		return this.materialIndex;
	}

	public Vector findVec(Vector point) {
		double z = -1* (this.normal.x*1+this.normal.y*1+this.offset)/this.normal.z;
		Vector z_vec = new Vector(1,1,z);
		Vector newVector = z_vec.add(point.scalarMult(-1));
		return newVector;
	}

}