
public class Light {

	Vector lightPosition;
	Vector lightColor;
	float specularIntensity;
	float shadowIntensity;
	float lightRadius;

	public Light(Vector lightPosition, Vector lightColor, float specularIntensity, float shadowIntensity,
				 float lightRadius) {
		this.lightPosition=lightPosition;
		this.lightColor=lightColor;
		this.specularIntensity=specularIntensity;
		this.shadowIntensity=shadowIntensity;
		this.lightRadius=lightRadius;
	}

}