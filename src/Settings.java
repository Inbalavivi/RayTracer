public class Settings {
    Vector backgroundCol ;
    int numShadowRays;
    int maxNumRec;

    public Settings(Vector col ,int numRays, int numRec){
        this.backgroundCol=col;
        this.numShadowRays=numRays;
        this.maxNumRec=numRec;
    }

}