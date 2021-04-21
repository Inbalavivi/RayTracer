public class Camera {
    Vector position;
    Vector lookAt;
    Vector upVector;
    float screenDistance;
    float screenWidth;
    boolean fishEye;
    float fishEyeTransVal;

    public Camera(Vector pos,Vector lookAt,Vector upVec,float distance,float width,boolean isFishEye, float fisheyeVal ) {
        this.position=pos;
        lookAt=lookAt.add(position.scalarMult(-1));
        lookAt.normalize();
        this.lookAt=lookAt;
        upVec.normalize();
        this.upVector=upVec;
        this.screenDistance=distance;
        this.screenWidth=width;
        this.fishEye=isFishEye;
        this.fishEyeTransVal=fisheyeVal;
    }
}