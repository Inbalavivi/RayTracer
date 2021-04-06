public class Box {
    Vector centerPos;
    double edgeLen;
    int materialIndex;

    public Box(Vector center, double len, int index){
        this.centerPos=center;
        this.edgeLen=len;
        this.materialIndex=index;
    }
}
