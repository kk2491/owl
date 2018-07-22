// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.map.Se2CoveringExponential;
import ch.ethz.idsc.owl.math.map.Se2CoveringGroupAction;
import ch.ethz.idsc.owl.math.map.Se2CoveringIntegrator;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** not sure if Sc is the proper name:
 * the Sc2 geodesic does <b>not</b> identify the angles 0 and 2 * pi */
/* package */ enum Sc2Geodesic implements GeodesicInterface {
  INSTANCE;
  // ---
  @Override // from GeodesicInterface
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    Tensor p_inv = new Se2CoveringGroupAction(p).inverse();
    Tensor delta = new Se2CoveringGroupAction(p_inv).combine(q);
    Tensor x = Se2CoveringExponential.INSTANCE.log(delta).multiply(scalar);
    return Se2CoveringIntegrator.INSTANCE.spin(p, x);
  }
}
