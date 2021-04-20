public class Material {
    Vector diffusion;
    Vector specular;
    Vector reflection;
    float shininess;
    float transparency;

    public Material (Vector diffusion,Vector specular,Vector reflection, float shininess, float transparent) {
        this.diffusion=diffusion;
        this.specular=specular;
        this.reflection=reflection;
        this.shininess=shininess;
        this.transparency=transparent;
    }
}
