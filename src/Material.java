public class Material {
    Vector diffuseColor;
    Vector specularColor;
    Vector reflectionColor;
    float phongSpecularityCoefficient;
    float transparency;
    float bonus;
    public Material () {};

    public void setDiffuseColor(Vector diffuseColor) {
        this.diffuseColor=diffuseColor;
    }
    public void setSpecularColor(Vector specularColor) {
        this.specularColor=specularColor;

    }
    public void setReflectionColor(Vector reflectionColor) {
        this.reflectionColor=reflectionColor;
    }
    public void setPhongSpecularityCoefficient(float phongSpecularityCoefficient) {
        this.phongSpecularityCoefficient=phongSpecularityCoefficient;
    }
    public void setTransparency(float transparency) {
        this.transparency=transparency;
    }
    public void setBonus(float bonus) {
        this.bonus=bonus;
    }

}
