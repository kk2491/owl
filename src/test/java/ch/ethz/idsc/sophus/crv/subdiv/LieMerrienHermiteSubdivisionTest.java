// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.lie.rn.RnExponential;
import ch.ethz.idsc.sophus.lie.rn.RnGroup;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringGroup;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class LieMerrienHermiteSubdivisionTest extends TestCase {
  public void testString() {
    Tensor control = Tensors.fromString("{{0, 0}, {1, 0}, {0, -1}, {0, 0}}");
    HermiteSubdivision hs1 = MerrienHermiteSubdivision.string(control);
    HermiteSubdivision hs2 = new LieMerrienHermiteSubdivision(RnGroup.INSTANCE, RnExponential.INSTANCE).string(control);
    for (int count = 0; count < 6; ++count) {
      Tensor it1 = hs1.iterate();
      Tensor it2 = hs2.iterate();
      assertEquals(it1, it2);
      ExactTensorQ.require(it1);
      ExactTensorQ.require(it2);
    }
  }

  public void testCyclic() {
    Tensor control = Tensors.fromString("{{0, 0}, {1, 0}, {0, -1}, {-1/2, 1}}");
    HermiteSubdivision hs1 = MerrienHermiteSubdivision.cyclic(control);
    HermiteSubdivision hs2 = new LieMerrienHermiteSubdivision(RnGroup.INSTANCE, RnExponential.INSTANCE).cyclic(control);
    for (int count = 0; count < 6; ++count) {
      Tensor it1 = hs1.iterate();
      Tensor it2 = hs2.iterate();
      assertEquals(it1, it2);
      ExactTensorQ.require(it1);
      ExactTensorQ.require(it2);
    }
  }

  public void testStringQuantity() {
    Tensor control = Tensors.fromString("{{0[m], 0[m*s^-1]}, {1[m], 0[m*s^-1]}, {0[m], -1[m*s^-1]}, {0[m], 0[m*s^-1]}}");
    HermiteSubdivision hermiteSubdivision = //
        new LieMerrienHermiteSubdivision(RnGroup.INSTANCE, RnExponential.INSTANCE).string(Quantity.of(1, "s"), control);
    hermiteSubdivision.iterate();
    hermiteSubdivision.iterate();
    hermiteSubdivision.iterate();
  }

  public void testCyclicQuantity() {
    Tensor control = Tensors.fromString("{{0[m], 0[m*s^-1]}, {1[m], 0[m*s^-1]}, {0[m], -1[m*s^-1]}, {0[m], 0[m*s^-1]}}");
    HermiteSubdivision hermiteSubdivision = //
        new LieMerrienHermiteSubdivision(RnGroup.INSTANCE, RnExponential.INSTANCE).cyclic(Quantity.of(1, "s"), control);
    hermiteSubdivision.iterate();
    hermiteSubdivision.iterate();
    hermiteSubdivision.iterate();
  }

  public void testNullFail() {
    try {
      new LieMerrienHermiteSubdivision(Se2CoveringGroup.INSTANCE, null);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      new LieMerrienHermiteSubdivision(null, Se2CoveringExponential.INSTANCE);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
