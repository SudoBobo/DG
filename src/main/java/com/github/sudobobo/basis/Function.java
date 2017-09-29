package com.github.sudobobo.basis;

public interface Function {
    public double getValue(double[] x);
    public Function getDerivative(int xOrder,int yOrder,int zOrder);
}
