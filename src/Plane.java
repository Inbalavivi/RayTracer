public class Plane implements Surface {
	Vector normal;
	double offset;
	int materialIndex;

	public Plane(Vector normal,double offset , int matIndex) {
		normal.normalize();
		this.normal=normal;
		this.offset=-1*offset;
		this.materialIndex=matIndex;
	}

	public double intersect(Ray ray)  {
		return (-1) * (ray.p0.dotProduct(this.normal)+ this.offset) / (ray.v.dotProduct(this.normal));
	}

	public Vector findVec(Vector point) {
		Vector vec = new Vector(1,1, -1 * (this.offset + this.normal.x + this.normal.y) / this.normal.z);
		return vec.add(point.scalarMult(-1));
	}
	public int getMatIndex(){
		return this.materialIndex;
	}
}