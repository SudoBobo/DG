package com.github.sudobobo.geometry;

import com.github.sudobobo.basis.Function;
import com.github.sudobobo.basis.PreLinear2DBasis;
import com.github.sudobobo.calculations.InitialConditionPhase;
import com.github.sudobobo.meshconstruction.InitialConditionConfig;
import com.github.sudobobo.meshconstruction.SourceConfig;
import org.jblas.DoubleMatrix;
import org.jblas.Solve;

import static com.github.sudobobo.calculations.Value.buildInitialConditionPhaseFunction;
import static com.github.sudobobo.meshconstruction.PhysicalAttributesMatrixes.calcRpqn;

public class SinPointSource extends PointSource {
    private DoubleMatrix spacialPart;
    public SinPointSource(SourceConfig s, PreLinear2DBasis basis, Triangle t,
                          double integrationStep) {
        super(s);
        // calculate spatial part
        assert (s.getProfile().equals("cos"));

        double width =  s.getSize()[0];
        InitialConditionConfig initialCondition =
            new InitialConditionConfig(s.getProfile(), width,
                                       s.getAmplitude(), s.getVector()[0],
                                       s.getVector()[1],  s.getVector()[2],
                                       s.getPoint());

        InitialConditionPhase initialConditionPhase =
            buildInitialConditionPhaseFunction(initialCondition);

        final Function initialConditionPhaseInInnerSystem = new Function() {
            @Override
            // todo this change of variables should be discused
            public double getValue(double[] x) {
                return initialConditionPhase.calc(
                    t.getX(x[0], x[1]), t.getY(x[0], x[1]), 0
                );
            }

            @Override
            public Function getDerivative(int xOrder, int yOrder, int zOrder) {
                return null;
            }
        };

        double []scalarFfi = basis.squareIntegralForCoeffCalc(initialConditionPhaseInInnerSystem, integrationStep);

        DoubleMatrix grammInv = Solve.pinv(basis.M());
        DoubleMatrix coeffsfi = new DoubleMatrix(3, 1);

        System.out.println("SinPointSource calculations assumes that M matrix are symmetrical\n");


        for (int fIndex = 0; fIndex < coeffsfi.rows; fIndex++) {
            for (int ss = 0; ss < coeffsfi.rows; ss++) {
                coeffsfi.put(fIndex, 0,
                    coeffsfi.get(fIndex,0) + grammInv.get(fIndex,ss) * scalarFfi[ss]
                );
            }
        }

        int varNumber = t.getA().rows;
        DoubleMatrix coefficients = new DoubleMatrix(varNumber, basis.getNumberOfBasisFunctions());

        for (int funcIndex = 0; funcIndex < basis.getNumberOfBasisFunctions(); funcIndex++) {
            for (int p = 0; p < varNumber; p++) {
                coefficients.put(p, funcIndex, coeffsfi.get(funcIndex, 0));
            }
        }

        double nXInnerTriangleSystem = s.getVector()[0];
        double nYInnerTriangleSystem = s.getVector()[1];

        Domain d = t.getDomain();
        DoubleMatrix pxVector = calcRpqn(d.getLambda(), d.getMu(), d.getCp(), d.getCs(), nXInnerTriangleSystem, nYInnerTriangleSystem).
            getColumn(4);

        for (int funcIndex = 0; funcIndex < basis.getNumberOfBasisFunctions(); funcIndex++) {
            for (int p = 0; p < varNumber; p++) {
                    coefficients.put(p, funcIndex,
                        coefficients.get(p, funcIndex) * pxVector.get(p, 0));
            }
        }

        spacialPart = coefficients;
    }

    // mind that integral should be calculated
    @Override
    public DoubleMatrix integrateOverTriangle(Triangle triangle, double t, double dt) {
        double timePart =
            Math.abs(((Math.cos(omega * t) - Math.cos(omega * (t + dt)))) *
                       amplitude / omega);
        return spacialPart.mul(timePart);
        }
}
