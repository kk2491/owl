// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.LieGroupGeodesic;
import ch.ethz.idsc.sophus.math.Nocopy;
import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** Merrien interpolatory Hermite subdivision scheme of order two
 * implementation for R^n
 * 
 * References:
 * "A family of Hermite interpolants by bisection algorithms", 1992,
 * by Merrien
 * 
 * "Construction of Hermite subdivision schemes reproducing polynomials", 2017
 * by Byeongseon Jeong, Jungho Yoon */
public class Hermite1Subdivision implements HermiteSubdivision {
  private static final Scalar _1_4 = RationalScalar.of(1, 4);
  // ---
  private final LieGroup lieGroup;
  private final LieExponential lieExponential;
  private final LieGroupGeodesic lieGroupGeodesic;

  /** @param lieGroup
   * @param lieExponential
   * @throws Exception if either parameters is null */
  public Hermite1Subdivision(LieGroup lieGroup, LieExponential lieExponential) {
    this.lieGroup = lieGroup;
    this.lieExponential = lieExponential;
    lieGroupGeodesic = new LieGroupGeodesic(lieGroup, lieExponential);
  }

  @Override // from HermiteSubdivision
  public TensorIteration string(Scalar delta, Tensor control) {
    return new Control(delta, control).new StringIteration();
  }

  @Override // from HermiteSubdivision
  public TensorIteration cyclic(Scalar delta, Tensor control) {
    return new Control(delta, control).new CyclicIteration();
  }

  private class Control {
    private Tensor control;
    private Scalar rgk;
    private Scalar rvk;

    private Control(Scalar delta, Tensor control) {
      this.control = control;
      rgk = delta.divide(RealScalar.of(8));
      rvk = RationalScalar.of(3, 2).divide(delta);
    }

    /** @param p == {pg, pv}
     * @param q == {qg, qv}
     * @return r == {rg, rv} */
    private Tensor midpoint(Tensor p, Tensor q) {
      Tensor pg = p.get(0);
      Tensor pv = p.get(1);
      Tensor qg = q.get(0);
      Tensor qv = q.get(1);
      Tensor rg;
      {
        Tensor rg1 = lieGroupGeodesic.midpoint(pg, qg);
        Tensor rpv = pv;
        Tensor rqv = qv;
        Tensor rg2 = lieExponential.exp(rpv.subtract(rqv).multiply(rgk));
        rg = lieGroup.element(rg1).combine(rg2);
      }
      Tensor log = lieExponential.log(lieGroup.element(pg).inverse().combine(qg));
      Tensor rv1 = log.multiply(rvk);
      Tensor pqv = qv;
      Tensor rv2 = pqv.add(pv).multiply(_1_4);
      Tensor rv = rv1.subtract(rv2);
      Tensor rrv = rv;
      return Tensors.of(rg, rrv);
    }

    private class StringIteration implements TensorIteration {
      @Override // from HermiteSubdivision
      public Tensor iterate() {
        int length = control.length();
        Nocopy string = new Nocopy(2 * length - 1);
        Tensor p = control.get(0);
        for (int index = 1; index < length; ++index) {
          string.append(p);
          Tensor q = control.get(index);
          string.append(midpoint(p, q));
          p = q;
        }
        string.append(p);
        rgk = rgk.multiply(RationalScalar.HALF);
        rvk = rvk.add(rvk);
        return control = string.tensor();
      }
    }

    private class CyclicIteration implements TensorIteration {
      @Override // from HermiteSubdivision
      public Tensor iterate() {
        int length = control.length();
        Nocopy string = new Nocopy(2 * length);
        Tensor p = control.get(0);
        for (int index = 1; index <= length; ++index) {
          string.append(p);
          Tensor q = control.get(index % length);
          string.append(midpoint(p, q));
          p = q;
        }
        rgk = rgk.multiply(RationalScalar.HALF);
        rvk = rvk.add(rvk);
        return control = string.tensor();
      }
    }
  }
}