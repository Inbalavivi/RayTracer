public class Light {
	Vector position;
	Vector color;
	float specularIntensity;
	float shadowIntensity;
	float radius;

	public Light(Vector pos, Vector col, float specular, float shadow, float radiusVal) {
		this.position=pos;
		this.color=col;
		this.specularIntensity=specular;
		this.shadowIntensity=shadow;
		this.radius=radiusVal;
	}
}
