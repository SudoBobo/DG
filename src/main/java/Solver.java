import org.jblas.DoubleMatrix;

public class Solver {
    SystemSolver systemSolver;

    public Solver(SystemSolver systemSolver) {
        this.systemSolver = systemSolver;
    }


    public void solveOneStep(Mesh previousCondition, Mesh newCondition, double timeStep){
        for(int triangleIdx = 0; triangleIdx < previousCondition.size(); triangleIdx++){
            newCondition.triangles.get(triangleIdx).u = solveOneTriangle(previousCondition.triangles.get(triangleIdx), timeStep);
        }
    }


    private DoubleMatrix solveOneTriangle(Triangle previousCondition, double timeStep){

        // method for debugging - calculations use two for cycles instead of matrix permutation
        // should be removed for solveOneTriangleMatrixUse

        // (as mentioned in the article) u_p_l in each triangle
        // p - stands for index of variable (sigma x, sigma y, etc)
        // l - stands for index of time-dependent coefficient
        // expected to be p x l matrics (p - rows, l - columns)

        Triangle tr = previousCondition;
        DoubleMatrix u = previousCondition.u;

        DoubleMatrix first = new DoubleMatrix(u.rows, u.columns);
        DoubleMatrix second = new DoubleMatrix(u.rows, u.columns);

        // TODO consider using mul for matrix * number, not mmul
        for (int j = 0 ; j < 3; j++){
            first. addi(tr.T(j). mmul(0.5). mmul( tr.An().add(tr.AAbs()) ).
                    mmul(tr.TInv(j)). mmul(u).mmul(tr.S(j)).
                    mmul(tr.Fkl()));


            second. addi( tr.T(j). mmul(0.5). mmul( tr.An(). sub(tr.AAbs())).
                    mmul(tr.TInv(j)). mmul(tr.uNeib(j).u). mmul(tr.S(j)).
                    mmul(tr.Fkl(j)));

        }

        DoubleMatrix third = new DoubleMatrix(u.rows, u.columns);
        third.addi(tr.AStr().mmul(u). mmul(tr.jacobian()). mmul(tr.KKsi()));

        DoubleMatrix fourth = new DoubleMatrix(u.rows, u.columns);
        fourth.addi(tr.BStr().mmul(u).mmul(tr.jacobian()). mmul(tr.KMu()));

        DoubleMatrix fifth = new DoubleMatrix(u.rows, u.columns);

        // p * l (size)
        DoubleMatrix dU = new DoubleMatrix(u.rows, u.columns);
        dU = ( third.add(fourth).sub(first).sub(second). div(tr.Mkl().mul(tr.jacobian())) );

        // TODO remove this with system solver
        return u.add(dU. mmul(timeStep));
    }

    private Triangle solveOneTriangleMatrixUse(Triangle previousCondition, double timeStep){
        return null;
    }



}
