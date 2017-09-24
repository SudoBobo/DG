import org.junit.Test;

import static com.github.sudobobo.MeshConstructor.calcNumberOfTrianglesForRegularMesh;

public class numberOfTrianglesTest {

    @Test
    public void checkCorrectnes() {

        double sideLength = 100;
        double fine = 0.1;

        int res = calcNumberOfTrianglesForRegularMesh(sideLength, fine);
        System.out.println(res);

    }
}
