// code by jph
package ch.ethz.idsc.sophus.hs.r2;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public enum R2IdentityFamily implements R2RigidFamily {
  INSTANCE;
  // ---
  private static final Tensor MATRIX = IdentityMatrix.of(3).unmodifiable();

  // ---
  @Override
  public TensorUnaryOperator inverse(Scalar scalar) {
    return t -> t;
  }

  @Override
  public TensorUnaryOperator forward(Scalar scalar) {
    return t -> t;
  }

  @Override
  public Tensor forward_se2(Scalar scalar) {
    return MATRIX;
  }
}
