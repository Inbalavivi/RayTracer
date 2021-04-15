
public class Settings {
    double [] backgroundColor ;
    int NumberOfShadowRays;
    int MaxNumberOfRecursion;
    int SuperSamplingLevel;

    public void setBackGroundColor(double[] backgroundColor) {
        this.backgroundColor=backgroundColor;

    }

    public void setNumberOfShadowRays(int NumberOfShadowRays) {
        this.NumberOfShadowRays=NumberOfShadowRays;
    }

    public void setMaxNumberOfRecursion(int MaxNumberOfRecursion) {
        this.MaxNumberOfRecursion=MaxNumberOfRecursion;

    }

    public void setSuperSamplingLevel(int SuperSamplingLevel) {
        this.SuperSamplingLevel=SuperSamplingLevel;
    }

}
