// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.MatrixPower;

/** Merrien interpolatory Hermite subdivision scheme of order two
 * implementation for R^n
 * 
 * References:
 * "A family of Hermite interpolants by bisection algorithms", 1992,
 * by Merrien
 * 
 * "Construction of Hermite subdivision schemes reproducing polynomials", 2017
 * by Byeongseon Jeong, Jungho Yoon */
/* package */ class MerrienHermiteSubdivision {
  private static final Tensor DIAG = DiagonalMatrix.of(RealScalar.ONE, RationalScalar.HALF);
  private static final Tensor AMP = Tensors.fromString("{{1/2, +1/8}, {-3/4, -1/8}}");
  private static final Tensor AMQ = Tensors.fromString("{{1/2, -1/8}, {+3/4, -1/8}}");

  public static HermiteSubdivision string(Tensor control) {
    return new MerrienHermiteSubdivision(control).new StringIteration();
  }

  public static HermiteSubdivision string(Tensor control, Tensor diag) {
    return new MerrienHermiteSubdivision(control).new StringIteration();
  }

  public static HermiteSubdivision cyclic(Tensor control) {
    return new MerrienHermiteSubdivision(control).new CyclicIteration();
  }

  // ---
  private Tensor control;
  private int k = 0;

  private MerrienHermiteSubdivision(Tensor control) {
    this.control = control;
  }

  private class StringIteration implements HermiteSubdivision {
    @Override // from HermiteSubdivision
    public Tensor iterate() {
      int length = control.length();
      Tensor string = Tensors.reserve(2 * length - 1);
      Tensor Dk = MatrixPower.of(DIAG, k);
      Tensor Dnk1 = MatrixPower.of(DIAG, -(k + 1));
      Tensor amp = Dot.of(Dnk1, AMP, Dk);
      Tensor amq = Dot.of(Dnk1, AMQ, Dk);
      for (int index = 0; index < length; ++index) {
        Tensor p = control.get(index);
        string.append(p);
        if (index < length - 1) {
          Tensor q = control.get(index + 1);
          string.append(amp.dot(p).add(amq.dot(q)));
        }
      }
      ++k;
      return control = string;
    }
  }

  private class CyclicIteration implements HermiteSubdivision {
    @Override // from HermiteSubdivision
    public Tensor iterate() {
      int length = control.length();
      Tensor string = Tensors.reserve(2 * length);
      Tensor Dk = MatrixPower.of(DIAG, k);
      Tensor Dnk1 = MatrixPower.of(DIAG, -(k + 1));
      Tensor amp = Dot.of(Dnk1, AMP, Dk);
      Tensor amq = Dot.of(Dnk1, AMQ, Dk);
      for (int index = 0; index < length; ++index) {
        Tensor p = control.get(index);
        string.append(p);
        Tensor q = control.get((index + 1) % length);
        string.append(amp.dot(p).add(amq.dot(q)));
      }
      ++k;
      return control = string;
    }
  }
}
