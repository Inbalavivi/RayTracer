public class Camera {
    Vector position;
    Vector lookAt;
    Vector upVector;
    float screenDistance;
    float screenWidth;

    public Camera() {
        // TODO Auto-generated constructor stub
    }
    public void setcamPosition(Vector camPos) {

        this.position=camPos;

    }
    public void SetCamScreenDistance(float camScreenDistance) {
        this.screenDistance=camScreenDistance;

    }
    public void SetCamScreenWidth(float camScreenWidth) {
        this.screenWidth=camScreenWidth;

    }
    public void setCamLookAtDirection(Vector camLookAt) {
        camLookAt.normalize();
        this.lookAt=camLookAt;

    }
    public void SetCamUpVector(Vector upVector) {
        upVector.normalize();
        this.upVector=upVector;

    }

}