public class Border {
    public double nX;
    public double nY;

    public Triangle neighbor;

    public Border(double nX, double nY, Triangle neighbor) {
        this.nX = nX;
        this.nY = nY;
        this.neighbor = neighbor;
    }

    public double getnX() {
        return nX;
    }

    public double getnY() {
        return nY;
    }

    public Triangle getNeighbor() {
        return neighbor;
    }
}


