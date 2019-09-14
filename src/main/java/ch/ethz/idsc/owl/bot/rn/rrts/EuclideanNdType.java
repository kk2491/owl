// code by gjoel
package ch.ethz.idsc.owl.bot.rn.rrts;

import ch.ethz.idsc.owl.data.nd.EuclideanNdCenter;
import ch.ethz.idsc.owl.data.nd.NdCenterInterface;
import ch.ethz.idsc.owl.rrts.NdType;
import ch.ethz.idsc.tensor.Tensor;

public enum EuclideanNdType implements NdType {
  INSTANCE;
  // ---
  @Override // from RrtsNdType
  public Tensor convert(Tensor tensor) {
    return tensor;
  }

  @Override // from RrtsNdType
  public NdCenterInterface ndCenterInterfaceBeg(Tensor tensor) {
    return EuclideanNdCenter.of(tensor);
  }

  @Override // from RrtsNdType
  public NdCenterInterface ndCenterInterfaceEnd(Tensor tensor) {
    return EuclideanNdCenter.of(tensor);
  }
}