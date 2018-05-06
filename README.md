## DG - Realisation of discontinuous Galerking Method (2D)

### Current data flow (for developing use)
1) Config is parsed to domens' attributes and initial condition
2) Mesh is built

    2.1) Constant Triangles asossiated with changable Values, then these Values  
    asossiated with ValuesToWrite. Values are used in calculations, then their
    'values' translated to ValuesToWrite. ValuesToWrite is used to write file input

3) Calculations are being conducted, along with the file input writing


### Notes
Transformation described in (18) works the following way:
