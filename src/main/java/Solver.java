import org.jblas.DoubleMatrix;

public class Solver {
    SystemSolver systemSolver;

    public Solver(SystemSolver systemSolver) {
        this.systemSolver = systemSolver;
    }

    public Mesh [] solve (Mesh initialCondition, double realFullTime, double timeStep){
        // in each moment store in memory only two meshes: for t and t+1
        return null;
    }

    private Mesh solveOneStep(Mesh previousCondition, double timeStep){
        return null;
    }


    private Triangle solveOneTriangle(Triangle previousCondition, double timeStep){

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

        for (int j = 0 ; j < 3; j++){
            first. addi(tr.T(j). mmul(0.5). mmuli( tr.A().addi(tr.AAbs()) ).
                    mmuli(tr.TInv(j)). mmuli(u).mmuli(tr.S(j)).
                    mmuli(tr.Fkl()));


            second. addi( tr.T(j). mmul(0.5). mmuli( tr.A(). subi(tr.AAbs())).
                    mmuli(tr.TInv(j)). mmuli(tr.uNeib(j)). mmuli(tr.S(j)).
                    mmuli(tr.Fkl(j)));

        }

        DoubleMatrix third = new DoubleMatrix(u.rows, u.columns);
        third.addi(tr.Astr().mmul(u). mmuli(tr.jacobian()). mmuli(tr.KKsi()));

        DoubleMatrix fourth = new DoubleMatrix(u.rows, u.columns);
        fourth.addi(tr.BStr().mmul(u).mmuli(tr.jacobian()). mmuli(tr.KMu()));

        DoubleMatrix fifth = new DoubleMatrix(u.rows, u.columns);

        // p * l (size)
        DoubleMatrix dU = new DoubleMatrix(u.rows, u.columns);
        dU = ( third.addi(fourth).subi(first).subi(second). divi(tr.Mkl().mul(tr.jacobian()));

        DoubleMatrix uNew = u.add(dU. mmuli(timeStep));
        return new Triangle(uNew);
    }

    private Triangle solveOneTriangleMatrixUse(Triangle previousCondition, double timeStep){
        return null;
    }



}
