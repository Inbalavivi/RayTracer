public class Camera {
    Vector camPosition;
    Vector lookAtDirection;
    Vector camUpVector;
    float camScreenDistance;
    float camScreenWidth;

    public Camera() {
        // TODO Auto-generated constructor stub
    }
    public void setcamPosition(Vector camPosition) {

        this.camPosition=camPosition;

    }
    public void SetCamScreenDistance(float camScreenDistance) {
        this.camScreenDistance=camScreenDistance;

    }
    public void SetCamScreenWidth(float camScreenWidth) {
        this.camScreenWidth=camScreenWidth;

    }
    public void setCamLookAtDirection(Vector camLookAtDirection) {
        camLookAtDirection.normalize();
        this.lookAtDirection=camLookAtDirection;

    }
    public void SetCamUpVector(Vector camUpVector) {
        camUpVector.normalize();
        this.camUpVector=camUpVector;

    }

}